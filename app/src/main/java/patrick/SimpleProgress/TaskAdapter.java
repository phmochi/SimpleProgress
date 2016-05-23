package patrick.SimpleProgress;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Patrick on 5/9/2016.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, ArrayList<Task> tasks){
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Task task = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task, parent, false);
        }

        TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        TextView taskCompleted = (TextView) convertView.findViewById(R.id.taskCompletedText);
        TextView taskGoal = (TextView) convertView.findViewById(R.id.taskGoalText);
        TextView taskCycle = (TextView) convertView.findViewById(R.id.taskCycleText);
        Context context = getContext();

        taskName.setText(task.getName());
        double done = task.getCompleted();
        double goal = task.getGoal();
        DecimalFormat format = new DecimalFormat("0.##");
        taskCompleted.setText(format.format(done));
        taskGoal.setText(" / " + format.format(goal));
        taskCycle.setText(task.getCycle().toString());

        if (done > goal){
            taskCompleted.setTextColor(ContextCompat.getColor(context, R.color.softgreen));
        } else {
            taskCompleted.setTextColor(ContextCompat.getColor(context, R.color.softred));
        }

        return convertView;
    }

}
