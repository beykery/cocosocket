package org.ngame.socket;

import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据来往监听器
 *
 * @author beykery
 */
public abstract class NListener
{

  private static final InternalLogger LOG =InternalLoggerFactory.getInstance(NListener.class);
  int max_connection = Integer.MAX_VALUE;
  AtomicInteger cur_connection = new AtomicInteger(0);

  public abstract void onOpen(NClient conn);

  public abstract void onClose(NClient conn, boolean local);

  public abstract void onMessage(NClient conn, Object message);

  public abstract void onError(NClient conn, Throwable ex);

  public abstract void onIdle(NClient conn, IdleStateEvent event);

  public abstract void onBusy(NClient conn);

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
      LOG.warn("配置最大连接数失败");
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
