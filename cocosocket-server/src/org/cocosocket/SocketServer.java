/**
 * socket服务器
 */
package org.cocosocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cocosocket.protocal.NullProtocal;
import org.cocosocket.protocal.Protocal;

/**
 *
 * @author beykery
 */
public abstract class SocketServer extends SocketListener
{

    private static final Logger LOG = Logger.getLogger(SocketServer.class.getName());
    protected static final int DEFAULT_PORT = 8888;
    protected int port = DEFAULT_PORT;
    protected InetSocketAddress address;
    protected Channel serverChannel;
    protected NioEventLoopGroup bossGroup;
    protected NioEventLoopGroup workerGroup;
    protected static int MAX_THREAD_SELECTOR = 2;
    protected static int MAX_THREAD_IO = Runtime.getRuntime().availableProcessors() * 2;
    protected Class<? extends Protocal> pClass = NullProtocal.class;

    static
    {
        try
        {
            MAX_THREAD_SELECTOR = Integer.parseInt(System.getProperty("game.socket.server.thread.selector"));
            MAX_THREAD_IO = Integer.parseInt(System.getProperty("game.socket.server.thread.io"));
        } catch (Exception e)
        {
            LOG.log(Level.WARNING, "服务器配置信息错误 " + e.getMessage());
        }
    }

    public SocketServer() throws UnknownHostException
    {
        this(null);
    }

    /**
     * 构造
     *
     * @param address
     */
    public SocketServer(InetSocketAddress address)
    {
        super();
        if (address == null)
        {
            /**
             * 端口号
             */
            try
            {
                String sport = System.getProperty("game.server.port");
                port = Integer.parseInt(sport);
            } catch (Exception e)
            {
                LOG.log(Level.WARNING, "服务器端口配置错误，将使用默认端口");
            }
            address = new InetSocketAddress(port);
        } else
        {
            port = address.getPort();
        }
        this.address = address;
    }

    /**
     * 服务器地址
     *
     * @return
     */
    public InetSocketAddress getAddress()
    {
        return address;
    }

    /**
     * 设置分帧处理器
     *
     * @param pClass
     */
    public void setProtocal(Class<? extends Protocal> pClass)
    {
        this.pClass = pClass;
    }

    /**
     * 查询分帧逻辑类
     *
     * @return
     */
    public Class<? extends Protocal> getProtocal()
    {
        return pClass;
    }

    /**
     * 启动服务器
     *
     * @param bossGroup
     * @param workerGroup
     * @throws InterruptedException
     */
    public void start(NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) throws InterruptedException
    {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.start();
    }

    /**
     * 启动服务器
     *
     * @throws InterruptedException
     */
    public void start() throws InterruptedException
    {
        bossGroup = this.bossGroup == null ? new NioEventLoopGroup(MAX_THREAD_SELECTOR) : bossGroup;
        workerGroup = this.workerGroup == null ? new NioEventLoopGroup(MAX_THREAD_IO) : workerGroup;
        final Class<NioServerSocketChannel> c = NioServerSocketChannel.class;
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).
            channel(c).
            option(ChannelOption.TCP_NODELAY, true).
            option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0).
            option(ChannelOption.SO_LINGER, 0).
            option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).
            childHandler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception
                    {
                        if (cur_connection.incrementAndGet() < max_connection)
                        {
                            ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
                            final Protocal p = pClass.newInstance();
                            final CocoSocket s = new CocoSocket(SocketServer.this, ch, p);
                            p.setContext(s);
                            ch.pipeline().addLast(s);
                        } else
                        {
                            ch.close();
                            cur_connection.decrementAndGet();
                        }
                    }
            });
        final ChannelFuture f = b.bind(port).sync();
        this.serverChannel = f.channel();
    }

    /**
     * 停止服务器
     *
     * @return
     */
    public ChannelFuture stop()
    {
        this.preStop();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        return this.serverChannel.close();
    }

    /**
     * 连接数
     *
     * @return
     */
    public int connections()
    {
        return cur_connection.get();
    }

    /**
     * 停止服务器前需要做的事情
     */
    protected abstract void preStop();

}