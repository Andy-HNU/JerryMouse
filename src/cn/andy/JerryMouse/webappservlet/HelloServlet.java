package cn.andy.JerryMouse.webappservlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class HelloServlet extends HttpServlet {
    public HelloServlet() {
        System.out.println(this+" 构造方法 created");
    }
    public void init(ServletConfig config)  {
        String author = config.getInitParameter("author");
        String site = config.getInitParameter("site");

        System.out.println("获取到参数author" + author);
        System.out.println("获取到参数site" + site);
        System.out.println(this+" init() author="+author+" site="+site);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println(this+" doGet()");
            response.getWriter().println("Hello, World!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void destory(){
        System.out.println(this+" 被销毁");
    }

}
