package com.worldpayment.demoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.KeyboardUtility;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.SplashActivity;
import com.worldpay.library.webservices.network.iM3HttpResponse;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.CloseCurrentBatchRequest;
import com.worldpay.library.webservices.tasks.BatchCloseCurrentTask;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.DebitCreditActivity.PREF_AUTH_TOKEN;

public class ActivitySettlement extends AppCompatActivity implements View.OnClickListener {

    EditText field_transaction_id;
    Button btn_get_batch, btn_close_current_batch;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);

        initComponents();
    }

    public void initComponents() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settlement");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
                SplashActivity.startProgressBar(progressDialog, "Closing Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {
                if (batchResponse.hasError()) {
                    return;
                }
                Log.d("batchResponse", "" + batchResponse.getId());
                if (batchResponse != null && batchResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                    SplashActivity.showSuccessDialog("Current Batch is closed", ActivitySettlement.this);
                } else {
                    SplashActivity.showSuccessDialog("Error!\n" + batchResponse.getMessage(), ActivitySettlement.this);
                }

                SplashActivity.dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
