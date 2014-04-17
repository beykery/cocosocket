package org.cocosocket.protocal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cocosocket.exeptions.InvalidDataException;
import org.cocosocket.exeptions.LimitExedeedException;

/**
 * 字节流协议的实现
 *
 * @author beykery
 */
public class LVProtocal extends Protocal
{

    private static final Logger LOG = Logger.getLogger(LVProtocal.class.getName());
    private ByteBuf incompleteframe;//尚未完成的帧
    private byte h, l;//高低字节用来记录长度
    private byte status;//当前状态
    private static final byte STATUS_H = 0;
    private static final byte STATUS_L = 1;
    private static final byte STATUS_C = 2;
    private static int maxFrameSize = 2048;//最大帧长度

    static
    {
        try
        {
            maxFrameSize = Integer.parseInt(System.getProperty("game.socket.protocal.maxFrameSize"));
        } catch (Exception e)
        {
            LOG.log(Level.WARNING, "socket帧长度设置错误，将使用默认值");
        }
    }

    /**
     * 构造
     */
    public LVProtocal()
    {
    }

    @Override
    public ByteBuf translateFrame(ByteBuf readBuffer) throws LimitExedeedException, InvalidDataException
    {
        while (readBuffer.isReadable())
        {
            switch (status)
            {
                case STATUS_H:
                    h = readBuffer.readByte();
                    status = STATUS_L;
                    break;
                case STATUS_L:
                    l = readBuffer.readByte();
                    final int blen = (0x0000ff00 & (h << 8)) | (0x000000ff & l);
                    if (context != null)
                    {
                        if (blen <= 0 || blen > maxFrameSize)
                        {
                            throw new LimitExedeedException("帧长度非法:" + h + "/" + l + ":" + blen);
                        }
                    }
                    incompleteframe = PooledByteBufAllocator.DEFAULT.buffer(blen + 8 + 2);
                    incompleteframe.writeShort(blen);
                    status = STATUS_C;
                    break;
                case STATUS_C:
                    int len = incompleteframe.writableBytes() - 8;
                    len = len < readBuffer.readableBytes() ? len : readBuffer.readableBytes();
                    //incompleteframe.writeBytes(readBuffer, len);
                    if (readBuffer.hasMemoryAddress())
                    {
                        PlatformDependent.copyMemory(readBuffer.memoryAddress() + readBuffer.readerIndex(), incompleteframe.memoryAddress() + incompleteframe.writerIndex(), len);
                    } else if (readBuffer.hasArray())
                    {
                        PlatformDependent.copyMemory(readBuffer.array(), readBuffer.arrayOffset() + readBuffer.readerIndex(), incompleteframe.memoryAddress() + incompleteframe.writerIndex(), len);
                    }
                    incompleteframe.writerIndex(incompleteframe.writerIndex() + len);
                    readBuffer.readerIndex(readBuffer.readerIndex() + len);
                    if ((incompleteframe.writableBytes() - 8) <= 0)
                    {
                        status = STATUS_H;
                        return incompleteframe;
                    }
                    break;
            }
        }
        return null;
    }
}
