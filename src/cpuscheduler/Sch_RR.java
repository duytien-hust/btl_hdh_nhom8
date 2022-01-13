package cpuscheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Sch_RR extends Scheduler{

    private double quantum; //khe thoi gian Quantumn
    private PriorityQueue<Process> pq; //tao 1 hang doi uu tien
    private ArrayList<Process> rrList;
    private int currProc; //thoi gian xu ly hien tai
    private double curTimeQuantum; //

    Sch_RR(double q) {
        // chon tien trinh vao hang doi uu tien
        pq = new PriorityQueue<Process>(new Comparator<Process>() {
            @Override // so sanh muc do uu tien cua 2 tien trinh
            //ss thoi gian den cua 2 tien trinh o1, o2 ; o1 >= o2 tra ve gia tri 1
            public int compare(Process o1, Process o2) {
                return (o1.getArrivalTime() >= o2.getArrivalTime()) ? 1 : -1;
            }
        });
        rrList = new ArrayList<Process>(); // khoi tao mang danh sach cac tien trinh
        quantum = q;
        curTimeQuantum = 0.0;
        activeProc = null;
        currProc = 0;
    }

    @Override //add thoi gian xu ly
    public void addProc(Process p) {
        pq.add(p);
    }

    @Override //xoa thoi gian xu ly
    public boolean removeProc(Process p) {
        return (pq.remove(p) || rrList.remove(p));
    }

    @Override
    public void setScheduler(Scheduler method) {
        while (pq.size() > 0) {
            rrList.add(pq.poll()); // pq.poll() xóa phần tử đầu List ( xử lý xong) //thêm phần tử đầu trong pq vào rrList
        }
        Iterator<Process> itr = pq.iterator(); //duyệt các phần tử từ đầu đến cuối
        while(itr.hasNext()){ //hasNext() Nó trả về true nếu iterator còn phần tử kế tiếp phần tử đang duyệt.
            method.addProc(itr.next()); //.next() Nó trả về phần tử hiện tại và di chuyển con trỏ trỏ tới phần tử tiếp theo.
            itr.remove();//.remove() Nó loại bỏ phần tử cuối được trả về bởi Iterator.
        }
    }

    @Override
    public Process getNextProc(double currentTime) {
        while (pq.size() > 0) {
            rrList.add(pq.poll());
        }

        if (quantum <= 1e-1) {
            activeProc = null;
        } else if (rrList.size() > 0) {
            if (activeProc == null) { //k có proc nào đang xử lý
                activeProc = rrList.get(currProc);  //bắt đầu xử lý
                curTimeQuantum = 0;
            } else if ((quantum - curTimeQuantum < 1e-1) && !activeProc.isIsFinished()) { //trường hợp hết quantum mà chưa xử lý xong
                currProc = (currProc + 1) % rrList.size(); //đẩy proc tiếp theo trong trong rr list lên xử lý
                activeProc = rrList.get(currProc);
                curTimeQuantum = 0;
            } else if (activeProc.isIsFinished()) { //trường hợp proc xử lý xong trong quatum hiện tại
                if (currProc == rrList.size()) {
                    currProc--;
                }
                activeProc = rrList.get(currProc);
                curTimeQuantum = 0;
            }
            curTimeQuantum += 1e-1;  //sau mỗi 1 quantum xử lý thì thời gian quatum hiện tại tăng lên 0.1
        } else {
            activeProc = null; // nếu hết proc trong hàng đợi thì dừng
        }
        return activeProc;

    }

    @Override
    public String getName() {
        return "RR";
    }

    @Override
    public boolean isProcLeft() {
        return !pq.isEmpty();
    }

}