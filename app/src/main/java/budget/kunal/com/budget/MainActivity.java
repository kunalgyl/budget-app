package budget.kunal.com.budget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageButton;
import android.support.design.widget.FloatingActionButton;
import budget.kunal.com.budget.R;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import java.util.Calendar;
import java.util.List;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.util.TypedValue;
import budget.kunal.com.budget.ExpenditureDBHelper;
import android.os.Handler;
import android.view.LayoutInflater;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;


public class MainActivity extends BaseActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
    }

}


