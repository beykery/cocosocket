package org.ngame.socket.framing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ngame.socket.protocol.Protocol;

/**
 * 帧
 *
 * @author beykery
 */
public class Framedata
{

	private static final Logger LOG = Logger.getLogger(Framedata.class.getName());
	private ByteBuf payload;//帧内容
	private boolean end;//是否封包

	/**
	 * 默认初始容量100
	 */
	public Framedata()
	{
		this(100);
	}

	/**
	 * 设置初始容量
	 *
	 * @param l
	 */
	public Framedata(int l)
	{
		this.payload = PooledByteBufAllocator.DEFAULT.buffer(l);
		this.payload.order(Protocol.order);
		this.payload.writeShort(0);
	}

	/**
	 * 返回buffer
	 *
	 * @return
	 */
	public ByteBuf getData()
	{
		return payload;
	}

	/**
	 * 数据
	 *
	 * @param bb
	 */
	public void setData(ByteBuf bb)
	{
		this.payload = bb;
	}

	/**
	 * 写入一个字节
	 *
	 * @param b
	 * @return
	 */
	public Framedata putByte(int b)
	{
		if (!end)
		{
			payload.writeByte(b);
		}
		return this;
	}

	public Framedata putBytes(byte[] b)
	{
		if (!end)
		{
			payload.writeBytes(b);
		}
		return this;
	}

	public Framedata putBytes(ByteBuf src)
	{
		if (!end)
		{
			payload.writeBytes(src);
		}
		return this;
	}

	public Framedata putShort(int s)
	{
		if (!end)
		{
			payload.writeShort(s);
		}
		return this;
	}

	public Framedata putInt(int s)
	{
		if (!end)
		{
			payload.writeInt(s);
		}
		return this;
	}

	public Framedata putLong(long s)
	{
		if (!end)
		{
			payload.writeLong(s);
		}
		return this;
	}

	public Framedata putFloat(float s)
	{
		if (!end)
		{
			payload.writeFloat(s);
		}
		return this;
	}

	public Framedata putString(String s)
	{
		if (!end)
		{
			byte[] b = s.getBytes();
			payload.writeShort(b.length);
			payload.writeBytes(b);
		}
		return this;
	}

	/**
	 * 取异或
	 *
	 * @param s
	 * @param ks
	 * @return
	 */
	public Framedata putString(String s, byte[] ks)
	{
		if (!end)
		{
			byte[] b = s.getBytes();
			payload.writeShort(b.length);
			xor(b, ks);
			payload.writeBytes(b);
		}
		return this;
	}

	public Framedata putString(String s, String encode)
	{
		if (!end)
		{
			try
			{
				byte[] b = s.getBytes(encode);
				payload.writeShort(b.length);
				payload.writeBytes(b);
			} catch (UnsupportedEncodingException e)
			{
				LOG.log(Level.SEVERE, "编码错误：" + encode);
			}
		}
		return this;
	}

	/**
	 * 取异或
	 *
	 * @param s
	 * @param encode
	 * @param ks
	 * @return
	 */
	public Framedata putString(String s, String encode, byte[] ks)
	{
		if (!end)
		{
			try
			{
				byte[] b = s.getBytes(encode);
				payload.writeShort(b.length);
				xor(b, ks);
				payload.writeBytes(b);
			} catch (UnsupportedEncodingException e)
			{
				LOG.log(Level.SEVERE, "编码错误：" + encode);
			}
		}
		return this;
	}

	/**
	 * 结束修改
	 */
	public void end()
	{
		final ByteBuf bb = payload;
		final int reader = bb.readerIndex();
		final int writer = bb.writerIndex();
		final int l = writer - reader - 2;//数据长度
		bb.writerIndex(reader);
		bb.writeShort(l);
		bb.writerIndex(writer);
		this.end = true;
	}

	/**
	 * 是否结束
	 *
	 * @return
	 */
	public boolean isEnd()
	{
		return end;
	}

	/**
	 * 设置end
	 *
	 * @param end
	 */
	public void setEnd(boolean end)
	{
		this.end = end;
		if (end)
		{
			this.end();
		}
	}

	/**
	 * 复制一个帧
	 *
	 * @return
	 */
	public Framedata duplicate()
	{
		Framedata fd = new Framedata();
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

	/**
	 * 复制一个帧
	 *
	 * @return
	 */
	public Framedata copy()
	{
		Framedata fd = new Framedata();
		fd.end = end;
		if (payload != null)
		{
			fd.payload = payload.copy();
		}
		return fd;
	}

	/**
	 * 释放所有字节
	 *
	 * @return
	 */
	public boolean release()
	{
		boolean r = false;
		if (payload != null && payload.refCnt() > 0)
		{
			r = payload.release(payload.refCnt());
		}
		return r;
	}

	/**
	 * 释放所有字节
	 *
	 * @param t
	 * @return
	 */
	public boolean release(int t)
	{
		boolean r = false;
		if (payload != null && t > 0 && t <= payload.refCnt())
		{
			r = payload.release(t);
		}
		return r;
	}

	/**
	 * 取异或
	 *
	 * @param bs
	 * @param ks
	 */
	public static void xor(byte[] bs, byte[] ks)
	{
		if (ks != null && ks.length > 0)
		{
			for (int i = 0; i < bs.length; i++)
			{
				bs[i] = (byte) (bs[i] ^ ks[i % ks.length]);
			}
		}
	}

	@Override
	public String toString()
	{
		return payload == null ? "empty" : payload.toString();
	}
}
