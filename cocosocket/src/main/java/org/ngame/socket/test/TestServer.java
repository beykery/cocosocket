/**
 * 测试
 */
package org.ngame.socket.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ngame.socket.NClient;
import org.ngame.socket.NServer;
import org.ngame.socket.protocol.Varint32HeaderProtocol;

/**
 *
 * @author beykery
 */
public class TestServer extends NServer
{

  private static final Logger LOG = Logger.getLogger(TestServer.class.getName());

  public TestServer(InetSocketAddress address)
  {
    super(address, NServer.NETWORK_SOCKET);
  }

  @Override
  public void onOpen(NClient conn)
  {
    LOG.log(Level.WARNING, "链接到来：" + conn);
    //conn.idle(2, 2, 2, TimeUnit.SECONDS);
  }

  @Override
  public void onClose(NClient conn, boolean local)
  {
    LOG.log(Level.WARNING, "连接关闭：" + conn + local);
  }

  @Override
  public void onMessage(NClient conn, Object message)
  {
    if (message instanceof TextWebSocketFrame)
    {
      TextWebSocketFrame f = (TextWebSocketFrame) message;
      System.out.println(f);
      System.out.println(f.text());
    } else
    {
      ByteBuf bb = (ByteBuf) message;
      bb.readerIndex(conn.getProtocol().headerLen());
      byte[] c = new byte[bb.readShort()];
      bb.readBytes(c);
      System.out.println(new String(c));
      bb.readerIndex(0);
      conn.sendFrame(bb);
    }
    try
    {
      Thread.sleep(500);
    } catch (Exception e)
    {
    }
  }

  @Override
  public void onError(NClient conn, Throwable ex)
  {
    ex.printStackTrace();
    LOG.log(Level.WARNING, "异常：" + ex.getMessage());
    conn.close();
  }

  public static void main(String... args) throws InterruptedException
  {
    TestServer server = new TestServer(new InetSocketAddress(1106));
    server.setProtocol(Varint32HeaderProtocol.class);
    server.start();
  }

  @Override
  protected void preStop()
  {
    LOG.log(Level.WARNING, "马上停止服务器");
  }

  @Override
  public void onIdle(NClient conn, IdleStateEvent event)
  {
    System.out.println("idle");
    conn.close();
  }
}
