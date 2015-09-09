/**
 * 协议
 */
package org.ngame.socket.protocol;

import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;
import org.ngame.socket.NClient;
import org.ngame.socket.exeptions.InvalidDataException;
import org.ngame.socket.exeptions.LimitExedeedException;

/**
 * 解析字节流的协议
 *
 * @author beykery
 */
public abstract class Protocol
{

  protected NClient context;
  public static ByteOrder order = ByteOrder.BIG_ENDIAN;
  protected int maxFrameSize = Integer.MAX_VALUE;//最大帧长度

  static
  {
    try
    {
      String o = System.getProperty("byte.order");
      order = (o == null || "BIG_ENDIAN".equals(o)) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    } catch (Exception e)
    {
    }
  }

  /**
   * 上下文
   *
   * @param context
   */
  public void setContext(NClient context)
  {
    this.context = context;
  }

  /**
   * 解析一帧数据
   *
   * @param buf
   * @return
   * @throws LimitExedeedException
   * @throws InvalidDataException
   */
  public abstract ByteBuf translateFrame(ByteBuf buf) throws Exception;

  /**
   * 解析完成的数据的头部字节数
   *
   * @return
   */
  public abstract int headerLen();

  public void setMaxFrameSize(int maxFrameSize)
  {
    this.maxFrameSize = maxFrameSize;
  }

  public int getMaxFrameSize()
  {
    return maxFrameSize;
  }
}
