package budget.kunal.com.budget;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AdapterView.OnItemSelectedListener;

import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.support.v4.view.MenuItemCompat;

import android.support.v7.app.ActionBarActivity;

import com.github.clans.fab.FloatingActionButton;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import android.graphics.Color;

import com.github.clans.fab.FloatingActionMenu;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public ExpenditureListAdapter mAdapter;
    public ExpenditureDBHelper dbHelper;
    private SuperRecyclerView mRecycler;
    private Comparator<ExpenditureModel> eComp = emCompTimeOld;
    public Activity mActivity;
    public FloatingActionMenu fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(mActivity.getApplicationContext(), AddRecievedActivity.class);
                i.putExtra("id", -1L);
                startActivityForResult(i, 0);
            }
        });

        view.findViewById(R.id.fab_spent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(mActivity.getApplicationContext(), AddSpentActivity.class);
                i.putExtra("id", -1L);
                startActivityForResult(i, 0);
            }
        });

        //ListView
        mRecycler = (SuperRecyclerView) view.findViewById(R.id.list);

        fab = (FloatingActionMenu) view.findViewById(R.id.fab);

        mRecycler.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(mActivity.getApplicationContext())
                        .color(Color.argb(255, 211, 211, 211))
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        dbHelper = ExpenditureDBHelper.getInstance(mActivity.getApplicationContext());

        mAdapter = new ExpenditureListAdapter(mActivity, dbHelper.getExpenditures(), dbHelper);
        mRecycler.setLayoutManager(getLayoutManager());
        mRecycler.setAdapter(mAdapter);

        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 4) {
                    if (dy > 0) {
                        fab.hideMenuButton(true);
                    } else {
                        fab.showMenuButton(true);
                    }
                }
            }
        });

        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuSortNew) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompTimeNew);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompTimeNew;
            return true;
        } else if (id == R.id.menuSortOld) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompTimeOld);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompTimeOld;
            return true;
        } else if (id == R.id.menuSortAmtHigh) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompAmtHigh);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompAmtHigh;
            return true;
        } else if (id == R.id.menuSortAmtLow) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompAmtLow);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompAmtLow;
            return true;
        } else if (id == R.id.menuSortImpHigh) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompImpHigh);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompImpHigh;
            return true;
        } else if (id == R.id.menuSortImpLow) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompImpLow);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompImpLow;
            return true;
        } else if (id == R.id.menuSortName) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompName);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompName;
            return true;
        } else if (id == R.id.menuSortPlace) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompPlace);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompPlace;
            return true;
        } else if (id == R.id.menuSortCategory) {
            List<ExpenditureModel> eList = dbHelper.getExpenditures();
            if(eList != null) Collections.sort(eList, emCompCategory);
            mAdapter.setExpenditures(eList);
            mAdapter.notifyDataSetChanged();
            eComp = emCompCategory;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                List<ExpenditureModel> list = dbHelper.getExpenditures();
                if(list != null) Collections.sort(list, eComp);
                mAdapter.setExpenditures(list);
                mAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    public void newExpenditure() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                List<ExpenditureModel> list = dbHelper.getExpenditures();
                if(list != null) Collections.sort(list, eComp);
                mAdapter.setExpenditures(list);
                mAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == mActivity.RESULT_OK) {
            newExpenditure();
        }
    }

    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(mActivity.getApplicationContext());
    }

    public void startExpenditureDetailsActivity(long id) {

    }

    //Comparators
    // Comparator for Newest Time
    public static Comparator<ExpenditureModel> emCompTimeNew = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            Long time1 = app1.time;
            Long time2 = app2.time;

            return time2.compareTo(time1);
        }
    };

    // Comparator for Oldest Time
    public static Comparator<ExpenditureModel> emCompTimeOld = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            Long time1 = app1.time;
            Long time2 = app2.time;

            return time1.compareTo(time2);
        }
    };

    // Comparator for Highest Amt
    public static Comparator<ExpenditureModel> emCompAmtHigh = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            Double d1 = app1.amount;
            Double d2 = app2.amount;

            return d2.compareTo(d1);
        }
    };

    // Comparator for Lowest Amt
    public static Comparator<ExpenditureModel> emCompAmtLow = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            Double d1 = app1.amount;
            Double d2 = app2.amount;

            return d1.compareTo(d2);
        }
    };

    // Comparator for Highest Imp
    public static Comparator<ExpenditureModel> emCompImpHigh = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            Integer i1 = app1.importance;
            Integer i2 = app2.importance;

            return i2.compareTo(i1);
        }
    };

    // Comparator for Lowest Imp
    public static Comparator<ExpenditureModel> emCompImpLow = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            Integer i1 = app1.importance;
            Integer i2 = app2.importance;

            return i1.compareTo(i2);
        }
    };

    // Comparator for Name
    public static Comparator<ExpenditureModel> emCompName = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            String name1 = app1.name;
            String name2 = app2.name;

            return name1.compareTo(name2);
        }
    };

    // Comparator for Place
    public static Comparator<ExpenditureModel> emCompPlace = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            String name1 = app1.place;
            String name2 = app2.place;

            if (name1 == null) return 1;
            if (name2 == null) return -1;

            return name1.compareTo(name2);
        }
    };

    // Comparator for Category
    public static Comparator<ExpenditureModel> emCompCategory = new Comparator<ExpenditureModel>() {

        public int compare(ExpenditureModel app1, ExpenditureModel app2) {

            String name1 = app1.category;
            String name2 = app2.category;

            if (name1 == null) return 1;
            if (name2 == null) return -1;

            return name1.compareTo(name2);
        }
    };
}