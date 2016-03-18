using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=-7,description="ping协议的返回")]
	[ProtoContract]
	public class Pong	// 协议:-7
	{
		[ProtoMember(3, IsRequired = false)]
		public long time; // 服务器的时间
		[ProtoMember(1, IsRequired = false)]
		public bool success; // 是否成功
		[ProtoMember(2, IsRequired = false)]
		public string info; // 错误消息

	}
}