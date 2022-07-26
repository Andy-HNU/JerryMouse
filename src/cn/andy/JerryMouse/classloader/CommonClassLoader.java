package cn.andy.JerryMouse.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CommonClassLoader extends URLClassLoader {
    public CommonClassLoader() throws MalformedURLException {
        super(new URL[] {});

        File workingFolder = new File(System.getProperty("user.dir"));
        File libFolder = new File(workingFolder, "lib");
        for (File file : libFolder.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                URL url = new URL("file:" + file.getAbsolutePath());
                this.addURL(url);
            }
        }
    }


}
