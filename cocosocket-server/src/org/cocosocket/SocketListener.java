package org.cocosocket;

import io.netty.buffer.ByteBuf;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数据来往监听器
 *
 * @author beykery
 */
public abstract class SocketListener
{

    private static final Logger LOG = Logger.getLogger(SocketListener.class.getName());
    int max_connection = 10000;
    AtomicInteger cur_connection = new AtomicInteger(0);

    public abstract void onOpen(CocoSocket conn);

    public abstract void onClose(CocoSocket conn, boolean local);

    public abstract void onMessage(CocoSocket conn, ByteBuf message);

    public abstract void onError(CocoSocket conn, Throwable ex);

    public abstract void onIdle(CocoSocket conn, IdleStateEvent event);

    /**
     * 初始化
     */
    public SocketListener()
    {
        try
        {
            max_connection = Integer.parseInt(System.getProperty("game.connections"));
        } catch (Exception e)
        {
            LOG.log(Level.WARNING, "配置最大连接数失败");
        }
    }

    /**
     * 连接关闭
     *
     * @param aThis
     * @param closeReason
     */
    void socketClosed(CocoSocket so, boolean closeReason)
    {
        cur_connection.decrementAndGet();
        this.onClose(so, closeReason);
    }
}
