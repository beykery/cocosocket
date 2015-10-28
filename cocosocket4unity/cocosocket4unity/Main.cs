using System;
using LitJson;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using protocol;
namespace cocosocket4unity
{
	class MainClass
	{
		public static void Main (string[] args)
		{
            AuthRequest ar = new AuthRequest();
            string json = JsonMapper.ToJson(ar);
            Console.WriteLine(ar.GetType().Name+":"+json);
            List<Type> ls = ClassUtil.GetClasses("protocol");
            foreach (Type item in ls)
            {
              Console.WriteLine(item.Name);
              ProtoAttribute arr=  (ProtoAttribute)ClassUtil.GetAttribute(item,typeof(ProtoAttribute));
                if(arr!=null)
              Console.WriteLine(arr.value);
            }
            Console.Read();
            /**
			SocketListner listner = new TestListner ();
			USocket us = new USocket ();
			us.setLister (listner);
			//Protocal p = new Varint32HeaderProtocol ();
            Protocal p = new LVProtocal();
			us.setProtocal (p);
			us.Connect ("localhost", 4887);
			Console.Read();
             */
		}
	}
}
