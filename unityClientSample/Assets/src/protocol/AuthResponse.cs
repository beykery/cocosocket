using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=-6,description="登陆返回")]
	[ProtoContract]
	public class AuthResponse	// 协议:-6
	{
		[ProtoMember(3, IsRequired = false)]
		public string pid; // 用户的id==null
		[ProtoMember(4, IsRequired = false)]
		public byte[] test; // 测试大字段
		[ProtoMember(1, IsRequired = false)]
		public bool success; // 是否成功
		[ProtoMember(2, IsRequired = false)]
		public string info; // 错误消息

	}
}