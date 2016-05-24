package patrick.SimpleProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Patrick on 5/17/2016.
 */
public class EntryManager {
    private HashMap<Integer, Entry> entryMap;

    public EntryManager(){
        entryMap = new HashMap<>();
    }

    public EntryManager(ArrayList<Entry> entries){
        entryMap = new HashMap<>();
        addEntries(entries);
    }

    public void addEntry(Entry e){
        entryMap.put(e.getId(), e);
    }

    public void addEntries(ArrayList<Entry> entries){
        for (Entry e: entries){
            entryMap.put(e.getId(), e);
        }
    }

    public void removeEntry(Entry e) {
        entryMap.remove(e.getId());
    }

    public void updateEntry(Entry updatedEntry){
        int key = updatedEntry.getId();
        if (entryMap.containsKey(key)) {
            Entry e = entryMap.get(updatedEntry.getId());
            e.setDate(updatedEntry.getDate());
            e.setHours(updatedEntry.getHours());
        } else {
            entryMap.put(key, updatedEntry);
        }
    }

    public ArrayList<Entry> getAllEntries(){

        ArrayList<Entry> entries = new ArrayList<> (entryMap.values());

        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        return entries;
    }

    public double getCompleted(){
        ArrayList<Entry> entries = getAllEntries();
        double completed = 0;

        for (Entry e: entries){
            completed += e.getHours();
        }

        return completed;
    }
}
