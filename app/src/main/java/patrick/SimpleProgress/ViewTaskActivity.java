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

public class ViewTaskActivity extends AppCompatActivity {

    static final int EDIT_ENTRY_REQUEST = 0;

    private DBHelper db;
    private int taskId;
    private String taskName;
    private String cycle;
    private EntryAdapter ea;
    private ArrayList<Entry> entriesToRemove;
    private ArrayList<Entry> entriesToUpdate;
    private EntryManager entryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(this);
        entriesToRemove = new ArrayList<>();
        entriesToUpdate = new ArrayList<>();
        ListView entryListView = (ListView) findViewById(R.id.entryListView);
        TextView taskCycleView = (TextView) findViewById(R.id.viewTaskCycleText);

        Bundle extras = getIntent().getExtras();
        taskId = extras.getInt("taskId");
        taskName = extras.getString("taskName");
        cycle = extras.getString("cycle");

        getSupportActionBar().setTitle(taskName);
        taskCycleView.setText(cycle);

        taskCycleView.setTextColor(ContextCompat.getColor(this, R.color.softblue));

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
    public void onBackPressed(){
        Intent intent = new Intent();
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
}
