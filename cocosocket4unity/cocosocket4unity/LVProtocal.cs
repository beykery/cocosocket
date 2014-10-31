using System;

namespace cocosocket4unity
{
	public class LVProtocal : Protocal
	{
		private int status;
		private int h;
		private int l;
		private short len;
		private ByteBuf frame;

		public LVProtocal ()
		{

		}
		/**
		 * 分帧逻辑
		 * 
		 **/ 
		public ByteBuf TranslateFrame(ByteBuf src)
		{
			while (src.ReadableBytes() > 0)
			{
				switch (status)
				{
				case 0:
					h = src.ReadByte();
					status = 1;
					break;
				case 1:
					l = src.ReadByte();
					len = (short)(((h << 8)&0x0000ff00) | (l));
					frame = new ByteBuf(len + 2);
					frame.WriteShort(len);
					status = 2;
					break;
				case 2:
					frame.WriteBytes(src);
					if (frame.WritableBytes() <= 0)
					{
						status = 0;
						return frame;
					}
					break;
				}
			}
			return null;
		}
	}
}

