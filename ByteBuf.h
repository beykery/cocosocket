/* 
 * File:   ByteBuf.h
 * Author: beykery
 *
 * Created on 2013年12月31日, 上午10:34
 */

#ifndef BYTEBUF_H
#define	BYTEBUF_H

class ByteBuf {
public:
    ByteBuf(int len);
    virtual ~ByteBuf();
    int Capacity();
    /**
     * 扩展容量
     * @param newCapacity
     * @return 
     */
    ByteBuf* Capacity(int newCapacity);
    int ReaderIndex();
    ByteBuf* ReaderIndex(int readerIndex);
    int WriterIndex();
    ByteBuf* WriterIndex(int writerIndex);
    ByteBuf* SetIndex(int readerIndex, int writerIndex);
    int ReadableBytes();
    int WritableBytes();
    int MaxWritableBytes();
    const ByteBuf* Clear();
    ByteBuf* MarkReaderIndex();
    ByteBuf* ResetReaderIndex();
    ByteBuf* MarkWriterIndex();
    ByteBuf* ResetWriterIndex();
    bool GetBoolean(int index);
    char GetByte(int index);
    short GetShort(int index);
    int GetInt(int index);
    long GetLong(int index);
    float GetFloat(int index);

    ByteBuf* SetBoolean(int index, bool value);
    ByteBuf* SetByte(int index, char value);
    ByteBuf* SetShort(int index, short value);
    ByteBuf* SetInt(int index, int value);
    ByteBuf* SetLong(int index, long value);
    ByteBuf* SetFloat(int index, float value);
    ByteBuf* SetBytes(int index, char* src, int len);
    bool ReadBool();
    char ReadByte();
    short ReadShort();
    int ReadInt();
    long ReadLong();
    float ReadFloat();
    wchar_t* ReadUTF8();
    ByteBuf* SkipBytes(int length);
    ByteBuf* WriteBoolean(bool value);
    ByteBuf* WriteByte(char value);
    ByteBuf* WriteShort(short value);
    ByteBuf* WriteInt(int value);
    ByteBuf* WriteLong(long value);
    ByteBuf* WriteFloat(float value);
    ByteBuf* WriteBytes(ByteBuf* in);
    ByteBuf* WriteUTF8(wchar_t* value);
    const ByteBuf* Copy();
    char* GetRaw();
private:
    char* data; //数据
    int len; //长度，可扩展长度
    int readerIndex; //读指针
    int writerIndex; //写指针
    int markReader; //读指针标记
    int markWriter; //写指针标记

};

#endif	/* BYTEBUF_H */

