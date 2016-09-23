using System;
using LitJson;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using protocol;
//引用的
using System.Reflection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Threading;

namespace cocosocket4unity
{
	public class TestKcp : KcpClient
	{
    public TestKcp(int port): base(port)
    {
        
    }
        protected override void HandleReceive(ByteBuf bb)
        {
           string content= System.Text.Encoding.UTF8.GetString(bb.GetRaw());
           Console.WriteLine("msg:"+content);
        }
        /// <summary>
        /// 异常
        /// </summary>
        /// <param name="ex"></param>
               protected override void HandleException(Exception ex)
               {
                  this.Stop();
               }
        /// <summary>
        /// 超时
        /// </summary>
               protected override void HandleTimeout()
              {
                   this.Stop();
              }

		public static void Main(string[] args)
		{
			KcpClient client = new TestKcp(2223);
			client.NoDelay(1, 10, 2, 1);//fast
			client.WndSize(64, 64);
			client.Timeout(10*1000);
            client.SetMtu(1000);
            client.Connect("119.29.153.92", 2222);
			client.Start();
			Thread.Sleep(2000);
			String s = "hi,heoll world! 你好啊！！";
            //for (int i = 0; i < 2; i++)
            //{
            //    s = s + s;
            //}
			ByteBuf bb = new ByteBuf(System.Text.Encoding.UTF8.GetBytes(s));
			Console.WriteLine(bb.ReadableBytes());
			client.Send(bb);
			Console.Read();
		}

	}
}

