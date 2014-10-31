#ifndef THREAD_H
#define THREAD_H
#include <pthread.h>
#include <sched.h>
#include <semaphore.h>

typedef void * (* func)(void *);

class Thread {
public:

    enum ThreadState {
        ERR_ALREADERY_INITIALIZED = -6,
        ERR_AT_CREATE_THREAD = -5,
        ERR_AT_CREATE_SEM = -4,
        ERR_NO_TASK = -3,
        ERR_NOT_IDLE = -2,
        UNINITIALIZED = -1,
        IDLE = 0,
        RUNNING = 1,
        QUITED = 9
    };
private:
    pthread_t threadId;
    int Create(func f, void * context, bool detached = false, bool scope = false);
    void End();
    int Init();
    static void * DoRun(void * context);
protected:
    int status;
    sem_t* sem;
public:
    Thread();
    virtual ~Thread();
    virtual void Run() = 0;
	sem_t* GetSem();
    int Detach();
    int Join(void ** retValue = NULL);
    void Exit(void * retValue = NULL);
    void Abalienate();
    bool IsCurrent();
    pthread_t GetThreadId();
    int GetStatus();
    int Start();
};
inline sem_t* Thread::GetSem()
{
	return sem;
}

inline pthread_t Thread::GetThreadId() 
{
    return threadId;
}

inline int Thread::Detach() 
{
    return pthread_detach(threadId);
}

inline int Thread::Join(void ** retValue) 
{
    return pthread_join(threadId, retValue);
}

inline void Thread::Exit(void * retValue)
{
    if (IsCurrent()) 
	{
        pthread_exit(retValue);
    }
}

inline bool Thread::IsCurrent() {
    if (pthread_equal(threadId, pthread_self()) != 0) {
        return true;
    } else {
        return false;
    }
}

inline void Thread::Abalienate() {
    sched_yield();
}

inline int Thread::GetStatus() {
    return status;
}
#endif


