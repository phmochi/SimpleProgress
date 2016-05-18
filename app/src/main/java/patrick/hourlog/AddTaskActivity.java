package patrick.hourlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddTaskActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText goalText;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button addTaskButton = (Button) findViewById(R.id.btnAddTask);
        Button cancelButton = (Button) findViewById(R.id.btnCancel);
        nameText = (EditText) findViewById(R.id.editTaskName);
        goalText = (EditText) findViewById(R.id.editTaskGoal);
        spinner = (Spinner) findViewById(R.id.addTaskSpinner);
        String[] values = Cycle.getStringArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);

        addTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String name = nameText.getText().toString().trim();
                String goal = goalText.getText().toString().trim();
                String cycle = spinner.getSelectedItem().toString();

                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("goal", goal);
                intent.putExtra("cycle", cycle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }

}
