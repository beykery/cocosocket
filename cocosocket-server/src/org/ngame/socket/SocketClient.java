package org.ngame.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import java.net.InetSocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.util.logging.Logger;
import org.ngame.socket.framing.Framedata;
import org.ngame.socket.protocol.Protocol;

/**
 * 客户端
 *
 * @author beykery
 */
public abstract class SocketClient extends SocketListener
{

	private static final Logger LOG = Logger.getLogger(SocketClient.class.getName());
	private InetSocketAddress address;
	private NSocket conn;
	private Protocol protocol;
	private EventLoopGroup group;
	protected static boolean linux;
	protected boolean http;

	static
	{
		linux = System.getProperty("os", "win").contains("linux");
	}

	/**
	 *
	 * @param address
	 * @param protocol
	 * @param group
	 * @param http
	 */
	public SocketClient(InetSocketAddress address, Protocol protocol, EventLoopGroup group, boolean http)
	{
		if (address == null)
		{
			throw new IllegalArgumentException();
		}
		if (protocol == null)
		{
			throw new IllegalArgumentException("解析器不能为空");
		}
		if (group == null)
		{
			throw new IllegalArgumentException("group不能为空");
		}
		this.address = address;
		this.protocol = protocol;
		this.group = group;
		this.http = http;
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
	 * 协议
	 *
	 * @return
	 */
	public Protocol getProtocol()
	{
		return protocol;
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
	 * @throws NotYetConnectedException
	 */
	public ChannelFuture sendFrame(Framedata data)
	{
		return conn.sendFrame(data);
	}

	/**
	 * 发送帧数据
	 *
	 * @param data
	 * @return
	 * @throws NotYetConnectedException
	 */
	public ChannelFuture sendFrame(ByteBuf data)
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
		final Class<? extends SocketChannel> c;
		c = linux ? EpollSocketChannel.class : NioSocketChannel.class;
		Bootstrap b = new Bootstrap();
		b.group(group)
			.channel(c)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_LINGER, 0)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0)
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			.handler(new ChannelInitializer<SocketChannel>()
				{
					@Override
					public void initChannel(SocketChannel ch) throws Exception
					{
						ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
						NSocket socket = new NSocket(SocketClient.this, ch, protocol);
						SocketClient.this.conn = socket;
						if (SocketClient.this.http)//如果是http，则先使用httpcodec
						{
							ch.pipeline().addLast(new HttpClientCodec());
							socket.setHttp(true);
							String uri = "http://" + remote.getAddress().getHostAddress() + ":" + remote.getPort() + "/";
							socket.setURI(uri);
						}
						socket.setClient(true);
						ch.pipeline().addLast(socket);
					}
			});
		return b.connect(remote);
	}

	public NSocket connection()
	{
		return conn;
	}

	/**
	 * 是否为http客户端
	 *
	 * @return
	 */
	public boolean isHttp()
	{
		return http;
	}
}
