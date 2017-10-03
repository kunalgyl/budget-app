package budget.kunal.com.budget;

import android.provider.BaseColumns;

public final class TodoChildContract {

    public TodoChildContract() {}

    public static abstract class TodoChild implements BaseColumns {
        public static final String TABLE_NAME = "todochild";
        public static final String COLUMN_NAME_TODO_CHILD_NAME = "name";
        public static final String COLUMN_NAME_TODO_CHILD_CHECKED = "checked";
        public static final String COLUMN_NAME_TODO_CHILD_PARENT = "parent";

    }

}

