package Islotus;

public class Main {
    public static void main(String args[]) {
        int poolSize = 10000;
        int groupNum = 10;
        int sleepTimeGps = 2000;  //milliSeconds
        int aliveTimeGps = 8;  //seconds
        int sleepTimeMeter = 4000;  //milliSeconds
        int aliveTimeMeter = 8;  //seconds

        ConfClass conf = new ConfClass(poolSize,groupNum,sleepTimeGps,aliveTimeGps,sleepTimeMeter,aliveTimeMeter);
        conf.setThreadConf();

        //System.out.println(conf.getPoolSize());

        SysLogTcp sysLogTcp = new SysLogTcp(conf);

        MyThreadPool myThreadPool = new MyThreadPool(conf.getPoolSize(), sysLogTcp);
        myThreadPool.doTask();
        myThreadPool.stop();
    }
}
