using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace cocosocket4unity
{
	public class USocket
	{
		private Socket clientSocket;
		private SocketListner listner;
		private Protocal protocal;
		private string ip;
		private int port;
		private int status;
		public static int STATUS_INIT=0;
		public static int STATUS_CONNECTING=1;
		public static int STATUS_CONNECTED=2;
		public static int STATUS_CLOSED=3;
		/**
		 * 构造（但不完善，需要设置监听器和协议解析器F）
		 */ 
		public USocket()
		{
		}
		/**
		 * 构造
		 */ 
		public USocket (SocketListner listner,Protocal protocal)
		{
			this.listner = listner;
			this.protocal = protocal;
		}

		/**
		 * 装入一个监听器F
		 */ 
		public void setLister (SocketListner listner)
		{
			this.listner = listner;
		}
		/**
		 * 装入一个协议解析器
		 */ 
		public void setProtocal(Protocal p)
		{
			this.protocal = p;
		}
		/**
		 * 连接指定地址
		 */ 
		public void Connect(string ip,int port)
		{
			this.status = STATUS_CONNECTING;
			this.ip = ip;
			this.port = port;
			clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
			clientSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.KeepAlive, true);
			clientSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.NoDelay, true);
			clientSocket.BeginConnect(this.ip, this.port, connected, this);
		}

		/**
		 * 关闭连接
		 */ 
		public void Close()
		{
			if(clientSocket != null && clientSocket.Connected)
			{
				clientSocket.Shutdown(SocketShutdown.Both);
				clientSocket.Close();
				this.status = STATUS_CLOSED;
			}
		}
		/**
		 * 连接成功
		 */ 
		private void connected(IAsyncResult asyncConnect)
		{
			if (this.clientSocket.Connected) 
			{
			this.clientSocket.EndConnect(asyncConnect);
			this.status = STATUS_CONNECTED;
			this.listner.OnOpen (this);
			Thread thread = new Thread(new ThreadStart(received));
			thread.IsBackground = true;
			thread.Start();
			}
		}
		/**
		 *发送
		 */
		public IAsyncResult Send(Frame frame)
		{
			return this.Send (frame.GetData ());
		}
		/**
		 *发送
		 */
		public IAsyncResult Send(ByteBuf buf)
		{
			try
			{
				byte[] msg=buf.GetRaw();
				IAsyncResult asyncSend = clientSocket.BeginSend (msg,buf.ReaderIndex(),buf.ReadableBytes(),SocketFlags.None,sended,buf);
				return asyncSend;
			}
			catch
			{
				return null;
			}
		}
		/**
		 * 发送成功的回调
		 */ 
		private void sended (IAsyncResult ar)
		{
			ByteBuf bb = (ByteBuf)ar.AsyncState;
			bb.ReaderIndex (bb.WriterIndex ());
			this.clientSocket.EndSend(ar);
		}
		/**
		 * 接收数据
		 */ 
		private void received()
		{
			ByteBuf buf=new ByteBuf(4096);
			while (true)
			{
				if(!clientSocket.Connected)
				{
					this.status = STATUS_CLOSED;
					this.listner.OnClose (this,true);
					break;
				}
				try
				{
					int size = clientSocket.Receive(buf.GetRaw());
					if(size <= 0)
					{
						this.status = STATUS_CLOSED;
						clientSocket.Close();
						this.listner.OnClose (this,true);
						break;
					}
					buf.ReaderIndex(0);
					buf.WriterIndex(size);
					while (true)
					{
						ByteBuf frame = this.protocal.TranslateFrame(buf);
						if (frame != null)
						{
							this.listner.OnMessage(this, frame);
						} else
						{
							break;
						}
					}
				}
				catch (Exception e)
				{
					this.status = STATUS_CLOSED;
					this.listner.OnError(this,e.Message);
					break;
				}
			}
		}	
	}
}

