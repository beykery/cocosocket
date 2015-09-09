/**
 * google protobuf 的varint32算法
 */
package org.ngame.socket.util;

import java.io.IOException;

public final class CodedInputStream
{

  private final byte[] buffer;
  private int bufferSize;
  private int bufferSizeAfterLimit;
  private int bufferPos;
  private int totalBytesRetired;
  private int currentLimit = Integer.MAX_VALUE;
  private final int sizeLimit = DEFAULT_SIZE_LIMIT;
  private static final int DEFAULT_SIZE_LIMIT = 64 << 20;  // 64MB

  public static CodedInputStream newInstance(final byte[] buf)
  {
    return newInstance(buf, 0, buf.length);
  }

  public static CodedInputStream newInstance(final byte[] buf, final int off, final int len)
  {
    CodedInputStream result = new CodedInputStream(buf, off, len);
    try
    {
      result.pushLimit(len);
    } catch (IllegalArgumentException ex)
    {
      throw new IllegalArgumentException(ex);
    }
    return result;
  }

  public int readRawVarint32() throws IOException
  {
    byte tmp = readRawByte();//-128 127
    if (tmp >= 0)
    {
      return tmp;
    }
    int result = tmp & 0x7f;
    if ((tmp = readRawByte()) >= 0)
    {
      result |= tmp << 7;
    } else
    {
      result |= (tmp & 0x7f) << 7;
      if ((tmp = readRawByte()) >= 0)
      {
        result |= tmp << 14;
      } else
      {
        result |= (tmp & 0x7f) << 14;
        if ((tmp = readRawByte()) >= 0)
        {
          result |= tmp << 21;
        } else
        {
          result |= (tmp & 0x7f) << 21;
          result |= (tmp = readRawByte()) << 28;
          if (tmp < 0)
          {
            // Discard upper 32 bits.
            for (int i = 0; i < 5; i++)
            {
              if (readRawByte() >= 0)
              {
                return result;
              }
            }
            throw new IOException();
          }
        }
      }
    }
    return result;
  }

  private CodedInputStream(final byte[] buffer, final int off, final int len)
  {
    this.buffer = buffer;
    bufferSize = off + len;
    bufferPos = off;
    totalBytesRetired = -off;
  }

  public int pushLimit(int byteLimit) throws IllegalArgumentException
  {
    if (byteLimit < 0)
    {
      throw new IllegalArgumentException();
    }
    byteLimit += totalBytesRetired + bufferPos;
    final int oldLimit = currentLimit;
    if (byteLimit > oldLimit)
    {
      throw new IllegalArgumentException();
    }
    currentLimit = byteLimit;
    recomputeBufferSizeAfterLimit();
    return oldLimit;
  }

  private void recomputeBufferSizeAfterLimit()
  {
    bufferSize += bufferSizeAfterLimit;
    final int bufferEnd = totalBytesRetired + bufferSize;
    if (bufferEnd > currentLimit)
    {
      // Limit is in current buffer.
      bufferSizeAfterLimit = bufferEnd - currentLimit;
      bufferSize -= bufferSizeAfterLimit;
    } else
    {
      bufferSizeAfterLimit = 0;
    }
  }

  private boolean refillBuffer(final boolean mustSucceed) throws IOException
  {
    if (totalBytesRetired + bufferSize == currentLimit)
    {
      // Oops, we hit a limit.
      if (mustSucceed)
      {
        throw new IOException();
      } else
      {
        return false;
      }
    }
    totalBytesRetired += bufferSize;
    bufferPos = 0;
    bufferSize = -1;
    if (bufferSize == 0 || bufferSize < -1)
    {
      throw new IllegalStateException(
              "InputStream#read(byte[]) returned invalid result: " + bufferSize
              + "\nThe InputStream implementation is buggy.");
    }
    if (bufferSize == -1)
    {
      bufferSize = 0;
      if (mustSucceed)
      {
        throw new IOException();
      } else
      {
        return false;
      }
    } else
    {
      recomputeBufferSizeAfterLimit();
      final int totalBytesRead = totalBytesRetired + bufferSize + bufferSizeAfterLimit;
      if (totalBytesRead > sizeLimit || totalBytesRead < 0)
      {
        throw new IOException();
      }
      return true;
    }
  }

  public byte readRawByte() throws IOException
  {
    if (bufferPos == bufferSize)
    {
      refillBuffer(true);
    }
    return buffer[bufferPos++];
  }
}
