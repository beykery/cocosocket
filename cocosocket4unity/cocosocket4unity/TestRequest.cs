using ProtoBuf;
using System.Collections.Generic;
using System;

namespace protocol
{
    /// <summary>
    /// 测试em 协议:900
    /// </summary>
	[Proto(value=900,description="测试em")]
	[ProtoContract]
	public class TestRequest
	{
        /// <summary>
        ///  颜色
        /// </summary>
		[ProtoMember(1, IsRequired = false)]
		public TestEnum c;

	}
}