# cocosocket  


cocosocket是一个为cocos2d-x和unity 3D手机网络游戏准备的底层通信框架，满足网络游戏客户端
对于高性能网络通信的需求，适用于基于socket的手机网游。

## 问题

开发cocos2d-x网络游戏其中一个重点是书写稳定可靠的socket通信层。而bsd socket是
一个c语言的函数库，使用起来颇繁琐（beykery不喜欢），我们需要一个稳定可靠并且
api又足够简洁的通信层。它需要具备建立连接读写基本类型（byte、short、int、long、
utf8字符串）的能力、可灵活设计通信协议（分帧、处理粘包）的能力；它还需要运行在
独立于游戏主线程的线程里、网络事件（建立、关闭、异常、包到来等）发生时回调监听
器。或许我们还有同步的需求假如我们把收到的包放到一个待处理队列（queue）里面，
而游戏主线程需要访问这个队列。

## 解决方案

cocosocket在bsd socket基础上实现了一个简洁优雅的c++的api，除了线程、线程池、
队列、套接字（socket）、套接字监听器（socketlisterner）外，重要的是还提供了
一种自定义通信协议的api（Protocal），并提供了一个基于lengthfield的协议实现
（推荐使用lengthfield）；cocosocket提供了基本数据类型的读写api，而这些读写
是按照网络流（big endian）顺序处理的。

cocosocket还拥有关于锁、队列、同步队列、线程池的实现，这些内容有些不是必须的
，但我想也会有助于扩展功能。

## 使用

使用cocosocket是很简单的，看下面代码：

    Socket* s = new Socket();//1
    SocketListerner* sl = new DefaultListerner();//2
    s->SetListerner(sl); //3
    s->SetProtocal(new LVProtocal());//4
    s->Connect("192.168.1.100", 3333);//5

是的，就是这么使用，我将分别介绍这几行代码及其背后的机制

1，初始化一个Socket对象。

2，3，两行初始化一个SocketListerner并设置，这个listerner就是socket各种事件的
的回调接口，需要用户根据自己的需求实现一个SocketListerner（继承它），这个监听
器会处理这几个事件：socket连接成功（OnOpen）、socket连接关闭（OnClose）、消息
到来（OnMessage）、连接超时（OnIdle）、连接异常（OnError）；这些方法的回调，
是独立于游戏主线程的，因为socket运行在一个独立的线程里。DefaultListerner仅仅
是beykery用来测试的，使用cocosocket的时候，需要你自己定制自己的listerner。

4，初始化一个Protocal并设置为socket的分帧协议。分帧是这样的，由于发送方（服务
器）发送的消息是一个不间断的流，因此我们需要从这个流里面分析出一个个帧（代表
某种逻辑意义）出来，换句话说，就是我们要找到每一帧的起始和终止位置并提取出来
。举个例子：服务器发送两帧数据过来,AB和CD，客户端接收的情况就很复杂，有可能是
先收到A，然后收到BCD；也有可能是先收到A，然后收到B，最后收到CD；等等等等复杂
情况，显然，我们的逻辑要求AB是一个逻辑单位（帧），CD是一个逻辑单位，如果按照
每次收到的信息作为一帧，则无法处理业务逻辑。

接下来看看LVProtocal，这个类继承了Protocal，它实现的分帧逻辑是这样的：先读取
两个字节，组合为一个两字节整数x，接下来会读入x字节的数据，如果当前数据较少，
则有多少读入多少；否则读入x字节。读完整x字节后，则一帧数据读完，之后SocketListerner
的OnMessage将会被调用。

如果你想要使用不同的分帧逻辑，则需要自己实现一个Protocal。建议使用LVProtocal
，因为这个只是分帧的逻辑，分完帧以后，也就是分出来的x字节，它的内容，就是我们
的业务协议的内容了，可能是一个字符串，或者按照某种特定顺序组织的（比如先两个
字节的整型代表协议号，然后八个字节是一个长整形表示用户的id，然后。。。），当
然也有可能这x个字节是经过某个加密算法加密过的，那么需要解密后再按照业务协议来进
行解析。

5，连接服务器，需要指定服务器ip地址和端口，如果连接成功，则SocketListerner的
OnOpen函数会被调用，如果失败OnClose将被调用。

##关于cocosocket-server

server部分是一个java程序，其底层依赖于netty，netty是一个真正高性能的通信框架
，cocosocket-server隐藏了许多netty的复杂性（netty不仅仅用于tcp/ip协议的通信）
，如果跟cocosocket-client作对比你会发现，他们的api很相似，这在某种程度上简化
了理解这两者的难度，你可以从server端或client端入手，这将非常有助于理解另一端
。

##关于cocosocket4unity

这个程序是为unity 3D准备的，api和c++版本的客户端很像，实现的是相同的功能，适用
于unity 3D网游。里面我嵌入了litjson的代码用并修复了它的一个bug：utf8字符串乱码
，如果你刚好需要json解析，那么建议使用，否则请删去litjson的代码即可。


## 结语

ok，那么我想你现在应该对cocosocket有个大概了解了。enjoy it.

	
	
