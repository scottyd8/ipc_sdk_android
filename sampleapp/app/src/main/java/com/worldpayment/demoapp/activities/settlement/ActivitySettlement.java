package com.worldpayment.demoapp.activities.settlement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.CloseCurrentBatchRequest;
import com.worldpay.library.webservices.services.batches.GetCurrentBatchRequest;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchRequest;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchResponse;
import com.worldpay.library.webservices.tasks.BatchCloseCurrentTask;
import com.worldpay.library.webservices.tasks.BatchGetCurrentTask;
import com.worldpay.library.webservices.tasks.TransactionGetBatchTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

public class ActivitySettlement extends WorldBaseActivity implements View.OnClickListener {

    EditText field_batch_id;
    Button btn_get_batch, btn_close_current_batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        setActivity(ActivitySettlement.this);
        initComponents();
    }

    public void initComponents() {

        field_batch_id = (EditText) findViewById(R.id.field_batch_id);

        btn_get_batch = (Button) findViewById(R.id.btn_get_batch);
        btn_close_current_batch = (Button) findViewById(R.id.btn_close_current_batch);

        btn_get_batch.setOnClickListener(this);
        btn_close_current_batch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_get_batch:
                if (!field_batch_id.getText().equals(" ") && field_batch_id.getText().length() > 5) {
                    KeyboardUtility.closeKeyboard(this, view);
                    getBatchInformation();

                } else {
                    KeyboardUtility.closeKeyboard(this, view);
                    getCurrentBatchInformation();
                }
                break;

            case R.id.btn_close_current_batch:

                KeyboardUtility.closeKeyboard(this, view);
                closeCurrentBatch();
                break;

            default:
                break;
        }
    }

    public void getCurrentBatchInformation() {

        GetCurrentBatchRequest getCurrentBatchRequest = new GetCurrentBatchRequest();
        TokenUtility.populateRequestHeaderFields(getCurrentBatchRequest, this);

        new BatchGetCurrentTask(getCurrentBatchRequest) {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ActivitySettlement.this);
                startProgressBar(progressDialog, "Getting Current Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {

                if (batchResponse != null) {
                    if (batchResponse.getResponseCode() == ResponseCode.APPROVED) {
                        Intent intent = new Intent(ActivitySettlement.this, TransactionListActivity.class);
                        intent.putExtra("batchPutExtra", batchResponse.toJson());
                        intent.putExtra("status", "current");
                        startActivity(intent);
                    } else {
                        showDialogView(getResources().getString(R.string.error), batchResponse.getResponseMessage(), ActivitySettlement.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.noTransaction), ActivitySettlement.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();

    }


    public void getBatchInformation() {

        GetTransactionsBatchRequest getTransactionsBatchRequest = new GetTransactionsBatchRequest();
        TokenUtility.populateRequestHeaderFields(getTransactionsBatchRequest, this);
        getTransactionsBatchRequest.setBatchId("" + field_batch_id.getText().toString());

        new TransactionGetBatchTask(getTransactionsBatchRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ActivitySettlement.this);
                startProgressBar(progressDialog, "Getting Batch...");
            }

            @Override
            protected void onPostExecute(GetTransactionsBatchResponse getTransactionsBatchResponse) {

                if (getTransactionsBatchResponse != null) {
                    if (getTransactionsBatchResponse.getResponseCode() == ResponseCode.APPROVED) {
                        Intent intent = new Intent(ActivitySettlement.this, TransactionListActivity.class);
                        intent.putExtra("batchResponse", getTransactionsBatchResponse.toJson());
                        intent.putExtra("status", "batch");
                        startActivity(intent);
                    } else {
                        showDialogView(getResources().getString(R.string.error), getTransactionsBatchResponse.getResponseMessage(), ActivitySettlement.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.noTransaction), ActivitySettlement.this);
                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }


    public void closeCurrentBatch() {

        CloseCurrentBatchRequest closeCurrentBatchRequest = new CloseCurrentBatchRequest();
        TokenUtility.populateRequestHeaderFields(closeCurrentBatchRequest, this);

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
                if (batchResponse != null) {
                    if (batchResponse.getResponseCode() == ResponseCode.APPROVED) {
                        showDialogView(getResources().getString(R.string.success), "Batch " + batchResponse.getId() + " " + getResources().getString(R.string.batchClosed), ActivitySettlement.this);
                    } else {
                        showDialogView(getResources().getString(R.string.error), batchResponse.getResponseMessage(), ActivitySettlement.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), "Null response! Service Error!", ActivitySettlement.this);
                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
