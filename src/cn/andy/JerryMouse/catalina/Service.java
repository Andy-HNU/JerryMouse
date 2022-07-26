package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.util.ServerXmlUtil;
import cn.andy.JerryMouse.util.WebXmlUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import org.apache.log4j.lf5.viewer.LogFactor5Dialog;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Service {
    private String serviceName;
    private Server server;
    private Engine engine;

    private List<Connector> connectors;
    public Service(){
        this.serviceName = ServerXmlUtil.getServiceName();
        this.engine = new Engine(this);
        this.connectors = ServerXmlUtil.getConnectors(this);
    }
    public Service(Server server){
        this.serviceName = ServerXmlUtil.getServiceName();
        this.server = server;
        this.engine = new Engine(this);
        this.connectors = ServerXmlUtil.getConnectors(this);
        System.out.println("Service.constructor"+this.connectors.size());
    }
    public Engine getEngine(){return this.engine;}
    public Server getServer(){return this.server;}

    public void start(){
        init();
    }

    private void init(){
        TimeInterval timeInterval = DateUtil.timer();
        for (Connector connector : connectors){
            connector.init();
        }
        LogFactory.get().info("Initializing Service [{}] in {}ms",serviceName,timeInterval.intervalMs());
        final int CORE_POOL_SIZE = 5;
        final int MAX_POOL_SIZE = 10;
        final int QUEUE_CAPACITY = 100;
        final Long KEEP_ALIVE_TIME = 1L;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                java.util.concurrent.TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
        for (Connector connector : connectors) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    connector.start();
                    System.out.println("port: "+connector.port);
                }
            });
        }
    }

}
