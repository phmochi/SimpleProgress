package patrick.SimpleProgress;

import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//TODO: updating an entry date doesn't propagate to taskmanager

/**
 * Created by Patrick on 5/13/2016.
 */
public class TaskManager {
    private HashMap<Integer, Task> taskMap;
    private Calendar currentCalendar;

    public TaskManager(ArrayList<Task> tasks){
        taskMap = new HashMap<>();
        addTasks(tasks);
        currentCalendar = Calendar.getInstance();
    }

    public void addTask(Task task){
        task.initializeEntryManager();
        taskMap.put(task.getId(), task);
    }

    public void addTasks(ArrayList<Task> tasks){
        for (Task t: tasks){
            t.initializeEntryManager();
            taskMap.put(t.getId(), t);
        }
    }

    public void removeTask(int taskId){
        taskMap.remove(taskId);
    }

    public void addEntries(ArrayList<Entry> entries){
        Task t;

        for (Entry e: entries){
            t = taskMap.get(e.getTaskId());
            if(isActive(t, e)) {
                t.getEntryManager().addEntry(e);
            }
        }
    }

    private boolean isActive(Task t, Entry e){
        Cycle cycle = t.getCycle();
        Date d = e.getDate();

        switch(cycle){
            case daily:
                if (isToday(d)){
                    return true;
                }
                break;
            case weekly:
                if (isInWeek(d)){
                    return true;
                }
                break;
            case monthly:
                if (isInMonth(d)){
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    private boolean isToday(Date date){
        return DateUtils.isToday(date.getTime());
    }

    private boolean isInWeek(Date date){
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar entryCalendar = Calendar.getInstance();
        entryCalendar.setTime(date);

        int entryWeek = entryCalendar.get(Calendar.WEEK_OF_YEAR);
        int entryYear = entryCalendar.get(Calendar.YEAR);

        return week == entryWeek && year == entryYear;
    }

    private boolean isInMonth(Date date){
        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar entryCalendar = Calendar.getInstance();
        entryCalendar.setTime(date);

        int entryMonth = entryCalendar.get(Calendar.MONTH);
        int entryYear = entryCalendar.get(Calendar.YEAR);

        return month == entryMonth && year == entryYear;
    }

    public void addEntry(Entry e){
        Task t = taskMap.get(e.getTaskId());
        if (isActive(t, e)){
            t.getEntryManager().addEntry(e);
        }
    }

    public void deleteEntries(ArrayList<Entry> entries){
        for (Entry e: entries){
            Task t = taskMap.get(e.getTaskId());
            t.getEntryManager().removeEntry(e);
        }
    }

    public void updateEntries(ArrayList<Entry> entries){
        for (Entry e: entries){
            Task t = taskMap.get(e.getTaskId());
            if (!isActive(t, e)){
                Log.d("updating", "is not active");
                t.getEntryManager().removeEntry(e);
            } else {
                Log.d("updating", "is active");
                t.getEntryManager().updateEntry(e);
            }
        }
    }

    public void updateTask(Task task, ArrayList<Entry> entries){
        task.initializeEntryManager();

        for (Entry e: entries){
            if (isActive(task, e)){
                task.getEntryManager().addEntry(e);
            }
        }

        taskMap.put(task.getId(), task);
    }

    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(taskMap.values());
    }
}
