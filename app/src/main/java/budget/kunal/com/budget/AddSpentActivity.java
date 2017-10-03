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



public class AddSpentActivity extends ActionBarActivity {

    private ExpenditureModel expenditureDetails;
    private long id;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spent);
        id = getIntent().getExtras().getLong("id");

        if(id == -1) {
            expenditureDetails = new ExpenditureModel();
        } else {
            ExpenditureDBHelper dbHelper = ExpenditureDBHelper.getInstance(this);
            expenditureDetails = dbHelper.getExpenditure(id);

            EditText edtName = (EditText) findViewById(R.id.text_name_spent);
            EditText edtAmount = (EditText) findViewById(R.id.text_amount_spent);
            EditText edtPlace = (EditText) findViewById(R.id.text_place_optional_spent);
            Slider slImportance = (Slider) findViewById(R.id.slider_spent_importance);
            Spinner spCategory = (Spinner) findViewById(R.id.spinner_category);

            edtName.setText(expenditureDetails.name);
            Double amt = expenditureDetails.amount;
            edtAmount.setText(amt.toString());
            edtPlace.setText(expenditureDetails.place);
            slImportance.setValue(expenditureDetails.importance, false);
            int pos = 0;
            if(expenditureDetails.category.equals("Others")) {
                pos = 0;
            } else if(expenditureDetails.category.equals("Transport")) {
                pos = 1;
            } else if(expenditureDetails.category.equals("Gas")) {
                pos = 2;
            } else if(expenditureDetails.category.equals("Food")) {
                pos = 3;
            } else if(expenditureDetails.category.equals("Entertainment")) {
                pos = 4;
            } else if(expenditureDetails.category.equals("Purchases")) {
                pos = 5;
            } else if(expenditureDetails.category.equals("Bills")) {
                pos = 6;
            } else {
                pos = 7;
            }
            spCategory.setSelection(pos);
            time = expenditureDetails.time;

        }

        findViewById(R.id.add_spent_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret = updateModelFromLayout();
                if(ret!=0) {
                    Toast.makeText(AddSpentActivity.this, "Incomplete Form", Toast.LENGTH_SHORT).show();
                } else {
                    ExpenditureDBHelper dbHelper = ExpenditureDBHelper.getInstance(AddSpentActivity.this);
                    if (expenditureDetails.id < 0) {
                        dbHelper.createExpenditure(expenditureDetails);
                    } else {
                        dbHelper.updateExpenditure(expenditureDetails);
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        Spinner spn = (Spinner) findViewById(R.id.spinner_category);
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

        EditText edtName = (EditText) findViewById(R.id.text_name_spent);
        EditText edtAmount = (EditText) findViewById(R.id.text_amount_spent);
        EditText edtPlace = (EditText) findViewById(R.id.text_place_optional_spent);
        Slider slImportance = (Slider) findViewById(R.id.slider_spent_importance);
        Spinner spCategory = (Spinner) findViewById(R.id.spinner_category);

        String edtNameStr = edtName.getText().toString();
        String edtAmountStr = edtAmount.getText().toString();
        String edtPlaceStr = edtPlace.getText().toString();

        if(edtNameStr == null || edtNameStr.isEmpty() ||edtAmountStr == null || edtAmountStr.isEmpty() ) {
            return 1;
        }

        expenditureDetails.id = id;

        expenditureDetails.name = edtNameStr;

        expenditureDetails.amount = Double.parseDouble(edtAmountStr);

        if(edtPlaceStr == null || edtPlaceStr.isEmpty()) {
            expenditureDetails.place = null;
        }
        else {
            expenditureDetails.place = edtPlaceStr;
        }

        expenditureDetails.importance = slImportance.getValue();

        expenditureDetails.category = spCategory.getSelectedItem().toString();

        if(id == -1) {
            expenditureDetails.time = System.currentTimeMillis();
        } else {
            expenditureDetails.time = time;
        }
        return 0;
    }
}