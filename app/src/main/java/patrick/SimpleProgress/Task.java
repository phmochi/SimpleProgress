package patrick.SimpleProgress;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Patrick on 5/10/2016.
 */
public class Task implements Parcelable {

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
    private int id;
    private String name;
    private double goal;
    private Cycle cycle;
    private EntryManager entryManager;
    private Date date;
    public Task(String name, double goal, Cycle cycle) {
        this.name = name;
        this.goal = goal;
        this.cycle = cycle;
        date = new Date();
    }

    public Task(int id, String name, double goal, Cycle cycle, Date date) {
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.cycle = cycle;
        this.date = date;
    }

/*
    public Task(int id, String name, double goal, Cycle cycle){
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.cycle = cycle;
        date = new Date();
    }
*/

    private Task(Parcel in) {
        id = in.readInt();
        name = in.readString();
        goal = in.readDouble();
        cycle = Cycle.valueOf(in.readString());
        date = new Date(in.readLong());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(goal);
        dest.writeString(cycle.toString());
        dest.writeLong(date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
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

    public void initializeEntryManager() {
        entryManager = new EntryManager();
    }

    public EntryManager getEntryManager() {
        return entryManager;
    }

    public double getCompleted() {
        return entryManager.getCompleted();
    }
}
