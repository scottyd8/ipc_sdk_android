package com.worldpayment.demoapp.activities.debitcredit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpayment.demoapp.Navigation;
import com.worldpayment.demoapp.R;

public class TransactionDetails extends AppCompatActivity implements View.OnClickListener {
    //TRANSACTION OVERVIEW
    TextView tv_transaction_id, tv_amount, tv_gratuity, tv_payment_type, tv_response_text, tv_response_message, tv_cashAmount;
    //CARD DATA
    TextView tv_card_no, tv_first_name, tv_last_name, tv_expiration;
    //BILLING ADDRESS
    TextView tv_street, tv_city, tv_state, tv_zip_code, tv_company, tv_phone;
    //CUSTOMER
    TextView tv_customer_id, tv_email;

    Button btn_continue_pay;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        initComponents();
        if (getIntent().getExtras() != null) {
            from = getIntent().getExtras().getString("from");

            if (from.equals("adapter")) {
                String batchResponse = getIntent().getExtras().getString("batchResponse");
                Gson gson = new Gson();
                TransactionResponse response = gson.fromJson(batchResponse, TransactionResponse.class);
                settingFields(response);
            } else if (from.equals("approved")) {
                String approvedResponse = getIntent().getExtras().getString("approvedResponse");
                Gson gson = new Gson();
                TransactionResponse response = gson.fromJson(approvedResponse, TransactionResponse.class);
                settingFields(response);
            }
        }
    }

    public void initComponents() {

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView) appBarLayout.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText("Transaction Details");
        getSupportActionBar().setTitle("");
        //TRANSACTION OVERVIEW
        tv_transaction_id = (TextView) findViewById(R.id.tv_transaction_id);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        tv_gratuity = (TextView) findViewById(R.id.tv_gratuity);
        tv_cashAmount = (TextView) findViewById(R.id.tv_cashAmount);
        tv_payment_type = (TextView) findViewById(R.id.tv_payment_type);
        tv_response_text = (TextView) findViewById(R.id.tv_response_text);
        tv_response_message = (TextView) findViewById(R.id.tv_response_message);

        //CARD DATA
        tv_card_no = (TextView) findViewById(R.id.tv_card_no);
        tv_first_name = (TextView) findViewById(R.id.tv_first_name);
        tv_last_name = (TextView) findViewById(R.id.tv_last_name);
        tv_expiration = (TextView) findViewById(R.id.tv_expiration);

        //BILLING ADDRESS
        tv_street = (TextView) findViewById(R.id.tv_street);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_zip_code = (TextView) findViewById(R.id.tv_zip_code);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        //CUSTOMER
        tv_customer_id = (TextView) findViewById(R.id.tv_customer_id);
        tv_email = (TextView) findViewById(R.id.tv_email);

        //Button
        btn_continue_pay = (Button) findViewById(R.id.btn_continue_pay);
        btn_continue_pay.setOnClickListener(this);

    }

    public void settingFields(TransactionResponse response) {

        //TRANSACTION OVERVIEW
        tv_transaction_id.setText("" + response.getId());
//        if (response.getGratuity() != null && response.getCashBackAmount() != null) {
//            BigDecimal additionAmount = response.getAmount();
//            additionAmount = additionAmount.add(response.getGratuity());
//            additionAmount = additionAmount.add(response.getCashBackAmount());
//            tv_amount.setText("" + additionAmount);
//            tv_cashAmount.setText("" + response.getCashBackAmount());
//            tv_gratuity.setText("" + response.getGratuity());
//
//        } else if (response.getGratuity() != null) {
//            tv_amount.setText("" + response.getAmount().add(response.getGratuity()));
//            tv_gratuity.setText("" + response.getGratuity());
//
//        } else if (response.getCashBackAmount() != null) {
//            tv_amount.setText("" + response.getAmount().add(response.getCashBackAmount()));
//            tv_cashAmount.setText("" + response.getCashBackAmount());
//
//        } else {
//            tv_amount.setText("" + response.getAmount());
//        }
        tv_amount.setText("" + response.getAmount());

        tv_cashAmount.setText("" + response.getCashBackAmount());
        tv_gratuity.setText("" + response.getGratuity());

        tv_payment_type.setText("" + response.getPaymentType());
        tv_response_text.setText("" + response.getResponseText());
        tv_response_message.setText("" + getResources().getString(R.string.success));

        //CARD DATA
        if (response.getCard() != null) {
            tv_card_no.setText("" + response.getCard().getNumber());
            tv_first_name.setText("" + response.getCard().getFirstName());
            tv_last_name.setText("" + response.getCard().getLastName());

            if (response.getCard() != null && response.getCard().getExpirationMonth() != 0 &&
                    response.getCard().getExpirationYear() != 0) {
                tv_expiration.setText("" + response.getCard().getExpirationMonth() + "/" + response.getCard().getExpirationYear());
            }
        }
        //BILLING ADDRESS
        if (response.getBillAddress() != null) {
            if (response.getBillAddress().getLine1() != null) {
                tv_street.setText("" + response.getBillAddress().getLine1());
            }

            if (response.getBillAddress().getCity() != null) {
                tv_city.setText("" + response.getBillAddress().getCity());
            }

            if (response.getBillAddress().getState() != null) {
                tv_state.setText("" + response.getBillAddress().getState());
            }

            if (response.getBillAddress().getZip() != null) {
                tv_zip_code.setText("" + response.getBillAddress().getZip());
            }

            if (response.getBillAddress().getCompany() != null) {
                tv_company.setText("" + response.getBillAddress().getCompany());
            }

            if (response.getBillAddress().getPhone() != null) {
                tv_phone.setText("" + response.getBillAddress().getPhone());
            }
        }
        //CUSTOMER
        tv_customer_id.setText("" + response.getCustomerId());
        tv_email.setText("" + response.getEmail());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_continue_pay:
                if (from.equals("adapter")) {
                    finish();
                } else {
                    Intent credit = new Intent(TransactionDetails.this, Navigation.class);
                    startActivity(credit);
                    finish();
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        return;
    }
}
