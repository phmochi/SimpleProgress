package patrick.SimpleProgress;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditEntryActivity extends AppCompatActivity {

    private Entry entry;
    private SimpleDateFormat sdf;
    private EditText editEntryHours;
    private EditText editEntryDate;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        sdf = new SimpleDateFormat("MM/dd/yyyy");
        editEntryHours = (EditText) findViewById(R.id.editEntryHours);
        editEntryDate = (EditText) findViewById(R.id.editEntryDate);
        myCalendar = Calendar.getInstance();

        entry = extras.getParcelable("entry");

        editEntryDate.setText(sdf.format(entry.getDate()));
        editEntryHours.setText(String.valueOf(entry.getHours()));

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
                String hours = editEntryHours.getText().toString().trim();
                String dateString = editEntryDate.getText().toString().trim();
                Date date = sdf.parse(dateString, new ParsePosition(0));

                if (date != null && !hours.equals("")) {
                    entry.setDate(date);
                    entry.setHours(Double.parseDouble(hours));

                    Intent intent = new Intent();
                    intent.putExtra("entry", entry);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (date == null){
                    Toast.makeText(EditEntryActivity.this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditEntryActivity.this, "Please enter completed value", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.btnCancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
