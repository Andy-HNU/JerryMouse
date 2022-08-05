package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.util.Constant;
import cn.andy.JerryMouse.util.ServerXmlUtil;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host {// 返回一个通过ContextMap 查询 path 返回 context
    private Map<String, Context> contextMap; // 解析Xml / 扫描路径 获得的 path - Context映射
    private String HostName;
    private Engine engine;
    public Host() {
        this.contextMap = new HashMap<>();
        this.HostName = ServerXmlUtil.getHostName();

        this.scanContextsInServerXML();
        this.scanContextsOnWebAppsFolder();
    }

    public Host(String hostName,Engine engine){
        this.contextMap = new HashMap<>();
        this.HostName = hostName;
        this.engine = engine;

        this.scanContextsOnWebAppsFolder();
        this.scanContextsInServerXML();
    }
    private void scanContextsOnWebAppsFolder(){
        File[] files = Constant.webappsFolder.listFiles();
        for (File file : files){
            if (file.isDirectory()){ //是文件夹
                this.loadContext(file);
            }
        }
    }
    private void loadContext(File folder){
        // 我觉得不妥, 若path 为 /FuncA/A-1/a.html,这样会丢失/A-1 部分 ----> 妥了, request里去除的只是/path前缀, uri此时为 /A-1/a.html
        // path: /FolderName ; docBase: FolderName的绝对路径
        String path = folder.getName();
        if ("ROOT".equals(path)){
            path = "/";
        }
        else {
            path = "/" + path;
        }
        String docBase = folder.getAbsolutePath();
        Context context = new Context(path,docBase,this,true);
        this.contextMap.put(context.getPath(),context);
    }

    public void reload(Context context){
        LogFactory.get().info("Reloading Context with name [{}] has started",context.getPath());
        String path = context.getPath();
        String docBase = context.getDocBase();
        boolean reloadable = context.getReloadable();
        context.stop();
        contextMap.remove(path);
        Context newContext = new Context(path,docBase,this,reloadable);
        contextMap.put(path,newContext);
        LogFactory.get().info("Reloading Context with name [{}] has finished",context.getPath());
    }
    private void scanContextsInServerXML(){
        List<Context> contexts = ServerXmlUtil.getContexts(this);
        for (Context context : contexts){
            System.out.println("scanContextInServerXML path:" + context.getPath());
            this.contextMap.put(context.getPath(),context);
        }
    }

    public String getHostName(){
        return this.HostName;
    }
    public Context getContext(String path){
        return this.contextMap.get(path);
    }

}
