package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.classloader.CommonClassLoader;
import cn.andy.JerryMouse.classloader.WebappClassLoader;
import cn.andy.JerryMouse.exception.WebConfigDuplicatedException;
import cn.andy.JerryMouse.util.ContextXmlUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;


public class Context {
    // path: /FolderName ; docBase: FolderName的绝对路径 ---> 在BootStrap内进行构建
    private String path;
    private String docBase;

    private WebappClassLoader webappClassLoader;

    private File contextWebXmlFile;

    private Map<String,String> url_servletClassName;
    private Map<String,String> url_servletName;
    private Map<String,String> servletName_className;
    private Map<String,String> className_servletName;

    public Context(String path, String docBase){
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;

        this.contextWebXmlFile = new File(docBase, ContextXmlUtil.getWatchedResource());

//        System.out.println("contextWebXmlFile: " + contextWebXmlFile.getAbsolutePath());
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
        this.url_servletClassName = new HashMap<>();
        /*
         * 提问：这里的commonClassLoader 是 通过 CurrentThread.getContextClassLoader() 获得的，
         *
         * BootStrap中设置的是 Thread.currentThread().setContextClassLoader(commonClassLoader)，
         * 但是 在 service 中 又增加了 ThreadPool 并开启了 Host数量个的线程，
         * 所以这里的commonClassLoader 能否拿到 BootStrap中的commonClassLoader？
         *
         * 证明：
         *   如若将webapp中的sevlet-api删去，配置到该tomcat中，如可正常运行
         *   则证明commonClassLoader是可以拿到的
         *   反之，则证明commonClassLoader是不可以拿到的
         * */
        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(this.docBase,commonClassLoader);

        deploy();

        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deployment of web application {} has finish",this.path);


    }

    private void init(){
        if(!contextWebXmlFile.exists()){
            return;
        }
        try {
            checkDuplicated();
        } catch (WebConfigDuplicatedException e) {
            e.printStackTrace();
            return;
        }
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document document = Jsoup.parse(xml);
        parseServletMapping(document);
    }

    private void deploy(){
        TimeInterval timeInterval = DateUtil.timer();
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        init();
        LogFactory.get().info("Deployment of web application {} has finish in {} ms",this.path, timeInterval.intervalMs());

    }

    public String getPath(){return this.path;}

    public String getDocBase(){return this.docBase;}

    public void setPath(String path){this.path = path;}

    public void setDocBase(String docBase){this.docBase = docBase;}

    public WebappClassLoader getWebappClassLoader(){return this.webappClassLoader;}

    private void parseServletMapping(Document document){
        // 解析web.xml文件，获取servlet的映射信息
        Elements mappingurlElments = document.select("servlet-mapping url-pattern");
        for (Element mappingUrlElement : mappingurlElments) {
            String urlPattern = mappingUrlElement.text();
            String servletName = mappingUrlElement.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern,servletName);
        }
        Elements servletNameElements = document.select("servlet servlet-name");
        for(Element servletNameElement : servletNameElements){
            String servletName = servletNameElement.text();
            String servletClass = servletNameElement.parent().select("servlet-class").first().text();
            servletName_className.put(servletName,servletClass);
            className_servletName.put(servletClass,servletName);
        }

        Set<String> urls = url_servletName.keySet();
        for(String url : urls){
            String servletName = url_servletName.get(url);
            String servletClass = servletName_className.get(servletName);
            url_servletClassName.put(url,servletClass);
        }
    }

    private void checkDuplicated(Document document,String mapping,String desc) throws WebConfigDuplicatedException{
        Elements elements = document.select(mapping);
        List<String> contents = new ArrayList<>();
        for (Element element : elements) {
            contents.add(element.text());
        }
        Collections.sort(contents);
        for (int i = 0; i < contents.size()-1; i++) {
            if(contents.get(i).equals(contents.get(i+1))){
                throw new WebConfigDuplicatedException(desc);
            }
        }
    }

    private void checkDuplicated() throws WebConfigDuplicatedException{
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document document = Jsoup.parse(xml);
        checkDuplicated(document,"servlet-mapping url-pattern","servlet url 重复");
        checkDuplicated(document,"servlet servlet-name","servlet name 重复");
        checkDuplicated(document,"servlet servlet-class","servlet name 重复");
    }

    public String getServletClassNameByUrl(String url){
        return url_servletClassName.get(url);
    }

}
