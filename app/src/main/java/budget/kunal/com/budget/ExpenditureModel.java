package budget.kunal.com.budget;

import android.net.Uri;
import android.graphics.Color;
import java.util.Calendar;

public class ExpenditureModel {

    public static final int OTHERS = 0;
    public static final int TRANSPORT = 1;
    public static final int GAS = 2;
    public static final int FOOD = 3;
    public static final int ENTERTAINMENT = 4;
    public static final int PURCHASES = 5;
    public static final int BILLS = 6;
    public static final int RECEIVED = 7;

    public String name;
    public String place;
    public double amount;
    public int importance;
    public String category;
    public long time;
    public long id;

    public int getColor() {
        int amt = (int) Math.min(amount, 100);
        int val = Math.max(125 - amt, 25);
        if (category.equals("Received")) {
            return Color.argb(255, val, 200, val + 50);
        } else {
            return Color.argb(255, 220, val + 10, val);
        }
    }

    public int getImpColor() {
        int val = 220 - importance / 5;
        return Color.argb(255, val, val, val);
    }
}
