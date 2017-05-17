package com.worldpayment.demoapp.activities.settlement;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchResponse;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.adapters.SettlementAdapter;

import java.util.List;

public class TransactionListActivity extends WorldBaseActivity {

    RecyclerView recyclerView;
    TextView toolbar_title;
    String status;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_recycler_toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        toolbar_title = (TextView) appBarLayout.findViewById(R.id.toolbar_title);

        if (getIntent().getExtras() != null) {
            status = getIntent().getExtras().getString("status");
            if (status.equals("current")) {
                String batchPutExtra = getIntent().getExtras().getString("batchPutExtra");
                Gson gson = new Gson();
                BatchResponse response = gson.fromJson(batchPutExtra, BatchResponse.class);
                if (getIntent().getExtras().getString("search") != null && getIntent().getExtras().getString("search").equals("search"))
                    setActivityTitle(TransactionListActivity.this, "Transaction List");
                else
                    setActivityTitle(TransactionListActivity.this, "Batch Id #" + response.getId());
                setCurrentBatchList(response);

            } else {
                String batchResponse = getIntent().getExtras().getString("batchResponse");
                Gson gson = new Gson();
                GetTransactionsBatchResponse response = gson.fromJson(batchResponse, GetTransactionsBatchResponse.class);
                setActivityTitle(TransactionListActivity.this, "Batch Id #" + response.getId());
                setBatchList(response);
            }
        }
    }

    public void setCurrentBatchList(BatchResponse response) {

        List<TransactionResponse> transactionResponses = response.getTransactions();
        if (transactionResponses != null) {
            SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, transactionResponses);
            adapter.notifyDataSetChanged();
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(TransactionListActivity.this, 1);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

        }
    }

    public void setBatchList(GetTransactionsBatchResponse response) {

        final List<TransactionResponse> transactionResponses = response.getTransactions();
        if (transactionResponses != null) {
            SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, transactionResponses);
            adapter.notifyDataSetChanged();
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            adapter.notifyDataSetChanged();
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            Log.d("...", "Last Item Wow !" + transactionResponses.get(newState));
                            //Do pagination.. i.e. fetch new data
                        }
                    }

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }


// Info With Batch Id

//    public void getBatchInformation(GetTransactionsBatchRequest getCurrentBatchRequest) {
//
//        new TransactionGetBatchTask(getCurrentBatchRequest) {
//            ProgressDialog progressDialog;
//            List<TransactionResponse> transactionResponses;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progressDialog = new ProgressDialog(TransactionListActivity.this);
//                startProgressBar(progressDialog, "Getting Batch...");
//            }
//
//            @Override
//            protected void onPostExecute(GetTransactionsBatchResponse getTransactionsBatchResponse) {
//
//                if (getTransactionsBatchResponse != null) {
//                    if (getTransactionsBatchResponse.getResponseCode() == ResponseCode.APPROVED) {
//                        transactionResponses = getTransactionsBatchResponse.getTransactions();
//                        if (transactionResponses != null || transactionResponses.equals("[]")) {
//                            toolbar_title.setText("Batch Id : " + batchId);
//                            SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, transactionResponses);
//                            adapter.notifyDataSetChanged();
//                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TransactionListActivity.this, 1);
//
//                            recyclerView.setLayoutManager(mLayoutManager);
//                            recyclerView.setHasFixedSize(true);
//                            recyclerView.setItemAnimator(new DefaultItemAnimator());
//                            recyclerView.setAdapter(adapter);
//
//                        }
//                    } else {
//                        showDialogView(getResources().getString(R.string.error), getTransactionsBatchResponse.getResponseMessage(), TransactionListActivity.this);
//                    }
//                } else {
//                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.noTransaction), TransactionListActivity.this);
//                }
//                dismissProgressBar(progressDialog);
//            }
//        }.execute();
//    }
}
