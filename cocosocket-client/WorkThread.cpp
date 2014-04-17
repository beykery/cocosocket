#include "WorkThread.h"
#include "errno.h"
#include "BlockingQueue.h"
#include "Thread.h"

WorkThread::WorkThread()
{
    this->q = new BlockingQueue<Thread>;
    this->Start();
}

WorkThread::~WorkThread()
{
    delete q;
}

void WorkThread::Run()
{
    while (this->status != QUITED)
    {
        Thread* task = (Thread*) q->Poll();
        if (task != NULL)
        {
            status = RUNNING;
            task->Run();
            delete task;
        } else
        {
            this->status = IDLE;
            sem_wait(this->sem); //等待添加新的task
        }
    }
}

int WorkThread::TaskCount()
{
    return q->Size();
}
