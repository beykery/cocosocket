/* 
 * File:   Socket.cpp
 * Author: beykery
 * 
 * Created on 2013年12月30日, 下午3:13
 */
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include "cocos2d.h"
#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32
#pragma comment(lib, "wsock32")
#pragma comment(lib,"ws2_32.lib")
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <arpa/inet.h>
#define INVALID_SOCKET -1
#define SOCKET_ERROR -1
#endif
#include <pthread.h>
#include "Socket.h"
#include "ByteBuf.h"
#include "SocketListerner.h"
#include "Protocal.h"
#include "Frame.h"


Socket::Socket():ip(NULL),listerner(NULL),protocal(NULL)
{
}

Socket::~Socket()
{
    delete this->protocal;
	delete this->listerner;
}

/**
 * 返回sockeid
 * @return 
 */
int Socket::GetSocketId()
{
    return this->sockid;
}

/**
 * 监听器
 * @return 
 */
SocketListerner* Socket::GetListerner()
{
    return this->listerner;
}

Protocal* Socket::GetProtocal()
{
    return this->protocal;
}

void Socket::SetProtocal(Protocal* p)
{
    this->protocal = p;
}

/**
 * 连接服务器
 * @return 
 */
void Socket::Connect(const char* ip, unsigned short port)
{
    this->ip = ip;
    this->port = port;
    if ((this->sockid = socket(AF_INET, SOCK_STREAM, 0)) != INVALID_SOCKET)
    {
		struct sockaddr_in addr;
        addr.sin_family = AF_INET;
        addr.sin_port = htons(this->port);
        addr.sin_addr.s_addr = inet_addr(ip);
        if (connect(sockid, (struct sockaddr *) &addr, sizeof (addr)) != SOCKET_ERROR)
        {
            this->listerner->OnOpen(this);
            this->listerner->Start();
        }else
		{
			 this->listerner->OnClose(this, false);
		}
    }
	else
    {
       this->listerner->OnClose(this, false);
    }
}

/**
 * 关闭
 */
int Socket::Close()
{
	if (sockid == -1)
	{
		return -1;
	}
	int t=sockid;
	sockid=-1;
#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32
	shutdown(t, SD_SEND);
	return (closesocket(t));
#else
	shutdown(t, SHUT_RDWR);
	return (close(t));
#endif
}

/**
 * 发送内容
 * @param frame
 * @return 
 */
int Socket::Send(ByteBuf* frame)
{
    char* content = frame->GetRaw();
    int bytes;
    int count = 0;
    int len = frame->ReadableBytes();
    while (count < len)
    {
        bytes = send(this->sockid, content + count, len - count, 0);
        if (bytes == -1 || bytes == 0)
            return -1;
        count += bytes;
        frame->ReaderIndex(frame->ReaderIndex() + bytes);
    }
    return count;
}

/**
 * 发送一帧
 * @param frame
 * @return 
 */
int Socket::Send(Frame* frame)
{
    if (frame->IsEnd())
    {
        return this->Send(frame->GetData());
    }
    return 0;
}

/**
 * 设置一个socket状态监听
 * @param listerner
 */
void Socket::SetListerner(SocketListerner* listerner)
{
    this->listerner = listerner;
    this->listerner->SetContext(this);
}