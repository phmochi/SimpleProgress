package patrick.SimpleProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//TODO: active entries
public class ViewTaskActivity extends AppCompatActivity {

    static final int EDIT_ENTRY_REQUEST = 0;
    static final int EDIT_TASK_REQUEST = 1;

    private DBHelper db;
    private EntryAdapter ea;
    private ArrayList<Entry> entriesToRemove;
    private ArrayList<Entry> entriesToUpdate;
    private EntryManager entryManager;
    private Task task;
    private ListView entryListView;
    private TextView taskCycleView;
    private boolean taskUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(this);
        entriesToRemove = new ArrayList<>();
        entriesToUpdate = new ArrayList<>();
        entryListView = (ListView) findViewById(R.id.entryListView);
        taskCycleView = (TextView) findViewById(R.id.viewTaskCycleText);
        taskUpdated = false;

        Bundle extras = getIntent().getExtras();

        task = extras.getParcelable("task");

        updateView();
        taskCycleView.setTextColor(ContextCompat.getColor(this, R.color.softblue));

        entryManager = new EntryManager(db.getEntriesFor(task.getId()));
        ea = new EntryAdapter(this, entryManager.getAllEntries());
        entryListView.setAdapter(ea);

        registerForContextMenu(entryListView);

        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                view.showContextMenu();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entry_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Entry entry;

        switch (item.getItemId()){
            case R.id.entryEdit:
                Intent intent = new Intent(ViewTaskActivity.this, EditEntryActivity.class);
                entry = ea.getItem(info.position);
                intent.putExtra("entry", entry);
                startActivityForResult(intent, EDIT_ENTRY_REQUEST);
                return true;
            case R.id.entryDelete:
                entry = ea.getItem(info.position);
                entriesToRemove.add(entry);
                db.deleteEntry(entry);
                ea.remove(entry);
                ea.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_viewtask, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        Intent intent;

        switch(id){
            case R.id.delete_task:
                intent = new Intent();
                intent.putExtra("result", "delete");
                intent.putExtra("taskId", task.getId());
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.edit_task:
                intent = new Intent(this, EditTaskActivity.class);
                intent.putExtra("task", task);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("result", "complete");
        intent.putExtra("task", task);
        intent.putExtra("taskUpdated", taskUpdated);
        intent.putExtra("entriesToRemove", entriesToRemove);
        intent.putExtra("entriesToUpdate", entriesToUpdate);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case EDIT_ENTRY_REQUEST:
                if (resultCode == RESULT_OK){
                    Entry e = data.getParcelableExtra("entry");
                    updateEntry(e);
                }
                break;
            case EDIT_TASK_REQUEST:
                if (resultCode == RESULT_OK){
                    Task t = data.getParcelableExtra("task");
                    updateTask(t);
                    taskUpdated = true;
                }
            default:
                break;
        }
    }

    private void updateEntry(Entry e){
        db.updateEntry(e);
        entryManager.updateEntry(e);
        entriesToUpdate.add(e);
        ea.clear();
        ea.addAll(entryManager.getAllEntries());
        ea.notifyDataSetChanged();
    }

    private void updateTask(Task t){
        task = t;
        db.updateTask(t);
        updateView();
        updateActiveEntries();
    }

    private void updateView(){
        getSupportActionBar().setTitle(task.getName());
        taskCycleView.setText(task.getCycle().toString());
    }

    private void updateActiveEntries(){

    }
}
