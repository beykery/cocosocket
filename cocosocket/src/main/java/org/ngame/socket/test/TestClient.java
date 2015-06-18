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
import org.ngame.socket.NClient;
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
        super(address, protocol, new NioEventLoopGroup(1));
    }

    @Override
    public void onOpen(NClient conn)
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
              //  while (true)
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
    public void onClose(NClient conn, boolean local)
    {
        LOG.log(Level.WARNING, "连接关闭:" + conn);
    }

    @Override
    public void onMessage(NClient conn, Object message)
    {
        ByteBuf bb = (ByteBuf) message;
        bb.readerIndex(2);
        short l = bb.readShort();
        byte[] m = new byte[l];
        bb.readBytes(m);
        LOG.log(Level.WARNING, "消息：" + new String(m));
        bb.readerIndex(0);
        conn.sendFrame(bb);
    }

    @Override
    public void onError(NClient conn, Throwable ex)
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
        TestClient tc = new TestClient(new InetSocketAddress(9999), new LVProtocol());
        tc.connect();
    }

    @Override
    public void onIdle(NClient conn, IdleStateEvent event)
    {
        conn.close();
    }
}
