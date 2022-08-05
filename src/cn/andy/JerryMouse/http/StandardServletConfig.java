package cn.andy.JerryMouse.http;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class StandardServletConfig implements ServletConfig {

    private ServletContext servletContext;
    private Map<String, String> initParamMap;
    private String servletName;

    public StandardServletConfig(ServletContext servletContext, Map<String, String> initParamMap, String servletName) {
        this.servletContext = servletContext;
        this.initParamMap = initParamMap;
        this.servletName = servletName;
        if (null == this.initParamMap){
            this.initParamMap = new HashMap<>();
        }
    }

    @Override
    public String getServletName() {
        return this.servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return this.initParamMap.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.initParamMap.keySet());
    }
}
