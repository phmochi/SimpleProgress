package patrick.hourlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class ViewTaskActivity extends AppCompatActivity {

    static final int EDIT_ENTRY_REQUEST = 0;

    private DBHelper db;
    private int taskId;
    private String taskName;
    private String cycle;
    private EntryAdapter ea;
    private ArrayList<Entry> entriesToRemove;
    private EntryManager entryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(this);
        entriesToRemove = new ArrayList<>();
        ListView entryListView = (ListView) findViewById(R.id.entryListView);
        TextView taskNameView = (TextView) findViewById(R.id.taskNameView);
        TextView taskCycleView = (TextView) findViewById(R.id.viewTaskCycleText);

        Bundle extras = getIntent().getExtras();
        taskId = extras.getInt("taskId");
        taskName = extras.getString("taskName");
        cycle = extras.getString("cycle");

        taskNameView.setText(taskName);
        taskCycleView.setText(cycle);
        entryManager = new EntryManager(db.getEntriesFor(taskId));
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
                intent.putExtra("id", entry.getId());
                intent.putExtra("date", entry.getDate().getTime());
                intent.putExtra("hours", entry.getHours());
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
    public void onBackPressed(){
        Log.d("view", "on destroy");
        Intent intent = new Intent();
        intent.putExtra("entriesToRemove", entriesToRemove);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case EDIT_ENTRY_REQUEST:
                if (resultCode == RESULT_OK){
                    int id = data.getIntExtra("id", -1);
                    Date date = new Date(data.getLongExtra("date", -1));
                    double hours = data.getDoubleExtra("hours", -1);
                    updateEntry(id, date, hours);
                }
                break;
            default:
                break;
        }
    }

    private void updateEntry(int id, Date date, double hours){
        db.updateEntry(id, date, hours);
        entryManager.updateEntry(id, date, hours);
        ea.notifyDataSetChanged();
    }
}
