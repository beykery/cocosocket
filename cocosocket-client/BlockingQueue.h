/* 
 * File:   BlockingQueue.h
 * Author: beykery
 *
 * Created on 2014年1月22日, 上午11:39
 */

#ifndef BLOCKINGQUEUE_H
#define	BLOCKINGQUEUE_H

#include "Mutext.h"
#include <stdlib.h>
#include "AutoLock.h"
#include "LinkedQueue.h"
template <class T>
class BlockingQueue:public LinedQueue<T> {
public:

    BlockingQueue() {
        lock = new Mutext();
    };

    virtual~BlockingQueue() {
        delete this->lock;
    };

    bool Offer(T* e) {
        AutoLock l(lock);
       return LinedQueue<T>::Offer(e);
    };

    T* Peek() {
        AutoLock l(lock);
        return LinedQueue<T>::Peek();
    };

    T* Poll() {
        AutoLock l(lock);
        return LinedQueue<T>::Poll();
    };

    int Size() {
        AutoLock l(lock);
        return this->s;
    };
private:
    Mutext* lock;
};

#endif	/* BLOCKINGQUEUE_H */

