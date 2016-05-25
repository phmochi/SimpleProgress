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

public class AddEntryActivity extends Activity {

    private int taskId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        Bundle extras = getIntent().getExtras();

        taskId = extras.getInt("taskId");
        Log.d("addentry", "task id: " + taskId);

        ImageButton addEntryButton = (ImageButton) findViewById(R.id.btnAddEntry);
        ImageButton cancelEntryButton = (ImageButton) findViewById(R.id.btnCancelEntry);
        final EditText toAddText = (EditText) findViewById(R.id.editToAdd);

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toAdd = toAddText.getText().toString().trim();
                Log.d("addentry", "to add: " + toAdd);

                Intent intent = new Intent();
                intent.putExtra("taskId", taskId);
                intent.putExtra("toAdd", toAdd);
                setResult(RESULT_OK, intent);
                finish();
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
