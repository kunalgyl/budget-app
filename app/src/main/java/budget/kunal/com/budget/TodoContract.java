package budget.kunal.com.budget;

import android.provider.BaseColumns;

public final class TodoContract {

    public TodoContract() {}

    public static abstract class Todo implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_TODO_NAME = "name";
        public static final String COLUMN_NAME_TODO_PLACE = "place";
        public static final String COLUMN_NAME_TODO_AMOUNT= "amount";
        public static final String COLUMN_NAME_TODO_IMPORTANCE = "importance";
        public static final String COLUMN_NAME_TODO_CATEGORY = "category";
        public static final String COLUMN_NAME_TODO_TIME = "time";

    }

}

