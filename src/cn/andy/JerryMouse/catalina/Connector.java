package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.http.Request;
import cn.andy.JerryMouse.http.Response;
import cn.andy.JerryMouse.util.Constant;
import cn.andy.JerryMouse.util.WebXmlUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Connector{
    public int port;
    private Service service;
    public Connector(Service service){
        this.service = service;
    }
    public void setPort(int port){
        this.port = port;
    }

    public void init(){
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]",port);
    }

    public void start(){
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]",port);
        func();
    }
    private void func(){
        try {
            ServerSocket ss = new ServerSocket(this.port);
            while (true) {
                Socket s = ss.accept();
                Request request = new Request(s, service);
                System.out.println("RequestString:" + request.getRequestString());

                Response response = new Response();

                new HttpProcessor().execute(s,request,response);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



}
