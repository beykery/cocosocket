/**
 * 无自定义类型（不再处理分帧逻辑）
 */
package org.ngame.socket.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.ngame.socket.exeptions.InvalidDataException;
import org.ngame.socket.exeptions.LimitExedeedException;

/**
 *
 * @author beykery
 */
public class NullProtocol extends Protocol
{

	public NullProtocol()
	{
	}

	@Override
	public ByteBuf translateFrame(ByteBuf buf) throws LimitExedeedException, InvalidDataException
	{
		if (buf.isReadable())
		{
			ByteBuf r = PooledByteBufAllocator.DEFAULT.buffer(buf.readableBytes() + 8);
			r.order(order);
			r.writeBytes(buf);
			return r;
		}
		return null;
	}
}
