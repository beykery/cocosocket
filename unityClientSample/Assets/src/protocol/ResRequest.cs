using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=9,description="资源查看")]
	[ProtoContract]
	public class ResRequest	// 协议:9
	{
		[ProtoMember(1, IsRequired = false)]
		public string pid; // player id

	}
}