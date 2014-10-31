/* 
 * File:   cocos2d.h
 * Author: Beykery
 *
 * Created on 2014年8月5日, 下午3:23
 */

#ifndef COCOS2D_H
#define	COCOS2D_H
// define supported target platform macro which CC uses.
#define CC_PLATFORM_UNKNOWN            0
#define CC_PLATFORM_IOS                1
#define CC_PLATFORM_ANDROID            2
#define CC_PLATFORM_WIN32              3
#define CC_PLATFORM_MARMALADE          4
#define CC_PLATFORM_LINUX              5
#define CC_PLATFORM_BADA               6
#define CC_PLATFORM_BLACKBERRY         7
#define CC_PLATFORM_MAC                8
#define CC_PLATFORM_NACL               9
#define CC_PLATFORM_EMSCRIPTEN        10
#define CC_PLATFORM_TIZEN             11
#define CC_PLATFORM_QT5               12
#define CC_PLATFORM_WP8               13
#define CC_PLATFORM_WINRT             14

// Determine target platform by compile environment macro.
#define CC_TARGET_PLATFORM             CC_PLATFORM_UNKNOWN


#endif	/* COCOS2D_H */

