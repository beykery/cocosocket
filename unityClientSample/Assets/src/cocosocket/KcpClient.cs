using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using UnityEngine;
using System.IO;

namespace cocosocket4unity
{
    /// <summary>
    /// kcp客戶端程序
    /// </summary>
	public abstract class KcpClient : KcpOnUdp
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
					Thread.Sleep(0);
                   end = DateTime.Now;
               }
           }
       }
      /// <summary>
      /// 处理udp的消息
      /// </summary>
      /// <param name="bb"></param>
//       protected override void HandleReceive(ByteBuf bb) 
//       {
//			short cmd = bb.ReadShort();
//			Type protocolType = MessageQueueHandler.GetProtocolType(cmd);
//			if (protocolType == null) {
//				Debug.LogWarning(cmd + " - 本地找不到该ProtocolType！");
//				return;
//			}
//			byte[] bs = bb.GetRaw();
//			MemoryStream stream = new MemoryStream(bs, bb.ReaderIndex(), bb.ReadableBytes());
//			object obj = ProtoBuf.Serializer.NonGeneric.Deserialize(protocolType, stream);
//			FieldInfo success = obj.GetType().GetField("success");
//			if (success != null) { 
//				if ((bool)success.GetValue(obj) == true) {
//					MessageQueueHandler.PushQueue(cmd, obj);
//				} else {
//					FieldInfo info = obj.GetType().GetField("info");
//					if (info != null && info.GetValue(obj) != null) {
//						Debug.LogWarning("下行\t出错, cmd=" + cmd + ", type=" + MessageQueueHandler.GetProtocolType(cmd).ToString() + ", " + JsonManager.GetInstance().SerializeObjectDealVector(obj).Replace("\n", ""));
//						MessageQueueHandler.PushError(info.GetValue(obj).ToString());
//					}
//				}
//			}
//       }
       /// <summary>
       /// 异常
       /// </summary>
       /// <param name="ex"></param>
//       protected override void HandleException(Exception ex)
//       {
//			Debug.LogWarning("异常: " + ex);
//           this.Stop();
//       }
       /// <summary>
       /// 超时
       /// </summary>
//       protected override void HandleTimeout()
//       {
//			Debug.LogWarning("超时: ");
//           this.Stop();
//       }
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
    }
}
