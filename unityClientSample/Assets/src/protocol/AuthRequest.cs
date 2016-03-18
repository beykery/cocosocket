using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=6,description="connector上的验证请求")]
	[ProtoContract]
	public class AuthRequest	// 协议:6
	{
		[ProtoMember(1, IsRequired = true)]
		public string loginid; // 用户中心的加密id
		[ProtoMember(2, IsRequired = true)]
		public int serverid; // 登陆的服务器id

	}
}