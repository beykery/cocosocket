/**
 * nsocket，基于netty实现
 */
package org.ngame.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.ngame.socket.framing.Framedata;
import org.ngame.socket.protocol.Protocol;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import java.net.SocketAddress;
import java.net.URI;
import org.ngame.socket.exeptions.InvalidDataException;

/**
 * @author beykery
 */
public final class NClient extends ChannelInboundHandlerAdapter
{

  private static final Logger LOG = Logger.getLogger(NClient.class.getName());
  private NListener listener;
  private final Channel channel;
  private boolean closeReason;//是否为服务器主动关闭
  private final Protocol protocol;
  private String sessionId;
  private Map<String, Object> sessions;
  private ChannelHandlerContext context;
  private String closeReasonString;//断开原因描述
  private boolean keepAlive;//是否keepalive
  private boolean client;//是否为http客户端
  private String uri;//服务器地址
  private int network;//http,websocket,socket,local
  private WebSocketServerHandshaker handshaker;
  private WebSocketClientHandshaker clientHandshaker;

  /**
   * 连接
   *
   * @param l
   * @param ch
   * @param protocol
   */
  public NClient(NListener l, Channel ch, Protocol protocol)
  {
    channel = ch;
    this.listener = l;
    this.protocol = protocol;
  }

  public Protocol getProtocol()
  {
    return protocol;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
  {
    ByteBuf bb = null;
    switch (this.network)
    {
      case NServer.NETWORK_LOCAL:
        this.listener.onMessage(this, msg);
        break;
      case NServer.NETWORK_SOCKET:
        bb = (ByteBuf) msg;
        this.cutFrame(bb);
        break;
      case NServer.NETWORK_WEBSOCKET:
        if (this.clientHandshaker != null)//客户端
        {
          if (!clientHandshaker.isHandshakeComplete())
          {
            clientHandshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
            this.listener.onOpen(this);
          }
          WebSocketFrame frame = (WebSocketFrame) msg;
          if (frame instanceof TextWebSocketFrame)
          {
            this.listener.onMessage(this, msg);
          } else if (frame instanceof CloseWebSocketFrame)
          {
            ctx.channel().close();
          }
        } else//服务器
        {
          if (msg instanceof FullHttpRequest)
          {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
          } else if (msg instanceof WebSocketFrame)
          {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
          }
        }
        break;
      case NServer.NETWORK_HTTP:
        if (msg instanceof DefaultFullHttpRequest)//server
        {
          DefaultFullHttpRequest req = (DefaultFullHttpRequest) msg;
          this.keepAlive = isKeepAlive(req);
          bb = req.content();
        } else if (msg instanceof HttpContent)//client
        {
          HttpContent response = (HttpContent) msg;
          bb = response.content();
        }
        if (bb != null)
        {
          cutFrame(bb);
        }
        break;
      default:
        throw new RuntimeException("错误的网络协议");
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
  {
    this.listener.onError(this, cause);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception
  {
    this.context = ctx;
    if (this.network == NServer.NETWORK_SOCKET || this.network == NServer.NETWORK_HTTP || this.network == NServer.NETWORK_LOCAL)
    {
      this.listener.onOpen(this);
    } else if (network == NServer.NETWORK_WEBSOCKET)
    {
      if (this.client)//客户端
      {
        this.clientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(new URI(this.uri), WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
        clientHandshaker.handshake(ctx.channel());
      }
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    this.listener.socketClosed(this, closeReason);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
  {
    if (evt instanceof IdleStateEvent)
    {
      IdleStateEvent event = (IdleStateEvent) evt;
      this.listener.onIdle(this, event);
    }
  }

  /**
   * 断开连接
   *
   * @return
   */
  public ChannelFuture close()
  {
    this.closeReason = true;
    return this.channel.close();
  }

  /**
   * 断开连接
   *
   * @param reason
   * @return
   */
  public ChannelFuture close(String reason)
  {
    this.closeReasonString = reason;
    this.closeReason = true;
    return this.channel.close();
  }

  /**
   * 发送一帧（完整的一帧）
   *
   * @param data
   * @return
   */
  public ChannelFuture sendFrame(Object data)
  {
    switch (this.network)
    {
      case NServer.NETWORK_LOCAL:
        return this.context.writeAndFlush(data);
      case NServer.NETWORK_SOCKET:
        if (data instanceof ByteBuf)
        {
          return this.context.writeAndFlush(data);
        } else if (data instanceof Framedata)
        {
          return this.context.writeAndFlush(((Framedata) data).getData());
        } else
        {
          throw new RuntimeException("无法发送此类型");
        }
      case NServer.NETWORK_WEBSOCKET:
        return this.context.writeAndFlush(data);
      case NServer.NETWORK_HTTP:
        ByteBuf bb = (ByteBuf) data;
        if (!client)//服务器回送
        {
          HttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, bb);
          res.headers().set(CONTENT_LENGTH, bb.readableBytes());
          return this.context.writeAndFlush(res);
        } else//客户端的request请求
        {
          DefaultFullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, uri, bb);
          request.headers().set(CONTENT_LENGTH, bb.readableBytes());
          return this.channel.writeAndFlush(request);
        }
      default:
        throw new RuntimeException("不支持的协议");
    }
  }

  /**
   * 是否联通
   *
   * @return
   */
  public boolean isOpen()
  {
    return this.channel.isActive();
  }

  /**
   * sessionid
   *
   * @return
   */
  public String getSessionId()
  {
    return sessionId;
  }

  public void setSessionId(String sessionId)
  {
    this.sessionId = sessionId;
  }

  /**
   * 获取session值
   *
   * @param key
   * @return
   */
  public Object getSession(String key)
  {
    return sessions == null ? null : sessions.get(key);
  }

  /**
   * 获取session值
   *
   * @param key
   * @param def
   * @return
   */
  public Object getSession(String key, Object def)
  {
    Object r = sessions == null ? null : sessions.get(key);
    return r == null ? def : r;
  }

  /**
   * 删除
   *
   * @param key
   * @return
   */
  public Object removeSession(String key)
  {
    return sessions == null ? null : sessions.remove(key);
  }

  /**
   * 清空session数据
   */
  public void clearSession()
  {
    sessions = null;
  }

  /**
   * 设置session值
   *
   * @param key
   * @param value
   */
  public void setSession(String key, Object value)
  {
    if (sessions == null)
    {
      sessions = new HashMap<>();
    }
    sessions.put(key, value);
  }

  /**
   * 获取所有的session键
   *
   * @return
   */
  public Set<String> getSessionKeys()
  {
    return sessions == null ? null : sessions.keySet();
  }

  @Override
  public String toString()
  {
    SocketAddress local = channel.localAddress();
    SocketAddress remote = channel.remoteAddress();
    return remote + "=>" + local;
  }

  /**
   * 远程地址
   *
   * @return
   */
  public SocketAddress remoteAddress()
  {
    return channel.remoteAddress();
  }

  /**
   * 本地地址
   *
   * @return
   */
  public SocketAddress localAddress()
  {
    return channel.localAddress();
  }

  public void setListener(NListener listener)
  {
    this.listener = listener;
  }

  public NListener getListener()
  {
    return listener;
  }

  /**
   * 获取channel
   *
   * @return
   */
  public Channel channel()
  {
    return channel;
  }

  /**
   * 断开原因
   *
   * @return
   */
  public String getCloseReason()
  {
    return closeReasonString;
  }

  /**
   * 设置idle
   *
   * @param readerIdleTime
   * @param writerIdleTime
   * @param allIdleTime
   * @param unit
   */
  public void idle(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit)
  {
    ChannelHandler ch = channel.pipeline().first();
    if (ch instanceof IdleStateHandler)
    {
      channel.pipeline().removeFirst();
    }
    channel.pipeline().addFirst(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, unit));
  }

  /**
   * 计算读idle
   *
   * @return
   */
  public long readerIdle()
  {
    ChannelHandler ch = channel.pipeline().first();
    if (ch instanceof IdleStateHandler)
    {
      IdleStateHandler ish = (IdleStateHandler) ch;
      return ish.getReaderIdleTimeInMillis();
    }
    return 0;
  }

  /**
   * 计算写idle
   *
   * @return
   */
  public long writerIdle()
  {
    ChannelHandler ch = channel.pipeline().first();
    if (ch instanceof IdleStateHandler)
    {
      IdleStateHandler ish = (IdleStateHandler) ch;
      return ish.getWriterIdleTimeInMillis();
    }
    return 0;
  }

  /**
   * 计算all idle
   *
   * @return
   */
  public long allIdle()
  {
    ChannelHandler ch = channel.pipeline().first();
    if (ch instanceof IdleStateHandler)
    {
      IdleStateHandler ish = (IdleStateHandler) ch;
      return ish.getAllIdleTimeInMillis();
    }
    return 0;
  }

  /**
   * 是否为client端
   *
   * @param client
   */
  public void setClient(boolean client)
  {
    this.client = client;
  }

  /**
   * 是否为客户端
   *
   * @return
   */
  public boolean isClient()
  {
    return client;
  }

  /**
   * uri
   *
   * @param uri
   */
  public void setURI(String uri)
  {
    this.uri = uri;
  }

  /**
   * 网络
   *
   * @param network
   */
  public void setNetwork(int network)
  {
    this.network = network;
  }

  /**
   * 获取网络协议
   *
   * @return
   */
  public int getNetwork()
  {
    return network;
  }

  /**
   * http请求
   *
   * @param ctx
   * @param fullHttpRequest
   */
  private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
  {
    if (!req.getDecoderResult().isSuccess())
    {
      sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
      return;
    }
    if (req.getMethod() != GET)
    {
      sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
      return;
    }
    WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
    handshaker = wsFactory.newHandshaker(req);
    if (handshaker == null)
    {
      //WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
    } else
    {
      handshaker.handshake(ctx.channel(), req);
      this.listener.onOpen(this);
    }
  }

  /**
   * 发送http响应
   *
   * @param ctx
   * @param req
   * @param res
   */
  private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res)
  {
    if (res.getStatus().code() != 200)
    {
      res.content().writeBytes(res.getStatus().toString().getBytes());
      HttpHeaders.setContentLength(res, res.content().readableBytes());
    }
    ChannelFuture f = ctx.channel().writeAndFlush(res);
    if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200)
    {
      f.addListener(ChannelFutureListener.CLOSE);
    }
  }

  private static String getWebSocketLocation(FullHttpRequest req)
  {
    String location = req.headers().get(HOST);
    return "ws://" + location;
  }

  /**
   * websocket帧
   *
   * @param ctx
   * @param webSocketFrame
   */
  private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame)
  {
    if (frame instanceof CloseWebSocketFrame)
    {
      handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
      return;
    }
    if (frame instanceof PingWebSocketFrame)
    {
      ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
      return;
    }
    if (!(frame instanceof TextWebSocketFrame))//只支持text frame，二进制不再支持
    {
      throw new UnsupportedOperationException(String.format("%s 不支持", frame.getClass().getName()));
    }
    this.listener.onMessage(this, frame);
  }

  /**
   * 分帧
   *
   * @param bb
   */
  private void cutFrame(ByteBuf bb) throws Exception
  {
    try
    {
      ByteBuf fd = protocol.translateFrame(bb);
      while (fd != null)
      {
        this.listener.onMessage(this, fd);
        fd = protocol.translateFrame(bb);
      }
    } finally
    {
      bb.release();
    }
  }
}
