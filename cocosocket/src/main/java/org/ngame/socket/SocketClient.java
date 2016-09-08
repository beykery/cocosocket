package org.ngame.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.nio.channels.NotYetConnectedException;
import org.ngame.socket.framing.Framedata;
import org.ngame.socket.protocol.Protocol;

/**
 * socket客户端(不再支持http和websocket客户端，因为不再需要他们)
 *
 * @author beykery
 */
public abstract class SocketClient extends NListener
{

    private static final InternalLogger LOG =InternalLoggerFactory.getInstance(SocketClient.class);
    private InetSocketAddress address;
    private NClient conn;
    private Protocol protocol;
    private EventLoopGroup group;
    protected static boolean epoll;

    static
    {
        epoll = Epoll.isAvailable();
    }

    /**
     *
     * @param address
     * @param protocol
     * @param group
     */
    public SocketClient(InetSocketAddress address, Protocol protocol, EventLoopGroup group)
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
 * epoll
 * @return 
 */
  public static boolean isEpoll()
  {
    return epoll;
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
        c = epoll ? EpollSocketChannel.class : NioSocketChannel.class;
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
                        NClient socket = new NClient(SocketClient.this, ch, protocol);
                        SocketClient.this.conn = socket;
                        socket.setClient(true);
                        ch.pipeline().addLast(socket);
                    }
            });
        return b.connect(remote);
    }

    public NClient connection()
    {
        return conn;
    }
}
