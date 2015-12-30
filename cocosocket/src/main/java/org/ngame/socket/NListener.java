package org.ngame.socket;

import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数据来往监听器
 *
 * @author beykery
 */
public abstract class NListener
{

	private static final Logger LOG = Logger.getLogger(NListener.class.getName());
	int max_connection = Integer.MAX_VALUE;
	AtomicInteger cur_connection = new AtomicInteger(0);

	public abstract void onOpen(NClient conn);

	public abstract void onClose(NClient conn, boolean local);

	public abstract void onMessage(NClient conn, Object message);

	public abstract void onError(NClient conn, Throwable ex);

	public abstract void onIdle(NClient conn, IdleStateEvent event);

	/**
	 * 初始化
	 */
	public NListener()
	{
		try
		{
			max_connection = Integer.parseInt(System.getProperty("game.connections"));
		} catch (Exception e)
		{
			LOG.log(Level.WARNING, "配置最大连接数失败");
		}
	}

	/**
	 * 连接关闭
	 *
	 * @param aThis
	 * @param closeReason
	 */
	void socketClosed(NClient so, boolean closeReason)
	{
		cur_connection.decrementAndGet();
		this.onClose(so, closeReason);
	}
}
