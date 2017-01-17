/**
 * socket服务器
 */
package org.ngame.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import javax.management.RuntimeErrorException;
import org.ngame.socket.protocol.NullProtocol;
import org.ngame.socket.protocol.Protocol;

/**
 *
 * @author beykery
 */
public abstract class NServer extends NListener
{

  /**
   * 协议
   */
  public static final int NETWORK_SOCKET = 0;
  public static final int NETWORK_WEBSOCKET = 1;
  public static final int NETWORK_HTTP = 2;
  public static final int NETWORK_LOCAL = 3;

  private static final InternalLogger LOG =InternalLoggerFactory.getInstance(NServer.class);
  protected static final int DEFAULT_PORT = 8888;
  protected int port = DEFAULT_PORT;
  protected InetSocketAddress address;
  protected Channel serverChannel;
  protected EventLoopGroup bossGroup;
  protected EventLoopGroup workerGroup;
  protected static int MAX_THREAD_SELECTOR = 2;
  protected static int MAX_THREAD_IO = Runtime.getRuntime().availableProcessors() * 2;
  protected Class<? extends Protocol> pClass = NullProtocol.class;
  protected static boolean epoll;
  protected final int network;
  protected static int maxCount;//最大消息数量
  protected static int interval;//秒

  static
  {
    try
    {
      MAX_THREAD_SELECTOR = Integer.parseInt(System.getProperty("game.socket.server.thread.selector"));
      MAX_THREAD_IO = Integer.parseInt(System.getProperty("game.socket.server.thread.io"));
      epoll = Epoll.isAvailable();
      maxCount = Integer.parseInt(System.getProperty("game.socket.server.busy.maxCount", "0"));
      interval = Integer.parseInt(System.getProperty("game.socket.server.busy.interval", "0"));
    } catch (Exception e)
    {
      LOG.warn("服务器配置信息错误 " + e.getMessage());
    }
  }

  /**
   * 服务器
   *
   * @param network
   * @throws UnknownHostException
   */
  public NServer(int network) throws UnknownHostException
  {
    this(null, network);
  }

  /**
   * 构造
   *
   * @param address
   * @param network
   */
  public NServer(InetSocketAddress address, int network)
  {
    super();
    this.network = network;
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
        LOG.warn("服务器端口配置错误，将使用默认端口");
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
  public void setProtocol(Class<? extends Protocol> pClass)
  {
    this.pClass = pClass;
  }

  /**
   * 查询分帧逻辑类
   *
   * @return
   */
  public Class<? extends Protocol> getProtocol()
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
  public void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws InterruptedException
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
    bossGroup = this.bossGroup == null ? (epoll ? new EpollEventLoopGroup(MAX_THREAD_SELECTOR) : new NioEventLoopGroup(MAX_THREAD_SELECTOR)) : bossGroup;
    workerGroup = this.workerGroup == null ? (epoll ? new EpollEventLoopGroup(MAX_THREAD_SELECTOR) : new NioEventLoopGroup(MAX_THREAD_IO)) : workerGroup;
    Class<? extends ServerChannel> c;
    if (network == NETWORK_LOCAL)
    {
      c = LocalServerChannel.class;
    } else
    {
      c = epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup, workerGroup).
            channel(c).
            option(ChannelOption.TCP_NODELAY, true).
            option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0).
            option(ChannelOption.SO_LINGER, 0).
            option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).
            childHandler(new ChannelInitializer<Channel>()
            {
              @Override
              public void initChannel(Channel ch) throws Exception
              {
                if (cur_connection.incrementAndGet() < max_connection)
                {
                  final ChannelPipeline pipeline = ch.pipeline();
                  ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
                  ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                  final Protocol p = pClass.newInstance();
                  final NClient s = new NClient(NServer.this, ch, p);
                  p.setContext(s);
                  switch (NServer.this.network)
                  {
                    case NETWORK_LOCAL:
                    case NETWORK_SOCKET:
                      break;
                    case NETWORK_WEBSOCKET:
                      pipeline.addLast(new HttpServerCodec());
                      pipeline.addLast(new HttpObjectAggregator(65536));
                      break;
                    case NETWORK_HTTP:
                      ch.pipeline().addLast(new HttpRequestDecoder());
                      ch.pipeline().addLast(new HttpResponseEncoder());
                      break;
                    default:
                      throw new RuntimeErrorException(null, "网络协议未被支持");
                  }
                  s.setNetwork(NServer.this.network);
                  ch.pipeline().addLast(s);
                  if (maxCount > 0 && interval > 0)
                  {
                    s.busy(maxCount, interval);
                  }
                } else
                {
                  ch.close();
                  cur_connection.decrementAndGet();
                }
              }
            });
    ChannelFuture f;
    if (this.network == NETWORK_LOCAL)
    {
      f = b.bind(new LocalAddress(String.valueOf(port))).sync();
    } else
    {
      f = b.bind(port).sync();
    }
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
 * epoll
 * @return 
 */
  public static boolean isEpoll()
  {
    return epoll;
  }

  
  /**
   * 停止服务器前需要做的事情
   */
  protected abstract void preStop();

}
