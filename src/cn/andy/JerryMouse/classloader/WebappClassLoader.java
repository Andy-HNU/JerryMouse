package cn.andy.JerryMouse.classloader;

import cn.hutool.core.io.FileUtil;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

public class WebappClassLoader extends URLClassLoader {
    public WebappClassLoader(String docbase, ClassLoader commonClassLoader) {
        super(new URL[]{}, commonClassLoader);

        File webinfFolder = new File(docbase, "WEB-INF");

        File classesFolder = new File(webinfFolder, "classes");

        File libFolder = new File(webinfFolder, "lib");

        System.out.println("classesFolder: " + classesFolder.getAbsolutePath());
        URL url;
        try {
            url = new URL("file:" + classesFolder.getAbsolutePath() + "/");
            this.addURL(url);

            //返回是否添加成功
            System.out.println("add url: " + url.toString());
            List<File> files = FileUtil.loopFiles(libFolder);
            for (File file : files) {
                if (file.getName().endsWith(".jar")) {
                    url = new URL("file:" + file.getAbsolutePath());
                    this.addURL(url);
                }
            }
            if (classesFolder.getAbsolutePath().equals("C:\\Users\\DELL\\Desktop\\JerryMouse\\Test\\javaWeb4Jerry\\web\\WEB-INF\\classes")){
                try {
                    Class<?> aClass = this.loadClass("cn.JavaWeb.Jerry.HelloServlet");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        try {
            this.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}