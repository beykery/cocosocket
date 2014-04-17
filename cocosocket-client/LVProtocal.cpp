/* 
 * File:   LVProtocal.cpp
 * Author: beykery
 * 
 * Created on 2013年12月31日, 下午5:05
 */

#include "LVProtocal.h"
#include "ByteBuf.h"
#include <stddef.h>

LVProtocal::LVProtocal()
{
    this->status = 0;

    frame = NULL;
}

LVProtocal::~LVProtocal()
{
    delete frame;
}

ByteBuf* LVProtocal::TranslateFrame(ByteBuf* in)
{
    while (in->ReadableBytes() > 0)
    {
        switch (status)
        {
            case 0:
                h = in->ReadByte();
                status = 1;
                break;
            case 1:
                l = in->ReadByte();
                len = ((h << 8)&0x0000ff00) | (l);
                frame = new ByteBuf(len + 2);
                frame->WriteShort(len);
                status = 2;
                break;
            case 2:
                this->frame->WriteBytes(in);
                if (frame->WritableBytes() <= 0)
                {
                    status = 0;
                    return frame;
                }
                break;
        }
    }
    return NULL;
}
