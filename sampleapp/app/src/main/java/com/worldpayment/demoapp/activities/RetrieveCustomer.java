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
import com.worldpay.library.webservices.network.iM3HttpResponse;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.customers.GetCustomerRequest;
import com.worldpay.library.webservices.tasks.CustomerGetTask;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.DebitCreditActivity.PREF_AUTH_TOKEN;

public class RetrieveCustomer extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btn_search, btn_cancel;
    private iM3FormEditText field_customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_customer);
        mappingViews();
    }

    public void mappingViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Retrieve Customer");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        field_customer_id = (iM3FormEditText) findViewById(R.id.field_customer_id);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_search.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_search:
                KeyboardUtility.closeKeyboard(this, v);
                fetchCustomers();
                break;

            case R.id.btn_cancel:
                KeyboardUtility.closeKeyboard(this, v);
                finish();
                break;

            default:
                break;
        }
    }

    public void fetchCustomers() {

        iM3Form validateAlls = new iM3Form();
        field_customer_id = (iM3FormEditText) findViewById(R.id.field_customer_id);
        field_customer_id.addValidator(new iM3NotEmptyValidator("Customer ID is required!"));
        validateAlls.addItem(field_customer_id);

        GetCustomerRequest getCustomerRequest = new GetCustomerRequest();
        if (validateAlls.validateAll()) {
            getCustomerRequest.setId(field_customer_id.getValue());
            String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
            getCustomerRequest.setAuthToken(authToken);
            getCustomerRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            getCustomerRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getCustomerRequest.setMerchantId(MERCHANT_ID);
            getCustomerRequest.setMerchantKey(MERCHANT_KEY);

            new CustomerGetTask(getCustomerRequest) {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(RetrieveCustomer.this);
                    SplashActivity.startProgressBar(progressDialog, "Retrieving customers...");
                }

                @Override
                protected void onPostExecute(CustomerResponse customerResponse) {
                    if (customerResponse.hasError()) {
                        return;
                    }
                    Log.d("customerResponse", "" + customerResponse.getFirstName());
                    if (customerResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                        SplashActivity.showSuccessDialog("Customer Name : " + customerResponse.getFirstName(), RetrieveCustomer.this);
                    } else {
                        SplashActivity.showErrorDialog("" + customerResponse.getMessage(), RetrieveCustomer.this);
                    }
                    SplashActivity.dismissProgressBar(progressDialog);
                }
            }.execute();

        }

    }
}
