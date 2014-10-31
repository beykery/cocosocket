/**
 * 测试
 */
package org.ngame.socket.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ngame.socket.NSocket;
import org.ngame.socket.SocketServer;
import org.ngame.socket.protocol.LVProtocol;

/**
 *
 * @author beykery
 */
public class TestServer extends SocketServer
{

    private static final Logger LOG = Logger.getLogger(TestServer.class.getName());

    public TestServer(InetSocketAddress address)
    {
        super(address,true);
    }

    @Override
    public void onOpen(NSocket conn)
    {
        LOG.log(Level.WARNING, "链接到来：" + conn);
		//conn.idle(2, 2, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onClose(NSocket conn, boolean local)
    {
        LOG.log(Level.WARNING, "连接关闭：" + conn + local);
    }

    @Override
    public void onMessage(NSocket conn, ByteBuf message)
    {
        message.readerIndex(0);
        conn.sendFrame(message);
    }

    @Override
    public void onError(NSocket conn, Throwable ex)
    {
        ex.printStackTrace();
        LOG.log(Level.WARNING, "异常：" + ex.getMessage());
        conn.close();
    }

    public static void main(String... args) throws InterruptedException
    {
        TestServer server = new TestServer(new InetSocketAddress(3210));
        server.setProtocol(LVProtocol.class);
        server.start();
    }

    @Override
    protected void preStop()
    {
        LOG.log(Level.WARNING, "马上停止服务器");
    }

    @Override
    public void onIdle(NSocket conn, IdleStateEvent event)
    {
        System.out.println("idle");
        conn.close();
    }
}
