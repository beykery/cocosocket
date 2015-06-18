package org.ngame.socket.test;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object>
{

	private final WebSocketClientHandshaker handshaker;
	private ChannelPromise handshakeFuture;

	public WebSocketClientHandler(WebSocketClientHandshaker handshaker)
	{
		this.handshaker = handshaker;
	}

	public ChannelFuture handshakeFuture()
	{
		return handshakeFuture;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx)
	{
		handshakeFuture = ctx.newPromise();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		handshaker.handshake(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx)
	{
		System.out.println("WebSocket Client disconnected!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		if (!handshakeFuture.isDone())
		{
			handshakeFuture.setFailure(cause);
		}
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		Channel ch = ctx.channel();
		if (!handshaker.isHandshakeComplete())
		{
			handshaker.finishHandshake(ch, (FullHttpResponse) msg);
			System.out.println("WebSocket Client connected!");
			handshakeFuture.setSuccess();
			return;
		}
		WebSocketFrame frame = (WebSocketFrame) msg;
		if (frame instanceof TextWebSocketFrame)
		{
			TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
			System.out.println("WebSocket Client received message: " + textFrame.text());
		} else if (frame instanceof PongWebSocketFrame)
		{
			System.out.println("WebSocket Client received pong");
		} else if (frame instanceof CloseWebSocketFrame)
		{
			System.out.println("WebSocket Client received closing");
			ch.close();
		}
	}
}
