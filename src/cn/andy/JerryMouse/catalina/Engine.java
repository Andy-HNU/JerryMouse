package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.util.ServerXmlUtil;

import java.util.List;

public class Engine {
    private final String defaultHost;
    private final List<Host> hostList;
    private Service service;
    public Engine(){
        this.defaultHost = ServerXmlUtil.getEngineDefaultHost();
        this.hostList = ServerXmlUtil.getHosts(this);
        CheckDefaultHost();
    }

    public Engine(Service service){
        this.service = service;
        this.defaultHost = ServerXmlUtil.getEngineDefaultHost();
        this.hostList = ServerXmlUtil.getHosts(this);
        CheckDefaultHost();
    }

    private void CheckDefaultHost(){
        if (this.defaultHost == null){
            throw new RuntimeException("No default host defined in server.xml");
        }
    }

    public Host getDefaultHost(){
        for (Host host : hostList){
            if (host.getHostName().equals(defaultHost)){
                return host;
            }
        }
        return null;
    }
}
