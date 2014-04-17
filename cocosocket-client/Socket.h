/* 
 * File:   Socket.h
 * Author: beykery
 *
 * Created on 2013年12月30日, 下午3:13
 */

#ifndef SOCKET_H
#define	SOCKET_H
#include "SocketListerner.h"
#include "Protocal.h"
#include "ByteBuf.h"
#include "Thread.h"
#include "Frame.h"

class Socket;

class SocketListerner : public Thread {
public:
    SocketListerner();
    virtual ~SocketListerner();
    virtual void OnMessage(Socket* so, ByteBuf* frame) = 0;
    virtual void OnClose(Socket* so, bool fromRemote) = 0;
    virtual void OnIdle(Socket* so) = 0;
    virtual void OnOpen(Socket* so) = 0;
    virtual void OnError(Socket* so, const char* e) = 0;
    void SetContext(Socket* context);
    virtual void Run();

private:
    Socket* context;
};

class Socket {
public:
    Socket();
    virtual ~Socket();
    void Connect(const char* ip, unsigned short port);
    int Close();
    int Send(ByteBuf* frame);
    int Send(Frame* frame);
    void SetListerner(SocketListerner* listerner);
    void SetProtocal(Protocal* p);
    Protocal* GetProtocal();
    int GetSocketId();
    SocketListerner* GetListerner();
private:
    const char* ip;
    unsigned short port;
    unsigned sockid;
    SocketListerner* listerner;
    Protocal* protocal;
};

#endif	/* SOCKET_H */

