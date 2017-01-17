using ProtoBuf;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.ComponentModel;
using System;

namespace protocol
{
	[ProtoContract]
	public enum TestEnum
	{
        /// <summary>
        ///  白
        /// </summary>
		//[EnumMember]
		[ProtoEnum]
		[Description("白")]
		WHITE=0,
        /// <summary>
        ///  绿
        /// </summary>
		//[EnumMember]
		[ProtoEnum]
		[Description("绿")]
		GREEN=1

	}
}