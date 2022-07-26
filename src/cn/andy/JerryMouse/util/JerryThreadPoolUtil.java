package cn.andy.JerryMouse.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JerryThreadPoolUtil {
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20,100,60, TimeUnit.SECONDS,new LinkedBlockingDeque<>(10));

    public static void run(Runnable r){
        threadPoolExecutor.execute(r);
    }

}
