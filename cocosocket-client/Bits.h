/* 
 * File:   Bits.h
 * Author: CYSY
 *
 * Created on 2014年2月10日, 上午11:59
 */

#ifndef BITS_H
#define	BITS_H

/**
 * 一个字节
 */
struct Byte {
    int a0 : 1;
    int a1 : 1;
    int a2 : 1;
    int a3 : 1;
    int a4 : 1;
    int a5 : 1;
    int a6 : 1;
    int a7 : 1;
};

class Bits {
public:
    Bits(int size);
    virtual ~Bits();
    int getBit(int index);
    void setBit(int index, int value);
private:
    int size;
    char* bits;
};

#endif	/* BITS_H */

