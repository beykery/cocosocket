/* 
 * File:   SocketListerner.h
 * Author: beykery
 *
 * Created on 2013年12月30日, 下午4:00
 */

#ifndef SOCKETLISTERNER_H
#define	SOCKETLISTERNER_H
#include "Socket.h"
#include "ByteBuf.h"

class Socket;

class SocketListerner {
public:
    SocketListerner();
    virtual ~SocketListerner();
    virtual void OnMessage(Socket* so, ByteBuf* frame) = 0;
    virtual void OnClose(Socket* so,bool fromRemote) = 0;
    virtual void OnIdle(Socket* so) = 0;
    virtual void OnOpen(Socket* so) = 0;
    virtual void OnError(Socket* so, const char* e) = 0;
private:

};

#endif	/* SOCKETLISTERNER_H */

