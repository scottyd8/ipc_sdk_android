package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.customers.GetCustomerRequest;
import com.worldpay.library.webservices.tasks.CustomerGetTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

public class RetrieveCustomer extends WorldBaseActivity implements View.OnClickListener {

    private Button btn_search;
    private WPFormEditText field_customer_id;
    WPForm validateID;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_customer);
        setActivity(RetrieveCustomer.this);
        mappingViews();
    }

    public void mappingViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        validateID = new WPForm();
        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        field_customer_id.addValidator(new WPNotEmptyValidator("Customer Id is required!"));
        validateID.addItem(field_customer_id);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_search:
                KeyboardUtility.closeKeyboard(this, v);
                fetchCustomers();
                break;
        }
    }


    public void fetchCustomers() {

        GetCustomerRequest getCustomerRequest = new GetCustomerRequest();
        if (validateID.validateAll()) {

            TokenUtility.populateRequestHeaderFields(getCustomerRequest, this);
            getCustomerRequest.setId(field_customer_id.getValue());

            new CustomerGetTask(getCustomerRequest) {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(RetrieveCustomer.this);
                    startProgressBar(progressDialog, "Retrieving customer...");
                }

                @Override
                protected void onPostExecute(CustomerResponse customerResponse) {

                    dismissProgressBar(progressDialog);

                    if (customerResponse != null) {

                        Log.d("customerResponse", "" + customerResponse.toJson());
//                        VaultPaymentMethod[] vaultPaymentMethods = customerResponse.getPaymentMethods();
//                        Log.d("vaultPaymentMethods", "" + vaultPaymentMethods.length + "   " + vaultPaymentMethods[0]);
//
//                        for (int i = 0; i < vaultPaymentMethods.length; i++) {
//                            list.add(vaultPaymentMethods[i]);
//                        }

                        if (customerResponse.getResponseCode() == ResponseCode.APPROVED) {

                            Intent retrieve = new Intent(RetrieveCustomer.this, CustomerDetailsActivity.class);
                            Gson gson = new Gson();
                            String response = gson.toJson(customerResponse);
                            retrieve.putExtra("response", response);
                            startActivity(retrieve);

//                            responseCustomerDetails = customerResponse;

                        } else {
                            showDialogView(getResources().getString(R.string.error), customerResponse.getResponseMessage(), RetrieveCustomer.this);
                        }
                    } else {
                        showDialogView(getResources().getString(R.string.error), "Web Service error!", RetrieveCustomer.this);
                    }
                }
            }.execute();

        }
    }

}
