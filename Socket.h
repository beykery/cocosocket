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
#include <pthread.h>
class SocketListerner;

class Socket {
public:
    virtual ~Socket();
    void Connect(const char* ip, int port);
    int Send(ByteBuf* frame);
    void SetListerner(SocketListerner* listerner);
    void SetProtocal(Protocal* p);
    Protocal* GetProtocal();
    int GetSocketId();
    SocketListerner* GetListerner();
    pthread_t GetRecvThreadId();
    static Socket* GetInstance();
private:
    Socket();
    static Socket* instance;
    const char* ip;
    int port;
    int sockfd;
    pthread_t pthread_recv_id;
    SocketListerner* listerner;
    Protocal* protocal;
};

#endif	/* SOCKET_H */

