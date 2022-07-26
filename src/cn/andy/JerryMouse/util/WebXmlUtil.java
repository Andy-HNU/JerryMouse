package cn.andy.JerryMouse.util;

import cn.andy.JerryMouse.catalina.Connector;
import cn.andy.JerryMouse.catalina.Context;
import cn.andy.JerryMouse.catalina.Service;
import cn.hutool.core.io.FileUtil;
import cn.andy.JerryMouse.util.Constant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.lang.model.util.ElementScanner6;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebXmlUtil {
    private static Map<String,String> mimeTypeMap = new HashMap<>();
    public static String getWelComeFile(Context context){
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements elements = d.getElementsByTag("welcome-file");
        for (Element e : elements){
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(), welcomeFileName);
            if (f.exists()){
                return f.getName();
            }
        }
        return "index.html";
    }

    private static void initMimeTypeMap(){
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements elements = d.select("mime-mapping");
        for (Element e : elements){
            String mimeType = e.select("extension").first().text();
            String fileType = e.select("mime-type").first().text();
            mimeTypeMap.put(mimeType, fileType);
        }
    }

    public static synchronized String getMimeType(String fileName){
        if (mimeTypeMap.isEmpty()){
            initMimeTypeMap();
        }
        String fileType = mimeTypeMap.get(fileName);
        if (fileType == null){
            return "text/html";
        }
        return fileType;
    }



}
