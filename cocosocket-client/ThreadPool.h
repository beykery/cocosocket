#ifndef THREADPOOL_H
#define THREADPOOL_H

#include <string.h>
#include "WorkThread.h"
#include "Mutext.h"

class ThreadPool {
private:
    int poolSize;
    WorkThread** pool;
public:
    ThreadPool(int ps = 1);
    ~ThreadPool();
public:
    void Offer(Thread* task);
    int GetPoolSize();
    void Shutdown();
};

inline int ThreadPool::GetPoolSize() {
    return poolSize;
}
#endif

