package org.ngame.socket.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * logging格式
 *
 * @author beykery
 */
public final class LoggingFormatter extends Formatter
{

	private final Date dat = new Date();
//    private final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.CHINESE);
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LoggingFormatter()
	{
	}

	/**
	 * 格式化
	 *
	 * @param record
	 * @return
	 */
	@Override
	public synchronized final String format(final LogRecord record)
	{
		dat.setTime(record.getMillis());
//        String source;
//        if (record.getSourceClassName() != null)
//        {
//            source = record.getSourceClassName();
//            if (record.getSourceMethodName() != null)
//            {
//                source += " <" + record.getSourceMethodName() + ">";
//            }
//        } else
//        {
//            source = record.getLoggerName();
//        }
		String message = record.getMessage();
		String throwable = "";
		if (record.getThrown() != null)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = sw.toString();
		}
//        return (df.format(dat) + " " + source + " " + record.getLevel().getLocalizedName() + " " + message + " " + throwable + "\n");
		if (message.startsWith("["))
		{
			return "[\"" + df.format(dat) + "\"," + message + "\"," + record.getSourceClassName() + "-->" + record.getSourceMethodName() + "]\n";
		} else
		{
			return "[\"" + df.format(dat) + "\",\"" + message + "\"," + record.getSourceClassName() + "-->" + record.getSourceMethodName() + "]\n";
		}
	}
}
