package patrick.hourlog;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Patrick on 5/10/2016.
 */
public class Task {

    private int id;
    private String name;
    private double goal;
    private Cycle cycle;
    private EntryManager entryManager;

    public Task(String name, double goal, Cycle cycle){
        this.name = name;
        this.goal = goal;
        this.cycle = cycle;
    }

    public Task(int id, String name, double goal, Cycle cycle){
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.cycle = cycle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    public void initializeEntryManager(){
        entryManager = new EntryManager();
    }

    public EntryManager getEntryManager(){
        return entryManager;
    }

    public double getCompleted(){
        return entryManager.getCompleted();
    }
}
