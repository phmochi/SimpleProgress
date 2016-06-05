package patrick.SimpleProgress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Patrick on 5/10/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "hourlog.db";
    private static DBHelper instance;

    //table for tasks
    private static final String TABLE_TASKS = "tasks";
    private static final String TASK_ID = "_id";
    private static final String TASK_NAME = "name";
    private static final String TASK_GOAL = "goal";
    private static final String TASK_CYCLE = "cycle";
    private static final String TASK_DATE = "date";

    //table for entries
    private static final String TABLE_ENTRIES = "entries";
    private static final String ENTRY_ID = "_id";
    private static final String ENTRY_TASK = "taskId";
    private static final String ENTRY_HOURS = "hours";
    private static final String ENTRY_DATE = "date";
    private static final String ENTRY_COMMENT = "comment";

    public static synchronized DBHelper getInstance(Context context){
        if (instance == null){
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL("CREATE TABLE " + TABLE_TASKS + "(" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_NAME + " TEXT NOT NULL," +
                TASK_GOAL + " REAL NOT NULL, " + TASK_CYCLE + " TEXT NOT NULL," + TASK_DATE + " INTEGER NOT NULL);");
        database.execSQL("CREATE TABLE " + TABLE_ENTRIES + " (" + ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ENTRY_TASK + " INTEGER NOT NULL, " + ENTRY_HOURS + " REAL NOT NULL, " + ENTRY_DATE + " INTEGER NOT NULL," +
                ENTRY_COMMENT + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);

        onCreate(db);
    }

    public int addTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_NAME, task.getName());
        values.put(TASK_GOAL, task.getGoal());
        values.put(TASK_CYCLE, task.getCycle().toString());
        values.put(TASK_DATE, task.getDate().getTime());

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();

        return (int) id;
    }

    public int addEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ENTRY_TASK, entry.getTaskId());
        values.put(ENTRY_HOURS, entry.getHours());
        values.put(ENTRY_DATE, entry.getDate().getTime());
        values.put(ENTRY_COMMENT, entry.getComment());

        long id = db.insert(TABLE_ENTRIES, null, values);
        db.close();

        return (int) id;
    }

    public Task getTask(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[] { TASK_ID, TASK_NAME, TASK_GOAL, TASK_CYCLE, TASK_DATE }, TASK_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Task task = makeTaskFromCursor(cursor);
        return task;
    }

    public ArrayList<Task> getAllTasks(){
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Task> taskList = addTasksFromCursor(cursor);

        return taskList;
    }

    public ArrayList<Entry> getAllEntries(){
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Entry> entryList = addEntriesFromCursor(cursor);

        return entryList;
    }

    public ArrayList<Entry> getEntriesFor(int id){
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " WHERE " + ENTRY_TASK + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Entry> entryList = addEntriesFromCursor(cursor);

        return entryList;
    }

    public ArrayList<Entry> getActiveEntriesFor(int id){
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " WHERE " + ENTRY_TASK + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Entry> entryList = addEntriesFromCursor(cursor);

        cursor = db.query(TABLE_TASKS, new String[] { TASK_CYCLE }, TASK_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Cycle cycle = Cycle.valueOf(cursor.getString(0));
        removeInactiveEntries(entryList, cycle);

        return entryList;
    }

    private void removeInactiveEntries(ArrayList<Entry> entries, Cycle c){
        ArrayList<Entry> toRemove = new ArrayList<>();

        for (Entry e:entries){
            if (!isActive(e, c)){
                toRemove.add(e);
            }
        }

        for (Entry e:toRemove){
            entries.remove(e);
        }
    }

    private boolean isActive(Entry e, Cycle c){
        Date d = e.getDate();

        switch(c){
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
        Calendar currentCalendar = Calendar.getInstance();

        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar entryCalendar = Calendar.getInstance();
        entryCalendar.setTime(date);

        int entryWeek = entryCalendar.get(Calendar.WEEK_OF_YEAR);
        int entryYear = entryCalendar.get(Calendar.YEAR);

        return week == entryWeek && year == entryYear;
    }

    private boolean isInMonth(Date date){
        Calendar currentCalendar = Calendar.getInstance();

        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar entryCalendar = Calendar.getInstance();
        entryCalendar.setTime(date);

        int entryMonth = entryCalendar.get(Calendar.MONTH);
        int entryYear = entryCalendar.get(Calendar.YEAR);

        return month == entryMonth && year == entryYear;
    }

    private ArrayList<Entry> addEntriesFromCursor(Cursor cursor){
        ArrayList<Entry> entryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do{
                int id = cursor.getInt(0);
                int taskId = cursor.getInt(1);
                double hours = cursor.getDouble(2);
                Date date = new Date(cursor.getLong(3));
                String comment = cursor.getString(4);
                Entry entry = new Entry(id, taskId, hours, date, comment);
                entryList.add(entry);
            } while (cursor.moveToNext());
        }

        return entryList;
    }

    private ArrayList<Task> addTasksFromCursor(Cursor cursor){
        ArrayList<Task> taskList = new ArrayList<>();

        if (cursor.moveToFirst()){
            do{
                Task task = makeTaskFromCursor(cursor);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        return taskList;
    }

    private Task makeTaskFromCursor(Cursor c){
        int id = c.getInt(0);
        String name = c.getString(1);
        double goal = c.getDouble(2);
        Cycle cycle = Cycle.valueOf(c.getString(3));
        Date date = new Date(c.getLong(4));

        return new Task(id, name, goal, cycle, date);
    }

    public void deleteTask(int taskId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void deleteEntries(ArrayList<Entry> entries){
        SQLiteDatabase db = this.getWritableDatabase();
        int id;
        ArrayList<String> entryIds = new ArrayList<>();

        for (Entry e: entries){
            id = e.getId();
            entryIds.add(String.valueOf(id));
        }

        db.delete(TABLE_ENTRIES, ENTRY_ID + " = ?", entryIds.toArray(new String[entryIds.size()]));
        db.close();
    }

    public void deleteEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();
        int id = entry.getId();
        db.delete(TABLE_ENTRIES, ENTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteEntriesWithId(int taskId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES, ENTRY_TASK + " =? ", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void updateTask(Task t){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TASK_NAME, t.getName());
        cv.put(TASK_GOAL, t.getGoal());
        cv.put(TASK_CYCLE, t.getCycle().toString());

        db.update(TABLE_TASKS, cv, "_id="+t.getId(), null);
        db.close();
    }

    public void updateEntry(Entry e){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ENTRY_DATE, e.getDate().getTime());
        cv.put(ENTRY_HOURS, e.getHours());
        cv.put(ENTRY_COMMENT, e.getComment());

        db.update(TABLE_ENTRIES, cv, "_id="+e.getId(), null);
        db.close();
    }
}
