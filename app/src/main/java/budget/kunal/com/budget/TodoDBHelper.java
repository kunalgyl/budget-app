package budget.kunal.com.budget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todobudget.db";

    private static final String SQL_CREATE_TODO =
            "CREATE TABLE " + TodoContract.Todo.TABLE_NAME + " (" +
                    TodoContract.Todo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TodoContract.Todo.COLUMN_NAME_TODO_NAME + " TEXT," +
                    TodoContract.Todo.COLUMN_NAME_TODO_PLACE + " TEXT," +
                    TodoContract.Todo.COLUMN_NAME_TODO_AMOUNT + " REAL," +
                    TodoContract.Todo.COLUMN_NAME_TODO_IMPORTANCE + " INTEGER," +
                    TodoContract.Todo.COLUMN_NAME_TODO_CATEGORY + " INTEGER," +
                    TodoContract.Todo.COLUMN_NAME_TODO_TIME + " INTEGER" + " )";

    private static final String SQL_DELETE_TODO =
            "DROP TABLE IF EXISTS " + TodoContract.Todo.TABLE_NAME;

    public TodoDBHelper(Context context) {
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

    private TodoModel populateModel(Cursor c) {
        TodoModel model = new TodoModel();
        model.id = c.getLong(c.getColumnIndex(TodoContract.Todo._ID));
        model.name = c.getString(c.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TODO_NAME));
        model.place = c.getString(c.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TODO_PLACE));
        model.amount = c.getDouble(c.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TODO_AMOUNT));
        model.importance = c.getInt(c.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TODO_IMPORTANCE));
        model.category = c.getString(c.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TODO_CATEGORY));
        model.time = c.getLong(c.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TODO_TIME));

        return model;
    }

    private ContentValues populateContent(TodoModel model) {
        ContentValues values = new ContentValues();
        values.put(TodoContract.Todo.COLUMN_NAME_TODO_NAME, model.name);
        values.put(TodoContract.Todo.COLUMN_NAME_TODO_PLACE, model.place);
        values.put(TodoContract.Todo.COLUMN_NAME_TODO_AMOUNT, model.amount);
        values.put(TodoContract.Todo.COLUMN_NAME_TODO_IMPORTANCE, model.importance);
        values.put(TodoContract.Todo.COLUMN_NAME_TODO_CATEGORY, model.category);
        values.put(TodoContract.Todo.COLUMN_NAME_TODO_TIME, model.time);

        return values;
    }

    public long createTodo(TodoModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().insert(TodoContract.Todo.TABLE_NAME, null, values);
    }

    public TodoModel getTodo(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + TodoContract.Todo.TABLE_NAME + " WHERE " + TodoContract.Todo._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }

        return null;
    }

    public long updateTodo(TodoModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().update(TodoContract.Todo.TABLE_NAME, values, TodoContract.Todo._ID + " = ?", new String[]{String.valueOf(model.id)});
    }

    public int deleteTodo(long id) {
        return getWritableDatabase().delete(TodoContract.Todo.TABLE_NAME, TodoContract.Todo._ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<TodoModel> getTodos() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + TodoContract.Todo.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<TodoModel> todoList = new ArrayList<TodoModel>();

        while (c.moveToNext()) {
            todoList.add(populateModel(c));
        }

        if (!todoList.isEmpty()) {
            return todoList;
        }

        return null;
    }

}


