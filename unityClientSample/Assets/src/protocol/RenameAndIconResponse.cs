using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=-15,description="重命名返回")]
	[ProtoContract]
	public class RenameAndIconResponse	// 协议:-15
	{
		[ProtoMember(3, IsRequired = false)]
		public string nickyName; // 修改后的昵称(null表示未做修改)
		[ProtoMember(4, IsRequired = false)]
		public string icon; // 修改后的icon(null表示未做修改)
		[ProtoMember(1, IsRequired = false)]
		public bool success; // 是否成功
		[ProtoMember(2, IsRequired = false)]
		public string info; // 错误消息

	}
}