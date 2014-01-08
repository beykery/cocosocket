/* 
 * File:   LVProtocal.h
 * Author: beykery
 *
 * Created on 2013年12月31日, 下午5:05
 */

#ifndef LVPROTOCAL_H
#define	LVPROTOCAL_H

#include "Protocal.h"
#include "ByteBuf.h"

class LVProtocal : public Protocal {
public:
    LVProtocal();
    virtual ~LVProtocal();
    ByteBuf* TranslateFrame(ByteBuf* in);
private:
    int status;
    int h;
    int l;
    short len;
    ByteBuf* frame;

};

#endif	/* LVPROTOCAL_H */

