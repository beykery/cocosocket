using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=-9,description="资源详情")]
	[ProtoContract]
	public class ResResponse	// 协议:-9
	{
		[ProtoMember(3, IsRequired = false)]
		public string pid; // player id
		[ProtoMember(4, IsRequired = false)]
		public int vip; // vip等级
		[ProtoMember(5, IsRequired = false)]
		public int level; // 等级
		[ProtoMember(6, IsRequired = false)]
		public string nickyName; // 昵称
		[ProtoMember(7, IsRequired = false)]
		public string icon; // 头像
		[ProtoMember(8, IsRequired = false)]
		public int camp; // 阵营
		[ProtoMember(9, IsRequired = false)]
		public int diamond; // 钻石
		[ProtoMember(10, IsRequired = false)]
		public int gold; // 黄金
		[ProtoMember(1, IsRequired = false)]
		public bool success; // 是否成功
		[ProtoMember(2, IsRequired = false)]
		public string info; // 错误消息

	}
}