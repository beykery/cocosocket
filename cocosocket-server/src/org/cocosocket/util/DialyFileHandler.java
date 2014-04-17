/**
 * 每天生产一个日志文件
 */
package org.cocosocket.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * @author beykery
 */
public class DialyFileHandler extends StreamHandler
{

    public final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private MeteredStream meter;
    private RollingCalendar rc;
    private long nextCheck;

    private class MeteredStream extends OutputStream
    {

        OutputStream out;
        int written;

        MeteredStream(OutputStream out, int written)
        {
            this.out = out;
            this.written = written;
        }

        @Override
        public void write(int b) throws IOException
        {
            out.write(b);
            written++;
        }

        @Override
        public void write(byte buff[]) throws IOException
        {
            out.write(buff);
            written += buff.length;
        }

        @Override
        public void write(byte buff[], int off, int len) throws IOException
        {
            out.write(buff, off, len);
            written += len;
        }

        @Override
        public void flush() throws IOException
        {
            out.flush();
        }

        @Override
        public void close() throws IOException
        {
            out.close();
        }
    }

    /**
     * 计算下一个检查时间
     */
    private class RollingCalendar extends GregorianCalendar
    {

        RollingCalendar()
        {
            super();
        }

        RollingCalendar(TimeZone tz, Locale locale)
        {
            super(tz, locale);
        }

        public long getNextCheckMillis(Date now)
        {
            return getNextCheckDate(now).getTime();
        }

        public Date getNextCheckDate(Date now)
        {
            this.setTime(now);
            this.set(Calendar.HOUR_OF_DAY, 0);
            this.set(Calendar.MINUTE, 0);
            this.set(Calendar.SECOND, 0);
            this.set(Calendar.MILLISECOND, 0);
            this.add(Calendar.DATE, 1);
            return getTime();
        }
    }

    /**
     * 构造
     */
    public DialyFileHandler()
    {
        super();
        this.rc = new RollingCalendar();
        String serverName = "ngame" + "_" + df.format(new Date()) + ".log";
        File log = new File("./logging/" + serverName);
        try
        {
            if (!log.exists())
            {
                log.getParentFile().mkdirs();
                log.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(log, true);
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            meter = new MeteredStream(bout, 0);
            this.setOutputStream(meter);
            this.nextCheck = rc.getNextCheckMillis(new Date());
        } catch (IOException | SecurityException e)
        {
            e.printStackTrace();
            System.out.println("无法初始化log配置");
        }
    }

    /**
     * 写入日志
     *
     * @param record
     */
    @Override
    public synchronized void publish(LogRecord record)
    {
        if (!isLoggable(record))
        {
            return;
        }
        super.publish(record);
        flush();
        if (System.currentTimeMillis() > this.nextCheck)
        {
            try
            {
                String serverName = "登录注册";
                this.meter.close();
                File log = new File("./logging/" + serverName + "_" + df.format(new Date()) + ".log");
                FileOutputStream fout = new FileOutputStream(log, true);
                BufferedOutputStream bout = new BufferedOutputStream(fout);
                meter = new MeteredStream(bout, 0);
                this.setOutputStream(meter);
                this.nextCheck = rc.getNextCheckMillis(new Date());
            } catch (IOException | SecurityException e)
            {
                System.out.println("计算下一个log文件失败");
            }
        }
    }
}
