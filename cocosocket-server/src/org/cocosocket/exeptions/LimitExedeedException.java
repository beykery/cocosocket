package org.cocosocket.exeptions;

/**
 * 数据长度非法
 *
 * @author Administrator
 */
public class LimitExedeedException extends InvalidDataException
{

    public LimitExedeedException(String message)
    {
        super(message);
    }
}
