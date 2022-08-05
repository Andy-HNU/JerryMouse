package cn.andy.JerryMouse.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import cn.andy.JerryMouse.catalina.Context;
import cn.andy.JerryMouse.catalina.Engine;
import cn.andy.JerryMouse.catalina.Host;
import cn.andy.JerryMouse.catalina.Service;
import cn.andy.JerryMouse.util.MiniBrowser;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.*;

public class Request extends BaseRequest {
    private final String requestString;
    private String uri;
    private final Context context;
    private final Host host;
    private String method;
    public Request(Socket socket,Service service) throws IOException {

        Engine engine = service.getEngine();
        this.host = engine.getDefaultHost();
        this.requestString = parseHttpRequest(socket); // RequestString:GET /a.html HTTP/1.1
        //System.out.println("Request's requestString: " + this.requestString);
        this.uri = parseHttpUri(this.requestString);    // Uri:/ or /a.html (ROOT/a.html, ROOT是一个特殊的路径) or /FuncA/b.html
        //System.out.println("Request's uri: " + this.uri);
        this.context = parseContext(uri); //获取uri中 如"/FuncA"的部分
        // uri 最后只保留 /FileName 的形式, 文件夹路径信息由Context的path保存
        System.out.println("Request's context: " + this.context.getPath());
        if (!"/".equals(this.context.getPath())){
            this.uri = StrUtil.removePrefix(this.uri,context.getPath());
            if (StrUtil.isEmpty(uri)){
                this.uri = "/";
            }
        }
        this.uri = this.CheckIfWelcome(this.uri);

        parseMethod();
    }

    private String CheckIfWelcome(String uri) { // 如果url 不包含 具体文件名，则认为是welcome页面
          if (!(uri.contains(".")) && !(uri.contains("-HardCode-"))){
                uri = "/";
          }
          return uri;
    }


    private String parseHttpRequest(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        byte[] bytes = MiniBrowser.ReadBytes(is,false); //这个要带个 false 参数，表示还像原来那样，如果读取到的数据不够 bufferSize ,那么就不继续读取了。
                                       // 为什么这里不能用过 true 呢？ 因为浏览器默认使用长连接，发出的连接不会主动关闭，那么 Request 读取数据的时候 就会卡在那里
        return new String(bytes, StandardCharsets.UTF_8);
    }
    private String parseHttpUri(String requestString2Uri){
        System.out.println("67-Request's requestString2Uri: " + requestString2Uri);
        requestString2Uri = StrUtil.subBetween(requestString2Uri," "," ");
        if (!StrUtil.contains(requestString2Uri,'?')){
            return requestString2Uri;
        }
        requestString2Uri = StrUtil.subBefore(requestString2Uri,'?',false);
        return requestString2Uri;
    }
    private Context parseContext(String uri2Context){
        Context parsed_context = host.getContext(uri2Context);
        if (parsed_context != null){
           return parsed_context;
        }
        String path = StrUtil.subBetween(uri2Context,"/","/");
        if (null == path) {
            path = "/";
        }else {
            path = "/" + path;
        }
        System.out.println("parseContext path: " + path);
        Context context = host.getContext(path);
        if (context == null){
            context = host.getContext("/");
        }
        return context;
    }


    private void parseMethod(){
        String[] arr = StrUtil.split(this.requestString," ");
        this.method = arr[0];
    }


    @Override
    public String getMethod() {
        return method;
    }

    public ServletContext getServletContext() {
        return context.getServletContext();
    }
    public String getRealPath(String path) {
        return context.getServletContext().getRealPath(path);
    }
    public String getUri(){
        return this.uri;
    }
    public String getRequestString(){
        return this.requestString;
    }

    public Context getContext(){
        return this.context;
    }
}
