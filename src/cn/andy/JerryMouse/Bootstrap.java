package cn.andy.JerryMouse;
import cn.andy.JerryMouse.catalina.Server;
import cn.andy.JerryMouse.classloader.CommonClassLoader;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.concurrent.ThreadPoolExecutor;


public class Bootstrap {
    //public static Map<String, Context> contextMap = new HashMap<>(); // 解析Xml / 扫描路径 获得的 path - Context映射
    public static void main(String[] args) throws Exception {

        CommonClassLoader commonClassLoader = new CommonClassLoader();

        Thread.currentThread().setContextClassLoader(commonClassLoader);

        String serverClassName = "cn.andy.JerryMouse.catalina.Server";

        Class<?> serverClass = commonClassLoader.loadClass(serverClassName);

        Object server = serverClass.newInstance();

        Method m = serverClass.getMethod("start");

        m.invoke(server);
    }
}