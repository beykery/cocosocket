using System;

namespace cocosocket4unity
{
	public interface Protocal
	{
		ByteBuf TranslateFrame (ByteBuf src); 
	}
}

