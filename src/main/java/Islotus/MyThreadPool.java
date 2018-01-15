package Islotus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//MyThreadPool为线程池管理类
//MyThread为实际需要运行的线程类

public class MyThreadPool {
    private int poolSize;
    private ExecutorService exe;
    private SysLogTcp sysLogTcp;

    public MyThreadPool(int poolSize, SysLogTcp sysLogTcp) {
        try {
            this.poolSize = poolSize;
            this.exe = Executors.newFixedThreadPool(poolSize);
            this.sysLogTcp = sysLogTcp;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doTask() {
        int i = 0;
        while (i < this.poolSize) {
            exe.execute(new MyThread(i, exe, sysLogTcp));
            i++;
        }
    }

    class MyThread implements Runnable
    {
        private int id;
        private ExecutorService exe;
        private SysLogTcp sysLogTcp;

        MyThread(int id, ExecutorService exe, SysLogTcp sysLogTcp) {
            try {
                this.id = id;
                this.exe = exe;
                this.sysLogTcp = sysLogTcp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void run() {
            try {
                sysLogTcp.produceMsg(id);
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void stop()  {
        this.exe.shutdown();
    }
}
