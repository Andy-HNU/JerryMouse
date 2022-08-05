package cn.andy.JerryMouse.test;

import cn.andy.JerryMouse.Bootstrap;
import cn.andy.JerryMouse.util.MiniBrowser;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestJerryMouse {
    private static int port = 18082;
    private static String ip = "127.0.0.1"; // 能不能换成公共域名呢？
    @BeforeClass
    public static void beforeClass() {
        String[] args = new String[1];
        new Thread(() -> {
            try {
                Bootstrap.main(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //uri == "/"时的特殊情况
    @Test
    public void testJerry(){
        String html = getContentString("/");
        Assert.assertEquals(html,"Hello DIY Tomcat from how2j.cn");
    }
    //从文件进行/ROOT/a.html的读取
    @Test
    public void testHtml(){
        String html = getContentString("/a.html");
        Assert.assertEquals(html,"Hello JerryMouse!");
    }
    // 多线程 线程池的应用 ，蛮简单
    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20,20,60, TimeUnit.SECONDS,new LinkedBlockingDeque<>(10));
        TimeInterval timeInterval = DateUtil.timer();

        for (int i = 0 ; i< 3 ;i++){
            threadPoolExecutor.execute(new Runnable() {
                                           @Override
                                           public void run() {
                                               getContentString("/a.html");
                                           }
                                       }
            );
        }
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(1, TimeUnit.MINUTES);

        long duration = timeInterval.intervalMs();
        System.out.println(duration);
    }


    // testContext() 注解：构建Context并进行测试
    /*
    * 我理解的Context uri的作用
    * uri --> 用于BootStrap中的 file 定位的一条路径
    * 一般由Socket发送的request格式为： RequestString:GET /FuncA/A1/c.html HTTP/1.1
    * 对应解析到的uri为 ：Uri: /FuncA/A1/c.html
    *
    * 为什么要加一个Context？
    * 因为获取文件时需要一个 Absolute address, uri不是 absolute address
    * 所以通过对webapps下的文件夹进行扫描 构建一个 FolderName - Absolute Path Of FolderName 集合
    * 现在要将uri进行 重构 /FuncA/A1/c.html -----> /FuncA + /A1/c.html
    * 前者为 path 可以查询到 Absolute Path, 后者就是 BootStrap中未来用于构建的 "uri" (其实是 path + “uri” 构成了真正的 uri)
    *
    * 这也解释了为什么 Request 中 要对 uri进行二次处理，删掉 path 这个前缀
    * */
    @Test
    public void testContext(){
        String html = getContentString("/FuncA/A1/c.html");
        Assert.assertEquals(html,"Hello from C!");
    }

    @Test
    public void testbIndex(){
        String html = getContentString("/A1/c.html");
        Assert.assertEquals(html,"Hello from C!");
    }

    @Test
    public void test404(){
        String response = getHttpString("/404.html");
        containAssert(response,"HTTP/1.1 404 Not Found");
    }
    @Test
    public void test500(){
        String response = getHttpString("/500.html");
        containAssert(response,"HTTP/1.1 500 Internal Server Error");
    }
    @Test
    public void testAIndexWelcome() {
        String html = getContentString("/a");
        Assert.assertEquals(html,"Hello DIY Tomcat from how2j.cn");
    }

    @Test
    public void testTxt(){
        String html = getHttpString("/sth.txt");
        System.out.println("Txt-response: " + html);
        containAssert(html,"Content-Type: text/plain");
    }
    @Test
    public void testhello() {
        String html = getContentString("/j2ee/hello-HardCode-");
        Assert.assertEquals(html,"Hello, World!");
    }
    @Test
    public void testJavawebHello() {
        String html1 = getContentString("/javaWeb4Jerry/hello-HardCode-");
        String html2 = getContentString("/javaWeb4Jerry/hello-HardCode-");
        Assert.assertEquals(html1,html2);
    }

    private String getContentString(String uri){
        String url = StrUtil.format("http://{}:{}{}",ip,port,uri);
        return MiniBrowser.getContentString(url);
    }
    private String getHttpString(String uri){
        String url = StrUtil.format("http://{}:{}{}",ip,port,uri);
        return MiniBrowser.getHttpString(url);
    }
    private void containAssert(String html,String string){
        boolean match = StrUtil.containsAny(html,string);
        Assert.assertTrue(match);
    }


}
