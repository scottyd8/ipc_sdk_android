package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.paymentmethods.DeletePaymentMethodRequest;
import com.worldpay.library.webservices.services.paymentmethods.PaymentMethodResponse;
import com.worldpay.library.webservices.tasks.PaymentMethodDeleteTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.util.HashMap;

public class CustomerDetailsActivity extends WorldBaseActivity implements View.OnClickListener {

    //CUSTOMER INFO
    TextView tv_customer_id, tv_first_name, tv_last_name, tv_email, tv_send_email_address, tv_notes;
    //ADDRESS
    TextView tv_line_one, tv_city, tv_state, tv_zip_code, tv_country, tv_company, tv_phone;
    //USER DEFINED FIELDS
    TextView tv_udfname;

    Button btn_edit, btn_delete, btn_payment_method;
    String responseFromIntent, customerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        //  setActivity(CustomerDetailsActivity.this);
        initComponents();

        if (getIntent().getExtras() != null) {
            responseFromIntent = getIntent().getExtras().getString("response");
            Gson gson = new Gson();
            CustomerResponse customerResponse = gson.fromJson(responseFromIntent, CustomerResponse.class);
            settingFields(customerResponse);
        }

    }

    public void initComponents() {

        //CUSTOMER INFO
        tv_customer_id = (TextView) findViewById(R.id.tv_customer_id);
        tv_first_name = (TextView) findViewById(R.id.tv_first_name);
        tv_last_name = (TextView) findViewById(R.id.tv_last_name);
        tv_email = (TextView) findViewById(R.id.tv_email_address);
        tv_send_email_address = (TextView) findViewById(R.id.tv_send_email_address);
        tv_notes = (TextView) findViewById(R.id.tv_notes);

        //ADDRESS
        tv_line_one = (TextView) findViewById(R.id.tv_line_one);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_zip_code = (TextView) findViewById(R.id.tv_zip_code);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_country = (TextView) findViewById(R.id.tv_country);
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        //USER DEFINED FIELDS
        tv_udfname = (TextView) findViewById(R.id.tv_udfname);

        //Button
        // btn_cancel = (Button) findViewById(btn_cancel);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_payment_method = (Button) findViewById(R.id.btn_payment_method);

        //  btn_cancel.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_payment_method.setOnClickListener(this);

    }

    public void settingFields(CustomerResponse response) {

        //Customer OVERVIEW
        customerID = response.getCustomerId();
        setActivity(CustomerDetailsActivity.this);
        setActivityTitle(CustomerDetailsActivity.this, "Customer Id : " + response.getCustomerId());

        tv_customer_id.setText("" + response.getCustomerId());
        tv_first_name.setText("" + response.getFirstName());
        tv_last_name.setText("" + response.getLastName());
        tv_email.setText("" + response.getEmail());

        if (response.getCompany() != null) {
            tv_company.setText("" + response.getCompany());
        }
        if (response.getPhone() != null) {
            tv_phone.setText("" + response.getPhone());
        }
        tv_notes.setText("" + response.getNotes());
        if (response.isSendEmailReceipts() == true)
            tv_send_email_address.setText("YES");
        else
            tv_send_email_address.setText("NO");

        //ADDRESS
        if (response.getAddress() != null) {

            if (response.getAddress().getLine1() != null) {
                tv_line_one.setText("" + response.getAddress().getLine1());
            }

            if (response.getAddress().getCity() != null) {
                tv_city.setText("" + response.getAddress().getCity());
            }

            if (response.getAddress().getState() != null) {
                HashMap<String, String> hashMap = new TokenUtility().getStates();
                tv_state.setText("" + hashMap.get(response.getAddress().getState()));
            }

            if (response.getAddress().getZip() != null) {
                tv_zip_code.setText("" + response.getAddress().getZip());
            }

            if (response.getAddress().getCountry() != null) {
                tv_country.setText("" + response.getAddress().getCountry());
            }

        }

        //USER DEFINED FIELDS
        if (response.getUserDefinedFields() != null) {

            HashMap<String, String> user = response.getUserDefinedFields();

            StringBuilder stringBuilder = new StringBuilder();

            if (user.get("UDF1") != null) {
                stringBuilder.append("User Defined Field #1: \t\t" + user.get("UDF1") + "\n");
            }
            if (user.get("UDF2") != null) {
                stringBuilder.append("User Defined Field #2: \t\t" + user.get("UDF2") + "\n");
            }
            if (user.get("UDF3") != null) {
                stringBuilder.append("User Defined Field #3: \t\t" + user.get("UDF3") + "\n");
            }
            if (user.get("UDF4") != null) {
                stringBuilder.append("User Defined Field #4: \t\t" + user.get("UDF4"));
            }
            tv_udfname.setText("" + stringBuilder);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_edit:
                Intent update = new Intent(this, CreateCustomer.class);
                update.putExtra("response", responseFromIntent);
                startActivity(update);
                break;

            case R.id.btn_delete:
                deleteCustomer();
                break;

            case R.id.btn_payment_method:

                Intent retrieve = new Intent(CustomerDetailsActivity.this, PaymentMethodDetailsActivity.class);
                retrieve.putExtra("response", responseFromIntent);
                startActivity(retrieve);

                break;

//            case btn_cancel:
//                finish();
//                break;
            default:
                break;
        }
    }

    public void deleteCustomer() {
        DeletePaymentMethodRequest deletePaymentMethodRequest = new DeletePaymentMethodRequest();
        TokenUtility.populateRequestHeaderFields(deletePaymentMethodRequest, this);
        deletePaymentMethodRequest.setCustomerId("" + customerID);
        deletePaymentMethodRequest.setPaymentMethodId("1");
        new PaymentMethodDeleteTask(deletePaymentMethodRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CustomerDetailsActivity.this);
                startProgressBar(progressDialog, "Deleting...");
            }

            @Override
            protected void onPostExecute(PaymentMethodResponse paymentMethodResponse) {

                Log.d("customerResponse", "" + paymentMethodResponse.toJson());

                if (paymentMethodResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (paymentMethodResponse != null) {
                    dismissProgressBar(progressDialog);

                } else {
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();


    }
}