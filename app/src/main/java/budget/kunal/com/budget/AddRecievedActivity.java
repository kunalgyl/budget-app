package budget.kunal.com.budget;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.rey.material.widget.EditText;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Spinner;

import java.util.Calendar;

import budget.kunal.com.budget.ExpenditureModel;



public class AddRecievedActivity extends ActionBarActivity {

    private ExpenditureModel expenditureDetails;
    private long id;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recieved);
        id = getIntent().getExtras().getLong("id");

        if(id == -1) {
            expenditureDetails = new ExpenditureModel();
        } else {
            ExpenditureDBHelper dbHelper = ExpenditureDBHelper.getInstance(this);
            expenditureDetails = dbHelper.getExpenditure(id);

            EditText edtName = (EditText) findViewById(R.id.text_name_recieved);
            EditText edtAmount = (EditText) findViewById(R.id.text_amount_recieved);
            EditText edtPlace = (EditText) findViewById(R.id.text_place_optional_recieved);
            Slider slImportance = (Slider) findViewById(R.id.slider_recieved_importance);

            edtName.setText(expenditureDetails.name);
            Double amt = expenditureDetails.amount;
            edtAmount.setText(amt.toString());
            edtPlace.setText(expenditureDetails.place);
            slImportance.setValue(expenditureDetails.importance, false);
            time = expenditureDetails.time;
        }

        findViewById(R.id.add_recieved_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret = updateModelFromLayout();
                if(ret!=0) {
                    Toast.makeText(AddRecievedActivity.this, "Incomplete Form", Toast.LENGTH_SHORT).show();
                } else {
                    ExpenditureDBHelper dbHelper = ExpenditureDBHelper.getInstance(AddRecievedActivity.this);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_recieved, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private int updateModelFromLayout() {

        EditText edtName = (EditText) findViewById(R.id.text_name_recieved);
        EditText edtAmount = (EditText) findViewById(R.id.text_amount_recieved);
        EditText edtPlace = (EditText) findViewById(R.id.text_place_optional_recieved);
        Slider slImportance = (Slider) findViewById(R.id.slider_recieved_importance);

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

        expenditureDetails.category = "Received";

        if(id == -1) {
            expenditureDetails.time = System.currentTimeMillis();
        } else {
            expenditureDetails.time = time;
        }
        return 0;
    }
}