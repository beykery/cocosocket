/**
 * 改写自google protobuf的varint32
 */
package org.ngame.socket.util;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

/**
 *
 * @author beykery
 */
public final class CodedOutputStream
{

  private final byte[] buffer;
  private final int limit;
  private int position;
  private final ByteBuf output;

  private CodedOutputStream(final ByteBuf output, final byte[] buffer)
  {
    this.output = output;
    this.buffer = buffer;
    position = 0;
    limit = buffer.length;
  }

  /**
   * Create a new {@code CodedOutputStream} wrapping the given
   * {@code OutputStream} with a given buffer size.
   *
   * @param output
   * @param bufferSize
   * @return
   */
  public static CodedOutputStream newInstance(final ByteBuf output, final int bufferSize)
  {
    return new CodedOutputStream(output, new byte[bufferSize]);
  }

  /**
   * Compute the number of bytes that would be needed to encode a varint.
   * {@code value} is treated as unsigned, so it won't be sign-extended if
   * negative.
   *
   * @param value
   * @return
   */
  public static int computeRawVarint32Size(final int value)
  {
    if ((value & (0xffffffff << 7)) == 0)
    {
      return 1;
    }
    if ((value & (0xffffffff << 14)) == 0)
    {
      return 2;
    }
    if ((value & (0xffffffff << 21)) == 0)
    {
      return 3;
    }
    if ((value & (0xffffffff << 28)) == 0)
    {
      return 4;
    }
    return 5;
  }

  /**
   * Encode and write a varint. {@code value} is treated as unsigned, so it
   * won't be sign-extended if negative.
   *
   * @param value
   * @throws java.io.IOException
   */
  public void writeRawVarint32(int value) throws IOException
  {
    while (true)
    {
      if ((value & ~0x7F) == 0)
      {
        writeRawByte(value);
        return;
      } else
      {
        writeRawByte((value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }

  /**
   * Write a single byte, represented by an integer value.
   *
   * @param value
   * @throws java.io.IOException
   */
  public void writeRawByte(final int value) throws IOException
  {
    writeRawByte((byte) value);
  }

  /**
   * Write a single byte.
   *
   * @param value
   * @throws java.io.IOException
   */
  public void writeRawByte(final byte value) throws IOException
  {
    if (position == limit)
    {
      refreshBuffer();
    }
    buffer[position++] = value;
  }

  /**
   * Internal helper that writes the current buffer to the output. The buffer
   * position is reset to its initial value when this returns.
   */
  private void refreshBuffer() throws IOException
  {
    // Since we have an output stream, this is our buffer
    // and buffer offset == 0
    output.writeBytes(buffer, 0, position);
    position = 0;
  }

  /**
   * Flushes the stream and forces any buffered bytes to be written. This does
   * not flush the underlying OutputStream.
   *
   * @throws java.io.IOException
   */
  public void flush() throws IOException
  {
    refreshBuffer();
  }
}
