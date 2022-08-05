package cn.andy.JerryMouse.servlets;


import cn.andy.JerryMouse.catalina.Context;
import cn.andy.JerryMouse.http.Request;
import cn.andy.JerryMouse.http.Response;
import cn.andy.JerryMouse.util.Constant;
import cn.hutool.core.util.ReflectUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*  单例模式

    意图：保证一个类仅有一个实例，并提供一个访问它的全局访问点。

    主要解决：一个全局使用的类频繁地创建与销毁。

    何时使用：当您想控制实例数目，节省系统资源的时候。

    如何解决：判断系统是否已经有这个单例，如果有则返回，如果没有则创建。

    关键代码：构造函数是私有的。

    1、单例类只能有一个实例。
    2、单例类必须自己创建自己的唯一实例。
    3、单例类必须给所有其他对象提供这一实例。
*/
public class InvokerServlet extends HttpServlet {
    private static InvokerServlet instance = new InvokerServlet();
    private InvokerServlet() {}
    public static InvokerServlet getInstance() {
        return instance;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;
        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName = context.getServletClassNameByUrl(uri);

        try {
            System.out.println("servletClassName:" + servletClassName);
            Class<?> servletClass = context.getWebappClassLoader().loadClass(servletClassName);
            System.out.println("servletClass:" + servletClass);
            System.out.println("servletClassLoader:" + servletClass.getClassLoader());
            Object servlet = context.getServlet(servletClass);
            ReflectUtil.invoke(servlet, "service",request,response);
            response.setStatus(Constant.CODE_200);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}

/*
* 解释一下 为什么HelloServlet 实现了 doGet方法，这里却反射的是service？ 怎么做到根据request的method
* 来判断是doGet 还是 doPost？
* 1. HelloServlet 中 extends 了 HttpServlet， 这里的反射会到suoer中寻找service方法
* 2. HttpServlet 的 service 方法
*   @Override
public void service(ServletRequest req, ServletResponse res)
    throws ServletException, IOException {

    HttpServletRequest  request;
    HttpServletResponse response;

    try {
        request = (HttpServletRequest) req;
        response = (HttpServletResponse) res;
    } catch (ClassCastException e) {
        throw new ServletException("non-HTTP request or response");
    }
    service(request, response);
}

protected void service(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    String method = req.getMethod();
    if (method.equals(METHOD_GET)) { // 如果是get请求，则调用doGet方法
        long lastModified = getLastModified(req);
        if (lastModified == -1) {
            // servlet doesn't support if-modified-since, no reason
            // to go through further expensive logic
            doGet(req, resp);
        } else {
            long ifModifiedSince;
            try {
                ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
            } catch (IllegalArgumentException iae) {
                // Invalid date header - proceed as if none was set
                ifModifiedSince = -1;
            }
            if (ifModifiedSince < (lastModified / 1000 * 1000)) {
                // If the servlet mod time is later, call doGet()
                // Round down to the nearest second for a proper compare
                // A ifModifiedSince of -1 will always be less
                maybeSetLastModified(resp, lastModified);
                doGet(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
        }

    } else if (method.equals(METHOD_HEAD)) { // 如果是head请求，则调用doHead方法
        long lastModified = getLastModified(req);
        maybeSetLastModified(resp, lastModified);
        doHead(req, resp);

    } else if (method.equals(METHOD_POST)) { // 如果是post请求，则调用doPost方法
        doPost(req, resp);

    } else if (method.equals(METHOD_PUT)) {  // 如果是put请求，则调用doPut方法
        doPut(req, resp);

    } else if (method.equals(METHOD_DELETE)) {
        doDelete(req, resp);

    } else if (method.equals(METHOD_OPTIONS)) {
        doOptions(req,resp);

    } else if (method.equals(METHOD_TRACE)) {
        doTrace(req,resp);

    } else {
        String errMsg = lStrings.getString("http.method_not_implemented");
        Object[] errArgs = new Object[1];
        errArgs[0] = method;
        errMsg = MessageFormat.format(errMsg, errArgs);

        resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
    }
}
* 3. HttpServlet 的 service 已经分好了method对应的方法
* 4. 实现这些的基础是Request 提供了 @Override getMethod() 方法，返回的是请求的方法名
* */
