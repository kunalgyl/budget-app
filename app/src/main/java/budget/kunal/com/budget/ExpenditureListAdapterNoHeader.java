package budget.kunal.com.budget;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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
import android.widget.FrameLayout;

import budget.kunal.com.budget.R;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import android.widget.Button;

/**
 * Created by kunal on 7/7/15.
 */

public class ExpenditureListAdapterNoHeader extends RecyclerView.Adapter<ExpenditureListAdapterNoHeader.ViewHolder> {

    private Context mContext;
    private List<ExpenditureModel> mExpenditures;

    public static class ViewHolder extends BaseSwipeAdapter.BaseSwipeableViewHolder {
        //General
        public TextView txtTime;
        public TextView txtName;
        public ImageView imgCategory;
        public TextView txtAmount;
        public CircleDisplay dispImportance;
        public TextView txtPlace;
        public Button deleteButton;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
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


    @Override
    public ExpenditureListAdapterNoHeader.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        //General
        View view = LayoutInflater.from(context).inflate(R.layout.expenditure_list_item_not_swipable, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExpenditureListAdapterNoHeader.ViewHolder holder, int position) {

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
        if(model.importance <= 30) {
            holder.dispImportance.setColor(Color.parseColor("#ff7a57"));
        } else if (model.importance <= 60) {
            holder.dispImportance.setColor(Color.parseColor("#ffcc6a"));
        } else {
            holder.dispImportance.setColor(Color.parseColor("#a1d949"));
        }        holder.dispImportance.setDimAlpha(255);
        holder.dispImportance.setDrawInnerCircle(false);
        holder.dispImportance.setStepSize(0.5f);
        holder.dispImportance.setDrawText(false);
        holder.dispImportance.setTouchEnabled(false);


        holder.txtPlace.setText(adaptText(model.place, 1));

    }

    public ExpenditureListAdapterNoHeader(Context context, List<ExpenditureModel> expenditures, ExpenditureDBHelper db) {
        mContext = context;
        mExpenditures = expenditures;
    }

    public void setExpenditures(List<ExpenditureModel> expenditures) {
        mExpenditures = expenditures;
    }

    @Override
    public int getItemCount() {
        if (mExpenditures != null) {
            return mExpenditures.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        if (mExpenditures != null) {
            return mExpenditures.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mExpenditures != null) {
            return mExpenditures.get(position).id;
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
