/* 
 * File:   DefaultListerner.cpp
 * Author: beykery
 * 
 * Created on 2013年12月30日, 下午7:33
 */
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>
#include "DefaultListerner.h"
#include "Socket.h"
#include "ByteBuf.h"
#include "Frame.h"
#include "iconv.h"
#include "cocos2d.h"
using namespace std;

DefaultListerner::DefaultListerner()
{
}

DefaultListerner::~DefaultListerner()
{
}

void DefaultListerner::OnClose(Socket* so, bool fromRemote)
{
    CCLOG("%s\n","closed");
}

void DefaultListerner::OnError(Socket* so, const char* e)
{
	CCLOG("%s\n","error connection");
}

void DefaultListerner::OnIdle(Socket* so)
{
	CCLOG("%s\n","connection idle");
}

/**
 * 有数据到来
 * @param so
 * @param message
 */
void DefaultListerner::OnMessage(Socket* so, ByteBuf* frame)
{
    //    int r = frame->ReadableBytes();
    // cout << frame->Capacity() << endl;
    frame->ReaderIndex(2);
    // setlocale(LC_ALL, "Chinese-simplified");
    //    wchar_t* u1 = frame->ReadUTF8();
    //    wchar_t* u2 = frame->ReadUTF8();
    float c = frame->ReadFloat();
	std::string ss=frame->ReadUTF8();
    CCLOG("%s\n",ss.c_str());
    delete frame;
    //    Frame* s = new Frame(r);
    //    s->PutInt(c);
    //    s->End();
    //    if (so->Send(s) >= s->GetData()->Capacity())
    //    {
    //        cout << "ok" << endl;
    //    }
    //    delete s;
}

void DefaultListerner::OnOpen(Socket* so)
{
    CCLOG("%s","connecting");
	Frame* f=new Frame(512);
    f->PutFloat(10.1f);
	std::string buff="我去啊a。。。。。";
	ByteBuf::Convert(buff,"utf-8","gbk");
	f->PutString((char*)buff.c_str());
	//f->PutString(s);
    f->End();
    so->Send(f);
    delete f;
}