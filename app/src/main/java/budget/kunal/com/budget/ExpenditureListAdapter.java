package budget.kunal.com.budget;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateUtils;
import android.app.Activity;
import android.widget.FrameLayout;

import budget.kunal.com.budget.R;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import android.widget.Button;

/**
 * Created by kunal on 7/7/15.
 */

public class ExpenditureListAdapter extends BaseSwipeAdapter<ExpenditureListAdapter.ViewHolder> {

    private Context mContext;
    private List<ExpenditureModel> mExpenditures;
    private List<ExpenditureModel> mExpendituresVisible;
    private ExpenditureDBHelper dbHelper;
    private Double spentAll;
    private Double receivedAll;
    private Double spentLastWeek;
    private Double receivedLastWeek;
    private Double spentLastMonth;
    private Double receivedLastMonth;

    public static class ViewHolder extends BaseSwipeAdapter.BaseSwipeableViewHolder {
        //General
        public TextView txtTime;
        public TextView txtName;
        public ImageView imgCategory;
        public TextView txtAmount;
        public CircleDisplay dispImportance;
        public TextView txtPlace;
        public Button deleteButton;

        //Header
        public TextView txtSpentThisWeek;
        public TextView txtReceivedThisWeek;
        public TextView txtThisWeek;
        public TextView txtSpentThisMonth;
        public TextView txtReceivedThisMonth;
        public TextView txtThisMonth;
        public TextView txtReceivedAllTime;
        public TextView txtSpentAllTime;
        public TextView txtAllTime;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                //Header
                txtSpentThisWeek = (TextView) itemView.findViewById(R.id.expenditure_header_spent_week);
                txtReceivedThisWeek = (TextView) itemView.findViewById(R.id.expenditure_header_received_week);
                txtThisWeek = (TextView) itemView.findViewById(R.id.expenditure_header_week);
                txtSpentThisMonth = (TextView) itemView.findViewById(R.id.expenditure_header_spent_month);
                txtReceivedThisMonth = (TextView) itemView.findViewById(R.id.expenditure_header_received_month);
                txtThisMonth = (TextView) itemView.findViewById(R.id.expenditure_header_month);
                txtReceivedAllTime = (TextView) itemView.findViewById(R.id.expenditure_header_received_all);
                txtSpentAllTime = (TextView) itemView.findViewById(R.id.expenditure_header_spent_all);
                txtAllTime = (TextView) itemView.findViewById(R.id.expenditure_header_all);
            } else {
                //General
                txtTime = (TextView) itemView.findViewById(R.id.expenditure_item_time);
                txtName = (TextView) itemView.findViewById(R.id.expenditure_item_name);
                imgCategory = (ImageView) itemView.findViewById(R.id.expenditure_item_category);
                txtAmount = (TextView) itemView.findViewById(R.id.expenditure_item_amount);
                dispImportance = (CircleDisplay) itemView.findViewById(R.id.expenditure_item_importance);
                txtPlace = (TextView) itemView.findViewById(R.id.expenditure_item_place);
                deleteButton = (Button) itemView.findViewById(R.id.delete);
            }
        }
    }


    @Override
    public ExpenditureListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        if (viewType == 0) {
            //Header
            View view = LayoutInflater.from(context).inflate(R.layout.expenditure_list_header, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view, viewType);
            return viewHolder;

        } else {
            //General
            View view = LayoutInflater.from(context).inflate(R.layout.expenditure_list_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view, viewType);
            SwipeLayout swipeLayout = viewHolder.swipeLayout;
            swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            swipeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ExpenditureModel model = dbHelper.getExpenditure(getItemId(viewHolder.getPosition()));
                    Intent intent = new Intent(mContext, AddSpentActivity.class);
                    if (model.category.equals("Received")) {
                        intent = new Intent(mContext, AddRecievedActivity.class);
                    }
                    intent.putExtra("id", model.id);
                    ((Activity) mContext).startActivityForResult(intent, 0);
                    return false;
                }
            });

            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(viewHolder.getPosition());
                }
            });
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(ExpenditureListAdapter.ViewHolder holder, int position) {

        if (position == 0) {
            holder.txtSpentThisWeek.setText(appendDollar(Double.toString(spentLastWeek)));
            holder.txtReceivedThisWeek.setText(appendDollar(Double.toString(receivedLastWeek)));
            holder.txtThisWeek.setText(appendDollar(Double.toString(receivedLastWeek + spentLastWeek)));
            holder.txtSpentThisMonth.setText(appendDollar(Double.toString(spentLastMonth)));
            holder.txtReceivedThisMonth.setText(appendDollar(Double.toString(receivedLastMonth)));
            holder.txtThisMonth.setText(appendDollar(Double.toString(receivedLastMonth + spentLastMonth)));
            holder.txtSpentAllTime.setText(appendDollar(Double.toString(spentAll)));
            holder.txtReceivedAllTime.setText(appendDollar(Double.toString(receivedAll)));
            holder.txtAllTime.setText(appendDollar(Double.toString(spentAll + receivedAll)));
            holder.txtSpentThisWeek.setTextColor(getColor(spentLastWeek));
            holder.txtReceivedThisWeek.setTextColor(getColor(receivedLastWeek));
            holder.txtThisWeek.setTextColor(getColor(receivedLastWeek + spentLastWeek));
            holder.txtSpentThisMonth.setTextColor(getColor(spentLastMonth));
            holder.txtReceivedThisMonth.setTextColor(getColor(receivedLastMonth));
            holder.txtThisMonth.setTextColor(getColor(receivedLastMonth + spentLastMonth));
            holder.txtSpentAllTime.setTextColor(getColor(spentAll));
            holder.txtReceivedAllTime.setTextColor(getColor(receivedAll));
            holder.txtAllTime.setTextColor(getColor(spentAll + receivedAll));
        } else {
            ExpenditureModel model = (ExpenditureModel) getItem(position);

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

            holder.txtTime.setText(calculateRelativeTime(model.time));
            holder.txtName.setText(adaptText(model.name, 0));
            holder.txtAmount.setText(appendDollar(Double.toString(model.amount)));
            holder.txtAmount.setTextColor(model.getColor());
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
    }

    public void remove(int position) {
        long id = getItemId(position);
        mExpenditures.remove(position - 1);
        closeItem(position);
        dbHelper.deleteExpenditure(id);
        expenditureDetailsUpdate();
        notifyDataSetChanged();
    }

    public ExpenditureListAdapter(Context context, List<ExpenditureModel> expenditures, ExpenditureDBHelper db) {
        mContext = context;
        mExpenditures = expenditures;
        dbHelper = db;
        expenditureDetailsUpdate();
    }

    public void setExpenditures(List<ExpenditureModel> expenditures) {
        mExpenditures = expenditures;
        expenditureDetailsUpdate();
    }

    @Override
    public int getItemCount() {
        if (mExpenditures != null) {
            return mExpenditures.size() + 1;
        }
        return 0;
    }

    public Object getItem(int position) {
        if (mExpenditures != null) {
            return mExpenditures.get(position - 1);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mExpenditures != null) {
            return mExpenditures.get(position - 1).id;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String calculateRelativeTime(long time) {
        String relativetime = DateUtils.getRelativeTimeSpanString(time).toString();
        return relativetime;
    }

    private String appendDollar(String string) {
        String ret = "$ " + string;
        return ret;
    }


    public void expenditureDetailsUpdate() {
        spentLastWeek = 0.0;
        receivedLastWeek = 0.0;
        spentLastMonth = 0.0;
        receivedLastMonth = 0.0;
        spentAll = 0.0;
        receivedAll = 0.0;

        if (mExpenditures == null) return;

        Calendar now = Calendar.getInstance();

        //Beginning of Week
        Calendar beginWeek = Calendar.getInstance();
        beginWeek.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        beginWeek.clear(Calendar.MINUTE);
        beginWeek.clear(Calendar.SECOND);
        beginWeek.clear(Calendar.MILLISECOND);
        // get start of this week in milliseconds
        beginWeek.set(Calendar.DAY_OF_WEEK, beginWeek.getFirstDayOfWeek());
        long weekStart = beginWeek.getTimeInMillis();

        Calendar beginMonth = Calendar.getInstance();
        beginMonth.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        beginMonth.clear(Calendar.MINUTE);
        beginMonth.clear(Calendar.SECOND);
        beginMonth.clear(Calendar.MILLISECOND);
        // get start of the month
        beginMonth.set(Calendar.DAY_OF_MONTH, 1);
        long monthStart = beginMonth.getTimeInMillis();


        for (int i = 0; i < mExpenditures.size(); i++) {
            if (mExpenditures.get(i).time >= weekStart) {
                if (mExpenditures.get(i).category.equals("Received")) {
                    receivedLastWeek += mExpenditures.get(i).amount;
                    receivedLastMonth += mExpenditures.get(i).amount;
                    receivedAll += mExpenditures.get(i).amount;
                } else {
                    spentLastWeek -= mExpenditures.get(i).amount;
                    spentLastMonth -= mExpenditures.get(i).amount;
                    spentAll -= mExpenditures.get(i).amount;
                }
            } else if (mExpenditures.get(i).time >= monthStart) {
                if (mExpenditures.get(i).category.equals("Received")) {
                    receivedLastMonth += mExpenditures.get(i).amount;
                    receivedAll += mExpenditures.get(i).amount;
                } else {
                    spentLastMonth -= mExpenditures.get(i).amount;
                    spentAll -= mExpenditures.get(i).amount;
                }
            } else {
                if (mExpenditures.get(i).category.equals("Received")) {
                    receivedAll += mExpenditures.get(i).amount;
                } else {
                    spentAll -= mExpenditures.get(i).amount;
                }
            }

        }
    }

    public int getColor(Double amount) {
        if (amount == 0.0) {
            return Color.argb(255, 100, 100, 100);
        } else if (amount > 0.0) {
            int amt = (int) Math.min(amount, 100);
            int val = Math.max(125 - amt, 25);
            return Color.argb(255, val, 200, val + 50);
        } else {
            int amt = (int) Math.min(amount * -1.0, 100);
            int val = Math.max(125 - amt, 25);
            return Color.argb(255, 220, val + 10, val);
        }
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

}
