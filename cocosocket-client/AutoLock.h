#ifndef AUTOMUTEX_H
#define AUTOMUTEX_H
#include "Mutext.h"

class AutoLock {
private:
    Mutext* lock;
public:

    AutoLock(Mutext * pMutex) {
        lock = pMutex;
        lock->Lock();
    }

    ~AutoLock() {
        lock->Unlock();
    }
};
#endif

