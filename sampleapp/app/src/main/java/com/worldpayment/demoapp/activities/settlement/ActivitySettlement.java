package com.worldpayment.demoapp.activities.settlement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.CloseCurrentBatchRequest;
import com.worldpay.library.webservices.tasks.BatchCloseCurrentTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class ActivitySettlement extends WorldBaseActivity implements View.OnClickListener {

    EditText field_transaction_id;
    Button btn_get_batch, btn_close_current_batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        setActivity(ActivitySettlement.this);
        initComponents();
    }

    public void initComponents() {

        field_transaction_id = (EditText) findViewById(R.id.field_transaction_id);
        btn_get_batch = (Button) findViewById(R.id.btn_get_batch);
        btn_close_current_batch = (Button) findViewById(R.id.btn_close_current_batch);

        btn_get_batch.setOnClickListener(this);
        btn_close_current_batch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_get_batch:
                if (!field_transaction_id.getText().equals(" ") && field_transaction_id.getText().length() > 5) {
//3674699
                    KeyboardUtility.closeKeyboard(this, view);
                    Intent intent = new Intent(ActivitySettlement.this, TransactionListActivity.class);
                    intent.putExtra("batchId", field_transaction_id.getText().toString());
                    startActivity(intent);

                } else {
                    KeyboardUtility.closeKeyboard(this, view);
                    Intent intent = new Intent(ActivitySettlement.this, TransactionListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_close_current_batch:

                String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
                CloseCurrentBatchRequest closeCurrentBatchRequest = new CloseCurrentBatchRequest();
                closeCurrentBatchRequest.setMerchantId(MERCHANT_ID);
                closeCurrentBatchRequest.setMerchantKey(MERCHANT_KEY);
                closeCurrentBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
                closeCurrentBatchRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
                closeCurrentBatchRequest.setAuthToken(authToken);
                closeCurrentBatch(closeCurrentBatchRequest);
                break;

            default:
                break;
        }
    }

    public void closeCurrentBatch(CloseCurrentBatchRequest closeCurrentBatchRequest) {

        new BatchCloseCurrentTask(closeCurrentBatchRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ActivitySettlement.this);
                startProgressBar(progressDialog, "Closing Current Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {
                if (batchResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }
                Log.d("batchResponse", "" + batchResponse.getId());
                if (batchResponse != null) {
                    showSuccessDialog(getResources().getString(R.string.success), getResources().getString(R.string.batchClosed), ActivitySettlement.this);
                } else {
                    showSuccessDialog(getResources().getString(R.string.error), batchResponse.getMessage(), ActivitySettlement.this);
                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
