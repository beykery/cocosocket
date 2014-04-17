/**
 * 测试服务器
 */
package org.cocosocket.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.cocosocket.protocal.LVProtocal;

/**
 *
 * @author beykery
 */
public class TestApp
{

    private static final Logger LOG = Logger.getLogger(TestApp.class.getName());

    /**
     * 初始化系统配置
     */
    static
    {
        try
        {
            LogManager.getLogManager().readConfiguration(new FileInputStream(new File("./logging.properties")));
            Properties p = new Properties();//系统属性
            p.load(new InputStreamReader(new FileInputStream(new File("./system.properties")), "UTF-8"));
            Set<Object> set = p.keySet();
            for (Object key : set)
            {
                System.getProperties().setProperty(key.toString(), p.getProperty(key.toString()));
            }
        } catch (IOException | SecurityException | NumberFormatException e)
        {
            LOG.log(Level.WARNING, "读取系统配置文件失败");
        }
    }

    public static void main(String... args) throws InterruptedException
    {
        EchoServer server = new EchoServer(new InetSocketAddress(2000));
        server.setProtocal(LVProtocal.class);
        server.start();
        LOG.info("服务器启动");
    }

}
