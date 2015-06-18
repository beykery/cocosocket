using System;
using LitJson;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
namespace cocosocket4unity
{
	class MainClass
	{
		public static void Main (string[] args)
		{

			SocketListner listner = new TestListner ();
			USocket us = new USocket ();
			us.setLister (listner);
			Protocal p = new LVProtocal ();
			us.setProtocal (p);
			us.Connect ("localhost", 3210);
			Console.Read();
		}
	}
}
