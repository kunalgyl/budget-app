package budget.kunal.com.budget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;
import com.rey.material.widget.EditText;

import java.util.EventListener;

import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kunal on 7/25/15.
 */
public class TodoListAdapter
        extends AbstractExpandableItemAdapter<TodoListAdapter.MyGroupViewHolder, TodoListAdapter.MyChildViewHolder>
        implements ExpandableSwipeableItemAdapter<TodoListAdapter.MyGroupViewHolder, TodoListAdapter.MyChildViewHolder> {

    private final RecyclerViewExpandableItemManager mExpandableItemManager;
    private Context mContext;
    private List<TodoModel> mTodos;
    private List<TodoChildModel> mTodoChilds;
    private TodoDBHelper dbHelper;
    private TodoChildDBHelper dbHelperChild;
    private EventListener mEventListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public static class MyGroupViewHolder extends AbstractSwipeableItemViewHolder implements ExpandableItemViewHolder {
        public RelativeLayout mContainer;
        public TextView txtName;
        public ImageView imgCategory;
        public CircleDisplay dispImportance;
        public TextView txtPlace;
        public LinearLayout expLayout;
        public EditText addChild;
        private int mExpandStateFlags;

        public MyGroupViewHolder(View v) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.container);
            txtName = (TextView) v.findViewById(R.id.todo_item_name);
            imgCategory = (ImageView) v.findViewById(R.id.todo_item_category);
            dispImportance = (CircleDisplay) v.findViewById(R.id.todo_item_importance);
            txtPlace = (TextView) v.findViewById(R.id.todo_item_place);
            expLayout = (LinearLayout) v.findViewById(R.id.todo_expandable);
            addChild = (EditText) v.findViewById(R.id.todo_add_child);
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public static class MyChildViewHolder extends AbstractSwipeableItemViewHolder implements ExpandableItemViewHolder {
        public RelativeLayout mContainer;
        public TextView txtChild;
        private int mExpandStateFlags;

        public MyChildViewHolder(View v) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.container);
            txtChild = (TextView) v.findViewById(R.id.todo_item_name_sub);
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public interface EventListener {
        void onGroupItemRemoved(int groupPosition);

        void onChildItemRemoved(int groupPosition, int childPosition);

        void onItemViewClicked(View v, boolean pinned);
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);

        return new MyGroupViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item_sub, parent, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {

        TodoModel model = (TodoModel) getGroupItem(groupPosition);

        if (model.category.equals("Received")) {
            holder.imgCategory.setImageResource(R.drawable.ic_money);
        } else if (model.category.equals("Gas")) {
            holder.imgCategory.setImageResource(R.drawable.ic_oil);
        } else if (model.category.equals("Transport")) {
            holder.imgCategory.setImageResource(R.drawable.ic_bus);
        } else if (model.category.equals("Groceries")) {
            holder.imgCategory.setImageResource(R.drawable.ic_apple);
        } else if (model.category.equals("Food")) {
            holder.imgCategory.setImageResource(R.drawable.ic_food);
        } else if (model.category.equals("Entertainment")) {
            holder.imgCategory.setImageResource(R.drawable.ic_popcorn);
        } else if (model.category.equals("Purchases")) {
            holder.imgCategory.setImageResource(R.drawable.ic_gift);
        } else if (model.category.equals("Bills")) {
            holder.imgCategory.setImageResource(R.drawable.ic_card);
        } else {
            holder.imgCategory.setImageResource(R.drawable.ic_air_balloon);
        }

        holder.txtName.setText(adaptText(model.name, 0));
        holder.dispImportance.setValueWidthPercent(0);
        if (model.importance <= 30) {
            holder.dispImportance.setColor(Color.parseColor("#ff7a57"));
        } else if (model.importance <= 60) {
            holder.dispImportance.setColor(Color.parseColor("#ffcc6a"));
        } else {
            holder.dispImportance.setColor(Color.parseColor("#a1d949"));
        }
        holder.dispImportance.setDimAlpha(255);
        holder.dispImportance.setDrawInnerCircle(false);
        holder.dispImportance.setStepSize(0.5f);
        holder.dispImportance.setDrawText(false);
        holder.dispImportance.setTouchEnabled(false);
        holder.txtPlace.setText(adaptText(model.place, 1));
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        TodoChildModel model = (TodoChildModel) getChildItem(groupPosition, childPosition);

        // set text
        holder.txtChild.setText(model.name);
    }

    public void removeGroup(int position) {
        long id = getItemId(position);
        long child_id = 0;
        TodoModel model = (TodoModel) getGroupItem(position);
        if(model.childs != null) {
            for (int i = 0; i < model.childs.size(); i++) {
                child_id = model.childs.get(i).id;
                dbHelperChild.deleteTodo(child_id);
            }
        }
        mTodos.remove(position);
        dbHelper.deleteTodo(id);
    }

    public void removeChild(int groupPosition, int childPosition) {
        long id = getChildId(groupPosition, childPosition);
        dbHelperChild.deleteTodo(id);
        mTodoChilds.remove(childPosition);
        notifyDataSetChanged();
    }

    public TodoListAdapter(Context context, RecyclerViewExpandableItemManager expandableItemManager, List<TodoModel> todos, List<TodoChildModel> childs, TodoDBHelper db, TodoChildDBHelper db_childs) {
        mExpandableItemManager = expandableItemManager;
        mContext = context;
        dbHelper = db;
        dbHelperChild = db_childs;
        mTodos = todos;
        mTodoChilds = childs;
        setLists();
        setHasStableIds(true);
    }

    public void setTodos(List<TodoModel> todos, List<TodoChildModel> childs) {
        mTodos = todos;
        mTodoChilds = childs;
        setLists();
    }

    public void setLists() {
        long id = 0;
        if((mTodos == null) || (mTodoChilds == null)) return;
        for(TodoModel todo : mTodos) {
            id = todo.id;
            for(TodoChildModel child : mTodoChilds) {
                if(id == child.parent_id) {
                    todo.childs.add(child);
                }
            }
        }
    }

    @Override
    public int getGroupCount() {
        if(mTodos != null) return mTodos.size();
        return 0;
    }

    @Override
    public int getChildCount(int groupPosition) {
        if(mTodos.get(groupPosition).childs != null ) return mTodos.get(groupPosition).childs.size();
        else return 0;
    }

    public Object getGroupItem(int position) {
        if (mTodos != null) {
            return mTodos.get(position);
        }
        return null;
    }

    public Object getChildItem(int groupPosition, int childPosition) {
        TodoModel group = (TodoModel) getGroupItem(groupPosition);
        if(group == null) return null;
        ArrayList<TodoChildModel> childs = group.childs;
        if(childs == null) return null;
        return childs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mTodos.get(groupPosition).id;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mTodos.get(groupPosition).childs.get(childPosition).id;
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    public String adaptText(String s, Integer i) {
        switch (i) {
            case 0:
                return (s.length() > 15 ? s.substring(0, 15) + "..." : s);
            case 1:
                if (s == null) return "";
                return "@" + (s.length() > 15 ? s.substring(0, 15) + "..." : s);
            default:
                return "err";

        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        return true;
    }

    @Override
    public int onGetGroupItemSwipeReactionType(MyGroupViewHolder holder, int groupPosition, int x, int y) {
        return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
    }

    @Override
    public int onGetChildItemSwipeReactionType(MyChildViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
    }

    @Override
    public void onSetGroupItemSwipeBackground(MyGroupViewHolder holder, int groupPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public void onSetChildItemSwipeBackground(MyChildViewHolder holder, int groupPosition, int childPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public int onSwipeGroupItem(MyGroupViewHolder holder, int groupPosition, int result) {
        switch (result) {
            // swipe
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public int onSwipeChildItem(MyChildViewHolder holder, int groupPosition, int childPosition, int result) {
        switch (result) {
            // swipe
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeGroupReaction(MyGroupViewHolder holder, int groupPosition, int result, int reaction) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        final int flatPosition = mExpandableItemManager.getFlatPosition(expandablePosition);
        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
            removeGroup(groupPosition);
            notifyDataSetChanged();

            if (mEventListener != null) {
                mEventListener.onGroupItemRemoved(groupPosition);
            }
        } else {
            //?
        }
    }

    @Override
    public void onPerformAfterSwipeChildReaction(MyChildViewHolder holder, int groupPosition, int childPosition, int result, int reaction) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
        final int flatPosition = mExpandableItemManager.getFlatPosition(expandablePosition);

        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
            removeChild(groupPosition, childPosition);
            notifyDataSetChanged();

            if (mEventListener != null) {
                mEventListener.onChildItemRemoved(groupPosition, childPosition);
            }
        } else {
            //?
        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }
}
