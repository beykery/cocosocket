/**
 * http请求
 */
package org.ngame.socket.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Beykery
 */
public class TestHttpClient
{

    public static void main(String[] args)
    {
        testJDK();
       // testNetty();
    }

    /**
     * jdk connection测试
     */
    private static void testJDK()
    {
        try
        {
            URL url = new URL("http://localhost:3210/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(0);
            conn.setReadTimeout(0);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Keep-Alive", "3000000");
            conn.connect();
            OutputStream o = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(o);
            String content = "hi";
            dos.writeShort(2);
            dos.write(content.getBytes());
            int code = conn.getResponseCode();
            if (code == 200)
            {
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                short l = dis.readShort();
                byte[] c = new byte[l];
                dis.readFully(c);
                System.out.println(new String(c));
            }

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(0);
            conn.setReadTimeout(0);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Keep-Alive", "3000000");
            conn.connect();
            o = conn.getOutputStream();
            dos = new DataOutputStream(o);
            content = "hi";
            dos.writeShort(2);
            dos.write(content.getBytes());
            dos.flush();
            code = conn.getResponseCode();
            if (code == 200)
            {
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                short l = dis.readShort();
                byte[] c = new byte[l];
                dis.readFully(c);
                System.out.println(new String(c));
            }
            conn.disconnect();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("异常");
        }
    }
}
