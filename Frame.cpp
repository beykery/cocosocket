/* 
 * File:   Frame.cpp
 * Author: beykery
 * 
 * Created on 2014年1月7日, 上午9:54
 */

#include "Frame.h"
#include "ByteBuf.h"

Frame::~Frame()
{
    delete this->payload;
}

Frame::Frame(int len)
{
    this->payload = new ByteBuf(len);
    this->payload->WriteShort(0);
}

ByteBuf* Frame::GetData()
{
    return this->payload;
}

const Frame* Frame::PutByte(char c)
{
    if (!e)
        this->payload->WriteByte(c);
    return this;
}

const Frame* Frame::PutBytes(ByteBuf* src)
{
    if (!e)
        this->payload->WriteBytes(src);
    return this;
}

const Frame* Frame::PutFloat(float s)
{
    if (!e)
        this->payload->WriteFloat(s);
    return this;
}

const Frame* Frame::PutInt(int s)
{
    if (!e)
        this->payload->WriteInt(s);
    return this;
}

const Frame* Frame::PutLong(long s)
{
    if (!e)
        this->payload->WriteLong(s);
    return this;
}

const Frame* Frame::PutShort(short s)
{
    if (!e)
        this->payload->WriteShort(s);
    return this;
}

const Frame* Frame::PutString(wchar_t* s)
{
    if (!e)
        this->payload->WriteUTF8(s);
    return this;
}

const Frame* Frame::duplicate()
{
    Frame* f = new Frame(this->payload->Capacity());
    this->payload->MarkReaderIndex();
    f->PutBytes(this->payload);
    this->payload->ResetReaderIndex();
    if (e)
    {
        f->end();
    }
    return f;
}

void Frame::end()
{
    ByteBuf* bb = payload;
    int reader = bb->ReaderIndex();
    int writer = bb->WriterIndex();
    const int l = writer - reader - 2; //数据长度
    bb->WriterIndex(reader);
    bb->WriteShort(l);
    bb->WriterIndex(writer);
    this->e = true;
}

bool Frame::isEnd()
{
    return e;
}

void Frame::setEnd(bool e)
{
    if (e)
    {
        this->end();
    } else
    {
        this->e = e;
    }
}

