/**
 * localserver测试
 */
package org.ngame.socket.test;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import org.ngame.socket.LocalClient;
import org.ngame.socket.LocalServer;
import org.ngame.socket.NClient;

/**
 *
 * @author beykery
 */
public class TestLocalServer extends LocalServer
{

	public TestLocalServer(InetSocketAddress addr)
	{
		super(addr);
	}

	@Override
	protected void preStop()
	{
		System.out.println("prestop...");
	}

	@Override
	public void onOpen(NClient conn)
	{
		System.out.println("onopen:" + conn);
	}

	@Override
	public void onClose(NClient conn, boolean local)
	{
		System.out.println("onclose:" + conn);
	}

	@Override
	public void onMessage(NClient conn, Object message)
	{
		System.out.println("onMessage:" + message);
	}

	@Override
	public void onError(NClient conn, Throwable ex)
	{
		System.out.println("onerror:" + ex);
	}

	@Override
	public void onIdle(NClient conn, IdleStateEvent event)
	{
		System.out.println("onidle:" + event);
	}

	public static void main(String[] args) throws UnknownHostException, InterruptedException
	{
		InetSocketAddress addr = new InetSocketAddress(9999);
		TestLocalServer ls = new TestLocalServer(addr);
		ls.start();
		LocalClient lc = new LocalClient(9999, new NioEventLoopGroup())
		{

			@Override
			public void onOpen(NClient conn)
			{
				System.out.println("onopen:" + conn);
				Object o = new Object();
				System.out.println(o);
				this.sendFrame(o);
			}

			@Override
			public void onClose(NClient conn, boolean local)
			{
				System.out.println("onclose:" + conn);
			}

			@Override
			public void onMessage(NClient conn, Object message)
			{
				System.out.println("onmessage:" + message);
			}

			@Override
			public void onError(NClient conn, Throwable ex)
			{
				System.out.println("onerror:" + ex);
			}

			@Override
			public void onIdle(NClient conn, IdleStateEvent event)
			{
				System.out.println("onidle:" + conn);
				conn.close();
			}

      @Override
      public void onBusy(NClient conn)
      {
       
      }
		};
		lc.connect().sync();
		lc.connection().idle(3, 0, 0, TimeUnit.SECONDS);
	}

  @Override
  public void onBusy(NClient conn)
  {
   
  }
}
