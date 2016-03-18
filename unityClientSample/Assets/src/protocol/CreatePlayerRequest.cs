using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=8,description="创建player")]
	[ProtoContract]
	public class CreatePlayerRequest	// 协议:8
	{
		[ProtoMember(1, IsRequired = true)]
		public string nickyName; // 昵称
		[ProtoMember(2, IsRequired = true)]
		public int camp; // 阵营
		[ProtoMember(3, IsRequired = true)]
		public string icon; // icon
		[ProtoMember(4, IsRequired = true)]
		public int sex; // 性别
		[ProtoMember(5, IsRequired = true)]
		public string loginid; // loginid

	}
}