/**
 * 无自定义类型（不再处理分帧逻辑）
 */
package org.cocosocket.protocal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.cocosocket.exeptions.InvalidDataException;
import org.cocosocket.exeptions.LimitExedeedException;

/**
 *
 * @author beykery
 */
public class NullProtocal extends Protocal
{

    public NullProtocal()
    {
    }

    @Override
    public ByteBuf translateFrame(ByteBuf buf) throws LimitExedeedException, InvalidDataException
    {
        if (buf.isReadable())
        {
            ByteBuf r = PooledByteBufAllocator.DEFAULT.buffer(buf.readableBytes() + 8);
            r.writeBytes(buf);
            return r;
        }
        return null;
    }
}
