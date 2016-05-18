package patrick.hourlog;

import android.util.Log;

/**
 * Created by Patrick on 5/10/2016.
 */
public class Task {

    private int id;
    private String name;
    private double goal;
    private double done;
    private Cycle cycle;

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

    public double getDone() {
        return done;
    }

    public void addToDone(double toAdd){
        done += toAdd;
    }

    public void setDone(double done) {
        this.done = done;
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
}
