package cn.andy.JerryMouse.http;

import cn.andy.JerryMouse.catalina.Context;
import cn.andy.JerryMouse.servlets.BaseServletContext;
import org.jsoup.Connection;

import java.io.File;
import java.util.*;

public class AplicationContext extends BaseServletContext {
    private Map<String, Object> attributeMap;
    private Context context;

    public AplicationContext(Context context) {
        this.attributeMap = new HashMap<>();
        this.context = context;
    }
    public void removeAttribute(String name) {
        attributeMap.remove(name);
    }
    public void setAttribute(String name, Object value) {
        attributeMap.put(name, value);
    }
    public Object getAttribute(String name) {
        return attributeMap.get(name);
    }
    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributeMap.keySet();
        return Collections.enumeration(keys);
    }

    public String getRealPath(String path) {
        return new File(context.getDocBase(),path).getAbsolutePath();
    }
}
