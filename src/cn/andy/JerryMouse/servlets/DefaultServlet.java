package cn.andy.JerryMouse.servlets;

import cn.andy.JerryMouse.catalina.Context;
import cn.andy.JerryMouse.http.Request;
import cn.andy.JerryMouse.http.Response;
import cn.andy.JerryMouse.util.Constant;
import cn.andy.JerryMouse.util.WebXmlUtil;
import cn.andy.JerryMouse.webappservlet.HelloServlet;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

public class DefaultServlet extends HttpServlet {
   private static DefaultServlet instance = new DefaultServlet();
    private DefaultServlet() {}

    public static DefaultServlet getInstance() {
        return instance;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    throws IOException, ServletException {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        Context context = request.getContext();

        String uri = request.getUri();

        if("/hello-HardCode-".equals(uri)){

            HelloServlet helloServlet = new HelloServlet();
            helloServlet.doGet(request, response);
            response.setStatus(Constant.CODE_200);
            return;

        }

        if("/500.html".equals(uri)){
            throw new RuntimeException("this is a deliberately created exception");
        }
        if ("/".equals(uri)) {
            uri = WebXmlUtil.getWelComeFile(request.getContext());
        }

        String filename = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(context.getDocBase(), filename);

        if (file.exists()) { //文件存在 - 发送文件内容
            byte [] bytes = FileUtil.readBytes(file);
            response.setContentBodyBytes(bytes);

            // 设置响应头 ContentType - 文件类型
            String mimeType = WebXmlUtil.getMimeType(FileUtil.extName(filename));
            response.setContentType(mimeType);

            if (filename.equals("a.html")) {
                ThreadUtil.sleep(1000);
            }

            response.setStatus(Constant.CODE_200);
        } else { //文件不存在 - 404
            //response.getPrintWriter().println("File Not Found");
            System.out.println("File Not Found");
            response.setStatus(Constant.CODE_404);
        }

    }
}
