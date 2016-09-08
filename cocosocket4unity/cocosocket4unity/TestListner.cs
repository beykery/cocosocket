using LitJson;
using ProtoBuf;
using ProtoBuf.Serializers;
using System.Collections.Generic;
using System;
using System.IO;
using protocol;
namespace cocosocket4unity
{
	public class TestListner : SocketListner
	{
		public TestListner ()
		{
		}

		public override  void OnMessage(USocket us,ByteBuf bb)
		{
           
			Console.WriteLine ("收到数据:");
			bb.ReaderIndex (us.getProtocal().HeaderLen());

            int cmd = bb.ReadShort();
            Type t=null;
            MemoryStream stream = new MemoryStream(bb.GetRaw(),bb.ReaderIndex(),bb.ReadableBytes());
            object response=ProtoBuf.Serializer.NonGeneric.Deserialize(t,stream);
            
            /**
			Console.WriteLine (response.pid);
            Console.WriteLine(response.info);
            Console.WriteLine(response.success);
            */
		}
		/**
		 * 
		 */ 
		public override  void OnClose(USocket us,bool fromRemote)
		{
			Console.WriteLine ("连接被关闭："+fromRemote);
		}
		public override  void OnIdle(USocket us)
		{
			Console.WriteLine ("连接超时：");
		}
		public override  void OnOpen(USocket us)
		{
          
			Console.WriteLine ("连接建立");
            AuthRequest request = new AuthRequest();
            request.loginid = "lkjlkj;sdf你好";
            request.serverid = 1;
            MemoryStream  stream = new MemoryStream();
            ProtoBuf.Serializer.Serialize<AuthRequest>(stream, request);

            Varint32Frame f = new Varint32Frame(512);
            f.PutShort(6);
            f.PutBytes(stream.ToArray());
            f.End();
			us.Send (f);
          
		}
		public override  void OnError(USocket us,string err)
		{
			Console.WriteLine ("错误："+err);
		}
	}
}

