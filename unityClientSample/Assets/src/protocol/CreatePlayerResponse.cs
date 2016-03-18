using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=-8,description="创建角色的response")]
	[ProtoContract]
	public class CreatePlayerResponse	// 协议:-8
	{
		[ProtoMember(3, IsRequired = false)]
		public string pid; // 创建的角色id
		[ProtoMember(1, IsRequired = false)]
		public bool success; // 是否成功
		[ProtoMember(2, IsRequired = false)]
		public string info; // 错误消息

	}
}