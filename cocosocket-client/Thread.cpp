#include "Thread.h"
#include "cocos2d.h"
#if CC_TARGET_PLATFORM == CC_PLATFORM_IOS
#include <string>
#include <sstream>
#include <iostream> 
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#endif

Thread::Thread() : status(UNINITIALIZED), sem(NULL)
{
}

Thread::~Thread()
{
	End();
}

/**
* 创建线程
* @param fun
* @param context
* @param detached
* @param scope
* @return 
*/
int Thread::Create(func fun, void * context, bool d, bool scope)
{
	pthread_attr_t thread_attr;
	int nStatus;
	nStatus = pthread_attr_init(&thread_attr);
	if (nStatus != 0)
	{
		return -1;
	}
	if (d)
	{
		nStatus = pthread_attr_setdetachstate(&thread_attr, PTHREAD_CREATE_DETACHED);
		if (nStatus != 0)
		{
			pthread_attr_destroy(&thread_attr);
			return -1;
		}
	}
	if (scope)
	{
		nStatus = pthread_attr_setscope(&thread_attr, PTHREAD_SCOPE_SYSTEM);
		if (nStatus != 0)
		{
			pthread_attr_destroy(&thread_attr);
			return -1;
		}
	}
	nStatus = pthread_create(&threadId, &thread_attr, fun, context);
	pthread_attr_destroy(&thread_attr);
	return nStatus;
}

/**
* 初始化
* @return 
*/
int Thread::Init()
{
	#if CC_TARGET_PLATFORM == CC_PLATFORM_IOS
	static int semIndex=0;
	std::stringstream ss;
	ss<<"cocosocket-";
	ss<<semIndex++;
	std::string name;
	ss>>name;
    sem = sem_open( name.c_str(),O_CREAT,0644,0);
	if(sem == SEM_FAILED)
	{
		return ERR_AT_CREATE_SEM;
	}
    #else
	sem = new sem_t;
	if (sem_init(sem, 0, 0) < 0)
	{
		return ERR_AT_CREATE_SEM;
	}
    #endif
	if (Create(&DoRun, (void *) this) < 0)
	{
		return ERR_AT_CREATE_THREAD;
	}
	status = IDLE;
	return status;
}

void * Thread::DoRun(void* context)
{
	Thread * thread = (Thread *) context;
	sem_wait(thread->sem);
	if (RUNNING == thread->status)
	{
		thread->Run();
	}
	thread->status = QUITED;
	return (void *) 0;
}

/**
*  启动线程
* @return 
*/
int Thread::Start()
{
	if (status == UNINITIALIZED)
	{
		this->Init();
		status = RUNNING;
		sem_post(sem);
	}
	return status;
}

/**
* 结束
*/
void Thread::End()
{
	if (sem!=NULL)
	{  
		sem_post(sem);
		#if CC_TARGET_PLATFORM == CC_PLATFORM_IOS
		sem_close(sem);
        #else
        sem_destroy(sem);
        #endif
		delete sem;
		sem = NULL;
	}
}


