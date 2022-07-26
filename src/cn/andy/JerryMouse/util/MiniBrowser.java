/*
* Mini Browser的重点在于 public static byte[] getHttpBytes(String url, boolean gzip)方法
* Map 和 Set 构造了 一个标准的 GET 请求头
* 通过 Socket client 的 connect, getInputStream, getOutputStram 进行 Http请求(链接，发送请求，接收返回)
* 其余的各个方法是对getHttpBytes的返回值的封装
* */
package cn.andy.JerryMouse.util;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MiniBrowser {
    public static byte[] getContentBytes(String url){
        return getContentBytes(url,false);
    }
    public static byte[] getContentBytes(String url, boolean gzip){
        byte[] response = getHttpBytes(url,gzip);
        byte[] doubleReturn = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);

        int pos = -1;
        for (int i = 0; i < response.length - doubleReturn.length; i++){
            byte[] temp = Arrays.copyOfRange(response, i, i+doubleReturn.length);

            if (Arrays.equals(temp,doubleReturn)){
                pos = i;
                break;
            }
        }
        if (pos == -1){
            return null;
        }
        pos += doubleReturn.length;

        byte[] result = Arrays.copyOfRange(response,pos,response.length);
        return result;
    }

    public static byte[] ReadBytes(InputStream inputStream,boolean fully) throws IOException {
        int buffer_size = 1024;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[buffer_size];
        while (true) {
            int length = inputStream.read(buffer);
            if (length == -1){
                break;
            }
            byteArrayOutputStream.write(buffer,0,length);
            if (!fully && length != buffer_size){
                break;
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    public static byte[] getHttpBytes(String url, boolean gzip) {
        byte[] result = null;
        try {
            URL u = new URL(url); // 创建一个URL对象
            Socket client = new Socket(); // 创建一个Socket对象

            int port = u.getPort(); // 获取端口号

            if (port == -1){ // 如果端口号为-1，则使用默认端口号
                port = 80;
            }
            InetSocketAddress isa = new InetSocketAddress(u.getHost(), port); // 创建一个InetSocketAddress对象
            System.out.println("URL"+ url +" Host: "+u.getHost()+ " Port:"+port);
            client.connect(isa,100); // 连接服务器

            Map<String,String> requestHeaders = new HashMap<>();

            requestHeaders.put("Host",u.getHost()+":"+port);
            requestHeaders.put("Accept","text/html");
            requestHeaders.put("Connection","close");
            requestHeaders.put("User-Agent","how2j mini brower / java1.8");

            if (gzip){
                requestHeaders.put("Accept-Encoding","gzip");
            }

            String path = u.getPath();
            if (path.length() == 0){
                path = "/";
            }

            String firstline = "GET " + path + " HTTP/1.1\r\n"; // GET path HTTP/1.1 注意空格 否则接受不到GET请求

            StringBuffer httpRequestString = new StringBuffer();
            httpRequestString.append(firstline);
            Set<String> headers = requestHeaders.keySet(); // 返回一个 Map的key视图
            /*
            * 生成诸如: key1:value1
            *          key2:value2
            * 的String
            * */
            for (String header : headers){
                String headerLine = header +":"+requestHeaders.get(header) + "\r\n";
                httpRequestString.append(headerLine);
            }
            // 在这里通过client的输入/输出 流 完成了 GET 的 request 和 response
            PrintWriter pWrtier = new PrintWriter(client.getOutputStream(),true);
            pWrtier.println(httpRequestString);
            InputStream is = client.getInputStream();

            // 处理inputStream接收的信息
            result = ReadBytes(is,true);
            /*
            * 当 fully 等于 true的时候，即便读取到的数据没有buffer_size 那么长，也会继续读取。
              * 为什么要这么改动呢？ 主要是为了测试 etf.pdf 这个文件， 这个文件比较大
              * 那么在传输过程中，可能就不会一次传输 1024个字节，有时候会小于这个字节数，如果读取到的
              * 数据小于这个字节就结束的话，那么读取到的文件就是不完整的。
            * */

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    public static String getHttpString(String url){
        return getHttpString(url,false);
    }
    public static String getHttpString(String url, boolean gzip){
        byte[] bytes = getHttpBytes(url, gzip);
        return new String(bytes).trim();
    }

    public static String getContentString(String url){
        return getContentString(url,false);
    }
    public static String getContentString(String url, boolean gzip){
        byte[] bytes = getContentBytes(url,gzip);
        if (null == bytes){
            return null;
        }
        try {
            return new String(bytes,"utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String url = "http://static.how2j.cn/diytomcat.html";
        String contentString = getContentString(url);
        System.out.println(contentString);
        String httpString = getHttpString(url);
        System.out.println(httpString);
    }
}
