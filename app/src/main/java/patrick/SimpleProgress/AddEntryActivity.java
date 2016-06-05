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
        final EditText sixtyText = (EditText) findViewById(R.id.editToAddSixty);
        final EditText commentText = (EditText) findViewById(R.id.editComment);

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toAdd = toAddText.getText().toString().trim();
                String toAddSixty = sixtyText.getText().toString().trim();
                Double toAddDbl = 0.0;

                if (toAdd.length() > 0){
                    toAddDbl += Double.parseDouble(toAdd);
                }

                if (toAddSixty.length() > 0){
                    toAddDbl += Double.parseDouble(toAddSixty)/60;
                }

                String comment = commentText.getText().toString().trim();

                if (toAddDbl < 0 || toAddDbl >= 10000){
                    Toast.makeText(AddEntryActivity.this, "Please enter completed value between 0 and 10000", Toast.LENGTH_SHORT).show();
                } else if (comment.length() > 50){
                    Toast.makeText(AddEntryActivity.this, "Please keep comments under 50 characters", Toast.LENGTH_SHORT).show();
                } else {
                    db.addEntry(new Entry(task.getId(),toAddDbl, comment));
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
