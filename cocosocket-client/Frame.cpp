/* 
 * File:   Frame.cpp
 * Author: beykery
 * 
 * Created on 2014年1月7日, 上午9:54
 */

#include "Frame.h"
#include "ByteBuf.h"
#include "AutoLock.h"

Frame::~Frame()
{
    delete this->payload;
}

Frame::Frame(int len) : e(false)
{
    this->payload = new ByteBuf(len);
    this->payload->WriteShort(0);
}

ByteBuf* Frame::GetData()
{
    return this->payload;
}

Frame* Frame::PutByte(char c)
{
    if (!e)
        this->payload->WriteByte(c);
    return this;
}

 Frame* Frame::PutBytes(ByteBuf* src)
{
    if (!e)
        this->payload->WriteBytes(src);
    return this;
}

 Frame* Frame::PutFloat(float s)
{
    if (!e)
        this->payload->WriteFloat(s);
    return this;
}

 Frame* Frame::PutInt(int s)
{
    if (!e)
        this->payload->WriteInt(s);
    return this;
}

 Frame* Frame::PutLong(long long s)
{
    if (!e)
        this->payload->WriteLong(s);
    return this;
}

 Frame* Frame::PutShort(short s)
{
    if (!e)
        this->payload->WriteShort(s);
    return this;
}

 Frame* Frame::PutString(char* s)
{
    if (!e)
        this->payload->WriteUTF8(s);
    return this;
}

 Frame* Frame::Duplicate()
{
    Frame* f = new Frame(this->payload->Capacity());
    this->payload->MarkReaderIndex();
    f->PutBytes(this->payload);
    this->payload->ResetReaderIndex();
    if (e)
    {
        f->End();
    }
    return f;
}

void Frame::End()
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

bool Frame::IsEnd()
{
    return e;
}

void Frame::SetEnd(bool e)
{
    if (e)
    {
        this->End();
    } else
    {
        this->e = e;
    }
}


