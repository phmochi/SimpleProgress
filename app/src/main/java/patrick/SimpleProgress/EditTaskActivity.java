package patrick.SimpleProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class EditTaskActivity extends AppCompatActivity {

    private Task task;
    private EditText nameText;
    private EditText goalText;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        nameText = (EditText) findViewById(R.id.editTaskNameEdit);
        goalText = (EditText) findViewById(R.id.editTaskGoalEdit);
        spinner = (Spinner) findViewById(R.id.editTaskCycleSpinner);
        String[] values = Cycle.getStringArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);

        task = extras.getParcelable("task");

        nameText.setText(task.getName());
        goalText.setText(String.valueOf(task.getGoal()));

        int spinnerPosition = adapter.getPosition(task.getCycle().toString());
        spinner.setSelection(spinnerPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case R.id.btnOk:
                String name = nameText.getText().toString().trim();
                String goal = goalText.getText().toString().trim();
                String cycle = spinner.getSelectedItem().toString();

                if (!name.equals("") && !goal.equals("") && isValidGoal(goal)) {
                    Intent intent = new Intent();
                    intent.putExtra("task", new Task(task.getId(), name, Double.parseDouble(goal), Cycle.valueOf(cycle)));
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (name.equals("")) {
                    Toast.makeText(EditTaskActivity.this, "Please enter task name", Toast.LENGTH_SHORT).show();
                } else if (goal.equals("")){
                    Toast.makeText(EditTaskActivity.this, "Please enter time completed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditTaskActivity.this, "Please enter a goal between 0 and 10000", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.btnCancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isValidGoal(String goalString){
        double goal = Double.parseDouble(goalString);

        if (goal >= 0 && goal < 10000){
            return true;
        }
        return false;
    }
}