/**
		 *  缓冲区
		 **/ 
using System;
using System.Text;

namespace cocosocket4unity
{
	public class ByteBuf
	{
		private int len;
		private byte[] data;
		private int readerIndex;
		private int writerIndex;
		private int markReader;
		private int markWriter;

		/**
				 * 初始化
				 **/
		public ByteBuf (int capacity)
		{
			this.len = capacity;
			this.data = new byte[len];
			readerIndex = 0;
			writerIndex = 0;
			markReader = 0;
			markWriter = 0;
		}
        public ByteBuf(byte[] content)
        {
            this.len = content.Length;
            this.data = content;
            readerIndex = 0;
            writerIndex = len;
            markReader = 0;
            markWriter = 0;
        }
		/**
		 *  容量
		 **/
		public int Capacity ()
		{
			return len;
		}

		/**
		 * 扩容
		 */
		public ByteBuf Capacity (int nc)
		{
			if (nc > len) {
				byte[] old = data;
				data = new byte[nc];
				Array.Copy (old, data, len);
				len = nc;
			}
			return this;
		}

		/**
	     * 清除掉所有标记
	     * @return 
	    **/
		public ByteBuf Clear ()
		{
			readerIndex = 0;
			writerIndex = 0;
			markReader = 0;
			markWriter = 0;
			return this;
		}
		/**
		 * 拷贝
		 **/ 
		public ByteBuf Copy()
		{
			ByteBuf item = new ByteBuf(len);
			Array.Copy (this.data, item.data, len);
			item.readerIndex = readerIndex;
			item.writerIndex = writerIndex;
			item.markReader = markReader;
			item.markWriter = markWriter;
			return item;
		}
		/**
		 * 获取一个字节
		 **/ 
		public byte GetByte(int index)
		{
			if (index < len)
			{
				return data[index];
			}
			return (byte)0;
		}
		/**
		 * 读取四字节整形F
		 **/ 
		public 	int GetInt(int index)
		{
			if (index + 3 < len)
			{
				int ret = ((int) data[index]) << 24;
				ret |= ((int) data[index + 1]) << 16;
				ret |= ((int) data[index + 2]) << 8;
				ret |= ((int) data[index + 3]);
				return ret;
			}
			return 0;
		}
		/**
		 * 读取两字节整形
		 **/ 
		public short GetShort(int index)
		{
			if (index + 1 < len)
			{
				short r1 = (short)(data[index] << 8);
				short r2 = (short)(data[index + 1]);
				short ret = (short)(r1 | r2);
				return ret;
			}
			return 0;
		}
		/**
		 * 标记读
		 **/ 
		public ByteBuf MarkReaderIndex()
		{
			markReader = readerIndex;
			return this;
		}
		/**
		 * 标记写
		 **/ 
		public ByteBuf MarkWriterIndex()
		{
			markWriter = writerIndex;
			return this;
		}
		/**
		 * 可写长度
		 **/ 
		public int MaxWritableBytes()
		{
			return len - writerIndex;
		}
		/**
		 * 读取一个字节
		 **/ 
		public byte ReadByte()
		{
			if (readerIndex < writerIndex)
			{
				byte ret = data[readerIndex++];
				return ret;
			}
			return (byte)0;
		}
		/**
		 * 读取四字节整形
		 **/ 
		public int ReadInt()
		{
			if (readerIndex + 3 < writerIndex)
			{
				unchecked
				{
					int ret = (int)(((data [readerIndex++]) << 24) & 0xff000000);
					ret |= (((data [readerIndex++]) << 16) & 0x00ff0000);
					ret |= (((data [readerIndex++]) << 8) & 0x0000ff00);
					ret |= (((data [readerIndex++])) & 0x000000ff);
					return ret;
				}
			}
			return 0;
		}
		/**
		 * 读取两个字节的整形
		 **/ 
		public short ReadShort()
		{
			if (readerIndex + 1 < writerIndex)
			{
				int h = data[readerIndex++];
				int l = data[readerIndex++]&0x000000ff;
				int len = ((h << 8)&0x0000ff00) | (l);
				return (short)len;
			}
			return 0;
		}
		/**
		 * 可读字节数
		 **/ 
		public int ReadableBytes()
		{
			return writerIndex - readerIndex;
		}
		/**
		 * 读指针
		 **/ 
		public int ReaderIndex()
		{
			return readerIndex;
		}
		/**
		 * 移动读指针
		 **/ 
		public ByteBuf ReaderIndex(int readerIndex)
		{
			if (readerIndex <= writerIndex)
			{
				this.readerIndex = readerIndex;
			}
			return this;
		}
		/**
		 * 重置读指针
		 **/ 
		public ByteBuf ResetReaderIndex()
		{
			if (markReader <= writerIndex)
			{
				this.readerIndex = markReader;
			}
			return this;
		}
		/**
		 * 重置写指针
		 **/ 
		public ByteBuf ResetWriterIndex()
		{
			if (markWriter >= readerIndex)
			{
				writerIndex = markWriter;
			}
			return this;
		}
		/**
		 * 设置字节
		 **/ 
		public ByteBuf SetByte(int index, byte value)
		{
			if (index < len)
			{
				data[index] = value;
			}
			return this;
		}


		/**
		 * 设置字节
		 **/ 
		public ByteBuf SetBytes(int index, byte[] src, int from, int len)
		{
			if (index + len <= len)
			{
				Array.Copy (src, from, data, index, len);
			}
			return this;
		}
		/**
		 * 设置读写指针
		 **/ 
		public ByteBuf SetIndex(int readerIndex, int writerIndex)
		{
			if (readerIndex >= 0 && readerIndex <= writerIndex && writerIndex <= len)
			{
				this.readerIndex = readerIndex;
				this.writerIndex = writerIndex;
			}
			return this;
		}
		/**
		 * 设置四字节整形
		 **/ 
		public ByteBuf SetInt(int index, int value)
		{
			if (index + 4 <= len)
			{
				data[index++] = (byte)((value >> 24) & 0xff);
				data[index++] = (byte)((value >> 16) & 0xff);
				data[index++] = (byte)((value >> 8) & 0xff);
				data[index++] = (byte)(value & 0xff);
			}
			return this;
		}
		/**
		 * 设置两字节整形
		 **/ 
		public ByteBuf SetShort(int index, short value)
		{
			if (index + 2 <= len)
			{
				data[index++] = (byte)((value >> 8) & 0xff);
				data[index++] = (byte)(value & 0xff);
			}
			return this;
		}
		/**
		 * 略过一些字节
		 **/ 
		public ByteBuf SkipBytes(int length)
		{
			if (readerIndex + length <= writerIndex)
			{
				readerIndex += length;
			}
			return this;
		}
		/**
		 * 剩余的可写字节数
		 **/ 
		public int WritableBytes()
		{
			return len - writerIndex;
		}
		/**
		 * 写入一个字节
		 * 
		 **/ 
		public ByteBuf WriteByte(byte value)
		{
			this.Capacity(writerIndex + 1);
			this.data[writerIndex++] = value;
			return this;
		}
		/**
		 * 写入四字节整形
		 **/ 
		public ByteBuf WriteInt(int value)
		{
			Capacity(writerIndex + 4);
			data[writerIndex++] = (byte)((value >> 24) & 0xff);
			data[writerIndex++] = (byte)((value >> 16) & 0xff);
			data[writerIndex++] = (byte)((value >> 8) & 0xff);
			data[writerIndex++] = (byte)(value & 0xff);
			return this;
		}
		/**
		 * 写入两字节整形
		 **/ 
		public ByteBuf WriteShort(short value)
		{
			Capacity(writerIndex + 2);
			data[writerIndex++] = (byte)((value >> 8) & 0xff);
			data[writerIndex++] = (byte)(value & 0xff);
			return this;
		}
		/**
		 * 写入一部分字节
		 **/ 
		public ByteBuf WriteBytes(ByteBuf src)
		{
			int sum = src.writerIndex - src.readerIndex;
			Capacity(writerIndex + sum);
			if (sum > 0)
			{
				Array.Copy (src.data, src.readerIndex, data, writerIndex, sum);
				writerIndex += sum;
				src.readerIndex += sum;
			}
			return this;
		}
		/**
		 * 写入一部分字节
		 **/ 
		public ByteBuf WriteBytes(ByteBuf src ,int len)
		{
			if (len > 0)
			{
				Capacity(writerIndex + len);
				Array.Copy (src.data, src.readerIndex, data, writerIndex, len);
				writerIndex += len;
				src.readerIndex += len;
			}
			return this;
		}
		/**
		 * 写入一部分字节
		 **/ 
		public ByteBuf WriteBytes(byte[] src)
		{
			int sum = src.Length;
			Capacity(writerIndex + sum);
			if (sum > 0)
			{
				Array.Copy (src, 0, data, writerIndex, sum);
				writerIndex += sum;
			}
			return this;
		}
		/**
		 * 写入一部分字节
		 **/ 
		public ByteBuf WriteBytes(byte[] src,int off,int len)
		{
			int sum = len;
			if (sum > 0)
			{
				Capacity(writerIndex + sum);
				Array.Copy (src,off, data, writerIndex, sum);
				writerIndex += sum;
			}
			return this;
		}
		/**
		 * 读取utf字符串
		 **/ 
		public string ReadUTF8()
		{
			short len = ReadShort(); // 字节数
			byte[] charBuff = new byte[len]; //
			Array.Copy (data, readerIndex, charBuff, 0, len);
			readerIndex += len;
			return Encoding.UTF8.GetString (charBuff);
		}

		/**
		 * 写入utf字符串
		 * 
		 **/ 
		public ByteBuf WriteUTF8(string value)
		{
			byte[] content = Encoding.UTF8.GetBytes (value.ToCharArray());
			int len = content.Length;
			Capacity(writerIndex + len + 2);
			WriteShort((short) len);
			Array.Copy (content, 0, data, writerIndex, len);
			writerIndex += len;
			return this;
		}
		/**
		 * 写指针
		 **/
		public int WriterIndex()
		{
			return writerIndex;
		}
		/**
		 * 移动写指针
		 **/ 
		public ByteBuf WriterIndex(int writerIndex)
		{
			if (writerIndex >= readerIndex && writerIndex <= len)
			{
				this.writerIndex = writerIndex;
			}
			return this;
		}
		/**
		 * 原始字节数组
		 **/ 
		public byte[] GetRaw()
		{
			return data;
		}







	}
}

