package patrick.SimpleProgress;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditEntryActivity extends AppCompatActivity {

    private Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        Bundle extras = getIntent().getExtras();

        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        final EditText editEntryHours = (EditText) findViewById(R.id.editEntryHours);
        final EditText editEntryDate = (EditText) findViewById(R.id.editEntryDate);
        final Calendar myCalendar = Calendar.getInstance();

        entry = extras.getParcelable("entry");

        editEntryDate.setText(sdf.format(entry.getDate()));
        editEntryHours.setText(String.valueOf(entry.getHours()));

        Button saveBtn = (Button) findViewById(R.id.editEntrySaveBtn);
        Button cancelBtn = (Button) findViewById(R.id.editEntryCancelBtn);

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                editEntryDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        editEntryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditEntryActivity.this, dateListener, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hours = editEntryHours.getText().toString().trim();
                String dateString = editEntryDate.getText().toString().trim();
                Date date = sdf.parse(dateString, new ParsePosition(0));
                entry.setDate(date);
                entry.setHours(Double.parseDouble(hours));

                Intent intent = new Intent();
                intent.putExtra("entry", entry);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}