#include <iostream>
#include "ThreadPool.h"
#include "Socket.h"
#include "SocketListerner.h"
#include "DefaultListerner.h"
#include "Protocal.h"
#include "LVProtocal.h"
#include <pthread.h>
#include <sched.h>
#include <unistd.h>
#include <stdlib.h>
#ifdef WIN32
#pragma comment(lib, "wsock32")
#pragma comment(lib,"ws2_32.lib")
#include <winsock2.h>
#endif
using namespace std;

class TestTask : public Thread
{
public:

    void Run()
    {
        int nCount = 0;
        while (true)
        {
            cout << "[" << ++nCount << "] sleep ..." << endl;
            if (nCount >= 3)
            {
                break;
            }
        }
    }
};

int main()
{
    cout<<sizeof(float)<<endl;
    setlocale(LC_ALL, "Chinese-simplified");
    //    wchar_t* c = L"哈哈哈";
    //    cout << sizeof (wchar_t) << endl;
    //    wcout << c << endl;
#ifdef WIN32
	WSADATA wsaData;
	WORD version = MAKEWORD(2, 0);
    WSAStartup(version, &wsaData);
#endif
    Socket* s = new Socket();
    SocketListerner* sl = new DefaultListerner();
    s->SetListerner(sl); //需要定制一个listerner，这里的是一个测试用的默认的listerner
    s->SetProtocal(new LVProtocal());
    s->Connect("192.168.1.100", 3333);
    sl->Join(NULL);
    s->Close();
    delete s;

    //    ThreadPool* cThreadPool = new ThreadPool();
    //    TestTask* cTest = new TestTask;
    //    cThreadPool->Offer(cTest);
    //    while (true)
    //    {
    //        cout << "Current AliveCount = " << cThreadPool->GetAliveCount() << endl;
    //        cTest = new TestTask;
    //        cThreadPool->Offer(cTest);
    //        cout << "Add one task." << endl;
    //        sleep(1);
    //    }
}