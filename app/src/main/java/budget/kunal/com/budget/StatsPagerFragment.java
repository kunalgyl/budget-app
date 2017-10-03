package budget.kunal.com.budget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.DashPathEffect;

import android.os.Build;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ExpoEase;
import com.db.chart.view.LineChartView;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.github.mikephil.charting.data.LineDataSet;
import com.db.chart.view.animation.style.DashAnimation;

import android.annotation.SuppressLint;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.text.DecimalFormat;


public class StatsPagerFragment extends Fragment {

    Activity mActivity;
    int fragPosition;
    public ExpenditureDBHelper dbHelper;

    private StackBarChartView mChartOne;

    long weekStart;
    long monthStart;
    long yearStart;

    private LineChartView mChartTwo;

    String[] spendCategories = {"Others", "Transport", "Gas", "Food", "Entertmt", "Purch", "Bills", "Groceries"};
    String[] labelsWeek = {"Mon", "Tue", "Wed", "Thurs", "Fri", "Sat", "Sun"};
    String[] labelsMonth = {"1st", "4th", "7th", "10th", "13th", "16th", "19th", "22nd", "25th", "28th", "31st"};
    String[] labelsYear = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};


    float[][] vals = new float[3][spendCategories.length];
    float[][] weekVals = new float[2][7];
    float[][] monthVals = new float[2][11];
    float[][] yearVals = new float[2][12];

    Double amtReceived = 0.0;

    private ViewPager vp;

    static StatsPagerFragment init(int val) {
        StatsPagerFragment Frag = new StatsPagerFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        Frag.setArguments(args);
        return Frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragPosition = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats_pager, container, false);
        dbHelper = ExpenditureDBHelper.getInstance(mActivity.getApplicationContext());

        List<ExpenditureModel> expenditureModelList = dbHelper.getExpenditures();
        dbHelper.close();

        Calendar beginWeek = Calendar.getInstance();
        beginWeek.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        beginWeek.clear(Calendar.MINUTE);
        beginWeek.clear(Calendar.SECOND);
        beginWeek.clear(Calendar.MILLISECOND);
        // get start of this week in milliseconds
        beginWeek.set(Calendar.DAY_OF_WEEK, beginWeek.getFirstDayOfWeek());
        weekStart = beginWeek.getTimeInMillis();

        Calendar beginMonth = Calendar.getInstance();
        beginMonth.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        beginMonth.clear(Calendar.MINUTE);
        beginMonth.clear(Calendar.SECOND);
        beginMonth.clear(Calendar.MILLISECOND);
        // get start of the month
        beginMonth.set(Calendar.DAY_OF_MONTH, 1);
        monthStart = beginMonth.getTimeInMillis();

        Calendar beginYear = Calendar.getInstance();
        beginYear.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        beginYear.clear(Calendar.MINUTE);
        beginYear.clear(Calendar.SECOND);
        beginYear.clear(Calendar.MILLISECOND);
        // get start of the month
        beginYear.set(Calendar.DAY_OF_YEAR, 1);
        yearStart = beginYear.getTimeInMillis();

        setData(fragPosition, expenditureModelList);

        //mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mChartOne = (StackBarChartView) view.findViewById(R.id.stats_stacked);
        mChartTwo = (LineChartView) view.findViewById(R.id.stats_line);

        produceOne(mChartOne);
        produceTwo(mChartTwo);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public Boolean thisWeek(ExpenditureModel expenditureModel) {
        if (expenditureModel.time >= weekStart) return true;
        return false;
    }

    public Boolean thisMonth(ExpenditureModel expenditureModel) {
        if (expenditureModel.time >= monthStart) return true;
        return false;
    }

    public Boolean thisYear(ExpenditureModel expenditureModel) {
        if (expenditureModel.time >= yearStart) return true;
        return false;
    }

    public void addElement(ExpenditureModel element) {
        Double amtDouble = element.amount;
        float amtFloat = amtDouble.floatValue();
        for (int i = 0; i < spendCategories.length; i++) {
            if (element.category.equals(spendCategories[i])) {
                if (element.importance <= 30) {
                    vals[0][i] += amtFloat;
                } else if (element.importance <= 60) {
                    vals[1][i] += amtFloat;
                } else {
                    vals[2][i] += amtFloat;
                }
            }
        }
        if (element.category.equals("Received")) {
            amtReceived += amtFloat;
        }
    }

    public void addWeek(ExpenditureModel expenditure) {
        if (thisWeek(expenditure)) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(expenditure.time);
            int tmp = cal.get(Calendar.DAY_OF_WEEK);
            int day = 0;
            if(tmp == Calendar.MONDAY) {
                day = 0;
            } else if(tmp == Calendar.TUESDAY) {
                day = 1;
            } else if(tmp == Calendar.WEDNESDAY) {
                day = 2;
            } else if(tmp == Calendar.THURSDAY) {
                day = 3;
            } else if(tmp == Calendar.FRIDAY) {
                day = 4;
            } else if(tmp == Calendar.SATURDAY) {
                day = 5;
            } else if(tmp == Calendar.SUNDAY) {
                day = 6;
            }
            Double amt = expenditure.amount;
            float amt_f = amt.floatValue();
            if (expenditure.category.equals("Received")) weekVals[0][day] += amt_f;
            else {
                weekVals[0][day] -= amt_f;
                weekVals[1][day] += amt_f;
            }
        }
    }

    public void addMonth(ExpenditureModel expenditure) {
        if (thisMonth(expenditure)) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(expenditure.time);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int index = (day - 1) / 3;
            Double amt = expenditure.amount;
            float amt_f = amt.floatValue();
            if (expenditure.category.equals("Received")) monthVals[0][index] += amt_f;
            else {
                monthVals[0][index] -= amt_f;
                monthVals[1][index] +=amt_f;
            }

        }
    }

    public void addYear(ExpenditureModel expenditure) {
        if (thisYear(expenditure)) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(expenditure.time);
            int tmp = cal.get(Calendar.MONTH);
            int month = 0;
            if(tmp == Calendar.JANUARY) {
                month = 0;
            } else if(tmp == Calendar.FEBRUARY) {
                month = 1;
            } else if(tmp == Calendar.MARCH) {
                month = 2;
            } else if(tmp == Calendar.APRIL) {
                month = 3;
            } else if(tmp == Calendar.MAY) {
                month = 4;
            } else if(tmp == Calendar.JUNE) {
                month = 5;
            } else if(tmp == Calendar.JULY) {
                month = 6;
            } else if(tmp == Calendar.AUGUST) {
                month = 7;
            } else if(tmp == Calendar.SEPTEMBER) {
                month = 8;
            } else if(tmp == Calendar.OCTOBER) {
                month = 9;
            } else if(tmp == Calendar.NOVEMBER) {
                month = 10;
            } else if(tmp == Calendar.DECEMBER) {
                month = 11;
            }
            Double amt = expenditure.amount;
            float amt_f = amt.floatValue();
            if (expenditure.category.equals("Received")) yearVals[0][month] += amt_f;
            else {
                yearVals[0][month] -= amt_f;
                yearVals[1][month] +=amt_f;
            }
        }
    }

    private void produceOne(ChartView chart) {
        StackBarChartView stackedChart = (StackBarChartView) chart;

        BarSet stackBarSet = new BarSet(spendCategories, vals[0]);
        stackBarSet.setColor(Color.parseColor("#ff7a57"));
        stackedChart.addData(stackBarSet);

        stackBarSet = new BarSet(spendCategories, vals[1]);
        stackBarSet.setColor(Color.parseColor("#ffcc6a"));
        stackedChart.addData(stackBarSet);

        stackBarSet = new BarSet(spendCategories, vals[2]);
        stackBarSet.setColor(Color.parseColor("#a1d949"));
        stackedChart.addData(stackBarSet);

        stackedChart.setBarSpacing(Tools.fromDpToPx(15));
        stackedChart.setRoundCorners(Tools.fromDpToPx(1));

        float max = 10;
        float temp = 0;

        for (int i = 0; i < spendCategories.length; i++) {
            temp = vals[0][i] + vals[1][i] + vals[2][i];
            if (temp > max) max = temp;
        }

        int step = Math.round(max / 10);

        stackedChart.setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setStep(step);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7};
        stackedChart.show(new Animation()
                .setOverlap(.5f, order));
        //.show()
        ;
    }

    public float maxFloatArray(float[] arr) {
        float max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }

    public void produceTwo(ChartView chart) {
        LineChartView lineChart = (LineChartView) chart;
        LineSet dataSet;
        LineSet dataSetSpent;
        float max = 0;
        float max_t = 0;

        switch (fragPosition) {
            case 0:
                dataSet = new LineSet(labelsWeek, weekVals[0]);
                dataSetSpent = new LineSet(labelsWeek, weekVals[1]);
                max = maxFloatArray(weekVals[1]);
                max_t = maxFloatArray(weekVals[0]);
                if(max_t > max) max = max_t;
                break;
            case 1:
                dataSet = new LineSet(labelsMonth, monthVals[0]);
                dataSetSpent = new LineSet(labelsMonth, monthVals[1]);
                max = maxFloatArray(monthVals[1]);
                max_t = maxFloatArray(monthVals[0]);
                if(max_t > max) max = max_t;
                break;
            default:
                dataSet = new LineSet(labelsYear, yearVals[0]);
                dataSetSpent = new LineSet(labelsYear, yearVals[1]);
                max = maxFloatArray(yearVals[1]);
                max_t = maxFloatArray(yearVals[0]);
                if(max_t > max) max = max_t;
                break;
        }

        Calendar now = Calendar.getInstance();

        int index = 0;
        if(fragPosition == 0) {
            index = now.get(Calendar.DAY_OF_WEEK) - 1;
        } else if(fragPosition == 1) {
            index = (now.get(Calendar.DAY_OF_MONTH) - 1) / 3;
        } else {
            index = now.get(Calendar.MONTH) - 1;
        }

        dataSet.setColor(getResources().getColor(R.color.blue))
                .setThickness(Tools.fromDpToPx(3))
                .setDashed(new float[]{10, 10})
                .setSmooth(false)
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeColor(getResources().getColor(R.color.blue))
                .setDotsColor(getResources().getColor(R.color.blue));
                //.endAt(index);

        dataSetSpent.setThickness(Tools.fromDpToPx(3))
                    .setColor(getResources().getColor(R.color.red))
                    .setDotsRadius(Tools.fromDpToPx(5))
                    .setDotsStrokeColor(getResources().getColor(R.color.red))
                    .setDotsColor(getResources().getColor(R.color.red));
                    //.endAt(index);

        chart.addData(dataSet);
        chart.addData(dataSetSpent);

        if (max <= 0) max = 10;

        int step = Math.round(max / 10);

        chart.setAxisLabelsSpacing(Tools.fromDpToPx(8))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                        //.setLabelsColor(Color.parseColor("#e08b36"))
                .setXAxis(false)
                .setYAxis(false)
                .setStep(step);


        Animation anim = new Animation();

        chart.show(anim);
        chart.animateSet(0, new DashAnimation());
    }

    public void foldArr(float[] arr) {
        for (int i = 0; i + 1 < arr.length; i++) {
            arr[i + 1] = arr[i + 1] + arr[i];
        }
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] < 0) arr[i] = 0;
        }
    }


    public void setData(int position, List<ExpenditureModel> eList) {
        ExpenditureModel element;
        for (ListIterator<ExpenditureModel> iter = eList.listIterator(); iter.hasNext(); ) {
            element = iter.next();
            if (position == 0) {
                if (thisWeek(element)) {
                    addElement(element);
                } else continue;
            } else if (position == 1) {
                if (thisMonth(element)) {
                    addElement(element);
                } else continue;
            } else {
                addElement(element);
            }
            addWeek(element);
            addMonth(element);
            addYear(element);
        }
        foldArr(weekVals[0]);
        foldArr(monthVals[0]);
        foldArr(yearVals[0]);
        foldArr(weekVals[1]);
        foldArr(monthVals[1]);
        foldArr(yearVals[1]);
    }

}