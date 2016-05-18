package patrick.hourlog;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEntryActivity extends AppCompatActivity {

    private int entryId;
    private Date date;
    private double hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        Bundle extras = getIntent().getExtras();

        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        final EditText editEntryHours = (EditText) findViewById(R.id.editEntryHours);
        final EditText editEntryDate = (EditText) findViewById(R.id.editEntryDate);
        final Calendar myCalendar = Calendar.getInstance();

        entryId = extras.getInt("id");
        date = new Date(extras.getLong("date"));
        hours = extras.getDouble("hours");

        Log.d("edit", "hours: " + hours);

        editEntryDate.setText(sdf.format(date));
        editEntryHours.setText(String.valueOf(hours));

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

                Intent intent = new Intent();
                intent.putExtra("id", entryId);
                intent.putExtra("hours", Double.parseDouble(hours));
                intent.putExtra("date", date.getTime());
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
