package patrick.hourlog;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
        TextView taskHours = (TextView) convertView.findViewById(R.id.taskGoal);
        TextView taskCycle = (TextView) convertView.findViewById(R.id.taskCycleText);

        taskName.setText(task.getName());
        double done = task.getDone();
        DecimalFormat format = new DecimalFormat("0.##");
        taskHours.setText(format.format(done) + " / " + format.format(task.getGoal()));
        taskCycle.setText(task.getCycle().toString());

        return convertView;
    }

}
