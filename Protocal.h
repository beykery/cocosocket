/* 
 * File:   Protocal.h
 * Author: beykery
 *
 * Created on 2013年12月31日, 下午4:59
 */

#ifndef PROTOCAL_H
#define	PROTOCAL_H
#include "ByteBuf.h"
class Protocal {
public:
    Protocal();
    virtual ~Protocal();
    virtual ByteBuf* TranslateFrame(ByteBuf* in) = 0;
private:

};

#endif	/* PROTOCAL_H */

