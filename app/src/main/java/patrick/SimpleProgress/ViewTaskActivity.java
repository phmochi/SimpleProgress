package patrick.SimpleProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;


public class ViewTaskActivity extends AppCompatActivity {

    private static final int EDIT_TASK_REQUEST = 0;
    private static final String TASK = "task";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DBHelper db;
    private Task task;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_overview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Bundle extras = getIntent().getExtras();

        db = DBHelper.getInstance(this);
        task = extras.getParcelable(TASK);
        updateView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewtask, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.delete_task:
                db.deleteTask(task.getId());
                db.deleteEntriesWithId(task.getId());
                setResult(RESULT_OK, new Intent());
                finish();
                return true;
            case R.id.edit_task:
                Intent intent = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
                intent.putExtra(TASK, task);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_TASK_REQUEST:
                    task = db.getTask(task.getId());
                    updateView();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateView() {
        getSupportActionBar().setTitle(task.getName());
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    public static class TaskOverviewFragment extends Fragment {
        private static final String TASK = "task";
        private static final String ENTRY = "entry";
        private static final int EDIT_ENTRY_REQUEST = 0;

        private EntryAdapter ea;
        private DBHelper db;
        private EntryManager entryManager;
        private TextView taskGoalView;
        private TextView taskCycleView;
        private Task task;
        private DecimalFormat format;

        public TaskOverviewFragment() {
        }

        public static TaskOverviewFragment newInstance(Task t) {
            TaskOverviewFragment fragment = new TaskOverviewFragment();
            Bundle args = new Bundle();
            args.putParcelable(TASK, t);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.content_view_task, container, false);
            format = new DecimalFormat("0.##");
            taskGoalView = (TextView) rootView.findViewById(R.id.viewTaskGoalText);
            taskCycleView = (TextView) rootView.findViewById(R.id.viewTaskCycleText);
            ListView entryListView = (ListView) rootView.findViewById(R.id.entryListView);

            task = getArguments().getParcelable(TASK);
            db = DBHelper.getInstance(getContext());

            updateTaskView();
            entryManager = new EntryManager(db.getActiveEntriesFor(task.getId()));
            ea = new EntryAdapter(getContext(), entryManager.getAllEntries());
            entryListView.setAdapter(ea);
            taskCycleView.setTextColor(ContextCompat.getColor(getContext(), R.color.softblue));

            registerForContextMenu(entryListView);

            entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.showContextMenu();
                }
            });

            return rootView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            getActivity().getMenuInflater().inflate(R.menu.entry_context, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Entry entry = ea.getItem(info.position);

            switch (item.getItemId()) {
                case R.id.entryEdit:
                    Intent intent = new Intent(getContext(), EditEntryActivity.class);
                    intent.putExtra(ENTRY, entry);
                    startActivityForResult(intent, EDIT_ENTRY_REQUEST);
                    return true;
                case R.id.entryDelete:
                    db.deleteEntry(entry);
                    updateEntryView();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case EDIT_ENTRY_REQUEST:
                    if (resultCode == RESULT_OK) {
                        updateEntryView();
                    }
                    break;
                default:
                    break;
            }
        }

        private void updateTaskView() {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(task.getName());
            taskGoalView.setText(format.format(task.getGoal()));
            taskCycleView.setText(task.getCycle().toString());
        }

        private void updateEntryView() {
            entryManager = new EntryManager(db.getActiveEntriesFor(task.getId()));
            ea.clear();
            ea.addAll(entryManager.getAllEntries());
            ea.notifyDataSetChanged();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TaskHistoryFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public TaskHistoryFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TaskHistoryFragment newInstance(int sectionNumber) {
            TaskHistoryFragment fragment = new TaskHistoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_view_task_history, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TaskOverviewFragment.newInstance(task);
                case 1:
                    return TaskHistoryFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "OVERVIEW";
                case 1:
                    return "HISTORY";
            }
            return null;
        }
    }
}
