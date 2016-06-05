package patrick.SimpleProgress;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Patrick on 5/15/2016.
 */
public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context, ArrayList<Entry> entries){
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Entry entry = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry, parent, false);
        }

        TextView entryHours = (TextView) convertView.findViewById(R.id.entryHours);
        TextView entryDate = (TextView) convertView.findViewById((R.id.entryDate));
        TextView entryComment = (TextView) convertView.findViewById(R.id.entryComment);

        DecimalFormat format = new DecimalFormat("0.##");
        entryHours.setText(format.format(entry.getHours()));
        entryDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(entry.getDate()));
        entryComment.setText(entry.getComment());

        return convertView;
    }
}
