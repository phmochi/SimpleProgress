package patrick.SimpleProgress;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//TODO: updating an entry date doesn't propagate to taskmanager

/**
 * Created by Patrick on 5/13/2016.
 */
public class TaskManager {
    private HashMap<Integer, Task> taskMap;
    private Calendar currentCalendar;
    private DBHelper db;

    public TaskManager(Context context, ArrayList<Task> tasks) {
        taskMap = new HashMap<>();
        addTasks(tasks);
        currentCalendar = Calendar.getInstance();
        db = DBHelper.getInstance(context);
    }

    public void addTasks(ArrayList<Task> tasks) {
        for (Task t : tasks) {
            t.initializeEntryManager();
            taskMap.put(t.getId(), t);
        }
    }

    public void addActiveEntries() {
        for (Task t : taskMap.values()) {
            t.getEntryManager().addEntries(db.getActiveEntriesFor(t.getId()));
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }
}
