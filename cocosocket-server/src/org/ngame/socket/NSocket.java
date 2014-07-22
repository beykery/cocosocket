/**
 * nsocket，基于netty实现
 */
package org.ngame.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.ngame.socket.framing.Framedata;
import org.ngame.socket.protocol.Protocol;

/**
 *
 * @author beykery
 */
public final class NSocket extends ChannelInboundHandlerAdapter
{

	private static final Logger LOG = Logger.getLogger(NSocket.class.getName());
	private SocketListener listener;
	private final SocketChannel channel;
	private boolean closeReason;//是否为服务器主动关闭
	private final Protocol protocol;
	private long sessionId;
	private Map<String, Object> sessions;
	private ChannelHandlerContext context;
	private String closeReasonString;//断开原因描述
	private boolean http;//通信协议是否为http
	private boolean keepAlive;//是否keepalive
	private boolean client;//是否为http客户端
	private String uri;//服务器地址

	/**
	 * 连接
	 *
	 * @param l
	 * @param ch
	 * @param protocol
	 */
	public NSocket(SocketListener l, SocketChannel ch, Protocol protocol)
	{
		channel = ch;
		this.listener = l;
		this.protocol = protocol;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		ByteBuf bb = null;
		if (http)//如果是http协议，则取出httprequest并分析
		{
//			if (msg instanceof DefaultHttpRequest)
//			{
//               System.out.println("binnnn");
//			} else 
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
		} else
		{
			bb = (ByteBuf) msg;
		}
		if (bb != null)
		{
			try
			{
				ByteBuf fd = protocol.translateFrame(bb);
				while (fd != null)
				{
					deliverMessage(fd);
					fd = protocol.translateFrame(bb);
				}
			} finally
			{
				bb.release();
			}
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
		this.listener.onOpen(this);
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
	public ChannelFuture sendFrame(ByteBuf data)
	{
		if (http)//如果是http协议，则发送response
		{
			if (!client)//服务器回送
			{
				HttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, data);
				res.headers().set(CONTENT_LENGTH, data.readableBytes());
				return this.context.writeAndFlush(res);
			} else//客户端的request请求
			{
				DefaultFullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, uri, data);
				request.headers().set(CONTENT_LENGTH, data.readableBytes());
				return this.channel.writeAndFlush(request);
			}
		} else
		{
			return this.context.writeAndFlush(data);
		}
	}

	/**
	 * 发送一帧
	 *
	 * @param data
	 * @return
	 */
	public ChannelFuture sendFrame(Framedata data)
	{
		return this.sendFrame(data.getData());
	}

	private void deliverMessage(ByteBuf fd)
	{
		this.listener.onMessage(this, fd);
	}

	public boolean isOpen()
	{
		return this.channel.isActive();
	}

	public long getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(long sessionId)
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
		InetSocketAddress local = channel.localAddress();
		InetSocketAddress remote = channel.remoteAddress();
		return local.getAddress().getHostAddress() + ":" + local.getPort() + "=>" + remote.getAddress().getHostAddress() + ":" + remote.getPort();
	}

	/**
	 * 远程地址
	 *
	 * @return
	 */
	public InetSocketAddress remoteAddress()
	{
		return channel.remoteAddress();
	}

	/**
	 * 本地地址
	 *
	 * @return
	 */
	public InetSocketAddress localAddress()
	{
		return channel.localAddress();
	}

	public void setListener(SocketListener listener)
	{
		this.listener = listener;
	}

	public SocketListener getListener()
	{
		return listener;
	}

	/**
	 * 获取channel
	 *
	 * @return
	 */
	public SocketChannel channel()
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
	 * 是否为http协议
	 *
	 * @param http
	 */
	public void setHttp(boolean http)
	{
		this.http = http;
	}

	/**
	 * 是否为http协议
	 *
	 * @return
	 */
	public boolean isHttp()
	{
		return http;
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

}
