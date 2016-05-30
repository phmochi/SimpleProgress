package patrick.SimpleProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

//TODO: list position (drag and drop)
//TODO: Only show active entries in entrylist
//TODO: Swipe left to show graphs on viewtask screen
//TODO: updating with shorter goal doesnt change spacing on task screen

public class MainActivity extends AppCompatActivity {

    static final int ADD_TASK_REQUEST = 0;
    static final int ADD_ENTRY_REQUEST = 1;
    static final int VIEW_TASK_REQUEST = 2;

    private TaskAdapter taskAdapter;
    private TaskManager taskManager;
    private DBHelper db;

    private LinearLayout welcomeMsg;
    private boolean editingTasks;
    MenuItem editItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(this);

        ArrayList<Task> tasks = db.getAllTasks();
        welcomeMsg = (LinearLayout) findViewById(R.id.welcomeMsgLayout);

        //if there are no tasks, show the welcome message
        if (tasks.size() < 1){
            showWelcomeMsg();
        }

        //initialize task and entry managers and add listeners
        taskManager = new TaskManager(tasks);
        editingTasks = false;

        Log.d("tasks", "there are: " + tasks.size() + "tasks");
        for (Task t: tasks){
            String log = "id: " + t.getId() + " name: " + t.getName() + " goal: " + t.getGoal() + " cycle: " + t.getCycle().toString();
            Log.d("tasks", log);
        }

        Log.d("Reading: ", "Reading all entries ..");
        ArrayList<Entry> entries = db.getAllEntries();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        for (Entry e: entries){
            String log = "id: " + e.getId() + " task: " + e.getTaskId() + " date: " + sdf.format(e.getDate()) + " hours: " + e.getHours();
            Log.d("entries", log);
        }

        taskManager.addEntries(entries);

        taskAdapter = new TaskAdapter(this, taskManager.getAllTasks());

        final ListView taskListView = (ListView) findViewById(R.id.taskListView);
        taskListView.setAdapter(taskAdapter);

        registerForContextMenu(taskListView);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                Task task = (Task) taskListView.getItemAtPosition(position);
                intent.putExtra("taskId", task.getId());
                startActivityForResult(intent, ADD_ENTRY_REQUEST);
            }
        });

        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ViewTaskActivity.class);
                Task task = (Task) taskListView.getItemAtPosition(position);
                intent.putExtra("task", task);
                startActivityForResult(intent, VIEW_TASK_REQUEST);

                return true;
            }
        });
    }

    private void showWelcomeMsg(){
        welcomeMsg.setVisibility(View.VISIBLE);
        editItem.setVisible(false);
    }

    private void hideWelcomeMsg(){
        welcomeMsg.setVisibility(View.GONE);
        editItem.setVisible(true);
        editingTasks = false;
    }

    public void addTask(Task task){
        //adding our first task so hide welcome message
        if (taskManager.getAllTasks().size() < 1){
            hideWelcomeMsg();
        }

        task.setId(db.addTask(task));
        taskManager.addTask(task);
        updateAdapter();
    }

    private void deleteTask(int taskId){
        db.deleteTask(taskId);
        db.deleteEntriesWithId(taskId);
        taskManager.removeTask(taskId);
        updateAdapter();

        //deleted all tasks so show welcome message
        if (taskManager.getAllTasks().size() < 1){
            showWelcomeMsg();
        }
    }

    public void addEntry(Entry entry){
        entry.setId(db.addEntry(entry));
        taskManager.addEntry(entry);
        updateAdapter();
    }

    private void deleteEntries(ArrayList<Entry> entries){
        taskManager.deleteEntries(entries);
        updateAdapter();
    }

    private void updateEntries(ArrayList<Entry> entries){
        taskManager.updateEntries(entries);
        updateAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        editItem = menu.findItem(R.id.edit_tasks);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.add_task:
                Intent intent = new Intent(this, AddTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
                return true;
            case R.id.edit_tasks:
                onEditClicked();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case ADD_TASK_REQUEST:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    String goal = data.getStringExtra("goal");
                    String cycle = data.getStringExtra("cycle");
                    addTask(new Task(name, Double.parseDouble(goal), Cycle.valueOf(cycle)));
                }
                break;
            case ADD_ENTRY_REQUEST:
                if (resultCode == RESULT_OK){
                    int taskId = data.getIntExtra("taskId", -1);
                    double toAdd = Double.parseDouble(data.getStringExtra("toAdd"));
                    if (taskId >= 1) {
                        addEntry(new Entry(taskId, toAdd));
                    }else{
                        throw new IllegalArgumentException();
                    }
                }
                break;
            case VIEW_TASK_REQUEST:
                if (resultCode == RESULT_OK){
                    String result = data.getStringExtra("result");

                    switch (result){
                        case "complete":
                            ArrayList<Entry> entriesToRemove = data.getParcelableArrayListExtra("entriesToRemove");
                            ArrayList<Entry> entriesToUpdate = data.getParcelableArrayListExtra("entriesToUpdate");
                            Boolean taskUpdated = data.getBooleanExtra("taskUpdated", false);

                            deleteEntries(entriesToRemove);
                            updateEntries(entriesToUpdate);

                            if (taskUpdated) {
                                Task task = data.getParcelableExtra("task");
                                ArrayList<Entry> entries = db.getEntriesFor(task.getId());
                                taskManager.updateTask(task, entries);
                                updateAdapter();
                            }
                        case "delete":
                            int taskId = data.getIntExtra("taskId", -1);
                            deleteTask(taskId);
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void updateAdapter(){
        taskAdapter.clear();
        taskAdapter.addAll(taskManager.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }

    private void onEditClicked(){
        if (editingTasks){
            editItem.setIcon(R.drawable.ic_create_white_24dp);
            editingTasks = false;
        } else {
            editItem.setIcon(R.drawable.ic_done_white_24dp);
            editingTasks = true;
        }
    }
}
