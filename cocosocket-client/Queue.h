/* 
 * File:   Queue.h
 * Author: beykery
 *
 * Created on 2014年1月22日, 上午11:22
 */

#ifndef QUEUE_H
#define	QUEUE_H


template <class T>
class Queue {
public:

    Queue() {
    };

    virtual ~Queue() {
    };
    virtual bool Offer(T* e) = 0;
    virtual T* Poll() = 0;
    virtual T* Peek() = 0;
    virtual int Size() = 0;
};

#endif	/* QUEUE_H */

