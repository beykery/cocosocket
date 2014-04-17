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
    Frame* PutByte(char c);
    Frame* PutBytes(ByteBuf* src);
    Frame* PutShort(short s);
    Frame* PutInt(int s);
    Frame* PutLong(long long s);
    Frame* PutFloat(float s);
    Frame* PutString(char* s);
    void End();
    bool IsEnd();
    void SetEnd(bool e);
    Frame* Duplicate();
private:
    ByteBuf* payload; //原始数据
    bool e; //是否封包
};

#endif	/* FRAME_H */

