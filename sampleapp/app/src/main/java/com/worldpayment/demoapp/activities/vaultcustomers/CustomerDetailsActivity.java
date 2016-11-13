package com.worldpayment.demoapp.activities.vaultcustomers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpayment.demoapp.R;

import static com.worldpayment.demoapp.activities.vaultcustomers.RetrieveCustomer.responseCustomerDetails;

public class CustomerDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //CUSTOMER INFO
    TextView tv_customer_id, tv_first_name, tv_last_name, tv_email, tv_send_email_address, tv_notes;
    //ADDRESS
    TextView tv_line_one, tv_city, tv_state, tv_zip_code, tv_country, tv_company, tv_phone;
    //USER DEFINED FIELDS
    TextView tv_udfname, tv_udffield;

    Button btn_done;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        initComponents();

        if (getIntent().getExtras() != null) {
            String customer_id = getIntent().getExtras().getString("customer_id");
            if (responseCustomerDetails != null) {
                settingFields(responseCustomerDetails, customer_id);
                toolbar.setTitle("CUSTOMER ID : " + customer_id);
            } else {
                Toast.makeText(this, "Null response", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void initComponents() {

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView) appBarLayout.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText("Customer Details");
        getSupportActionBar().setTitle("");

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
        tv_udffield = (TextView) findViewById(R.id.tv_udffield);

        //Button
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);

    }

    public void settingFields(CustomerResponse response, String id) {

        //Customer OVERVIEW
        tv_customer_id.setText("" + id);
        tv_first_name.setText("" + response.getFirstName());
        tv_last_name.setText("" + response.getLastName());
        tv_email.setText("" + response.getEmail());
        tv_company.setText("" + response.getCompany());

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
                tv_state.setText("" + response.getAddress().getState());
            }

            if (response.getAddress().getZip() != null) {
                tv_zip_code.setText("" + response.getAddress().getZip());
            }

            if (response.getAddress().getCountry() != null) {
                tv_country.setText("" + response.getAddress().getCountry());
            }

        }

        //USER DEFINED FIELDS
        tv_udfname.setText("" + response.getUserDefinedFields());
        tv_udffield.setText("" + response.getUserDefinedFields());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_done:
                Intent credit = new Intent(CustomerDetailsActivity.this, VaultOperations.class);
                startActivity(credit);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent credit = new Intent(CustomerDetailsActivity.this, VaultOperations.class);
        startActivity(credit);
        finish();
    }
}