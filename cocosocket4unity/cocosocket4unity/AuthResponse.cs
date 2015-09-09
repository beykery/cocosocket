using ProtoBuf;
using System.Collections.Generic;
using System;

namespace cocosocket4unity
{
    [ProtoContract]
    public class AuthResponse	// 协议:-6
    {
        [ProtoMember(3, IsRequired = false)]
        public string pid; // 用户的id
        [ProtoMember(1, IsRequired = false)]
        public bool success; // 是否成功
        [ProtoMember(2, IsRequired = false)]
        public string info; // 错误消息
    }
}
