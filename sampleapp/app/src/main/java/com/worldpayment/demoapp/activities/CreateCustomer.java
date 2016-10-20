package com.worldpayment.demoapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.KeyboardUtility;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.SplashActivity;
import com.worldpay.library.views.iM3Form;
import com.worldpay.library.views.iM3FormEditText;
import com.worldpay.library.views.iM3NotEmptyValidator;
import com.worldpay.library.webservices.services.customers.CreateCustomerRequest;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.tasks.CustomerCreateTask;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.DebitCreditActivity.PREF_AUTH_TOKEN;
import static com.worldpayment.demoapp.activities.RefundVoidViewActivity.buttonEnabled;
import static com.worldpayment.demoapp.activities.RefundVoidViewActivity.count;

public class CreateCustomer extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button btn_create, btn_cancel, btn_yes, btn_no;
    iM3FormEditText field_first_name, field_last_name, field_phone_number, field_email_address;
    private iM3Form validateAlls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        mappingViews();
    }

    public void mappingViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Customer");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_no = (Button) findViewById(R.id.btn_no);

        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_create:
                KeyboardUtility.closeKeyboard(this, view);
                validationFields();
                break;

            case R.id.btn_cancel:
                break;

            case R.id.btn_yes:
                count = 0;
                buttonEnabled(btn_yes, btn_no, count);
                break;

            case R.id.btn_no:
                count = 1;
                buttonEnabled(btn_no, btn_yes, count);
                break;

            default:
                break;
        }
    }


    public void validationFields() {

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();

        createCustomerRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
        createCustomerRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);

        validateAlls = new iM3Form();

        field_first_name = (iM3FormEditText) findViewById(R.id.field_first_name);
        field_first_name.addValidator(new iM3NotEmptyValidator("First Name is required!"));
        validateAlls.addItem(field_first_name);

        field_last_name = (iM3FormEditText) findViewById(R.id.field_last_name);
        field_last_name.addValidator(new iM3NotEmptyValidator("Last Name is required!"));
        validateAlls.addItem(field_last_name);

        if (validateAlls.validateAll()) {
            createCustomerRequest.setFirstName(field_first_name.getValue());
            createCustomerRequest.setLastName(field_last_name.getValue());

            if (count == 0) {
                createCustomerRequest.setSendEmailReceipts(true);
            } else {
                createCustomerRequest.setSendEmailReceipts(false);
            }
            String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
            createCustomerRequest.setAuthToken(authToken);
            createCustomerRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            createCustomerRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            createCustomerRequest.setMerchantId(MERCHANT_ID);
            createCustomerRequest.setMerchantKey(MERCHANT_KEY);
            createCustomer(createCustomerRequest);
        }
    }

    public void createCustomer(CreateCustomerRequest createCustomerRequest) {

        new CustomerCreateTask(createCustomerRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreateCustomer.this);
                SplashActivity.startProgressBar(progressDialog, "Creating...");
            }

            @Override
            protected void onPostExecute(CustomerResponse customerResponse) {
                if (customerResponse.hasError()) {
                    return;
                }
                Log.d("customerResponse", "" + customerResponse.getHttpStatusCode());
                if (customerResponse.getHttpStatusCode().equals("OK")) {
                    SplashActivity.showSuccessDialog("Customer created successfully", CreateCustomer.this);
                } else {
                    SplashActivity.showErrorDialog("Customer creation failed! \n" + customerResponse.getMessage(), CreateCustomer.this);
                }

                SplashActivity.dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
