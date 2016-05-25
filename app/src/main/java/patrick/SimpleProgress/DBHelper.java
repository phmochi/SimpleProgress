package patrick.SimpleProgress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Patrick on 5/10/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "hourlog.db";

    //table for tasks
    private static final String TABLE_TASKS = "tasks";
    private static final String TASK_ID = "_id";
    private static final String TASK_NAME = "name";
    private static final String TASK_GOAL = "goal";
    private static final String TASK_CYCLE = "cycle";

    //table for entries
    private static final String TABLE_ENTRIES = "entries";
    private static final String ENTRY_ID = "_id";
    private static final String ENTRY_TASK = "taskId";
    private static final String ENTRY_HOURS = "hours";
    private static final String ENTRY_DATE = "date";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL("CREATE TABLE " + TABLE_TASKS + "(" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_NAME + " TEXT NOT NULL," +
                TASK_GOAL + " REAL NOT NULL, " + TASK_CYCLE + " TEXT NOT NULL);");
        database.execSQL("CREATE TABLE " + TABLE_ENTRIES + " (" + ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ENTRY_TASK + " INTEGER NOT NULL, " + ENTRY_HOURS + " REAL NOT NULL, " + ENTRY_DATE + " INTEGER NOT NULL);");
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

        long id = db.insert(TABLE_ENTRIES, null, values);
        db.close();

        return (int) id;
    }

    public Task getTask(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[] { TASK_ID, TASK_NAME, TASK_GOAL }, TASK_ID + "=?",
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

    private ArrayList<Entry> addEntriesFromCursor(Cursor cursor){
        ArrayList<Entry> entryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do{
                int id = cursor.getInt(0);
                int taskId = cursor.getInt(1);
                double hours = cursor.getDouble(2);
                Date date = new Date(cursor.getLong(3));
                Entry entry = new Entry(id, taskId, hours, date);
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

        return new Task(id, name, goal, cycle);
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

        int deleted = db.delete(TABLE_ENTRIES, ENTRY_ID + " = ?", entryIds.toArray(new String[entryIds.size()]));
        Log.d("db", deleted + " rows deleted.");
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

        db.update(TABLE_ENTRIES, cv, "_id="+e.getId(), null);
        db.close();
    }
}
