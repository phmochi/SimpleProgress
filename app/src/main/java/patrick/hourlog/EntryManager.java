package patrick.hourlog;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Patrick on 5/17/2016.
 */
public class EntryManager {
    private HashMap<Integer, Entry> entryMap;

    public EntryManager(ArrayList<Entry> entries){
        entryMap = new HashMap<>();
        addEntries(entries);
    }

    public void addEntries(ArrayList<Entry> entries){
        for (Entry e: entries){
            entryMap.put(e.getId(), e);
        }
    }

    public void removeEntry(Entry e) {
        entryMap.remove(e.getId());
    }

    public void updateEntry(int id, Date date, double hours){
        Log.d("em", "id: " + id);
        Entry e = entryMap.get(id);
        e.setDate(date);
        e.setHours(hours);
    }

    public ArrayList<Entry> getAllEntries(){
        return new ArrayList<>(entryMap.values());
    }
}
