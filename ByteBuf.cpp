/* 
 * File:   ByteBuf.cpp
 * Author: beykery
 * 
 * Created on 2013年12月31日, 上午10:34
 */

#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include "ByteBuf.h"

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
    ByteBuf* item = new ByteBuf(this->len);
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

        int ret = data[index] << 24;
        ret |= (data[index + 1] << 16);
        ret |= (data[index + 2] << 8);
        ret |= data[index + 3];

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
        int ret = data[index] << 24;
        ret |= (data[index + 1] << 16);
        ret |= (data[index + 2] << 8);
        ret |= data[index + 3];
        return ret;
    }
    return 0;
}

long ByteBuf::GetLong(int index)
{
    long ret = 0;
    if (index + 7 < len)
    {
        ret = ((long) data[index]) << 56;
        ret |= ((long) data[index + 1]) << 48;
        ret |= ((long) data[index + 2]) << 40;
        ret |= ((long) data[index + 3]) << 32;
        ret |= data[index + 4] << 24;
        ret |= data[index + 5] << 16;
        ret |= data[index + 6] << 8;
        ret |= data[index + 7];
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
        int ret = data[readerIndex++] << 24;
        ret |= data[readerIndex++] << 16;
        ret |= data[readerIndex++] << 8;
        ret |= data[readerIndex++];

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
        int ret = data[readerIndex++] << 24;
        ret |= data[readerIndex++] << 16;
        ret |= data[readerIndex++] << 8;
        ret |= data[readerIndex++];
        return ret;
    }
    return 0;
}

long ByteBuf::ReadLong()
{
    long ret = 0;
    if (readerIndex + 7 < writerIndex)
    {
        ret = ((long) data[readerIndex++]) << 56;
        ret |= ((long) data[readerIndex++]) << 48;
        ret |= ((long) data[readerIndex++]) << 40;
        ret |= ((long) data[readerIndex++]) << 32;
        ret |= data[readerIndex++] << 24;
        ret |= data[readerIndex++] << 16;
        ret |= data[readerIndex++] << 8;
        ret |= data[readerIndex++];
    }
    return ret;
}

short ByteBuf::ReadShort()
{
    if (readerIndex + 1 < writerIndex)
    {
        short r1 = data[readerIndex++] << 8;
        short r2 = data[readerIndex++];
        short ret = r1 | r2;
        return ret;
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

ByteBuf* ByteBuf::SetLong(int index, long value)
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

ByteBuf* ByteBuf::WriteLong(long value)
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

wchar_t* ByteBuf::ReadUTF8()
{
    short len = ReadShort(); // 字节数
    wchar_t* charBuff = new wchar_t[len]; //>=字符数量
    memset(charBuff, 0, len * sizeof (wchar_t));
    int d = 0;
    while (ReadableBytes())
    {
        unsigned char c = ReadByte();
        if ((c & 0x80) == 0)
        {
            charBuff[d++] += c;
        } else if ((c & 0xE0) == 0xC0) ///< 110x-xxxx 10xx-xxxx
        {
            wchar_t& wideChar = charBuff[d++];
            wideChar = (c & 0x3F) << 6;
            wideChar |= (ReadByte() & 0x3F);
        } else if ((c & 0xF0) == 0xE0) ///< 1110-xxxx 10xx-xxxx 10xx-xxxx
        {
            wchar_t& wideChar = charBuff[d++];
            wideChar = (c & 0x1F) << 12;
            wideChar |= (ReadByte() & 0x3F) << 6;
            wideChar |= (ReadByte() & 0x3F);
        } else if ((c & 0xF8) == 0xF0) ///< 1111-0xxx 10xx-xxxx 10xx-xxxx 10xx-xxxx 
        {
            wchar_t& wideChar = charBuff[d++];
            wideChar = (c & 0x0F) << 18;
            wideChar = (ReadByte() & 0x3F) << 12;
            wideChar |= (ReadByte() & 0x3F) << 6;
            wideChar |= (ReadByte() & 0x3F);
        } else
        {
            wchar_t& wideChar = charBuff[d++]; ///< 1111-10xx 10xx-xxxx 10xx-xxxx 10xx-xxxx 10xx-xxxx 
            wideChar = (c & 0x07) << 24;
            wideChar = (ReadByte() & 0x3F) << 18;
            wideChar = (ReadByte() & 0x3F) << 12;
            wideChar |= ((ReadByte() & 0x3F) << 6);
            wideChar |= (ReadByte() & 0x3F);
        }
    }
    charBuff[d] = '\0';
    return charBuff;
}

ByteBuf* ByteBuf::WriteUTF8(wchar_t* value)
{
    int len = wcslen(value);
    int old = this->writerIndex;
    WriteShort(0);
    int d = 0;
    for (int i = 0; i < len; i++)
    {
        wchar_t wc = value[i];
        char cs[2] = {wc & 0xff, wc >> 8};
        if (wc <= 0x7f)
        { // ASCII  0x00 ~ 0x7f
            WriteByte(cs[0]);
            d++;
        } else if (wc <= 0x7ff)
        { // 0x080 ~ 0x7ff
            WriteByte(0xC0 | ((wc >> 6) & 0x1F));
            WriteByte(0x80 | ((wc >> 0) & 0x3F));
            d += 2;
        } else
        { // 0x0800 ~ 0xFFFF
            WriteByte(0xE0 | ((wc >> 12) & 0x0F));
            WriteByte(0x80 | ((wc >> 6) & 0x3F));
            WriteByte(0x80 | ((wc >> 0) & 0x3F));
            d += 3;
        }
    }
    this->writerIndex = old;
    WriteShort(d);
    this->writerIndex += d;
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

