package cpuscheduler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class CPU {
    
    private Scheduler sm;       // scheduler method
    private double cs = 0.4;    //context switch = 0.4
    private int csCount = 0;    //dem so lan context switch 
    private double currentTime = 0.0; 
    private String simData = "";//simulation data
    private String report = "";//ket qua tinh toan
    
    private ArrayList<Process> allProcs = new ArrayList<>();      //tao list luu tat ca cac process
    private ArrayList<Process> procQueue = new ArrayList<>();     //tao process queue
    private ArrayList<Process> readyQueue = new ArrayList<>();    // tao ready queue
    private static ArrayList<String> randomData = new ArrayList<>();//tao list luu data random
    private static ArrayList<String> levels = new ArrayList<>();  //tao list luu so hang doi
    private Process preProc = null;                               // tien trinh tiep theo
    
    private Process activeProc = null;                            // tien trinh dang chay
    
    private double AWT = 0.0;       // average waitting time
    private double ATT = 0.0;       // average turnaround time
    private double Util = 0.0;      // CPU utilization
    private double Potency = 0.0;

    /*Khoi tao CPU truoc khi cac process duoc thuc hien */
    CPU(String data, String schName) {
        levels.clear();             //xoa tat ca cac phan tu trong arrayList levels
        sm = setSchMethod(schName); //lay vao ten thuat toan lap lich
        sm.setScheduler(sm);        
        activeProc = null;          //chua co tien trinh nao duoc thuc thi
        Process proc = null;        
        double b = 0, d = 0;        //b,d,p dung de luu cac gia tri input cua moi tien trinh
        int p = 0;
        String[] lines = data.split("\n"); //doc du lieu input va luu theo tung dong
        int i = 1;
        for (String line : lines) {
            String[] split = line.split("\\s+");    //lay 4 tham so trong tung dong
            b = Double.parseDouble(split[0]);       // luu cac tham so vao cac bien luu tru
            d = Double.parseDouble(split[1]);
            p = (int)Double.parseDouble(split[2]);
            proc = new Process(i, b, d, p);
            proc.setLevel((int)Double.parseDouble(split[3])); //cap nhat so hang doi duoc su dung
            i++;
            allProcs.add(proc); //add cac tien trinh input vao arrayList allProcs
        }
        initProcQueue(allProcs); //dua cac tien trinh vao hang cho tien trinh
    }

    /* Khoi tao CPU truoc khi cac tien trinh duoc thuc hien voi truong hop co Preemptive */
    CPU(String data, ArrayList<String> schName, String isPreemptive) {
        levels.addAll(schName);
        sm = setSchMethod(isPreemptive + "Multi Level"); //lay ten thuat toan lap lich
        sm.setScheduler(sm);
        activeProc = null;      //chua co tien trinh nao duoc thuc thi
        Process proc = null;
        double b = 0, d = 0;    //b,d,p dung de luu cac gia tri input cua moi tien trinh
        int p = 0;
        String[] lines = data.split("\n");//doc du lieu input va luu theo tung dong
        int i = 1;
        for (String line : lines) {
            String[] split = line.split("\\s+"); //lay 4 tham so trong tung dong
            b = Double.parseDouble(split[0]);          // luu cac tham so vao cac bien luu tru
            d = Double.parseDouble(split[1]);
            p = (int)Double.parseDouble(split[2]);
            proc = new Process(i, b, d, p);
            proc.setLevel((int)Double.parseDouble(split[3]));
            i++;
            allProcs.add(proc);
        }
        initProcQueue(allProcs);
    }

    /* ham dua cac thong so random cua tien trinh vao list randomData */
    public static void randProc(int processNum, boolean isMulti, int levelNum) {
        Process p;
        randomData.clear(); //xoa het cac phan tu trong randomData
        for (int i = 0; i < processNum; i++) {
            //khoi tao process p bang ham Process
            p = new Process(i+1, 8.33, 2.1, 2.46, 0.7);
            p.setLevel(new Random().nextInt(levelNum)+1); //set hang doi thuc hien tien trinh p.
            randomData.add(p.getBurstTime() + " " + p.getDelayTime() 
                    + " " + p.getPriority() + " " + p.getLevel());
            //add cac gia tri input vao
        }
    }

    /* add cac tien trinh vao hang cho tien trinh */
    private void initProcQueue(ArrayList<Process> allProcess) {
        Process p;
        double arrivalTime = 0;
        for (int i = 0; i < allProcess.size(); i++) {
            p = (Process) allProcess.get(i);                //xet tat ca cac tien trinh input
            arrivalTime += p.getDelayTime();
            p.setArrivalTime(arrivalTime);                  //add gia tri arrival time cho process
            procQueue.add(p);
        }
    }
    /* add cac tien trinh vao ready queue*/
    private void initReadyQueue() {
        Process p;
        for (int i = 0; i < procQueue.size(); i++) {        //xet tat ca cac tien trinh proQueue
            p = (Process) procQueue.get(i);
            if (p.getArrivalTime() - currentTime < 1e-1) {  //Neu thoi gian den nho hon hoac bang thoi gian hien tai
                readyQueue.add(p);                          // thi tien trinh duoc add vao RQ
                sm.addProc(p);
            }
        }
        
    }
    /*remove tien trinh khoi ready queue khi da thuc hien xong */
    private void refReadyQueue() {
        Process p;
        for (int i = 0; i < readyQueue.size(); i++) {
            p = (Process) readyQueue.get(i);
            if (p.isIsFinished() == true) {
                readyQueue.remove(i);
                sm.removeProc(p);
            }
        }
    }
    /*remove tien trinh khoi process queue*/
    private void refProcQueue() {
        Process p;
        for (int i = 0; i < procQueue.size(); i++) {
            p = (Process) procQueue.get(i);
            if (p.isIsFinished() == true) {
                procQueue.remove(i);
                sm.removeProc(p);
            }
        }
    }

    /* ham lap lich */
    void Schedule() {
        Process p = null;
        activeProc = sm.getNextProc(currentTime);
        if(activeProc != preProc && preProc != null){       //nếu tiến trình đến mà vẫn còn tiến trình đang thực hiện
            if(cs > 0.4) currentTime += (cs - 0.4);
            csCount++;
        }
        if (activeProc != null){
            activeProc.executing(currentTime);  //tiến trình được thực thi
            simData += activeProc.toString();   //kết quả tính toán được lưu vào simulation data
            preProc = activeProc;
        }
        for (int i = 0; i < readyQueue.size(); ++i) {       //xet cac tien trinh trong ready queue
            p = (Process) readyQueue.get(i);
            if (p.getPID() != activeProc.getPID()) {        //neu khac active process thì
                p.waiting(currentTime);                     //các tiến trình sẽ phải chờ
            }
        }
    }
    /* tinh toan ket qua */
    private void report() {
        Process p = null;
        int procCount = 0;                              //dem so tien trinh
        
        for (int i = 0; i < allProcs.size(); i++) {
            p = (Process) allProcs.get(i);
            
            if (p.isIsFinished()) {
                procCount++;
                double waited = p.getWaitTime();        //thoi gian cho
                double turned = p.getTurnAroundTime();  //thoi gian hoan thanh
                AWT += waited;                          //tong thoi gian cho
                ATT += turned;                          //tong thoi gian thuc hien
            }
        }
        
        if (procCount > 0) {
            AWT /= (double) procCount;                  //tinh thoi gian cho trung binh
            ATT /= (double) procCount;                  //tinh thoi gian hoan thanh trung binh
        } else {
            AWT = 0.0;
            ATT = 0.0;
        }
        
        Util = (Math.abs(currentTime - (cs * csCount)) / currentTime) * 100; //tinh CPU utilization
        Potency = currentTime / procCount;
        
        report = "AWT : " + String.format("%.1f", AWT) + "\nATT : " + String.format("%.1f", ATT)
                + "\nCPU Util : " + String.format("%.1f", Util) + "%\nPotency : " + String.format("%.1f", Potency);
        // luu cac ket qua vao list report
    }
    
    /* khoi tao cac thuat toan lap lich */
    public static Scheduler setSchMethod(String method) {
        
        String split[] = method.split(":");
        
        switch(split[0]){
            case "FCFS":
                return new Sch_FCFS();
            case "PSJF":
                return new Sch_SJF(true);
            case "SJF":
                return new Sch_SJF(false);
            case "Round Robin":
                return new Sch_RR(Double.valueOf(split[1]));
            case "Multi Level":
                return new Sch_Multilevel(levels, false);
            case "Preemptive Multi Level":
                return new Sch_Multilevel(levels, true);
        }
        return null;
    }
    
    /*mo phong*/
    public void Simulate(){
        
        boolean check;
        check = true;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        
        while(check){
            if (procQueue.isEmpty()) {          //neu hang cho tien trinh trong
                check = false;                  //check = false, ket thuc mo phong, hien thi report
            } else {
                initReadyQueue();               //add tien trinh vao ready queue, thuc hien lap lich
                check = true;
                if (!readyQueue.isEmpty()) {
                    Schedule();                 //tien trinh thuc hien
                    refProcQueue();             //remove tien trinh khoi hang cho va ready queue
                    refReadyQueue();
                }
                currentTime+=1e-1;
                currentTime = Double.valueOf(df.format(currentTime));
            }
        }
        report();       //hien thi ket qua
        resetAll();     //reset tat ca cac gia tri
    }
    
    /*ham reset trả ve gia tri khoi tao cho dau vao */
    public void resetAll() {
        Process p;
        
        activeProc = null;
        sm = null;
        currentTime = 0;
        csCount = 0;
        AWT = 0.0;
        ATT = 0.0;
        Util = 0.0;
        Potency = 0.0;
        
        
        for (int i = 0; i < allProcs.size(); i++) {
            p = (Process) allProcs.get(i);
            p.resetAll();
        }
        
        procQueue.clear();
        readyQueue.clear();
        initProcQueue(allProcs);
    }

    public Process getActiveProc() {     /*ham lay tien trinh dang duoc chay */
        return activeProc;
    }

    public double getCurrentTime() {    /*ham lay gia tri current Time */
        return currentTime;
    }

    public String getSimData() {        /*ham lay data input*/
        return simData;
    }

    public String getReport() {         /*ham lay ket qua output */
        return report;
    }

    public void resetSimData(){         /* ham reset data input */
        this.simData = "";
    }

    public void resetReport(){          /* ham reset ket qua output*/
        this.report = "";
    }

    public void setCs(double cs) {      /*ham cap nhat gia tri context switch */
        if(cs > 0.4) this.cs = cs;
    }

    public ArrayList<Process> getAllProcs() {       /*ham lay cai tien trinh input */
        return allProcs;
    }

    public static ArrayList<String> getRandomData() {   /*ham lay list data random*/
        return randomData;
    }
    
}
