/* 
 * File:   Bits.cpp
 * Author: CYSY
 * 
 * Created on 2014年2月10日, 上午11:59
 */

#include <stdlib.h>
#include <string.h>

#include "Bits.h"

Bits::Bits(int size)
{
    int bs = (size / 8) + 1;
    this->size = bs * 8;
    this->bits = (char*) malloc(bs);
    memset(bits, 0, bs);
}

Bits::~Bits()
{
    free(bits);
}

int Bits::getBit(int index)
{
    if (index >= 0 && index < size)
    {
        int i = index / 8;
        int j = index % 8;
        struct Byte* a = (struct Byte*) (bits + i);
        int r = 0;
        switch (j)
        {
            case 0:
                r = a->a0;
                break;
            case 1:
                r = a->a1;
                break;
            case 2:
                r = a->a2;
                break;
            case 3:
                r = a->a3;
                break;
            case 4:
                r = a->a4;
                break;
            case 5:
                r = a->a5;
                break;
            case 6:
                r = a->a6;
                break;
            case 7:
                r = a->a7;
                break;
        }
        r = 0x00000001 & r;
        return r;
    } else
    {
        return 0;
    }
}

void Bits::setBit(int index, int value)
{
    if (index >= size)
    {
        int bs = ((index + 1) / 8) + 1; //字节数
        char* temp = (char*) malloc(bs);
        memset(temp, 0, bs);
        memcpy(temp, bits, size / 8);
        free(bits);
        bits = temp;
        size = bs * 8;
    }
    if (index >= 0)
    {
        int i = index / 8;
        int j = index % 8;
        struct Byte* a = (struct Byte*) (bits + i);
        switch (j)
        {
            case 0:
                a->a0 = value;
                break;
            case 1:
                a->a1 = value;
                break;
            case 2:
                a->a2 = value;
                break;
            case 3:
                a->a3 = value;
                break;
            case 4:
                a->a4 = value;
                break;
            case 5:
                a->a5 = value;
                break;
            case 6:
                a->a6 = value;
                break;
            case 7:
                a->a7 = value;
                break;
        }
    }
}

