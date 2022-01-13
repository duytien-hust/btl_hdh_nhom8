/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package cpuscheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Sch_SJF extends Scheduler{
    
    private boolean preemptive;
    private PriorityQueue<Process> pq;
    
    public Sch_SJF(boolean isPreemptive) {
        preemptive = isPreemptive;
        pq = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) { //so sanh gia tri burst time cua 2 tien trinh
                return (o1.getBurstTime() >= o2.getBurstTime()) ? 1 : -1; //neu o1 co burst time lon hon hoac bang o2
            }                                                             //tra ve 1, nguoc lai tra ve -1
        });
    }
    
    @Override
    public void addProc(Process p) {
        pq.add(p);
    }
    
    @Override
    public boolean removeProc(Process p) {
        return pq.remove(p);
    }
    
    @Override
    public void setScheduler(Scheduler method) {
        Iterator<Process> itr = pq.iterator(); //duyet cac phan tu tu dau den cuoi
        while(itr.hasNext()){                  //hasNext() Nó trả về true nếu iterator còn phần tử kế tiếp phần tử đang duyệt.
            method.addProc(itr.next());        //.next() Nó trả về phần tử hiện tại và di chuyển con trỏ trỏ tới phần tử tiếp theo.
            itr.remove();                      //.remove() Nó loại bỏ phần tử cuối được trả về bởi Iterator.
        }
    }
    
    @Override
    public Process getNextProc(double currentTime) {
        if ((isPreemptive() && pq.peek().isIsArrived()) || activeProc == null || activeProc.isIsFinished()) {
            activeProc = pq.peek(); //activeProc duoc gan bang phan tu dau dien trong pq
        }
        return activeProc;
    }
    
    @Override
    public String getName() {
        return !isPreemptive() ? "SJF" : "Premetive SJF";
    }
    
    public boolean isPreemptive() {
        return preemptive;
    }
    
    @Override
    public boolean isProcLeft() {
        return !pq.isEmpty();
    }
    
}
