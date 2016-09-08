using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace cocosocket4unity
{
    /// <summary>
    /// kcp客戶端程序
    /// </summary>
   public class KcpClient : KcpOnUdp
    {
       private LinkedList<ByteBuf> sendList;
       protected volatile bool running;
      /// <summary>
      /// 初始化kcp
      /// </summary>
      /// <param name="port">监听端口</param>
       public KcpClient(int port):base(port)
       {
           this.sendList = new LinkedList<ByteBuf>();
       }
       /// <summary>
       /// 是否在运行状态
       /// </summary>
       /// <returns></returns>
       public bool IsRunning()
       {
           return this.running;
       }
     /// <summary>
     /// 停止udp
     /// </summary>
       public void Stop()
       {
           if(running)
           { 
           running = false;
           try 
           {
            this.client.Close(); 
           }catch (Exception ex)
           {
           }
           }
       }
       /// <summary>
       /// 开启线程开始工作
       /// </summary>
       public void Start()
       {
           if (!this.running)
           {
               this.running = true;
               Thread t = new Thread(new ThreadStart(run));//启动发送线程，同步发送
               t.IsBackground = true;
               t.Start();
           }
       }
       private void run()
       {
           while (running)
           {
               DateTime st = DateTime.Now;
               this.Update(); 
               lock (this.sendList)
               {
                   while(this.sendList.Count>0)
                   {
                       ByteBuf bb=this.sendList.First.Value;
                       sendList.RemoveFirst();
                       this.kcp.Send(bb);
                   }
               }
               if (this.needUpdate)
               {
                   continue;
               }
               DateTime end = DateTime.Now;
               while ((end - st).TotalMilliseconds < 10)
               {
                   if (this.needUpdate) 
                   {
                       break;                         
                   }
                   Thread.Yield();
                   end = DateTime.Now;
               }
           }
       }
      /// <summary>
      /// 处理udp的消息
      /// </summary>
      /// <param name="bb"></param>
       protected override void HandleReceive(ByteBuf bb) 
       {
          string s = System.Text.Encoding.UTF8.GetString(bb.GetRaw(),0,bb.ReadableBytes());
          Console.WriteLine("收到消息: "+s);
          //this.Send(bb);//回送
       }
       /// <summary>
       /// 异常
       /// </summary>
       /// <param name="ex"></param>
       protected override void HandleException(Exception ex)
       {
           Console.WriteLine("异常: " + ex);
           this.Stop();
       }
       /// <summary>
       /// 超时
       /// </summary>
       protected override void HandleTimeout()
       {
           Console.WriteLine("超时: ");
           this.Stop();
       }
       public void Send(ByteBuf content)
       {
           lock (this.sendList)
           {
               this.sendList.AddLast(content);
               this.needUpdate = true;
           }
       }
       /// <summary>
       /// 測試
       /// </summary>
       /// <param name="args"></param>
       public static void Main(string[] args)
       {
           KcpClient client = new KcpClient(2223);
           client.NoDelay(1, 10, 2, 1);//fast
           client.WndSize(64, 64);
           client.Timeout(10*1000);
           client.Connect("10.18.121.15",2222);
           client.Start();
           Thread.Sleep(2000);
           String s = "hi,heoll world! 你好啊！！";
           for (int i = 0; i < 2; i++)
           {
               s = s + s;
           }
           ByteBuf bb = new ByteBuf(System.Text.Encoding.UTF8.GetBytes(s));
           Console.WriteLine(bb.ReadableBytes());
           client.Send(bb);
           Console.Read();
       }
    }
}
