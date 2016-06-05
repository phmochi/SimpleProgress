package patrick.SimpleProgress;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Patrick on 5/9/2016.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    private Task task;

    public TaskAdapter(Context context, ArrayList<Task> tasks){
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        task = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task, parent, false);
        }

        TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        TextView taskCompleted = (TextView) convertView.findViewById(R.id.taskCompletedText);
        TextView taskGoal = (TextView) convertView.findViewById(R.id.taskGoalText);
        TextView taskElapsed = (TextView) convertView.findViewById(R.id.taskElapsedText);
        Context context = getContext();
        String elapsed = "";

        taskName.setText(task.getName());
        double done = task.getCompleted();
        double goal = task.getGoal();
        DecimalFormat format = new DecimalFormat("0.##");
        taskCompleted.setText(format.format(done));
        taskGoal.setText("/" + format.format(goal));

        switch (task.getCycle().toString()){
            case "daily":
                elapsed += "Day ";
                break;
            case "weekly":
                elapsed += "Week ";
                break;
            case "monthly":
                elapsed += "Month ";
                break;
            default:
                break;
        }

        elapsed = addElapsed(elapsed);

        taskElapsed.setText(elapsed);

        if (done > goal){
            taskCompleted.setTextColor(ContextCompat.getColor(context, R.color.softgreen));
        } else {
            taskCompleted.setTextColor(ContextCompat.getColor(context, R.color.softred));
        }

        return convertView;
    }

    private String addElapsed(String s){
        Date dateStart = task.getDate();
        Date dateEnd = new Date();
        int count = 1;


        switch (task.getCycle().toString()){
            case "daily":
                count += getDaysBetween(dateStart, dateEnd);
                break;
            case "weekly":
                count += getWeeksBetween(dateStart, dateEnd);
                break;
            case "monthly":
                count += getMonthsBetween(dateStart, dateEnd);
                break;
            default:
                break;
        }

        return s + String.valueOf(count);
    }

    private int getDaysBetween(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private int getWeeksBetween(Date d1, Date d2){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(d1);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(d2);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffWeek = diffYear * 52 + endCalendar.get(Calendar.WEEK_OF_YEAR) - startCalendar.get(Calendar.WEEK_OF_YEAR);

        return diffWeek;
    }

    private int getMonthsBetween(Date d1, Date d2){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(d1);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(d2);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth;
    }

}
