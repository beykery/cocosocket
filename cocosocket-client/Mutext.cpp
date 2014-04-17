#include "Mutext.h"

Mutext::Mutext(int nShared, int nType)
{
    pthread_mutexattr_t attr;
    pthread_mutexattr_init(&attr);
    pthread_mutexattr_setpshared(&attr, nShared);
    pthread_mutexattr_settype(&attr, nType);
    pthread_mutex_init(&mutext, &attr);
    pthread_mutexattr_destroy(&attr);
}

Mutext::~Mutext()
{
    pthread_mutex_destroy(&mutext);
}

