package org.ngame.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * 本地客户端，用来连接本地服务器
 *
 * @author beykery
 */
public abstract class LocalClient extends NListener
{

	private static final Logger LOG = Logger.getLogger(LocalClient.class.getName());
	private InetSocketAddress address;
	private NClient conn;
	private EventLoopGroup group;

	/**
	 *
	 * @param port
	 * @param group
	 */
	public LocalClient(int port, EventLoopGroup group)
	{
		if (group == null)
		{
			throw new IllegalArgumentException("group不能为空");
		}
		this.address = new InetSocketAddress(port);
		this.group = group;
	}

	/**
	 * 地址
	 *
	 * @return
	 */
	public InetSocketAddress getAddress()
	{
		return address;
	}

	/**
	 * 连接
	 *
	 * @return
	 */
	public ChannelFuture connect()
	{
		try
		{
			return tryToConnect(address);
		} catch (InterruptedException ex)
		{
			this.onError(conn, ex);
		}
		return null;
	}

	/**
	 * 关闭连接
	 *
	 * @return
	 */
	public ChannelFuture close()
	{
		return this.conn.close();
	}

	/**
	 * 发送帧数据
	 *
	 * @param data
	 * @return
	 */
	public ChannelFuture sendFrame(Object data)
	{
		return conn.sendFrame(data);
	}

	/**
	 * 是否打开
	 *
	 * @return
	 */
	public boolean isOpen()
	{
		return conn != null && conn.isOpen();
	}

	/**
	 * 链接
	 *
	 * @param remote
	 * @throws InterruptedException
	 */
	private ChannelFuture tryToConnect(final InetSocketAddress remote) throws InterruptedException
	{
		final Class<? extends LocalChannel> c;
		c = LocalChannel.class;
		Bootstrap b = new Bootstrap();
		b.group(group)
				.channel(c)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.handler(new ChannelInitializer<Channel>()
						{
							@Override
							public void initChannel(Channel ch) throws Exception
							{
								ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
								NClient socket = new NClient(LocalClient.this, ch, null);
								socket.setNetwork(NServer.NETWORK_LOCAL);
								LocalClient.this.conn = socket;
								socket.setClient(true);
								ch.pipeline().addLast(socket);
							}
				});
		return b.connect(new LocalAddress(String.valueOf(remote.getPort())));
	}

	public NClient connection()
	{
		return conn;
	}
}
