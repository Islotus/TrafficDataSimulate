package Islotus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Random;
import java.text.SimpleDateFormat;


public class SysLogTcp {
    private String hostGps;
    private int portGps;
    private Socket clientGps;
    private OutputStream outGps;

    private String hostMeter;
    private int portMeter;
    private Socket clientMeter;
    private OutputStream outMeter;

    private ConfClass conf;
    private Random randFloat;
    private SimpleDateFormat sdf;

    public SysLogTcp(ConfClass conf) {
        try {
            this.hostGps = "10.42.43.10";
            this.portGps = 8800;
            this.clientGps =  new Socket(hostGps, portGps);
            this.outGps= clientGps.getOutputStream();

            this.hostMeter = "10.42.43.10";
            this.portMeter = 8899;
            this.clientMeter =  new Socket(hostMeter, portMeter);
            this.outMeter= clientMeter.getOutputStream();

            this.conf = conf;
            this.randFloat = new java.util.Random();
            this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public SysLogTcp(String hostGps, int portGps,String hostMeter, int portMeter, ConfClass conf) {
        try {
            this.hostGps = hostGps;
            this.portGps = portGps;
            this.clientGps =  new Socket(hostGps, portGps);
            this.outGps= clientGps.getOutputStream();

            this.hostMeter = hostMeter;
            this.portMeter = portMeter;
            this.clientMeter =  new Socket(hostMeter, portMeter);
            this.outMeter= clientMeter.getOutputStream();

            this.conf = conf;
            this.randFloat = new java.util.Random();
            this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String produceInfoGps(String plateId,int groupNum) {
        String info = null;  //info must have "\n" at the end
        try {
            //String joinString = String.valueOf(randFloat.nextFloat()*100);
            info = plateId + "," + sdf.format(System.currentTimeMillis()) + "," + Integer.toString(groupNum)+ "\n";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    private String produceInfoMeter(String plateId) {
        String info = null;  //info must have "\n" at the end
        try {
            String joinString = String.valueOf((int)(randFloat.nextFloat()*100));
            info = plateId + "," + sdf.format(System.currentTimeMillis()) + "," + joinString + "\n";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public void produceMsg(int id) {
        try {
            int runTimeGps = conf.getThreadConf()[id][1] * 1000;
            int sleepTimeGps = conf.getThreadConf()[id][0];
            int runTimeMeter = conf.getThreadConf()[id][3] *1000;
            int sleepTimeMeter = conf.getThreadConf()[id][2];
            int groupNum = conf.getThreadConf()[id][4];
            int sleepTimeMeterFlag = sleepTimeMeter;
            String plateId = conf.getPlateId(5);
            while(runTimeGps > 0) {
                if(runTimeGps >= sleepTimeGps) {  //have enough time
                    Thread.sleep(sleepTimeGps);
                    String eventGps = produceInfoGps(plateId,groupNum);
                    // there must be "\n" at the end of string
                    outGps.write(eventGps.getBytes());
                    outGps.flush();
                    //System.out.print(event);

                    //Thread.sleep(sleepTimeGps);
                    runTimeGps -= sleepTimeGps;
                    runTimeMeter -= sleepTimeGps;
                    sleepTimeMeterFlag -= sleepTimeGps;
                }
                if(runTimeGps > 0 && runTimeMeter > 0 && sleepTimeMeterFlag >= 0 && sleepTimeMeterFlag <= sleepTimeGps){
                    if(runTimeMeter >= sleepTimeMeterFlag) {
                        Thread.sleep(sleepTimeMeterFlag);
                        String eventMeter = produceInfoMeter(plateId);
                        outMeter.write(eventMeter.getBytes());
                        outGps.flush();

                        runTimeMeter -= sleepTimeMeterFlag;
                        runTimeGps -= sleepTimeMeterFlag;  //
                    } else {
                        runTimeMeter = -1;  //no more time to send meter msg
                        sleepTimeMeterFlag = 0;
                    }
                    if(runTimeMeter >= 0) {  //msg of Meter has sent
                        int intervalTime = sleepTimeGps - sleepTimeMeterFlag;
                        if(intervalTime == 0){
                            String eventGps = produceInfoGps(plateId,groupNum);
                            outGps.write(eventGps.getBytes());
                            outGps.flush();

                            sleepTimeMeterFlag = sleepTimeMeter;

                        }else if(runTimeGps > intervalTime) {
                            Thread.sleep(intervalTime);
                            String eventGps = produceInfoGps(plateId,groupNum);
                            outGps.write(eventGps.getBytes());
                            outGps.flush();

                            runTimeGps -= intervalTime;
                            runTimeMeter -= intervalTime;
                            sleepTimeMeterFlag = sleepTimeMeter - intervalTime;

                        } else {
                            runTimeGps = -1;
                            sleepTimeMeterFlag = 0;
                        }
                    }
                }
            }
            Thread.sleep(5*1000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //System.out.println("get into finally");
            try {
                outGps.close();
                outMeter.close();
                //System.out.println("out close");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                clientGps.close();
                clientMeter.close();
                //System.out.println("client close");
            }  catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //System.out.println("finish!");
        }
    }

    public void setHostGps(String hostGps) {
        try {
            this.hostGps = hostGps;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public String getHostGps() {
        return this.hostGps;
    }
    public void setHostMeter(String hostMeter){
        try {
            this.hostMeter = hostMeter;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public String getHostMeter() {
        return this.hostMeter;
    }
    public void setPortGps(int portGps) {
        try {
            this.portGps = portGps;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void setPortMeter(int portMeter) {
        try {
            this.portMeter = portMeter;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public int getPortGps() {
        return this.portGps;
    }
    public int getPortMeter() {
        return this.portMeter;
    }
}



