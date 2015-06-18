/**
 * 进程内的本地server
 */
package org.ngame.socket;

import java.net.InetSocketAddress;

/**
 *
 * @author beykery
 */
public abstract class LocalServer extends NServer
{

	public LocalServer(InetSocketAddress addr)
	{
		super(addr, NETWORK_LOCAL);
	}
}
