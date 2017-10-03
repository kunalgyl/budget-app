package budget.kunal.com.budget;

import android.provider.BaseColumns;


/**
 * Created by kunal on 7/7/15.
 */
public final class ExpenditureContract {

    public ExpenditureContract() {}

    public static abstract class Expenditure implements BaseColumns {
        public static final String TABLE_NAME = "expenditure";
        public static final String COLUMN_NAME_EXPENDITURE_NAME = "name";
        public static final String COLUMN_NAME_EXPENDITURE_PLACE = "place";
        public static final String COLUMN_NAME_EXPENDITURE_AMOUNT= "amount";
        public static final String COLUMN_NAME_EXPENDITURE_IMPORTANCE = "importance";
        public static final String COLUMN_NAME_EXPENDITURE_CATEGORY = "category";
        public static final String COLUMN_NAME_EXPENDITURE_TIME = "time";
    }

}
