package budget.kunal.com.budget;

import android.database.sqlite.SQLiteOpenHelper;
import budget.kunal.com.budget.ExpenditureContract.Expenditure;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.util.Calendar;
import android.content.ContentValues;
import java.util.List;
import java.util.ArrayList;


/**
 * Created by kunal on 7/7/15.
 */
public class ExpenditureDBHelper extends SQLiteOpenHelper {

    private static ExpenditureDBHelper mInstance = null;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "expenditurebudget.db";

    private static final String SQL_CREATE_EXPENDITURE =
            "CREATE TABLE " + Expenditure.TABLE_NAME + " (" +
                    Expenditure._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Expenditure.COLUMN_NAME_EXPENDITURE_NAME + " TEXT," +
                    Expenditure.COLUMN_NAME_EXPENDITURE_PLACE + " TEXT," +
                    Expenditure.COLUMN_NAME_EXPENDITURE_AMOUNT + " REAL," +
                    Expenditure.COLUMN_NAME_EXPENDITURE_IMPORTANCE + " INTEGER," +
                    Expenditure.COLUMN_NAME_EXPENDITURE_CATEGORY + " INTEGER," +
                    Expenditure.COLUMN_NAME_EXPENDITURE_TIME + " INTEGER" + " )";

    private static final String SQL_DELETE_EXPENDITURE =
            "DROP TABLE IF EXISTS " + Expenditure.TABLE_NAME;

    public static ExpenditureDBHelper getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new ExpenditureDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private ExpenditureDBHelper(Context context) {
        /**
         * Constructor should be private to prevent direct instantiation.
         * make call to static factory method "getInstance()" instead.
         */
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EXPENDITURE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EXPENDITURE);
        onCreate(db);
    }

    private ExpenditureModel populateModel(Cursor c) {
        ExpenditureModel model = new ExpenditureModel();
        model.id = c.getLong(c.getColumnIndex(Expenditure._ID));
        model.name = c.getString(c.getColumnIndex(Expenditure.COLUMN_NAME_EXPENDITURE_NAME));
        model.place = c.getString(c.getColumnIndex(Expenditure.COLUMN_NAME_EXPENDITURE_PLACE));
        model.amount = c.getDouble(c.getColumnIndex(Expenditure.COLUMN_NAME_EXPENDITURE_AMOUNT));
        model.importance = c.getInt(c.getColumnIndex(Expenditure.COLUMN_NAME_EXPENDITURE_IMPORTANCE));
        model.category = c.getString(c.getColumnIndex(Expenditure.COLUMN_NAME_EXPENDITURE_CATEGORY));
        model.time = c.getLong(c.getColumnIndex(Expenditure.COLUMN_NAME_EXPENDITURE_TIME));


        return model;
    }

    private ContentValues populateContent(ExpenditureModel model) {
        ContentValues values = new ContentValues();
        values.put(Expenditure.COLUMN_NAME_EXPENDITURE_NAME, model.name);
        values.put(Expenditure.COLUMN_NAME_EXPENDITURE_PLACE, model.place);
        values.put(Expenditure.COLUMN_NAME_EXPENDITURE_AMOUNT, model.amount);
        values.put(Expenditure.COLUMN_NAME_EXPENDITURE_IMPORTANCE, model.importance);
        values.put(Expenditure.COLUMN_NAME_EXPENDITURE_CATEGORY, model.category);
        values.put(Expenditure.COLUMN_NAME_EXPENDITURE_TIME, model.time);

        return values;
    }

    public long createExpenditure(ExpenditureModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().insert(Expenditure.TABLE_NAME, null, values);
    }

    public ExpenditureModel getExpenditure(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Expenditure.TABLE_NAME + " WHERE " + Expenditure._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }

        return null;
    }

    public long updateExpenditure(ExpenditureModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().update(Expenditure.TABLE_NAME, values, Expenditure._ID + " = ?", new String[]{String.valueOf(model.id)});
    }

    public int deleteExpenditure(long id) {
        return getWritableDatabase().delete(Expenditure.TABLE_NAME, Expenditure._ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<ExpenditureModel> getExpenditures() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Expenditure.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<ExpenditureModel> expenditureList = new ArrayList<ExpenditureModel>();

        while (c.moveToNext()) {
            expenditureList.add(populateModel(c));
        }

        if (!expenditureList.isEmpty()) {
            return expenditureList;
        }

        return null;
    }
}

