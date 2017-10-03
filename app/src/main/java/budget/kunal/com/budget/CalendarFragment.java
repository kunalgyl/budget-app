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

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import android.graphics.Color;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.HashMap;

import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.DatePickerController;
import me.nlmartian.silkcal.SimpleMonthAdapter;

public class CalendarFragment extends Fragment
        implements DatePickerController {
    private Activity mActivity;
    private DayPickerView calendarView;

    public ExpenditureListAdapterNoHeader mAdapter;
    public ExpenditureDBHelper dbHelper;
    private SuperRecyclerView mRecycler;
    private List<ExpenditureModel> mExpenditures;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        mRecycler = (SuperRecyclerView) view.findViewById(R.id.list);

        mRecycler.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(mActivity.getApplicationContext())
                        .color(Color.argb(255, 211, 211, 211))
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        dbHelper = ExpenditureDBHelper.getInstance(mActivity.getApplicationContext());

        mExpenditures = dbHelper.getExpenditures();

        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);

        HashMap countMap = new HashMap();
        SimpleMonthAdapter.CalendarDay selDay;

        ExpenditureModel element;
        Calendar cal = Calendar.getInstance();
        int day_t;
        int month_t;
        int year_t;
        for (ListIterator<ExpenditureModel> iter = mExpenditures.listIterator(); iter.hasNext(); ) {
            element = iter.next();
            if (element.amount != 0) {
                cal.setTimeInMillis(element.time);
                day_t = cal.get(Calendar.DAY_OF_MONTH);
                month_t = cal.get(Calendar.MONTH);
                year_t = cal.get(Calendar.YEAR);
                selDay = new SimpleMonthAdapter.CalendarDay(year_t, month_t, day_t);
                countMap.put(selDay, 1);
            }
        }

        calendarView = (DayPickerView) view.findViewById(R.id.calendar_view);
        calendarView.setController(this);
        calendarView.setCountMap(countMap);

        mAdapter = new ExpenditureListAdapterNoHeader(mActivity.getApplicationContext(), filterByDate(mExpenditures, year, month, day, 1), dbHelper);
        mRecycler.setLayoutManager(getLayoutManager());
        mRecycler.setAdapter(mAdapter);

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
    public int getMaxYear() {
        return 0;
    }

    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(mActivity.getApplicationContext());
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        mAdapter.setExpenditures(filterByDate(mExpenditures, year, month, day, 1));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }

    public List<ExpenditureModel> filterByDate(List<ExpenditureModel> expenditures, int year, int month, int day, int number) {
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        startDate.clear(Calendar.MINUTE);
        startDate.clear(Calendar.SECOND);
        startDate.clear(Calendar.MILLISECOND);
        // get start of the month
        startDate.set(year, month, day);
        long startTime = startDate.getTimeInMillis();
        long interval = number * 24 * 60 * 60 * 1000;
        List<ExpenditureModel> result = new ArrayList<>();
        ExpenditureModel element;
        for (ListIterator<ExpenditureModel> iter = expenditures.listIterator(); iter.hasNext(); ) {
            element = iter.next();
            if ((element.time >= startTime) && (element.time < startTime + interval)) {
                result.add(element);
            }
        }
        return result;
    }

}