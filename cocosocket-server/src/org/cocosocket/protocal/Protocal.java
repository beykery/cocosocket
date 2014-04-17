/**
 * 协议
 *//**
 * 协议
 */
package org.cocosocket.protocal;


import io.netty.buffer.ByteBuf;
import org.cocosocket.CocoSocket;
import org.cocosocket.exeptions.InvalidDataException;
import org.cocosocket.exeptions.LimitExedeedException;

/**
 * 解析字节流的协议
 *
 * @author beykery
 */
public abstract class Protocal
{

    protected CocoSocket context;

    /**
     * 上下文
     *
     * @param context
     */
    public void setContext(CocoSocket context)
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
