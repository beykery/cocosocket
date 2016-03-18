using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=-10,description="随机名字")]
	[ProtoContract]
	public class RandomNickyNameResponse	// 协议:-10
	{
		[ProtoMember(3, IsRequired = false)]
		public int sex; // 性别
		[ProtoMember(4, IsRequired = false)]
		public string nickyName; // 昵称
		[ProtoMember(1, IsRequired = false)]
		public bool success; // 是否成功
		[ProtoMember(2, IsRequired = false)]
		public string info; // 错误消息

	}
}