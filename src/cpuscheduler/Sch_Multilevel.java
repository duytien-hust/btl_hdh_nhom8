
package cpuscheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Sch_Multilevel extends Scheduler{

    private ArrayList<Scheduler> levels;       //khai bao mang dong levels
    private boolean preemptive;             //

    public Sch_Multilevel(ArrayList<String> lvls, boolean isPreemptive) {
        levels = new ArrayList<>();         //khai bao mang dong levels
        for (String lvl : lvls) {
            levels.add(CPU.setSchMethod(lvl));        //them che do lap lich cho vao mang levels
        }
        this.preemptive = isPreemptive;    //set preemptive = isPreemptive (false or true) (CPU.java : line 215-218)
        activeProc = null;
    }

    @Override
    public void addProc(Process p) {
        levels.get(p.getLevel()-1).addProc(p);   //lay phan tu thu (p.level-1) cua mang levels roi them process
    }

    @Override
    public boolean removeProc(Process p) {
        for(int i=0; i<levels.size(); i++){
            if(levels.get(i).removeProc(p)){    //lay phan tu thu i trong arraylist levels roi xoa
                return true;
            }
        }
        return false;
    }

    @Override
    public void setScheduler(Scheduler method) {    //lay che do lap lich thu i trong levels de set che do lap lich
        for(int i=0; i<levels.size(); i++){
            levels.get(i).setScheduler(method);
        }
    }

    @Override
    public Process getNextProc(double currentTime) {           //lua chon tien trinh tiep theo xu ly
        if(activeProc != null && activeProc.isIsFinished()){ //khi tien trinh được thực thi xong
            activeProc = null;
        }
        if(isPreemptive() || activeProc == null){           //neu preemptive la true thi kiem tra levels ko trong dau tien
            for (int i = 0; i < levels.size(); i++) {
                if(levels.get(i).isProcLeft()){             //kiem tra hang doi con trong hay khong?
                    activeProc = levels.get(i).getNextProc(currentTime);   //chon process tiep theo dua vao current time
                    break;
                }
            }
        }
        return activeProc;
    }

    public String getName() {
        return !isPreemptive() ? "Multi Level" : "Preemptive Multi Level";
    }

    public boolean isPreemptive() {
        return preemptive;
    }

    @Override
    public boolean isProcLeft() {
        return false;
    }
}
