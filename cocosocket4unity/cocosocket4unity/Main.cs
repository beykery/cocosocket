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
using System.IO;
namespace cocosocket4unity
{
	class MainClass
	{
		public static void Main(string[] args)
		{

            FightFBResultRequest request = new FightFBResultRequest();
            request.starList = new List<int>();
            request.starList.Add(1);
            MemoryStream stream = new MemoryStream();
            ProtoBuf.Serializer.NonGeneric.Serialize(stream, request);
            byte[] bs = stream.ToArray();
            try{
                FileStream fsWrite = new FileStream("./request.data", FileMode.Create);
                fsWrite.Write(bs, 0, bs.Length);
            }catch(Exception ex)
            {
                Console.WriteLine("error.");
            }
           
            
            /**
            AuthRequest ar = new AuthRequest();
            string json = JsonMapper.ToJson(ar);
            Console.WriteLine(ar.GetType().Name+":"+json);
            List<Type> ls = ClassUtil.GetClasses("protocol");
            foreach (Type item in ls)
            {
              Console.WriteLine(item.Name);
              ConstructorInfo constructor = item.GetConstructor(new Type[0]);
              //使用构造器对象来创建对象
              object obj = constructor.Invoke(new Object[0]);

              ProtoAttribute arr=  (ProtoAttribute)ClassUtil.GetAttribute(item,typeof(ProtoAttribute));
                if(arr!=null)
              Console.WriteLine(arr.value);
            }
            Console.Read();
             */ 
            
             /**
            long time_JAVA_Long = 1446050129676L;//java长整型日期，毫秒为单位             
            DateTime dt_1970 = new DateTime(1970, 1, 1, 0, 0, 0);              
            long tricks_1970 = dt_1970.Ticks;//1970年1月1                 
            long time_tricks = tricks_1970 + time_JAVA_Long * 10000;//日志日期刻度    
            DateTime dt = new DateTime(time_tricks).AddHours(8);//转化为DateTime
            Console.WriteLine(string.Format("{0:G}", dt));
            Console.Read();
            */
            /**
			SocketListner listner = new TestListner ();
			USocket us = new USocket ();
			us.setLister (listner);
			Protocal p = new Varint32HeaderProtocol ();
            //Protocal p = new LVProtocal();
			us.setProtocal (p);
			us.Connect ("localhost", 4887);
			Console.Read();
             */
		}
	}
}
