package budget.kunal.com.budget;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;

import com.github.mikephil.charting.utils.ValueFormatter;
import com.rey.material.widget.TabPageIndicator;

import android.support.v4.view.PagerAdapter;

import com.rey.material.app.ToolbarManager;

import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.Arrays;

import android.widget.RelativeLayout;
import android.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.app.FragmentManager;

public class StatsFragment extends Fragment {
    private Activity mActivity;
    private TabPageIndicator tpi;
    private MyPagerAdapter mPagerAdapter;
    private ToolbarManager mToolbarManager;
    private ViewPager vp;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        vp = (ViewPager) view.findViewById(R.id.stats_vp);
        tpi = (TabPageIndicator) view.findViewById(R.id.stats_tpi);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        vp.setAdapter(mPagerAdapter);

        tpi.setViewPager(vp);
        tpi.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

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

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        int NumberOfPages = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public Fragment getItem(int position) {
            return StatsPagerFragment.init(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Last Week";
                case 1:
                    return "Last Month";
                case 2:
                    return "All Time";
                default:
                    return "undef";
            }
        }

    }


}
