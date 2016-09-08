/**
 * 采用varint32头部的framedata
 */
package org.ngame.socket.framing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.ngame.socket.protocol.Protocol;
import org.ngame.socket.util.CodedOutputStream;

/**
 *
 * @author beykery
 */
public class Varint32Framedata extends Framedata
{

  private static final InternalLogger LOG =InternalLoggerFactory.getInstance(Varint32Framedata.class);

  public Varint32Framedata(int l)
  {
    this.payload = PooledByteBufAllocator.DEFAULT.buffer(l);
    this.payload = this.payload.order(Protocol.order);
  }

  protected Varint32Framedata()
  {
  }

  @Override
  public Varint32Framedata copy()
  {
    Varint32Framedata fd = new Varint32Framedata();
    fd.end = end;
    if (payload != null)
    {
      fd.payload = payload.copy();
    }
    return fd;
  }

  @Override
  public Varint32Framedata duplicate()
  {
    Varint32Framedata fd = new Varint32Framedata();
    fd.end = end;
    if (payload != null)
    {
      if (payload.refCnt() > 0)
      {
        payload.retain();
      }
      fd.payload = payload.duplicate();
    }
    return fd;
  }

  @Override
  public void end()
  {
    if (!this.end)
    {
      final ByteBuf bb = payload;
      final int reader = bb.readerIndex();
      final int writer = bb.writerIndex();
      final int l = writer - reader;//数据长度
      ByteBuf tar = PooledByteBufAllocator.DEFAULT.buffer(l + 5);
      tar = tar.order(Protocol.order);
      writeVarint32(tar, l);
      tar.writeBytes(bb);
      payload = tar;
      bb.release();
      this.end = true;
    }
  }

  /**
   * 写入一个varint32
   *
   * @param tar
   * @param v
   * @return
   */
  public static ByteBuf writeVarint32(ByteBuf tar, int v)
  {
    try
    {
      int len = CodedOutputStream.computeRawVarint32Size(v);
      CodedOutputStream headerOut = CodedOutputStream.newInstance(tar, len);
      headerOut.writeRawVarint32(v);
      headerOut.flush();
      return tar;
    } catch (Exception e)
    {
      LOG.error("写入varint32" + v + "失败");
      return null;
    }
  }

}
