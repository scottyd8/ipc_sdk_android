package com.worldpayment.demoapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.SplashActivity;
import com.worldpayment.demoapp.adapters.SettlementAdapter;
import com.worldpay.library.webservices.network.iM3HttpResponse;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.GetCurrentBatchRequest;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchRequest;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchResponse;
import com.worldpay.library.webservices.tasks.BatchGetCurrentTask;
import com.worldpay.library.webservices.tasks.TransactionGetBatchTask;

import java.util.List;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.DebitCreditActivity.PREF_AUTH_TOKEN;

public class TransactionListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionResponse[] transactionResponses;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_search);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Batch Details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        if (getIntent().getExtras() != null) {

            GetTransactionsBatchRequest getTransactionsBatchRequest = new GetTransactionsBatchRequest();
            String batchId = getIntent().getExtras().getString("batchId");
            getSupportActionBar().setTitle("Batch ID : " + batchId);
            getTransactionsBatchRequest.setBatchId(batchId);

            getTransactionsBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getTransactionsBatchRequest.setMerchantId(MERCHANT_ID);
            getTransactionsBatchRequest.setMerchantKey(MERCHANT_KEY);
            getTransactionsBatchRequest.setAuthToken(authToken);
            getTransactionsBatchRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);

            getBatchInformation(getTransactionsBatchRequest);

        } else {
            GetCurrentBatchRequest getCurrentBatchRequest = new GetCurrentBatchRequest();

            getCurrentBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getCurrentBatchRequest.setMerchantId(MERCHANT_ID);
            getCurrentBatchRequest.setMerchantKey(MERCHANT_KEY);
            getCurrentBatchRequest.setAuthToken(authToken);
            getCurrentBatchRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);

            getCurrentBatchInformation(getCurrentBatchRequest);
        }
    }

    //Current Batch Info
    public void getCurrentBatchInformation(GetCurrentBatchRequest getCurrentBatchRequest) {

        new BatchGetCurrentTask(getCurrentBatchRequest) {
            ProgressDialog progressDialog;
            List<TransactionResponse> transactionResponses;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(TransactionListActivity.this);
                SplashActivity.startProgressBar(progressDialog, "Getting Current Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {
                if (batchResponse.hasError()) {
                    return;
                }
                Log.d("batchResponse", "" + batchResponse.getHttpStatusCode());
                getSupportActionBar().setTitle("Batch ID : " + batchResponse.getId());
                if (batchResponse != null && batchResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                    transactionResponses = batchResponse.getTransactions();
                    if (transactionResponses != null) {
                        SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, transactionResponses);
                        adapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TransactionListActivity.this, 1);

                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);

                    } else {
                        SplashActivity.showSuccessDialog("No transaction at the moment!", TransactionListActivity.this);
                    }
                } else {
                    SplashActivity.showErrorDialog("" + batchResponse.getMessage(), TransactionListActivity.this);
                }
                SplashActivity.dismissProgressBar(progressDialog);
            }
        }.execute();

    }

    // Info With Batch Id
    public void getBatchInformation(GetTransactionsBatchRequest getCurrentBatchRequest) {

        new TransactionGetBatchTask(getCurrentBatchRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(TransactionListActivity.this);
                SplashActivity.startProgressBar(progressDialog, "Getting data...");
            }

            @Override
            protected void onPostExecute(GetTransactionsBatchResponse getTransactionsBatchResponse) {
                if (getTransactionsBatchResponse.hasError()) {
                    return;
                }
                Log.d("batchResponse", "" + getTransactionsBatchResponse.getMessage());
                SplashActivity.dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
