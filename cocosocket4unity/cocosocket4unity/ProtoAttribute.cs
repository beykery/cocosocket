using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace cocosocket4unity
{
    [AttributeUsage(AttributeTargets.Class)]
    class ProtoAttribute : Attribute
    {
        public int value { get; set; }
    }
}
