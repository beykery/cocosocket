/**
 * 协议
 */
package org.ngame.socket.protocol;

import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;
import org.ngame.socket.NSocket;
import org.ngame.socket.exeptions.InvalidDataException;
import org.ngame.socket.exeptions.LimitExedeedException;

/**
 * 解析字节流的协议
 *
 * @author beykery
 */
public abstract class Protocol
{

	protected NSocket context;
	public static ByteOrder order = ByteOrder.BIG_ENDIAN;

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
	public void setContext(NSocket context)
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
	public abstract ByteBuf translateFrame(ByteBuf buf) throws LimitExedeedException, InvalidDataException;

}
