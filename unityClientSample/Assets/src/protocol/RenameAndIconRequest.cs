using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
	[Proto(value=15,description="重命名")]
	[ProtoContract]
	public class RenameAndIconRequest	// 协议:15
	{
		[ProtoMember(1, IsRequired = false)]
		public string nickyName; // 昵称(null表示不做修改)
		[ProtoMember(2, IsRequired = false)]
		public string icon; // icon(null表示不做修改)

	}
}