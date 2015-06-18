package org.ngame.socket.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public final class WebSocketClient
{

	static final String URL = "ws://10.18.121.202:8080";
	static final Map<String, Channel> cs = new HashMap<>();
	static EventLoopGroup group = new NioEventLoopGroup(8);

	/**
	 * 连接
	 *
	 * @param id
	 * @return
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 */
	private static Channel getConnection(String id) throws URISyntaxException, InterruptedException
	{
		Channel c = cs.get(id);
		if (c == null)
		{
			URI uri = new URI(URL);
			final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, 
					WebSocketVersion.V07, null, false, new DefaultHttpHeaders()));
			Bootstrap b = new Bootstrap();
			b.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>()
							{
								@Override
								protected void initChannel(SocketChannel ch)
								{
									ChannelPipeline p = ch.pipeline();
									p.addLast(
											new HttpClientCodec(),
											new HttpObjectAggregator(8192),
											handler);
								}
					});

			c = b.connect(uri.getHost(), uri.getPort()).sync().channel();
			handler.handshakeFuture().sync();
			cs.put(id, c);
		}
		return c;
	}

	static String mysqlurl = "jdbc:mysql://10.18.103.140:3306/slamdunk?user=root&password=123456&useUnicode=true&characterEncoding=UTF8";

	static Connection getMysqlConnection(String addr)
	{
		try
		{

			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("成功加载MySQL驱动程序");
			Connection conn = DriverManager.getConnection(addr);
			return conn;
		} catch (Exception e)
		{
		}
		return null;
	}

	public static void main(String[] args) throws Exception
	{
		Connection c = getMysqlConnection(mysqlurl);
		Statement stmt = c.createStatement();
		String sql = "select id,socketId,params from user_command_log_10";
		ResultSet rs = stmt.executeQuery(sql);
		int i = 0;
		while (rs.next())
		{
			i++;
			int id=rs.getInt(1);
			String socketId = rs.getString(2);
			String params = rs.getString(3);
			System.out.println(id);
			Channel channel=getConnection(socketId);
			channel.writeAndFlush(new TextWebSocketFrame(params));
		}
		System.out.println(i);
	}

}
