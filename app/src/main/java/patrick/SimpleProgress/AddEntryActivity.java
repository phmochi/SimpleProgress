package patrick.SimpleProgress;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddEntryActivity extends Activity {

    private DBHelper db;
    private Task task;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        Bundle extras = getIntent().getExtras();

        task = extras.getParcelable(MainActivity.TASK);
        db = DBHelper.getInstance(this);

        ImageButton addEntryButton = (ImageButton) findViewById(R.id.btnAddEntry);
        ImageButton cancelEntryButton = (ImageButton) findViewById(R.id.btnCancelEntry);
        final EditText toAddText = (EditText) findViewById(R.id.editToAdd);

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toAdd = toAddText.getText().toString().trim();
                Double toAddDbl = Double.parseDouble(toAdd);

                if (toAdd == "" || toAddDbl < 0 || toAddDbl >= 10000){
                    Toast.makeText(AddEntryActivity.this, "Please enter completed value between 0 and 10000", Toast.LENGTH_SHORT).show();
                } else {
                    db.addEntry(new Entry(task.getId(),toAddDbl));
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        cancelEntryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
}
