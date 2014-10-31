/* 
 * File:   ByteBuf.cpp
 * Author: beykery
 * 
 * Created on 2013年12月31日, 上午10:34
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>
#include "ByteBuf.h"
#include <string.h>
//#include "iconv.h"
#include "cocos2d.h"
ByteBuf::ByteBuf(int len)
{
    this->len = len;
    this->data = (char*) malloc(len);
    this->readerIndex = 0;
    this->writerIndex = 0;
    this->markReader = 0;
    this->markWriter = 0;
}

ByteBuf::~ByteBuf()
{
    free(this->data);
}

int ByteBuf::Capacity()
{
    return this->len;
}

ByteBuf* ByteBuf::Capacity(int nc)
{
    if (nc > len)
    {
        char* old = this->data;
        this->data = (char*) malloc(nc);
        memcpy(this->data, old, this->len);
        free(old);
        this->len = nc;
    }
    return this;
}

/**
 * 清除掉所有标记
 * @return 
 */
const ByteBuf* ByteBuf::Clear()
{
    this->readerIndex = 0;
    this->writerIndex = 0;
    this->markReader = 0;
    this->markWriter = 0;
    return this;
}

const ByteBuf* ByteBuf::Copy()
{
    ByteBuf* item = new ByteBuf(len);
    memcpy(item->data, this->data, len);
    item->readerIndex = this->readerIndex;
    item->writerIndex = this->writerIndex;
    item->markReader = this->markReader;
    item->markWriter = this->markWriter;
    return item;
}

bool ByteBuf::GetBoolean(int index)
{
    if (index < len)
    {
        return *(data + index);
    }
    return false;
}

char ByteBuf::GetByte(int index)
{
    if (index < len)
    {
        return *(data + index);
    }
    return '\0';
}

float ByteBuf::GetFloat(int index)
{
    if (index + 3 < this->len)
    {

        int ret = ((int) data[index]) << 24;
        ret |= ((int) data[index + 1]) << 16;
        ret |= ((int) data[index + 2]) << 8;
        ret |= ((int) data[index + 3]);

        union
        {
            float f;
            int i;
        } u;
        u.i = ret;
        return u.f;
    }
    return 0.0f;
}

int ByteBuf::GetInt(int index)
{
    if (index + 3 < this->len)
    {
        int ret = ((int) data[index]) << 24;
        ret |= ((int) data[index + 1]) << 16;
        ret |= ((int) data[index + 2]) << 8;
        ret |= ((int) data[index + 3]);
        return ret;
    }
    return 0;
}

long long ByteBuf::GetLong(int index)
{
    long long ret = 0;
    if (index + 7 < len)
    {
        ret = ((long long) data[index]) << 56;
        ret |= ((long long) data[index + 1]) << 48;
        ret |= ((long long) data[index + 2]) << 40;
        ret |= ((long long) data[index + 3]) << 32;
        ret |= ((long long) data[index + 4]) << 24;
        ret |= ((long long) data[index + 5]) << 16;
        ret |= ((long long) data[index + 6]) << 8;
        ret |= ((long) data[index + 7]);
    }
    return ret;
}

short ByteBuf::GetShort(int index)
{
    if (index + 1 < len)
    {
        short r1 = data[index] << 8;
        short r2 = data[index + 1];
        short ret = r1 | r2;
        return ret;
    }
    return 0;
}

ByteBuf* ByteBuf::MarkReaderIndex()
{
    this->markReader = this->readerIndex;
    return this;
}

ByteBuf* ByteBuf::MarkWriterIndex()
{
    this->markWriter = this->writerIndex;
    return this;
}

int ByteBuf::MaxWritableBytes()
{
    return this->len - this->writerIndex;
}

bool ByteBuf::ReadBool()
{
    if (this->readerIndex < writerIndex)
    {
        bool ret = (bool)(data[readerIndex++]);
        return ret;
    }
    return false;
}

char ByteBuf::ReadByte()
{
    if (this->readerIndex < writerIndex)
    {
        char ret = data[readerIndex++];
        return ret;
    }
    return '\0';
}

float ByteBuf::ReadFloat()
{
    if (readerIndex + 3 < this->writerIndex)
    {
        int ret = ((data[readerIndex++]) << 24)&0xff000000;
        ret |= (((data[readerIndex++]) << 16)&0x00ff0000);
        ret |= (((data[readerIndex++]) << 8)&0x0000ff00);
        ret |= (((data[readerIndex++]))&0x000000ff);

        union
        {
            float f;
            int i;
        } u;
        u.i = ret;
        return u.f;
    }
    return 0.0f;
}

int ByteBuf::ReadInt()
{
    if (readerIndex + 3 < this->writerIndex)
    {
        int ret = ((data[readerIndex++]) << 24)&0xff000000;
        ret |= (((data[readerIndex++]) << 16)&0x00ff0000);
        ret |= (((data[readerIndex++]) << 8)&0x0000ff00);
        ret |= (((data[readerIndex++]))&0x000000ff);
        return ret;
    }
    return 0;
}

long long ByteBuf::ReadLong()
{
    long long ret = 0;
    if (readerIndex + 7 < writerIndex)
    {
        ret = (((long long) data[readerIndex++]) << 56)&0xff00000000000000;
        ret |= ((((long long) data[readerIndex++]) << 48)&0x00ff000000000000);
        ret |= ((((long long) data[readerIndex++]) << 40)&0x0000ff0000000000);
        ret |= ((((long long) data[readerIndex++]) << 32)&0x000000ff00000000);
        ret |= (((data[readerIndex++]) << 24)&0xff000000);
        ret |= (((data[readerIndex++]) << 16)&0x00ff0000);
        ret |= (((data[readerIndex++]) << 8)&0x0000ff00);
        ret |= (((data[readerIndex++]))&0x000000ff);
    }
    return ret;
}

short ByteBuf::ReadShort()
{
    if (readerIndex + 1 < writerIndex)
    {
        int h = data[readerIndex++];
        int l = data[readerIndex++]&0x000000ff;
        int len = ((h << 8)&0x0000ff00) | (l);
        return len;
    }
    return 0;
}

int ByteBuf::ReadableBytes()
{
    return this->writerIndex - this->readerIndex;
}

int ByteBuf::ReaderIndex()
{
    return this->readerIndex;
}

ByteBuf* ByteBuf::ReaderIndex(int readerIndex)
{
    if (readerIndex <= writerIndex)
    {
        this->readerIndex = readerIndex;
    }
    return this;
}

ByteBuf* ByteBuf::ResetReaderIndex()
{
    if (markReader <= writerIndex)
    {
        this->readerIndex = markReader;
    }
    return this;
}

ByteBuf* ByteBuf::ResetWriterIndex()
{
    if (markWriter >= readerIndex)
    {
        this->writerIndex = markWriter;
    }
    return this;
}

ByteBuf* ByteBuf::SetBoolean(int index, bool value)
{
    if (index < len)
    {
        this->data[index] = (char) value;
    }
    return this;
}

ByteBuf* ByteBuf::SetByte(int index, char value)
{
    if (index < len)
    {
        this->data[index] = value;
    }
    return this;
}

ByteBuf* ByteBuf::SetBytes(int index, char* src, int len)
{
    if (index + len <= this->len)
    {
        memcpy(data + index, src, len);
    }
    return this;
}

ByteBuf* ByteBuf::SetFloat(int index, float value)
{
    if (index + 4 <= len)
    {

        union
        {
            float f;
            int i;
        } u;
        u.f = value;
        data[index] = (u.i >> 24) & 0xff;
        data[index + 1] = (u.i >> 16) & 0xff;
        data[index + 2] = (u.i >> 8) & 0xff;
        data[index + 3] = u.i & 0xff;
    }
    return this;
}

ByteBuf* ByteBuf::SetIndex(int readerIndex, int writerIndex)
{
    if (readerIndex >= 0 && readerIndex <= writerIndex && writerIndex <= len)
    {
        this->readerIndex = readerIndex;
        this->writerIndex = writerIndex;
    }
    return this;
}

ByteBuf* ByteBuf::SetInt(int index, int value)
{
    if (index + 4 <= len)
    {
        data[index++] = (value >> 24) & 0xff;
        data[index++] = (value >> 16) & 0xff;
        data[index++] = (value >> 8) & 0xff;
        data[index++] = value & 0xff;
    }
    return this;
}

ByteBuf* ByteBuf::SetLong(int index, long long value)
{
    if (index + 8 <= len)
    {
        data[index++] = (value >> 56) & 0xff;
        data[index++] = (value >> 48) & 0xff;
        data[index++] = (value >> 40) & 0xff;
        data[index++] = (value >> 32) & 0xff;
        data[index++] = (value >> 24) & 0xff;
        data[index++] = (value >> 16) & 0xff;
        data[index++] = (value >> 8) & 0xff;
        data[index++] = value & 0xff;
    }
    return this;
}

ByteBuf* ByteBuf::SetShort(int index, short value)
{
    if (index + 2 <= len)
    {
        data[index++] = (value >> 8) & 0xff;
        data[index++] = value & 0xff;
    }
    return this;
}

ByteBuf* ByteBuf::SkipBytes(int length)
{
    if (readerIndex + length <= writerIndex)
    {
        this->readerIndex += length;
    }
    return this;
}

int ByteBuf::WritableBytes()
{
    return this->len - this->writerIndex;
}

ByteBuf* ByteBuf::WriteBoolean(bool value)
{
    this->Capacity(writerIndex + 1);
    this->data[writerIndex++] = (char) value;
    return this;
}

ByteBuf* ByteBuf::WriteByte(char value)
{
    this->Capacity(writerIndex + 1);
    this->data[writerIndex++] = value;
    return this;
}

ByteBuf* ByteBuf::WriteFloat(float value)
{
    this->Capacity(writerIndex + 4);

    union
    {
        float f;
        int i;
    } u;
    u.f = value;
    data[writerIndex++] = (u.i >> 24) & 0xff;
    data[writerIndex++] = (u.i >> 16) & 0xff;
    data[writerIndex++] = (u.i >> 8) & 0xff;
    data[writerIndex++] = u.i & 0xff;
    return this;
}

ByteBuf* ByteBuf::WriteInt(int value)
{
    this->Capacity(writerIndex + 4);
    data[writerIndex++] = (value >> 24) & 0xff;
    data[writerIndex++] = (value >> 16) & 0xff;
    data[writerIndex++] = (value >> 8) & 0xff;
    data[writerIndex++] = value & 0xff;
    return this;
}

ByteBuf* ByteBuf::WriteLong(long long value)
{
    this->Capacity(writerIndex + 8);
    data[writerIndex++] = (value >> 56) & 0xff;
    data[writerIndex++] = (value >> 48) & 0xff;
    data[writerIndex++] = (value >> 40) & 0xff;
    data[writerIndex++] = (value >> 32) & 0xff;
    data[writerIndex++] = (value >> 24) & 0xff;
    data[writerIndex++] = (value >> 16) & 0xff;
    data[writerIndex++] = (value >> 8) & 0xff;
    data[writerIndex++] = value & 0xff;
    return this;
}

ByteBuf* ByteBuf::WriteShort(short value)
{
    this->Capacity(writerIndex + 2);
    data[writerIndex++] = (value >> 8) & 0xff;
    data[writerIndex++] = value & 0xff;
    return this;
}

ByteBuf* ByteBuf::WriteBytes(ByteBuf* in)
{
    int remain = len - this->writerIndex;
    int sum = in->writerIndex - in->readerIndex;
    int v = remain < sum ? remain : sum;
    if (v > 0)
    {
        memcpy(this->data + this->writerIndex, in->data + in->readerIndex, v);
        this->writerIndex += v;
        in->readerIndex += v;
    }
    return this;
}

char* ByteBuf::ReadUTF8()
{
    short len = ReadShort(); // 字节数
    char* charBuff = new char[len+1]; //
    memcpy(charBuff, this->data + this->readerIndex, len);
	charBuff[len]='\0';
    this->readerIndex += len;
    return charBuff;
}

ByteBuf* ByteBuf::WriteUTF8(char* value)
{
    int len = strlen(value);
    this->Capacity(writerIndex + len + 2);
    this->WriteShort((short) len);
    memcpy(this->data + writerIndex, value, len);
    this->writerIndex += len;
    return this;
}

int ByteBuf::WriterIndex()
{
    return this->writerIndex;
}

ByteBuf* ByteBuf::WriterIndex(int writerIndex)
{
    if (writerIndex >= this->readerIndex && writerIndex <= len)
    {
        this->writerIndex = writerIndex;
    }
    return this;
}

char* ByteBuf::GetRaw()
{
    return this->data;
}

/**
int ByteBuf::Convert(std::string& str, const char* to, const char* from)
{
#if(CC_TARGET_PLATFORM == CC_PLATFORM_WIN32)
	iconv_t iconvH;
	iconvH = iconv_open(to, from);
	if (iconvH == 0)
	{
		return -1;
	}
	const char* strChar = str.c_str();
	const char** pin = &strChar;
	size_t strLength = str.length();
	char* outbuf = (char*) malloc(strLength*4);
	char* pBuff = outbuf;
	memset( outbuf, 0, strLength*4);
	size_t outLength = strLength*4;
	if (-1 == iconv(iconvH, pin, &strLength, &outbuf, &outLength))
	{
		iconv_close(iconvH);
		return -1;
	}
	str = pBuff;
	iconv_close(iconvH);
	return 0;
#endif

#if(CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
	iconv_t cd = iconv_open(to,from);
	if(cd==0)
		return -1;
	char* inbuf=str.c_str();
	size_t inlen = str.length();
	char* outbuf=(char*) malloc(inlen*4);
	size_t outlen = inlen*4;
	memset(outbuf,0,outlen);
	if(!iconv(cd, &inbuf, &inlen, &outbuf, &outlen))  
	{  
		iconv_close(cd);  
		return -1;  
	}
	iconv_close(cd);
	return 0;
#endif

#if(CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
	return 0;
#endif
}
**/