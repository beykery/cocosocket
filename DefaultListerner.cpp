/* 
 * File:   DefaultListerner.cpp
 * Author: beykery
 * 
 * Created on 2013年12月30日, 下午7:33
 */

#include "DefaultListerner.h"
#include "ByteBuf.h"
#include "Frame.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>
using namespace std;

DefaultListerner::DefaultListerner()
{
}

DefaultListerner::~DefaultListerner()
{
}

void DefaultListerner::OnClose(Socket* so, bool fromRemote)
{
    cout << "连接关闭" << endl;
}

void DefaultListerner::OnError(Socket* so, const char* e)
{
    cout << "连接错误" << endl;
}

void DefaultListerner::OnIdle(Socket* so)
{
    cout << "连接超时" << endl;
}

/**
 * 有数据到来
 * @param so
 * @param message
 */
void DefaultListerner::OnMessage(Socket* so, ByteBuf* frame)
{
    cout << frame->Capacity() << endl;
    frame->ReaderIndex(2);
    setlocale(LC_ALL, "Chinese-simplified");
    wcout << frame->ReadUTF8() << endl;
    frame->ReaderIndex(0);
    so->Send(frame);
    delete frame;
}

void DefaultListerner::OnOpen(Socket* so)
{
    cout << "连接建立" << endl;
    Frame* f = new Frame(100);
    f->PutString(L"你好");
    f->end();
    so->Send(f->GetData());
    delete f;
}



