/* 
 * File:   main.cpp
 * Author: beykery
 *
 * Created on 2013年12月30日, 下午3:11
 */

#include <cstdlib>
#include <iostream>

#include "Socket.h"
#include "DefaultListerner.h"
#include "LVProtocal.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv)
{
    //setlocale(LC_ALL, "Chinese-simplified");
    //    wchar_t* c = L"哈哈哈";
    //    cout << sizeof (wchar_t) << endl;
    //    wcout << c << endl;

    Socket* s = Socket::GetInstance();
    s->SetListerner(new DefaultListerner()); //需要定制一个listerner，这里的是一个测试用的默认的listerner
    s->SetProtocal(new LVProtocal());
    cout << "建立连接中" << endl;
    s->Connect("192.168.10.150", 3333);
    pthread_join(s->GetRecvThreadId(), NULL);
    cout << "失败" << endl;
    delete s;
}

