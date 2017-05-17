package com.worldpayment.demoapp.activities.transactionmanagment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPCurrencyTextWatcher;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.transactions.SearchTransactionsRequest;
import com.worldpay.library.webservices.services.transactions.SearchTransactionsResponse;
import com.worldpay.library.webservices.tasks.TransactionSearchTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.activities.settlement.TransactionListActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchTransaction extends WorldBaseActivity {

    private WPFormEditText fld_start_date, fld_end_date, transaction_id, amount, customer_id, order_id;
    private WPCurrencyTextWatcher transactionAmountTextWatcher;
    private Button btn_search_transactions;
    private WPForm validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_transaction);
        setActivity(this);
        mappingViews();
    }

    private void mappingViews() {

        validator = new WPForm();
        transaction_id = (WPFormEditText) findViewById(R.id.transaction_id);
        customer_id = (WPFormEditText) findViewById(R.id.customer_id);
        order_id = (WPFormEditText) findViewById(R.id.order_id);

        amount = (WPFormEditText) findViewById(R.id.amount);
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        amount.addTextChangedListener(transactionAmountTextWatcher);

        fld_start_date = (WPFormEditText) findViewById(R.id.fld_start_date);
        //fld_start_date.addValidator(new WPNotEmptyValidator("Start Date is required!"));
        validator.addItem(fld_start_date);

        fld_end_date = (WPFormEditText) findViewById(R.id.fld_end_date);
        //fld_end_date.addValidator(new WPNotEmptyValidator("End Date is required!"));
        validator.addItem(fld_end_date);

        fld_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time currDate = new Time(Time.getCurrentTimezone());
                currDate.setToNow();
                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchTransaction.this, datePickerListenerStart,
                        currDate.year, currDate.month, currDate.monthDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        fld_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time currDate = new Time(Time.getCurrentTimezone());
                currDate.setToNow();
                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchTransaction.this, datePickerListenerEnd,
                        currDate.year, currDate.month, currDate.monthDay);

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btn_search_transactions = (Button) findViewById(R.id.btn_search_transactions);
        btn_search_transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validator.validateAll()) {
                    KeyboardUtility.closeKeyboard(SearchTransaction.this, view);
                    searchTransactions();
                }
            }
        });
    }

    private void searchTransactions() {

        SearchTransactionsRequest request = new SearchTransactionsRequest();
        TokenUtility.populateRequestHeaderFields(request, this);

        request.setTransactionId(transaction_id.getText().toString().replace(" ", ""));
        request.setCustomerID(customer_id.getText().toString().replace(" ", ""));
        request.setOrderID(order_id.getText().toString().replace(" ", ""));


        if (!TextUtils.isEmpty(amount.getValue())) {
            BigDecimal amount = new BigDecimal(this.amount.getValue().replaceAll("[^\\d.]", ""));
            if(amount.compareTo(BigDecimal.ZERO) > 0)
                request.setAmount(amount);
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date startDate = simpleDateFormat.parse(fld_start_date.getText().toString());
            Date endDate = simpleDateFormat.parse(fld_end_date.getText().toString());
            request.setStartDate(startDate);
            request.setEndDate(endDate);
        } catch (ParseException e) {
            Log.e("er", " " + e);
        }


        new TransactionSearchTask(request) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(SearchTransaction.this);
                startProgressBar(progressDialog, "Searching Transactions...");
            }

            @Override
            protected void onPostExecute(SearchTransactionsResponse searchTransactionsResponse) {
                if (searchTransactionsResponse != null) {
                    if (searchTransactionsResponse.getResponseCode() == ResponseCode.APPROVED) {
                        Intent intent = new Intent(SearchTransaction.this, TransactionListActivity.class);
                        intent.putExtra("batchPutExtra", searchTransactionsResponse.toJson());
                        intent.putExtra("status", "current");
                        intent.putExtra("search", "search");
                        startActivity(intent);
                    } else {
                        showDialogView(getResources().getString(R.string.error), searchTransactionsResponse.getResponseMessage(), SearchTransaction.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.noTransaction), SearchTransaction.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }

    private DatePickerDialog.OnDateSetListener datePickerListenerStart =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    fld_start_date.setText(new StringBuilder().append(selectedMonth + 1)
                            .append("/").append(selectedDay).append("/").append(selectedYear)
                            .append(" "));
                }
            };


    private DatePickerDialog.OnDateSetListener datePickerListenerEnd =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    fld_end_date.setText(new StringBuilder().append(selectedMonth + 1)
                            .append("/").append(selectedDay).append("/").append(selectedYear)
                            .append(" "));
                }
            };

}
