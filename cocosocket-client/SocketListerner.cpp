/* 
 * File:   SocketListerner.cpp
 * Author: beykery
 * 
 * Created on 2013年12月30日, 下午4:00
 */

#include "SocketListerner.h"
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
#endif
#include "Socket.h"
#include "ByteBuf.h"
#include "Protocal.h"

SocketListerner::SocketListerner():context(NULL)
{
}

SocketListerner::~SocketListerner()
{
}

void SocketListerner::SetContext(Socket* context)
{
    this->context = context;
}

void SocketListerner::Run()
{
    ByteBuf* buf = new ByteBuf(1024);
    int size = 0;
    int sockid = context->GetSocketId();
    while (true)
    {
        size = recv(sockid, buf->GetRaw(), 1024, 0);
        if (size > 0)
        {
            buf->ReaderIndex(0);
            buf->WriterIndex(size);
            while (true)
            {
                ByteBuf* frame = context->GetProtocal()->TranslateFrame(buf);
                if (frame != NULL)
                {
                    this->OnMessage(context, frame);
                } else
                {
                    break;
                }
            }
        } else
        {
            break;
        }
    }
    delete buf;
    this->OnClose(context, true);
}

