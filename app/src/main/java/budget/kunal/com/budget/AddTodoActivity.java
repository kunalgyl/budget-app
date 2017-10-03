package budget.kunal.com.budget;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import budget.kunal.com.budget.ExpenditureModel;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Slider;
import java.util.Calendar;
import com.rey.material.widget.Spinner;
import android.widget.Toast;



public class AddTodoActivity extends ActionBarActivity {

    private TodoModel todoDetails;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        id = getIntent().getExtras().getLong("id");

        if(id == -1) {
            todoDetails = new TodoModel();
        } else {
            TodoDBHelper dbHelper = new TodoDBHelper(this);
            todoDetails = dbHelper.getTodo(id);

            EditText edtName = (EditText) findViewById(R.id.add_name_todo);
            EditText edtPlace = (EditText) findViewById(R.id.text_place_optional_todo);
            Slider slImportance = (Slider) findViewById(R.id.slider_todo_importance);
            Spinner spCategory = (Spinner) findViewById(R.id.spinner_category_todo);

            edtName.setText(todoDetails.name);
            edtPlace.setText(todoDetails.place);
            slImportance.setValue(todoDetails.importance, false);
            int pos = 0;
            if(todoDetails.category.equals("Others")) {
                pos = 0;
            } else if(todoDetails.category.equals("Transport")) {
                pos = 1;
            } else if(todoDetails.category.equals("Gas")) {
                pos = 2;
            } else if(todoDetails.category.equals("Food")) {
                pos = 3;
            } else if(todoDetails.category.equals("Entertainment")) {
                pos = 4;
            } else if(todoDetails.category.equals("Purchases")) {
                pos = 5;
            } else if(todoDetails.category.equals("Bills")) {
                pos = 6;
            } else {
                pos = 7;
            }
            spCategory.setSelection(pos);

        }

        findViewById(R.id.add_todo_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret = updateModelFromLayout();
                if(ret!=0) {
                    Toast.makeText(AddTodoActivity.this, "Incomplete Form", Toast.LENGTH_SHORT).show();
                } else {
                    TodoDBHelper dbHelper = new TodoDBHelper(AddTodoActivity.this);
                    if (todoDetails.id < 0) {
                        dbHelper.createTodo(todoDetails);
                    } else {
                        dbHelper.updateTodo(todoDetails);
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        Spinner spn = (Spinner) findViewById(R.id.spinner_category_todo);
        String[] items = new String[8];
        items[0] = "Others";
        items[1] = "Transport";
        items[2] = "Gas";
        items[3] = "Food";
        items[4] = "Entertainment";
        items[5] = "Purchases";
        items[6] = "Bills";
        items[7] = "Groceries";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_spn, items);
        spn.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_spent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private int updateModelFromLayout() {

        EditText edtName = (EditText) findViewById(R.id.add_name_todo);
        EditText edtPlace = (EditText) findViewById(R.id.text_place_optional_todo);
        Slider slImportance = (Slider) findViewById(R.id.slider_todo_importance);
        Spinner spCategory = (Spinner) findViewById(R.id.spinner_category_todo);

        String edtNameStr = edtName.getText().toString();
        String edtPlaceStr = edtPlace.getText().toString();

        if(edtNameStr == null || edtNameStr.isEmpty()) {
            return 1;
        }

        todoDetails.id = id;

        todoDetails.name = edtNameStr;

        if(edtPlaceStr == null || edtPlaceStr.isEmpty()) {
            todoDetails.place = null;
        }
        else {
            todoDetails.place = edtPlaceStr;
        }

        todoDetails.importance = slImportance.getValue();

        todoDetails.category = spCategory.getSelectedItem().toString();

        return 0;
    }
}