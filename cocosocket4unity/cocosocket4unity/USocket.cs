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
        private bool asyc;//异步收取
        private bool serverClose=true;//服务器主动关闭
		public const  int STATUS_INIT=0;
        public const int STATUS_CONNECTING = 1;
        public const int STATUS_CONNECTED = 2;
        public const int STATUS_CLOSED = 3;
		private ByteBuf buf;
		/**
		 * 构造（但不完善，需要设置监听器和协议解析器F）
		 */ 
		public USocket()
		{
			buf=new ByteBuf(4096);
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
		 * 协议
		 */ 
		public Protocal getProtocal()
		{
			return this.protocal;
		}
        public int getStatus()
        {
            return this.status;
        }
        public bool isAsyc()
        {
            return asyc;
        }
        public void setAsyc(bool a)
        {
            this.asyc = a;
        }
        public string getIp()
        {
            return this.ip;
        }
        public int getPort()
        {
            return this.port;
        }
		/**
		 * 连接指定地址
		 */
        public void Connect(string ip, int port)
        {
            this.status = STATUS_CONNECTING;
            this.ip = ip;
            this.port = port;
            clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            clientSocket.NoDelay = true;
            LingerOption linger = new LingerOption(false, 0);
            clientSocket.LingerState = linger;
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
                this.serverClose = false;
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
			Thread thread = new Thread(new ThreadStart(receive));
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
		private void receive()
		{
			while (this.status==STATUS_CONNECTED)
			{
				if(clientSocket.Poll(-1, SelectMode.SelectRead))
				{
                    try{
                    if (asyc)//异步收取
                    {
                          clientSocket.BeginReceive(buf.GetRaw(), 0, buf.GetRaw().Length, SocketFlags.None, new AsyncCallback(onRecieved), clientSocket);
                    }else //同步收取
                    {
                      int len= clientSocket.Receive(buf.GetRaw());
                      if (len > 0)
                      {
                          buf.ReaderIndex(0);
                          buf.WriterIndex(len);
                          while (true)
                          {
                              ByteBuf frame = this.protocal.TranslateFrame(buf);
                              if (frame != null)
                              {
                                  this.listner.OnMessage(this, frame);
                              }
                              else
                              {
                                  break;
                              }
                          }
                      }
                      else
                      {
                          this.status = STATUS_CLOSED;
                          this.listner.OnClose(this, serverClose);
                      }
                    }
                    }
                    catch (Exception e)
                    {
                        this.status = STATUS_CLOSED;
                        this.listner.OnError(this, e.Message);
                        break;
                    }
				}else
				{
					this.status = STATUS_CLOSED;
                    this.listner.OnClose(this, serverClose);
					break;
				}
			}
            this.listner.OnClose(this, serverClose);
		}	
		/**
		 * 异步收取信息
		 */ 
		private void onRecieved(IAsyncResult ar)
		{
			try
			{
			Socket so = (Socket)ar.AsyncState;
			int len = so.EndReceive(ar);
			if (len > 0) 
			{
				buf.ReaderIndex (0);
				buf.WriterIndex (len);
				while (true) 
				{
					ByteBuf frame = this.protocal.TranslateFrame (buf);
					if (frame != null)
					{
						this.listner.OnMessage (this, frame);
					} else 
					{
						break;
					}
				}
			} else 
			{
				this.status = STATUS_CLOSED;
                this.listner.OnClose(this, serverClose);
			}
			}catch(Exception e)
			{
				this.status = STATUS_CLOSED;
				this.listner.OnError(this,e.Message);
			}
		}
	}
}

