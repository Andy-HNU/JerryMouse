package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.http.Request;
import cn.andy.JerryMouse.http.Response;
import cn.andy.JerryMouse.util.Constant;
import cn.andy.JerryMouse.util.JerryThreadPoolUtil;
import cn.andy.JerryMouse.util.WebXmlUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Server {
    private Service service;
    public Server(){
        this.service = new Service(this);
    }
    public void start(){
        TimeInterval timeInterval = DateUtil.timer();
        logJVM();
        service.start();
        LogFactory.get().info("Server startup in {} ms",timeInterval.intervalMs());
    }

    private void logJVM(){
        Map<String,String> infos = new LinkedHashMap<>();
        infos.put("Server Version","JerryMouse1.0.1");
        infos.put("Server built","2022-5-31");
        infos.put("Server number","1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version\t",SystemUtil.get("os.version"));
        infos.put("Architecture",SystemUtil.get("os.arch"));
        infos.put("Java Home",SystemUtil.get("java.home"));
        infos.put("JVM version",SystemUtil.get("java.runtime.version"));
        infos.put("JVM vendor","java.vm.specification.vendor");
        Set<String> Keys = infos.keySet();
        for (String key: Keys){
            LogFactory.get().info(key + ":\t\t" + infos.get(key));
        }
    }

}
