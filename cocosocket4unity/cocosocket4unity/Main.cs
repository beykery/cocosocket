using System;
using LitJson;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using protocol;
using System.Reflection;
using System.IO;

namespace cocosocket4unity
{
	class MainClass
	{
		public static void Main0 (string[] args)
		{
            TestRequest request = new TestRequest();
            request.c = TestEnum.WHITE;
            MemoryStream stream = new MemoryStream();
            ProtoBuf.Serializer.NonGeneric.Serialize(stream, request);
            byte[] bs = stream.ToArray();
            Type type = typeof(TestRequest);
            Stream s = stream;
            request=  (TestRequest)ProtoBuf.Serializer.NonGeneric.Deserialize(type, s);
            Console.WriteLine(request);
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
			SocketListener listener = new TestListener ();
			USocket us = new USocket ();
			us.setListener (listener);
			Protocol p = new Varint32HeaderProtocol ();
            //Protocol p = new LVProtocol();
			us.setProtocol (p);
			us.Connect ("localhost", 4887);
			Console.Read();
             */

        }
    }
}
