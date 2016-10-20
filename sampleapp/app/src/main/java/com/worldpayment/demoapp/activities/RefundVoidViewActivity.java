package com.worldpayment.demoapp.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.utils.iM3Logger;
import com.worldpay.library.views.iM3CurrencyTextWatcher;
import com.worldpay.library.views.iM3Form;
import com.worldpay.library.views.iM3FormEditText;
import com.worldpay.library.views.iM3NotEmptyValidator;
import com.worldpay.library.webservices.network.iM3HttpResponse;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.tasks.PaymentRefundTask;
import com.worldpay.library.webservices.tasks.PaymentVoidTask;
import com.worldpay.ui.TransactionDialogFragment;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.KeyboardUtility;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.SplashActivity;

import java.math.BigDecimal;
import java.util.Locale;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.DebitCreditActivity.PREF_AUTH_TOKEN;
import static com.worldpayment.demoapp.DebitCreditActivity.TAG;
import static com.worldpayment.demoapp.DebitCreditActivity.openApprovedDialog;

public class RefundVoidViewActivity extends AppCompatActivity implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {
    Toolbar toolbar;
    iM3FormEditText field_transaction_id, field_transaction_amount;
    private Button btn_refund, btn_void, btn_start_transaction;
    TextView amount_textView;
    public static int count = 7;
    iM3Form validateRefund, validateVoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_void_view);
        initComponents();
        count = 0;
    }

    public void initComponents() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Refund/Void");
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

        btn_refund = (Button) findViewById(R.id.btn_refund);
        btn_refund.setOnClickListener(this);

        btn_void = (Button) findViewById(R.id.btn_void);
        btn_void.setOnClickListener(this);

        amount_textView = (TextView) findViewById(R.id.amount_textView);
        field_transaction_id = (iM3FormEditText) findViewById(R.id.field_transaction_id);
        field_transaction_amount = (iM3FormEditText) findViewById(R.id.field_transaction_amount);

        validateRefund = new iM3Form();
        validateVoid = new iM3Form();

        field_transaction_id.addValidator(new iM3NotEmptyValidator("Transaction ID is required!"));
        validateRefund.addItem(field_transaction_id);
        validateVoid.addItem(field_transaction_id);

        field_transaction_amount
                .addValidator(new iM3NotEmptyValidator("Transaction amount required!"));
        iM3CurrencyTextWatcher transactionAmountTextWatcher =
                new iM3CurrencyTextWatcher(field_transaction_amount, Locale.US,
                        new BigDecimal("999999.99"), true, true);
        field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validateRefund.addItem(field_transaction_amount);


        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);
    }

    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {
        iM3Logger.d(TAG,
                "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                        paymentResponse);

        switch (result) {
            case APPROVED:
                break;
            case AMOUNT_REJECTED:
                break;
            case CANCELED:
                break;
            case READER_ERROR:
                break;
            case AUTHENTICATION_FAILURE:
                break;
            case DECLINED_REVERSAL_FAILED:
                break;
            case DECLINED:
                break;
            default:
                break;
        }


    }

    @Override
    public void onTransactionError(@NonNull TransactionDialogFragment.TransactionError error,
                                   @Nullable String message) {
        iM3Logger.d(TAG, "onTransactionError :: error=" + error + ";message=" + message);
    }

    @Override
    public void onTransactionReversalFailed(ReversalRequest reversalRequest) {
        iM3Logger.d(TAG, "onTransactionReversalFailed :: reversalType=" + reversalRequest.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_refund:
                KeyboardUtility.closeKeyboard(this, v);
                count = 0;
                field_transaction_amount.setVisibility(View.VISIBLE);
                amount_textView.setVisibility(View.VISIBLE);
                buttonEnabled(btn_refund, btn_void, count);
                Log.d("btn_refund", "" + count);
                break;

            case R.id.btn_void:
                KeyboardUtility.closeKeyboard(this, v);
                count = 1;
                amount_textView.setVisibility(View.GONE);
                field_transaction_amount.setVisibility(View.GONE);
                buttonEnabled(btn_void, btn_refund, count);
                Log.d("btn_void", "" + count);

                break;

            case R.id.btn_start_transaction:
                KeyboardUtility.closeKeyboard(this, v);
                showTransactionFragment();
                break;
        }
    }

    public static void buttonEnabled(Button btn1, Button btn2, int flag) {
        if (flag == 0) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundResource(R.drawable.button_shap);

            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundResource(R.drawable.button_disable);
        } else if (flag == 1) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundResource(R.drawable.button_shap);

            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundResource(R.drawable.button_disable);
        }
    }

    private void showTransactionFragment() {

        ReversalRequest reversalRequest = new ReversalRequest();
        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        reversalRequest.setAuthToken(authToken);
        reversalRequest.setMerchantId(MERCHANT_ID);
        reversalRequest.setMerchantKey(MERCHANT_KEY);
        reversalRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
        reversalRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
        if (count == 0) {
            if (validateRefund.validateAll()) {

                reversalRequest.setTransactionId(field_transaction_id.getValue());


                if (!TextUtils.isEmpty(field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));

                    if (!transactionAmount.toString().equals("0.00")) {
                        reversalRequest.setAmount(transactionAmount);
                        refundCalling(reversalRequest);
                        Log.d("transactionAmount", "" + transactionAmount);
                    } else {
                        Toast.makeText(this, "Amount should be greater than zero!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        } else if (count == 1) {
            Log.d("count", "" + count);
            if (validateVoid.validateAll()) {
                reversalRequest.setTransactionId(field_transaction_id.getValue());
                voidCalling(reversalRequest);
            }
        }
    }


    public void refundCalling(ReversalRequest reversalRequest) {

        new PaymentRefundTask(reversalRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(RefundVoidViewActivity.this);
                SplashActivity.startProgressBar(progressDialog, "Paying refund...");
            }

            @Override
            protected void onPostExecute(PaymentResponse paymentResponse) {
                if (paymentResponse.hasError()) {
                    return;
                }
                if (paymentResponse != null && paymentResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                    if (paymentResponse.getTransactionResponse() != null) {
                        openApprovedDialog(paymentResponse.getTransactionResponse(), RefundVoidViewActivity.this);
                    }
                } else {
                    SplashActivity.showErrorDialog("Transaction failed!\n" + paymentResponse.getMessage(), RefundVoidViewActivity.this);
                }
                SplashActivity.dismissProgressBar(progressDialog);
            }
        }.execute();

    }


    public void voidCalling(ReversalRequest reversalRequest) {

        new PaymentVoidTask(reversalRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(RefundVoidViewActivity.this);
                SplashActivity.startProgressBar(progressDialog, "Paying void...");
            }

            @Override
            protected void onPostExecute(PaymentResponse paymentResponse) {
                if (paymentResponse.hasError()) {
                    return;
                }
                Log.d("reversalRequest", "" + paymentResponse.getTransactionResponse());
                if (paymentResponse != null && paymentResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                    if (paymentResponse.getTransactionResponse() != null) {
                        openApprovedDialog(paymentResponse.getTransactionResponse(), RefundVoidViewActivity.this);
                    }
                } else {
                    SplashActivity.showErrorDialog("Transaction failed!\n" + paymentResponse.getMessage(), RefundVoidViewActivity.this);

                }

                SplashActivity.dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
