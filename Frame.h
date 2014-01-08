/* 
 * File:   Frame.h
 * Author: beykery
 *
 * Created on 2014年1月7日, 上午9:54
 */

#ifndef FRAME_H
#define	FRAME_H
#include "ByteBuf.h"

class Frame {
public:
    Frame(int len);
    virtual ~Frame();
    ByteBuf* GetData();
    const Frame* PutByte(char c);
    const Frame* PutBytes(ByteBuf* src);
    const Frame* PutShort(short s);
    const Frame* PutInt(int s);
    const Frame* PutLong(long s);
    const Frame* PutFloat(float s);
    const Frame* PutString(wchar_t* s);
    void end();
    bool isEnd();
    void setEnd(bool e);
    const Frame* duplicate();
private:
    ByteBuf* payload; //原始数据
    bool e; //是否封包
};

#endif	/* FRAME_H */

