using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=7,description="ping协议，保活")]
	[ProtoContract]
	public class Ping	// 协议:7
	{
		[ProtoMember(1, IsRequired = false)]
		public long time; // 客户端的时间

	}
}