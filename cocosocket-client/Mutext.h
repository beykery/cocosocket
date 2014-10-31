#ifndef MUTEX_H
#define MUTEX_H
#include <pthread.h>

class Mutext {
private:
    std::mutex mutext;
public:
    Mutext(int sh = PTHREAD_PROCESS_PRIVATE, int type = PTHREAD_MUTEX_NORMAL);
    ~Mutext();
private:

    Mutext(const Mutext & cMutex) {
    }

    Mutext & operator =(const Mutext & cMutex) {
        return *this;
    }

public:
    int Lock();
    int Unlock();
    int Trylock();
};

inline int Mutext::Lock() {
    return pthread_mutex_lock(&mutext);
}

inline int Mutext::Unlock() {
    return pthread_mutex_unlock(&mutext);
}

inline int Mutext::Trylock() {
    return pthread_mutex_trylock(&mutext);
}

#endif
