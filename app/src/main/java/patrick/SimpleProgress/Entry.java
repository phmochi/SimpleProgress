package patrick.SimpleProgress;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Patrick on 5/9/2016.
 */
public class Entry implements Parcelable {
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
    private int id;
    private int taskId;
    private double hours;
    private Date date;
    private String comment;

    public Entry(int taskId, double hours) {
        this.taskId = taskId;
        this.hours = hours;
        date = new Date();
        comment = "";
    }

    public Entry(double hours, Date date) {
        this.hours = hours;
        this.date = date;
        comment = "";
    }

    public Entry(int taskId, double hours, String comment) {
        this.taskId = taskId;
        this.hours = hours;
        this.comment = comment;
        date = new Date();
    }

    public Entry(int taskId, double hours, Date date) {
        this.taskId = taskId;
        this.hours = hours;
        this.date = date;
        comment = "";
    }

    private Entry(Parcel in) {
        id = in.readInt();
        taskId = in.readInt();
        hours = in.readDouble();
        date = new Date(in.readLong());
        comment = in.readString();
    }

    public Entry(int id, int taskId, double hours, Date date, String comment) {
        this.id = id;
        this.taskId = taskId;
        this.hours = hours;
        this.date = date;
        this.comment = comment;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
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
        dest.writeInt(taskId);
        dest.writeDouble(hours);
        dest.writeLong(date.getTime());
        dest.writeString(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
