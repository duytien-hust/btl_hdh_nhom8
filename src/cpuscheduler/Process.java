package cpuscheduler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;


public class Process {
    
    private int PID;
    
    private double curTime;
    
    private boolean isActive = false;
    private boolean isArrived = false;
    private boolean isStarted = false;
    private boolean isFinished = false;
    
    private double arrivalTime = 0.0;
    private double startTime = 0.0;
    private double finishTime = 0.0;
    private double burstTime = 0.0;
    private double totalBurstTime = 0.0;
    private double delayTime = 0.0;
    private double waitTime = 0.0;
    private double responseTime = 0.0;
    private double turnAroundTime = 0.0;
    private int priority = 0;
    private int level = 0;

    Process(int PID, double meanB, double stddevB, double meanD, double stddevD){
        this.PID = PID;
        Random gen = new Random(System.nanoTime()); // random biến gen
        DecimalFormat df = new DecimalFormat("#.##");  //khởi tạo giá trị thập phân df
        df.setRoundingMode(RoundingMode.CEILING);
        // gen.nextGaussian: Trả về giá trị kép giả ngẫu nhiên tiếp theo, được phân phối Gaussian với giá trị trung bình là 0,0 và độ lệch chuẩn 1,0 từ trình tự của trình tạo số ngẫu nhiên
        burstTime = Double.valueOf(df.format(gen.nextGaussian() * stddevB + meanB));
        totalBurstTime = burstTime;
        delayTime = Double.valueOf(df.format(gen.nextGaussian() * stddevD + meanD));
        priority = (int) Math.round((Math.random() * 9));
    }
    
    Process(int PID, double burstTime, double delayTime, int priority){
        this.PID = PID;
        this.burstTime = burstTime;
        totalBurstTime = burstTime;
        this.delayTime = delayTime;
        this.priority = priority;
    }
    
    public void executing(double timeNow){
        isActive = true;
        
        if( Math.abs(timeNow - arrivalTime) < 1e-1){  //kiem tra tien trinh den ready queue chua
            isArrived = true;
        }
        
        if( Math.abs(burstTime - totalBurstTime) < 1e-1){ //kiem tra tien trinh da bat dau duoc thuc thi chua
            isStarted = true;
            startTime = timeNow;
            responseTime = startTime - arrivalTime;
        }

        if(curTime != timeNow){
            burstTime-=1e-1;      //tien trinh duoc xu ly 1 don vi thoi gian 0.1
            turnAroundTime+=1e-1; //thoi gian hoan thanh tang len 1 don vi thoi gian 0.1
        }
        
        if( Math.abs(burstTime) < 1e-1){   //kiem tra tien trinh da chay xong chua
            isFinished = true;
            finishTime = timeNow;
        }
        curTime = timeNow;
    }
    
    public void waiting(double timeNow){
        isActive = false;
        if(curTime != timeNow){
            waitTime+=1e-1;     //thoi gian cho tang them 1
            turnAroundTime+=1e-1;//tong thoi gian hoan thanh tang len 1
        }
        if( Math.abs(timeNow - arrivalTime) < 1e-1){ //kiem tra xem da den ready queue chua
            isArrived = true;
        }
        curTime = timeNow;
    }
    
    @Override
    public String toString() {          //ham tra ve cac gia tri ket qua
        return this != null ? PID +
                " " + burstTime  +
                " " + totalBurstTime +
                //"Delay Time  : " + delayTime + "\n" +
                " " + priority +
                " " + arrivalTime +
                " " + startTime +
                " " + finishTime +
                " " + waitTime +
                " " + turnAroundTime +
                " " + responseTime + "\n" : "NULL";
    }
    
    public void resetAll(){         //reset cac thong so ve gia tri khoi tao
        burstTime = totalBurstTime;
        responseTime = 0.0;
        startTime    = 0.0;
        waitTime     = 0.0;
        turnAroundTime = 0.0;
        isActive   = false;
        isStarted  = false;
        isFinished = false;
        isArrived  = false;
    }
    
    public int getPID() {
        return PID;
    }       //lay gia tri ID process
    
    public void setPID(int PID) {
        this.PID = PID;
    } //set gia tri ID process
    
    public boolean isIsActive() {
        return isActive;
    }
    
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public boolean isIsArrived() {
        return isArrived;
    }
    
    public void setIsArrived(boolean isArrived) {
        this.isArrived = isArrived;
    }
    
    public boolean isIsStarted() {
        return isStarted;
    }
    
    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }
    
    public boolean isIsFinished() {
        return isFinished;
    }
    
    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
    
    public double getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public double getStartTime() {
        return startTime;
    }
    
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
    
    public double getFinishTime() {
        return finishTime;
    }
    
    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }
    
    public double getBurstTime() {
        return burstTime;
    }
    
    public void setBurstTime(double burstTime) {
        this.burstTime = burstTime;
    }
    
    public double getTotalBurstTime() {
        return totalBurstTime;
    }
    
    public void setTotalBurstTime(double totalBurstTime) {
        this.totalBurstTime = totalBurstTime;
    }
    
    public double getDelayTime() {
        return delayTime;
    }
    
    public void setDelayTime(double delayTime) {
        this.delayTime = delayTime;
    }
    
    public double getWaitTime() {
        return waitTime;
    }
    
    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }
    
    public double getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public double getTurnAroundTime() {
        return turnAroundTime;
    }
    
    public void setTurnAroundTime(double turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
}
