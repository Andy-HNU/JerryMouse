package cn.andy.JerryMouse.util;

import cn.andy.JerryMouse.catalina.*;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.List;


public class ServerXmlUtil {
    public static List<Context> getContexts(Host host) {
        List<Context> res = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml); // 获得setver.xml 并生成对象

        Elements es = d.select("Context");// 获得<Context>
        for (Element e : es){
            String Path = e.attr("path"); // 对<Context>检索
            String DocBase = e.attr("docBase");// 对<Context>检索
            boolean roadable = Convert.toBool(e.attr("reloadable"), true);// 对<Context>检索
            Context context = new Context(Path, DocBase,host,roadable);
            res.add(context);
        }
        return res;
    }

    public static String getHostName(){ // 获得hostName
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile); // 读取server.xml
        Document d = Jsoup.parse(xml); // 获得setver.xml 并生成对象

        Element host = d.select("Host").first(); // 获得<Host>
        return host.attr("name"); // 对<Host>检索
    }

    public static String getEngineDefaultHost(){
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element engine = d.select("Engine").first();
        return engine.attr("defaultHost");
    }

    public static String getServiceName(){
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element service = d.select("Service").first();
        return service.attr("name");
    }

    public static List<Host> getHosts(Engine engine){
        List<Host> res = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Elements hosts = d.select("Host");
        for (Element e : hosts){
            res.add(new Host(e.attr("name"),engine));
        }

        return res;
    }
    public static List<Connector> getConnectors(Service service){
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements elements = d.select("Connector");
        List<Connector> connectors = new ArrayList<>();
        for (Element e : elements){
            Connector connector = new Connector(service);
            connector.setPort(Integer.parseInt(e.attr("port")));
            connectors.add(connector);
        }
        return connectors;
    }

}
