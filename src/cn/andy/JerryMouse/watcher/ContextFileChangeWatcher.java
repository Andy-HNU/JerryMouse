package cn.andy.JerryMouse.watcher;

import cn.andy.JerryMouse.catalina.Context;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class ContextFileChangeWatcher {
    private WatchMonitor watchMonitor;
    private boolean stop = false;

    public ContextFileChangeWatcher(Context context) {
        this.watchMonitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {
            private void dealwith(WatchEvent<?> event){
                synchronized (ContextFileChangeWatcher.class){
                    String filename = event.context().toString();
                    if(stop)return;
                    if(filename.endsWith(".class")||filename.endsWith(".jar")){
                        stop = true;
                        LogFactory.get().info(ContextFileChangeWatcher.this + " 检测到了Web应用下的重要文件变化 {} " + filename);
                        context.reload();
                    }
                }
            }

            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealwith(watchEvent);
            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealwith(watchEvent);
            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealwith(watchEvent);
            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealwith(watchEvent);
            }
        });
        this.watchMonitor.setDaemon(true);
    }

    public void start(){
        this.watchMonitor.start();
    }
    public void stop(){
        this.watchMonitor.close();
    }
}
