/**
 * varint32头分帧协议
 */
package org.ngame.socket.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.ngame.socket.exeptions.InvalidDataException;
import org.ngame.socket.util.CodedInputStream;
import org.ngame.socket.util.CodedOutputStream;

/**
 *
 * @author beykery
 */
public class Varint32HeaderProtocol extends Protocol
{

  private static final int STATUS_HEADER = 0;//读头
  private static final int STATUS_CONTENT = 1;//读内容

  private final byte[] header;
  private int index;
  private int status;
  private int len;
  private ByteBuf incompleteframe;//尚未完成的帧
  private int headerLen;//头部长度

  /**
   * 初始化头缓存
   */
  public Varint32HeaderProtocol()
  {
    header = new byte[5];
  }

  @Override
  public ByteBuf translateFrame(ByteBuf buf) throws Exception
  {
    while (buf.isReadable())
    {
      switch (status)
      {
        case STATUS_HEADER:
          for (; index < header.length; index++)
          {
            if (!buf.isReadable())
            {
              break;
            }
            header[index] = buf.readByte();
            if (header[index] >= 0)
            {
              int length = 0;
              try
              {
                length = CodedInputStream.newInstance(header, 0, index + 1).readRawVarint32();
                if (length < 0 || length > maxFrameSize)
                {
                  throw new InvalidDataException("帧长度非法：" + length);
                }
              } catch (Exception e)
              {
                throw new InvalidDataException("读取帧头异常:" + length);
              }
              len = length;
              status = STATUS_CONTENT;
              headerLen = CodedOutputStream.computeRawVarint32Size(len);
              incompleteframe = PooledByteBufAllocator.DEFAULT.buffer(len + headerLen);
              incompleteframe = incompleteframe.order(Protocol.order);
              CodedOutputStream headerOut = CodedOutputStream.newInstance(incompleteframe, headerLen);
              headerOut.writeRawVarint32(len);
              headerOut.flush();
              break;
            }
          }
          break;
        case STATUS_CONTENT:
          int l = Math.min(buf.readableBytes(), incompleteframe.writableBytes());
          if (l > 0)
          {
            incompleteframe.writeBytes(buf, l);
          }
          if (incompleteframe.writableBytes() <= 0)
          {
            status = STATUS_HEADER;
            index = 0;
            return incompleteframe;
          }
          break;
      }
    }
    return null;
  }

  @Override
  public int headerLen()
  {
    return this.headerLen;
  }
}
