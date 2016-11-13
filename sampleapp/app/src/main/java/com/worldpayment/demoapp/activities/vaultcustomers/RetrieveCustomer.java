package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.customers.GetCustomerRequest;
import com.worldpay.library.webservices.tasks.CustomerGetTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class RetrieveCustomer extends WorldBaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btn_search, btn_update;
    private WPFormEditText field_customer_id;
    WPForm validateID;

    public static CustomerResponse responseCustomerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_customer);
        setActivity(RetrieveCustomer.this);
        mappingViews();
    }

    public void mappingViews() {

        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_update = (Button) findViewById(R.id.btn_update);

        btn_search.setOnClickListener(this);
        btn_update.setOnClickListener(this);

        validateID = new WPForm();
        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        field_customer_id.addValidator(new WPNotEmptyValidator("Customer ID is required!"));
        validateID.addItem(field_customer_id);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_search:
                KeyboardUtility.closeKeyboard(this, v);
                fetchCustomers("retrieve");
                break;

            case R.id.btn_update:
                KeyboardUtility.closeKeyboard(this, v);
                fetchCustomers("update");
                break;

            default:
                break;
        }
    }


    public void fetchCustomers(final String check) {

        GetCustomerRequest getCustomerRequest = new GetCustomerRequest();
        if (validateID.validateAll()) {
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
                    startProgressBar(progressDialog, "Retrieving customers...");
                }

                @Override
                protected void onPostExecute(CustomerResponse customerResponse) {

                    Log.d("customerResponse", "" + customerResponse.toJson());

                    if (customerResponse.hasError()) {
                        dismissProgressBar(progressDialog);
                        return;
                    }

                    if (customerResponse != null) {
//                        SplashActivity.showSuccessDialog("SUCCESS", customerResponse.toJson(), RetrieveCustomer.this);
                        responseCustomerDetails = customerResponse;
                        if (responseCustomerDetails != null) {
                            dismissProgressBar(progressDialog);
                            if (check.equals("retrieve")) {
                                Intent retrieve = new Intent(RetrieveCustomer.this, CustomerDetailsActivity.class);
                                retrieve.putExtra("customer_id", field_customer_id.getValue());
                                startActivity(retrieve);
                            } else if (check.equals("update")) {
                                Intent update = new Intent(RetrieveCustomer.this, UpdateCustomer.class);
                                update.putExtra("customer_id", field_customer_id.getValue());
                                startActivity(update);
                            }
                        }
                    } else {
                        showSuccessDialog(getResources().getString(R.string.error), customerResponse.getMessage(), RetrieveCustomer.this);
                    }
                    dismissProgressBar(progressDialog);
                }
            }.execute();

        }
    }

}
