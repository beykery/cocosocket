using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=10,description="随机名字")]
	[ProtoContract]
	public class RandomNickyNameRequest	// 协议:10
	{
		[ProtoMember(1, IsRequired = false)]
		public int sex; // 性别

	}
}