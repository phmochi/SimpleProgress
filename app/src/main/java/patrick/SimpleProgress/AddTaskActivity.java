package patrick.SimpleProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AddTaskActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText goalText;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameText = (EditText) findViewById(R.id.addTaskNameEdit);
        goalText = (EditText) findViewById(R.id.addTaskGoalEdit);
        spinner = (Spinner) findViewById(R.id.addTaskSpinner);
        String[] values = Cycle.getStringArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);
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
                    intent.putExtra("name", name);
                    intent.putExtra("goal", goal);
                    intent.putExtra("cycle", cycle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (name.equals("")) {
                    Toast.makeText(AddTaskActivity.this, "Please enter task name", Toast.LENGTH_SHORT).show();
                } else if (goal.equals("")){
                    Toast.makeText(AddTaskActivity.this, "Please enter numerical goal", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Please enter a goal between 0 and 10000", Toast.LENGTH_SHORT).show();
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
