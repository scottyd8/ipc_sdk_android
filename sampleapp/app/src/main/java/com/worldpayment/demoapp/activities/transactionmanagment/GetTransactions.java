package com.worldpayment.demoapp.activities.transactionmanagment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.library.webservices.services.transactions.GetTransactionRequest;
import com.worldpay.library.webservices.services.transactions.GetTransactionResponse;
import com.worldpay.library.webservices.tasks.TransactionGetTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.activities.debitcredit.TransactionDetails;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

public class GetTransactions extends WorldBaseActivity implements View.OnClickListener {

    private Button btn_search;
    private WPFormEditText transaction_id;
    WPForm validateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_transactions);
        setActivity(this);
        mappingViews();
    }

    public void mappingViews() {
        transaction_id = (WPFormEditText) findViewById(R.id.transaction_id);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        validateID = new WPForm();
        transaction_id.addValidator(new WPNotEmptyValidator("Transaction Id is required!"));
        validateID.addItem(transaction_id);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_search:
                KeyboardUtility.closeKeyboard(this, view);
                fetchTransaction();
                break;
        }
    }

    public void fetchTransaction() {

        GetTransactionRequest request = new GetTransactionRequest();
        if (validateID.validateAll()) {

            TokenUtility.populateRequestHeaderFields(request, this);
            request.setId(transaction_id.getValue());

            new TransactionGetTask(request) {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(GetTransactions.this);
                    startProgressBar(progressDialog, "Getting Transaction...");
                }

                @Override
                protected void onPostExecute(GetTransactionResponse getTransactionResponse) {

                    dismissProgressBar(progressDialog);

                    if (getTransactionResponse != null) {

                        if (getTransactionResponse.getResponseCode() == ResponseCode.APPROVED) {
                            TransactionResponse transactionResponse = getTransactionResponse.getTransaction();
                            Intent retrieve = new Intent(GetTransactions.this, TransactionDetails.class);
                            Gson gson = new Gson();
                            String response = gson.toJson(transactionResponse);
                            retrieve.putExtra("from", "adapter");
                            retrieve.putExtra("batchResponse", response);
                            startActivity(retrieve);

                        } else {
                            showDialogView(getResources().getString(R.string.error), getTransactionResponse.getResponseMessage(), GetTransactions.this);
                        }
                    } else {
                        showDialogView(getResources().getString(R.string.error), "Web Service error!", GetTransactions.this);
                    }
                }
            }.execute();

        }
    }

}
