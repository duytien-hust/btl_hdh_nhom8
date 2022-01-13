/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package cpuscheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Sch_FCFS extends Scheduler{

    private PriorityQueue<Process> pq;

    public Sch_FCFS() {
        pq = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                return (o1.getArrivalTime() >= o2.getArrivalTime()) ? 1 : -1;
            }
        });//dung giao dien Comparator cua java de so sanh arrival time  cua hai tien trinh,
        // tien trinh nao den truoc thi thuc thi truoc
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
        Iterator<Process> itr = pq.iterator(); // duyet cac phan tu tu dau den cuoi cua collection bang iterator
        while(itr.hasNext()){
            method.addProc(itr.next()); // lan luot them cac tien trinh vao , sau khi xu li xong thi remove
            itr.remove();
        }
    }

    @Override
    public Process getNextProc(double currentTime) {
        return pq.peek();
    }

    @Override
    public String getName() {
        return "FCFS";
    }

    @Override
    public boolean isProcLeft() {
        return !pq.isEmpty();//kiem tra xem con tien trinh nao con sot lai khong
    }

}
