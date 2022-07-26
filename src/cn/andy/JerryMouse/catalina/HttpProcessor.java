package cn.andy.JerryMouse.catalina;

import cn.andy.JerryMouse.http.Request;
import cn.andy.JerryMouse.http.Response;
import cn.andy.JerryMouse.servlets.DefaultServlet;
import cn.andy.JerryMouse.servlets.InvokerServlet;
import cn.andy.JerryMouse.util.Constant;
import cn.andy.JerryMouse.util.WebXmlUtil;
import cn.andy.JerryMouse.webappservlet.HelloServlet;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpProcessor {
    public void execute(Socket s, Request request, Response response) throws IOException {
        try {

            String uri = request.getUri();

            if (uri == null) {
                return;
            }

            Context context = request.getContext();
            System.out.println("Uri:" + uri);

            String servletClassName = context.getServletClassNameByUrl(uri);

            if(null != servletClassName){
                InvokerServlet.getInstance().service(request, response);
            }
            else {
                DefaultServlet.getInstance().service(request, response);
            }

            if (Constant.CODE_404 == response.getStatus()) {
                handle404(s, uri);
            }
            if (Constant.CODE_200 == response.getStatus()) {
                handle200(s, response);
            }

        } catch (Exception e) {
            LogFactory.get().error(e);
            handle500(s,e);
        } finally { // 关闭Socket
            System.out.println("Server stop");
            try{
                if (s != null && !s.isClosed()) {
                    s.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    private void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText,contentType);
        byte[] head = headText.getBytes(StandardCharsets.UTF_8);
        byte[] body = response.getBody();
        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head,0,responseBytes,0,head.length);
        ArrayUtil.copy(body,0,responseBytes,head.length,body.length);

        OutputStream os =s.getOutputStream();
        os.write(responseBytes);
        s.close();

    }

    private void handle404(Socket s, String url) throws IOException {
        OutputStream os = s.getOutputStream();
        String reponseText = Constant.response_head_404;
        reponseText = StrUtil.format(reponseText,url);

        byte[] responseBytes = reponseText.getBytes(StandardCharsets.UTF_8);
        os.write(responseBytes);
    }

    private void handle500(Socket s, Exception e) throws IOException {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString());
        sb.append("\r\n");

        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append("\t");
            sb.append(stackTraceElement.toString());
            sb.append("\r\n");
        }

        String msg = e.getMessage();

        if (null != msg && msg.length() > 20){
            msg = msg.substring(0,19);
        }

        String reponseText = StrUtil.format(Constant.textFormat_500.toString(), sb.toString());
        reponseText = Constant.response_head_500 + reponseText;
        byte[] responseBytes = reponseText.getBytes(StandardCharsets.UTF_8);
        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
    }
}
