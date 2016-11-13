package com.worldpayment.demoapp.activities.settlement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.worldpay.library.webservices.network.WPHttpResponse;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.GetCurrentBatchRequest;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchRequest;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchResponse;
import com.worldpay.library.webservices.tasks.BatchGetCurrentTask;
import com.worldpay.library.webservices.tasks.TransactionGetBatchTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.adapters.SettlementAdapter;

import java.util.List;

import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class TransactionListActivity extends WorldBaseActivity {

    RecyclerView recyclerView;
    TextView toolbar_title;
    String batchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_search);
        setActivity(TransactionListActivity.this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        toolbar_title = (TextView) appBarLayout.findViewById(R.id.toolbar_title);

        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        if (getIntent().getExtras() != null) {

            GetTransactionsBatchRequest getTransactionsBatchRequest = new GetTransactionsBatchRequest();
            batchId = getIntent().getExtras().getString("batchId");
            getTransactionsBatchRequest.setBatchId(batchId);
            getTransactionsBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getTransactionsBatchRequest.setMerchantId(BuildConfig.MERCHANT_ID);
            getTransactionsBatchRequest.setMerchantKey(BuildConfig.MERCHANT_KEY);
            getTransactionsBatchRequest.setAuthToken(authToken);
            getTransactionsBatchRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            getBatchInformation(getTransactionsBatchRequest);

        } else {
            GetCurrentBatchRequest getCurrentBatchRequest = new GetCurrentBatchRequest();
            getCurrentBatchRequest.setAuthToken(authToken);
            getCurrentBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getCurrentBatchRequest.setMerchantKey(BuildConfig.MERCHANT_KEY);
            getCurrentBatchRequest.setMerchantId(BuildConfig.MERCHANT_ID);
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
                startProgressBar(progressDialog, "Getting Current Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {
                if (batchResponse.hasError()) {
                    return;
                }
                if (batchResponse != null && batchResponse.getHttpStatusCode() == WPHttpResponse.HttpStatus.OK) {
                    toolbar_title.setText("Batch ID : " + batchResponse.getId());
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
                        showSuccessDialog(getResources().getString(R.string.success), getResources().getString(R.string.noTransaction), TransactionListActivity.this);
                    }
                } else {
                    showSuccessDialog(getResources().getString(R.string.error), batchResponse.getMessage(), TransactionListActivity.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();

    }

// Info With Batch Id

    public void getBatchInformation(GetTransactionsBatchRequest getCurrentBatchRequest) {

        new TransactionGetBatchTask(getCurrentBatchRequest) {
            ProgressDialog progressDialog;
            List<TransactionResponse> transactionResponses;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(TransactionListActivity.this);
                startProgressBar(progressDialog, "Getting Batch...");
            }

            @Override
            protected void onPostExecute(GetTransactionsBatchResponse getTransactionsBatchResponse) {
                if (getTransactionsBatchResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (getTransactionsBatchResponse != null && getTransactionsBatchResponse.getHttpStatusCode() == WPHttpResponse.HttpStatus.OK) {
                    transactionResponses = getTransactionsBatchResponse.getTransactions();
                    if (transactionResponses != null) {
                        toolbar_title.setText("Batch ID : " + batchId);
                        SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, transactionResponses);
                        adapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TransactionListActivity.this, 1);

                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);

                    } else {
                        showSuccessDialog(getResources().getString(R.string.success), getResources().getString(R.string.noTransaction), TransactionListActivity.this);
                    }
                } else {
                    showSuccessDialog(getResources().getString(R.string.error), getTransactionsBatchResponse.getMessage(), TransactionListActivity.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
