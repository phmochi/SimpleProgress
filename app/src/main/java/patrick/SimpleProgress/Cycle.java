package patrick.SimpleProgress;

/**
 * Created by Patrick on 5/18/2016.
 */
public enum Cycle {
    daily, weekly, monthly;

    public static String[] getStringArray(){
        return new String[]{Cycle.daily.name(),Cycle.weekly.name(), Cycle.monthly.name()};
    }

}
