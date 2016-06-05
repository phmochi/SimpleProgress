package patrick.SimpleProgress;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

//TODO: list position (drag and drop)
//TODO: Swipe left to show graphs on viewtask screen
//TODO: updating with shorter goal doesnt change spacing on task screen (need to redraw but delay issue)

public class MainActivity extends AppCompatActivity {

    static final int ADD_TASK_REQUEST = 0;
    static final int ADD_ENTRY_REQUEST = 1;
    static final int VIEW_TASK_REQUEST = 2;
    public static final String TASK = "task";

    private TaskAdapter taskAdapter;
    private TaskManager taskManager;
    private DBHelper db;

    private LinearLayout welcomeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = DBHelper.getInstance(this);;
        welcomeMsg = (LinearLayout) findViewById(R.id.welcomeMsgLayout);

        ArrayList<Task> tasks = db.getAllTasks();
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

        updateTaskManager();
        taskAdapter = new TaskAdapter(this, taskManager.getAllTasks());

        final ListView taskListView = (ListView) findViewById(R.id.taskListView);
        taskListView.setAdapter(taskAdapter);

        registerForContextMenu(taskListView);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                Task task = (Task) taskListView.getItemAtPosition(position);
                intent.putExtra(TASK, task);
                startActivityForResult(intent, ADD_ENTRY_REQUEST);
            }
        });

        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ViewTaskActivity.class);
                Task task = (Task) taskListView.getItemAtPosition(position);
                intent.putExtra(TASK, task);
                startActivityForResult(intent, VIEW_TASK_REQUEST);
                return true;
            }
        });

        updateView();
    }

    private void updateTaskManager(){
        taskManager = new TaskManager(db.getAllTasks());
        taskManager.addEntries(db.getAllEntries());
    }

    private void showWelcomeMsg(){
        welcomeMsg.setVisibility(View.VISIBLE);
    }

    private void hideWelcomeMsg(){
        welcomeMsg.setVisibility(View.GONE);
    }

    private void updateView(){
        updateTaskManager();
        updateAdapter();

        if (taskManager.getAllTasks().size() < 1){
            showWelcomeMsg();
        } else {
            hideWelcomeMsg();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            updateView();

         /*   Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    recreate();
                }
            }, 1);*/
        }
    }

    private void updateAdapter(){
        taskAdapter.clear();
        taskAdapter.addAll(taskManager.getAllTasks());
        taskAdapter.sort(new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                double lhsCompleted = lhs.getCompleted() / lhs.getGoal();
                double rhsCompleted = rhs.getCompleted() / rhs.getGoal();
                return Double.compare(lhsCompleted, rhsCompleted);
            }
        });
        taskAdapter.notifyDataSetChanged();
    }
}
