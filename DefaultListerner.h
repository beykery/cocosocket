/* 
 * File:   DefaultListerner.h
 * Author: beykery
 *
 * Created on 2013年12月30日, 下午7:33
 */

#ifndef DEFAULTLISTERNER_H
#define	DEFAULTLISTERNER_H
#include "ByteBuf.h"
#include "SocketListerner.h"

class DefaultListerner : public SocketListerner {
public:
    DefaultListerner();
    virtual ~DefaultListerner();
    void OnClose(Socket* so,bool fromRemote);
    void OnError(Socket* so, const char* e);
    void OnIdle(Socket* so);
    void OnMessage(Socket* so, ByteBuf* frame);
    void OnOpen(Socket* so);
private:

};

#endif	/* DEFAULTLISTERNER_H */

