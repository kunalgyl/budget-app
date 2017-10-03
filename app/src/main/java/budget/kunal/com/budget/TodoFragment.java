package budget.kunal.com.budget;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TodoFragment extends Fragment {

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";

    public TodoDBHelper dbHelper;
    public TodoChildDBHelper dbHelperChild;
    public Activity mActivity;
    public FloatingActionButton fab;
    private Comparator<TodoModel> tComp = tCompImpHigh;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        view.findViewById(R.id.todo_fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(mActivity.getApplicationContext(), AddTodoActivity.class);
                i.putExtra("id", -1L);
                startActivityForResult(i, 0);
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.todo_fab_add);

        //ListView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.todo_list);
        mLayoutManager = new LinearLayoutManager(getActivity());

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);

        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        /* mRecycler.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(mActivity.getApplicationContext())
                        .color(Color.argb(255, 211, 211, 211))
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build()); */
        dbHelper = new TodoDBHelper(mActivity.getApplicationContext());
        dbHelperChild = new TodoChildDBHelper(mActivity.getApplicationContext());

        final TodoListAdapter myItemAdapter =
                new TodoListAdapter(mActivity, mRecyclerViewExpandableItemManager,dbHelper.getTodos(), dbHelperChild.getTodos(), dbHelper, dbHelperChild);

        myItemAdapter.setEventListener(new TodoListAdapter.EventListener() {
            @Override
            public void onGroupItemRemoved(int groupPosition) {
                ((MainActivity) getActivity()).onGroupItemRemoved(groupPosition);
            }

            @Override
            public void onChildItemRemoved(int groupPosition, int childPosition) {
                ((MainActivity) getActivity()).onChildItemRemoved(groupPosition, childPosition);
            }

            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                onItemViewClick(v, pinned);
            }
        });

        mAdapter = myItemAdapter;
        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(myItemAdapter);
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 4) {
                    if (dy > 0) {
                        fab.hide(true);
                    } else {
                        fab.show(true);
                    }
                }
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current state to support screen rotation, etc...
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager.release();
            mRecyclerViewExpandableItemManager = null;
        }

        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }


        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
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
        inflater.inflate(R.menu.todo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.todoImpHigh) {
            List<TodoModel> tList = dbHelper.getTodos();
            Collections.sort(tList, tCompImpHigh);
            mAdapter.setTodos(tList, dbHelperChild.getTodos());
            mAdapter.notifyDataSetChanged();
            tComp = tCompImpHigh;
            return true;
        } else if (id == R.id.todoImpLow) {
            List<TodoModel> tList = dbHelper.getTodos();
            Collections.sort(tList, tCompImpLow);
            mAdapter.setTodos(tList, dbHelperChild.getTodos());
            mAdapter.notifyDataSetChanged();
            tComp = tCompImpLow;
            return true;
        } else if (id == R.id.todoSortName) {
            List<TodoModel> tList = dbHelper.getTodos();
            Collections.sort(tList, tCompName);
            mAdapter.setTodos(tList, dbHelperChild.getTodos());
            mAdapter.notifyDataSetChanged();
            tComp = tCompName;
            return true;
        } else if (id == R.id.todoSortPlace) {
            List<TodoModel> tList = dbHelper.getTodos();
            Collections.sort(tList, tCompPlace);
            mAdapter.setTodos(tList, dbHelperChild.getTodos());
            mAdapter.notifyDataSetChanged();
            tComp = tCompPlace;
            return true;
        } else if (id == R.id.todoSortCategory) {
            List<TodoModel> tList = dbHelper.getTodos();
            Collections.sort(tList, tCompCategory);
            mAdapter.setTodos(tList, dbHelperChild.getTodos());
            mAdapter.notifyDataSetChanged();
            tComp = tCompCategory;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newExpenditure() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                List<TodoModel> list = dbHelper.getTodos();
                List<TodoChildModel> childs = dbHelperChild.getTodos();
                if(list != null) Collections.sort(list, tComp);
                mAdapter.setTodos(list, childs);
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
    }*/

    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(mActivity.getApplicationContext());
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    // Comparator for Highest Imp
    public static Comparator<TodoModel> tCompImpHigh = new Comparator<TodoModel>() {

        public int compare(TodoModel app1, TodoModel app2) {

            Integer i1 = app1.importance;
            Integer i2 = app2.importance;

            return i2.compareTo(i1);
        }
    };

    // Comparator for Lowest Imp
    public static Comparator<TodoModel> tCompImpLow = new Comparator<TodoModel>() {

        public int compare(TodoModel app1, TodoModel app2) {

            Integer i1 = app1.importance;
            Integer i2 = app2.importance;

            return i1.compareTo(i2);
        }
    };

    // Comparator for Name
    public static Comparator<TodoModel> tCompName = new Comparator<TodoModel>() {

        public int compare(TodoModel app1, TodoModel app2) {

            String name1 = app1.name;
            String name2 = app2.name;

            return name1.compareTo(name2);
        }
    };

    // Comparator for Place
    public static Comparator<TodoModel> tCompPlace = new Comparator<TodoModel>() {

        public int compare(TodoModel app1, TodoModel app2) {

            String name1 = app1.place;
            String name2 = app2.place;

            if (name1 == null) return 1;
            if (name2 == null) return -1;

            return name1.compareTo(name2);
        }
    };

    // Comparator for Category
    public static Comparator<TodoModel> tCompCategory = new Comparator<TodoModel>() {

        public int compare(TodoModel app1, TodoModel app2) {

            String name1 = app1.category;
            String name2 = app2.category;

            if (name1 == null) return 1;
            if (name2 == null) return -1;

            return name1.compareTo(name2);
        }
    };

    private void onItemViewClick(View v, boolean pinned) {
        final int flatPosition = mRecyclerView.getChildPosition(v);

        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final long expandablePosition = mRecyclerViewExpandableItemManager.getExpandablePosition(flatPosition);
        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            ((MainActivity) getActivity()).onGroupItemClicked(groupPosition);
        } else {
            ((MainActivity) getActivity()).onChildItemClicked(groupPosition, childPosition);
        }
    }

    public void notifyGroupItemRestored(int groupPosition) {
        mAdapter.notifyDataSetChanged();

        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);
        mRecyclerView.scrollToPosition(flatPosition);
    }

    public void notifyChildItemRestored(int groupPosition, int childPosition) {
        mAdapter.notifyDataSetChanged();

        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);
        mRecyclerView.scrollToPosition(flatPosition);
    }

    public void notifyGroupItemChanged(int groupPosition) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);

        mAdapter.notifyItemChanged(flatPosition);
    }

    public void notifyChildItemChanged(int groupPosition, int childPosition) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);

        mAdapter.notifyItemChanged(flatPosition);
    }
}