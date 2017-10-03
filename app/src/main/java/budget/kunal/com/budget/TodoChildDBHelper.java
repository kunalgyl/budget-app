package budget.kunal.com.budget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoChildDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todochildbudget.db";

    private static final String SQL_CREATE_TODO =
            "CREATE TABLE " + TodoChildContract.TodoChild.TABLE_NAME + " (" +
                    TodoChildContract.TodoChild._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_NAME + " TEXT," +
                    TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_CHECKED + " INTEGER," +
                    TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_PARENT + " REAL" + " )";

    private static final String SQL_DELETE_TODO =
            "DROP TABLE IF EXISTS " + TodoChildContract.TodoChild.TABLE_NAME;

    public TodoChildDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TODO);
        onCreate(db);
    }

    private TodoChildModel populateModel(Cursor c) {
        TodoChildModel model = new TodoChildModel();
        model.id = c.getLong(c.getColumnIndex(TodoChildContract.TodoChild._ID));
        model.name = c.getString(c.getColumnIndex(TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_NAME));
        model.checked = c.getInt(c.getColumnIndex(TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_CHECKED));
        model.parent_id = c.getLong(c.getColumnIndex(TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_PARENT));
        return model;
    }

    private ContentValues populateContent(TodoChildModel model) {
        ContentValues values = new ContentValues();
        values.put(TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_CHECKED, model.name);
        values.put(TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_CHECKED, model.checked);
        values.put(TodoChildContract.TodoChild.COLUMN_NAME_TODO_CHILD_PARENT, model.parent_id);
        return values;
    }

    public long createTodo(TodoChildModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().insert(TodoContract.Todo.TABLE_NAME, null, values);
    }

    public TodoChildModel getTodo(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + TodoChildContract.TodoChild.TABLE_NAME + " WHERE " + TodoChildContract.TodoChild._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }

        return null;
    }

    public long updateTodo(TodoChildModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().update(TodoChildContract.TodoChild.TABLE_NAME, values, TodoChildContract.TodoChild._ID + " = ?", new String[]{String.valueOf(model.id)});
    }

    public int deleteTodo(long id) {
        return getWritableDatabase().delete(TodoChildContract.TodoChild.TABLE_NAME, TodoChildContract.TodoChild._ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<TodoChildModel> getTodos() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + TodoChildContract.TodoChild.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<TodoChildModel> todoList = new ArrayList<TodoChildModel>();

        while (c.moveToNext()) {
            todoList.add(populateModel(c));
        }

        if (!todoList.isEmpty()) {
            return todoList;
        }

        return null;
    }

}


