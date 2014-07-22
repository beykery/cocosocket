/**
 * 客户端测试
 */
package org.ngame.socket.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ngame.socket.NSocket;
import org.ngame.socket.SocketClient;
import org.ngame.socket.framing.Framedata;
import org.ngame.socket.protocol.LVProtocol;
import org.ngame.socket.protocol.Protocol;

/**
 *
 * @author beykery
 */
public class TestClient extends SocketClient
{

    private static final Logger LOG = Logger.getLogger(TestClient.class.getName());

    public TestClient(InetSocketAddress address, Protocol protocol)
    {
        super(address, protocol, new NioEventLoopGroup(1),true);
    }

    @Override
    public void onOpen(NSocket conn)
    {
        LOG.log(Level.WARNING, "连接建立:" + conn);
//		try
//		{
//			Thread.sleep(50);
//		} catch (InterruptedException ex)
//		{
//			Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
//		}
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    Framedata fd = new Framedata(100);
                    fd.putString("你好");
                    fd.end();
                    TestClient.this.sendFrame(fd);
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex)
                    {
                        Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClose(NSocket conn, boolean local)
    {
        LOG.log(Level.WARNING, "连接关闭:" + conn);
    }

    @Override
    public void onMessage(NSocket conn, ByteBuf message)
    {
		message.readerIndex(2);
		short l = message.readShort();
		byte[] m = new byte[l];
		message.readBytes(m);
		LOG.log(Level.WARNING, "消息：" + new String(m));
		message.readerIndex(0);
		conn.sendFrame(message);
    }

    @Override
    public void onError(NSocket conn, Throwable ex)
    {
        LOG.log(Level.WARNING, "异常：" + ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String... args)
    {
        TestClient tc = new TestClient(new InetSocketAddress(3210), new LVProtocol());
        tc.connect();
    }

    @Override
    public void onIdle(NSocket conn, IdleStateEvent event)
    {
        conn.close();
    }
}
