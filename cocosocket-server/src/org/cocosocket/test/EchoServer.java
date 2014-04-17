/**
 * 测试
 *//**
 * 测试
 *//**
 * 测试
 *//**
 * 测试
 */
package org.cocosocket.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cocosocket.CocoSocket;
import org.cocosocket.SocketServer;

/**
 *
 * @author beykery
 */
public class EchoServer extends SocketServer
{

    private static final Logger LOG = Logger.getLogger(EchoServer.class.getName());

    public EchoServer(InetSocketAddress address)
    {
        super(address);
    }

    @Override
    public void onOpen(CocoSocket conn)
    {
        LOG.log(Level.WARNING, "链接到来：" + conn);
    }

    @Override
    public void onClose(CocoSocket conn, boolean local)
    {
        LOG.log(Level.WARNING, "连接关闭：" + conn + local);
    }

    @Override
    public void onMessage(CocoSocket conn, ByteBuf message)
    {
        message.readerIndex(0);
        conn.sendFrame(message);
    }

    @Override
    public void onError(CocoSocket conn, Throwable ex)
    {
        ex.printStackTrace();
        LOG.log(Level.WARNING, "异常：" + ex.getMessage());
        conn.close();
    }

    @Override
    protected void preStop()
    {
        LOG.log(Level.WARNING, "马上停止服务器");
    }

    @Override
    public void onIdle(CocoSocket conn, IdleStateEvent event)
    {
        System.out.println("idle");
        conn.close();
    }
}
