/**
 * http请求
 */
package org.ngame.socket.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import org.ngame.socket.NSocket;
import org.ngame.socket.SocketClient;
import org.ngame.socket.protocol.LVProtocol;

/**
 *
 * @author Beykery
 */
public class TestHttpClient
{

	public static void main(String[] args)
	{
		//testJDK();
		testNetty();
	}

	/**
	 * jdk connection测试
	 */
	private static void testJDK()
	{
		try
		{
			URL url = new URL("http://localhost:3210/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Keep-Alive", "3000000");
			conn.connect();
			OutputStream o = conn.getOutputStream();
			DataOutputStream dos = new DataOutputStream(o);
			String content = "hi";
			dos.writeShort(2);
			dos.write(content.getBytes());
			int code = conn.getResponseCode();
			if (code == 200)
			{
				DataInputStream dis = new DataInputStream(conn.getInputStream());
				short l = dis.readShort();
				byte[] c = new byte[l];
				dis.readFully(c);
				System.out.println(new String(c));
			}

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Keep-Alive", "3000000");
			conn.connect();
			o = conn.getOutputStream();
			dos = new DataOutputStream(o);
			content = "hi";
			dos.writeShort(2);
			dos.write(content.getBytes());
			dos.flush();
			code = conn.getResponseCode();
			if (code == 200)
			{
				DataInputStream dis = new DataInputStream(conn.getInputStream());
				short l = dis.readShort();
				byte[] c = new byte[l];
				dis.readFully(c);
				System.out.println(new String(c));
			}
			conn.disconnect();
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("异常");
		}
	}

	/**
	 * netty http客户端测试
	 */
	private static void testNetty()
	{
		SocketClient sc = new SocketClient(new InetSocketAddress("localhost", 3210), new LVProtocol(), new NioEventLoopGroup(4), true)
		{

			@Override
			public void onOpen(NSocket conn)
			{
				System.out.println("连接成功");
			}

			@Override
			public void onClose(NSocket conn, boolean local)
			{
				System.out.println("连接关闭：" + local);
			}

			@Override
			public void onMessage(NSocket conn, ByteBuf message)
			{
				short l = message.readShort();
				byte[] content = new byte[l];
				message.readBytes(content);
				message.release();
				System.out.println("收到消息" + new String(content));
			}

			@Override
			public void onError(NSocket conn, Throwable ex)
			{
				ex.printStackTrace();
			}

			@Override
			public void onIdle(NSocket conn, IdleStateEvent event)
			{
				System.out.println("超时。。。");
			}
		};
		try
		{
			sc.connect().await();
			ByteBuf bb = PooledByteBufAllocator.DEFAULT.buffer(128);
			bb.writeShort(2);
			bb.writeBytes("hi".getBytes());
			bb.retain();
			sc.sendFrame(bb.duplicate());
			sc.sendFrame(bb);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
