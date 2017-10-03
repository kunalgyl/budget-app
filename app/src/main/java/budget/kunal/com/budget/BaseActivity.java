package budget.kunal.com.budget;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;
import android.content.Intent;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;

/**
 * Created by kunal on 7/14/15.
 */
public class BaseActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";
    private static final String FRAGMENT_TODO_LIST_VIEW = "todo list view";

    public NavigationDrawerFragment mNavigationDrawerFragment;
    protected FrameLayout frameLayout;
    public Toolbar mToolbar;
    public MainFragment mainFragment;
    public int currPosition = 0;
    static Menu menu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_base_layout);

        frameLayout = (FrameLayout)findViewById(R.id.rootLayout);

        FragmentManager fm = getSupportFragmentManager();

        mainFragment = new MainFragment();

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.rootLayout, mainFragment);
        ft.commit();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.main_title);
        getSupportActionBar().setElevation(0);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_base, menu);

            this.menu = menu;

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch(position) {
            case 0:
                if(currPosition == 0) break;
                ft.replace(R.id.rootLayout, new MainFragment());
                ft.commit();

                mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle(R.string.main_title);
                getSupportActionBar().setElevation(0);

                mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
                mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
                currPosition = 0;
                break;

            case 1:
                if(currPosition == 2) break;
                //todo - calc
                break;

            case 2:
                if(currPosition == 2) break;
                ft.replace(R.id.rootLayout, new StatsFragment());
                ft.commit();

                mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle(R.string.title_activity_stats);
                getSupportActionBar().setElevation(0);

                mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
                mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
                currPosition = 2;
                break;

            case 3:
                if(currPosition == 3) break;
                ft.replace(R.id.rootLayout, new CalendarFragment());
                ft.commit();

                mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle(R.string.title_activity_calendar);
                getSupportActionBar().setElevation(0);

                mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
                mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
                currPosition = 3;
                break;
            case 4:
                if(currPosition == 4) break;
                ft.replace(R.id.rootLayout, new TodoFragment(), FRAGMENT_TODO_LIST_VIEW);
                ft.commit();

                mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle(R.string.title_activity_todo);
                getSupportActionBar().setElevation(0);

                mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
                mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
                currPosition = 4;
                break;
        }
    }

    //Refresh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mainFragment.newExpenditure();
        }
    }

    /**
     * This method will be called when a group item is removed
     *
     * @param groupPosition The position of the group item within data set
     */
    public void onGroupItemRemoved(int groupPosition) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_group_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a child item is removed
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemRemoved(int groupPosition, int childPosition) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_child_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.snackbar_action_color_done));
        snackbar.show();
    }

    public void onGroupItemClicked(int groupPosition) {
        //?
    }

    public void onChildItemClicked(int groupPosition, int childPosition) {
        //?
    }
}
