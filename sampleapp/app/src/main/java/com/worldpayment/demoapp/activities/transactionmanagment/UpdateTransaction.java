package com.worldpayment.demoapp.activities.transactionmanagment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;

import com.worldpay.library.domain.LevelTwoData;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.enums.StatusType;
import com.worldpay.library.views.WPCurrencyTextWatcher;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPSimpleFormSpinner;
import com.worldpay.library.webservices.services.transactions.GetTransactionResponse;
import com.worldpay.library.webservices.services.transactions.UpdateTransactionRequest;
import com.worldpay.library.webservices.tasks.TransactionUpdateTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.math.BigDecimal;
import java.util.Locale;

public class UpdateTransaction extends WorldBaseActivity {

    private WPFormEditText fld_order_date, transaction_id, duty_amount, freight_amount, retail_lane_no, tax_amount, purchase_order_no;//, notes;
    private WPCurrencyTextWatcher transactionDutyAmountTextWatcher, transactionFreightAmountTextWatcher, transactionTaxAmountTextWatcher;
    private WPSimpleFormSpinner tax_status;
    private StatusType statusType;
    private boolean isSpinnerInitial = true;
    private WPForm validateID;
    private Button btn_update_transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);
        setActivity(this);
        mappingViews();
    }

    private void mappingViews() {

        transaction_id = (WPFormEditText) findViewById(R.id.transaction_id);
        validateID = new WPForm();
        transaction_id.addValidator(new WPNotEmptyValidator("Transaction Id is required!"));
        validateID.addItem(transaction_id);

        retail_lane_no = (WPFormEditText) findViewById(R.id.retail_lane_no);
        purchase_order_no = (WPFormEditText) findViewById(R.id.purchase_order_no);

        tax_status = (WPSimpleFormSpinner) findViewById(R.id.tax_status);
        tax_status.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                StatusType.values()));

        tax_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isSpinnerInitial) {
                    isSpinnerInitial = false;
                } else {
                    statusType = (StatusType) tax_status.getAdapter().getItem(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tax_status.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                statusType = StatusType.NOT_INCLUDED;
                return false;
            }
        });


        duty_amount = (WPFormEditText) findViewById(R.id.duty_amount);
        transactionDutyAmountTextWatcher = new WPCurrencyTextWatcher(duty_amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        duty_amount.addTextChangedListener(transactionDutyAmountTextWatcher);

        tax_amount = (WPFormEditText) findViewById(R.id.tax_amount);
        transactionTaxAmountTextWatcher = new WPCurrencyTextWatcher(tax_amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        tax_amount.addTextChangedListener(transactionTaxAmountTextWatcher);


        freight_amount = (WPFormEditText) findViewById(R.id.freight_amount);
        transactionFreightAmountTextWatcher = new WPCurrencyTextWatcher(freight_amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        freight_amount.addTextChangedListener(transactionFreightAmountTextWatcher);


        fld_order_date = (WPFormEditText) findViewById(R.id.fld_order_date);
        fld_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time currDate = new Time(Time.getCurrentTimezone());
                currDate.setToNow();

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateTransaction.this, datePickerListenerStart,
                        currDate.year, currDate.month, currDate.monthDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        btn_update_transaction = (Button) findViewById(R.id.btn_update_transaction);
        btn_update_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateID.validateAll()) {
                    KeyboardUtility.closeKeyboard(UpdateTransaction.this, view);
                    updateTransactions();
                }
            }
        });
    }

    private void updateTransactions() {

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        TokenUtility.populateRequestHeaderFields(request, this);
        request.setId(transaction_id.getText().toString());

        LevelTwoData data = new LevelTwoData();
        if (!TextUtils.isEmpty(duty_amount.getValue())) {
            BigDecimal dutyAmount = new BigDecimal(duty_amount.getValue().replaceAll("[^\\d.]", ""));
            data.setDutyAmount(dutyAmount);
        }

        if (!TextUtils.isEmpty(freight_amount.getValue())) {
            BigDecimal freightAmount = new BigDecimal(freight_amount.getValue().replaceAll("[^\\d.]", ""));
            data.setFreightAmount(freightAmount);
        }

        if (!TextUtils.isEmpty(tax_amount.getValue())) {
            BigDecimal taxAmount = new BigDecimal(tax_amount.getValue().replaceAll("[^\\d.]", ""));
            data.setTaxAmount(taxAmount);
        }

        if (!TextUtils.isEmpty(purchase_order_no.getValue())) {

            data.setPurchaseOrder(purchase_order_no.getValue());
        }
        if (!TextUtils.isEmpty(fld_order_date.getValue())) {
            data.setOrderDate(fld_order_date.getValue());
        }

        if (!TextUtils.isEmpty(retail_lane_no.getValue())) {
            data.setRetailLaneNumber(Integer.parseInt(retail_lane_no.getValue()));
        }

        if (statusType != null) {
            data.setStatus(statusType);
        }

        request.setLevelTwoData(data);

        new TransactionUpdateTask(request) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(UpdateTransaction.this);
                startProgressBar(progressDialog, "Updating Transaction...");
            }

            @Override
            protected void onPostExecute(GetTransactionResponse getTransactionResponse) {
                super.onPostExecute(getTransactionResponse);
                dismissProgressBar(progressDialog);
                if (getTransactionResponse != null) {

                    if (getTransactionResponse.getResponseCode() == ResponseCode.APPROVED) {
//                        TransactionResponse transactionResponse = getTransactionResponse.getTransaction();
//                        Intent retrieve = new Intent(UpdateTransaction.this, TransactionDetails.class);
//                        Gson gson = new Gson();
//                        String response = gson.toJson(transactionResponse);
//                        retrieve.putExtra("from", "adapter");
//                        retrieve.putExtra("batchResponse", response);
//                        startActivity(retrieve);

                        showDialogView(getResources().getString(R.string.success),
                                "Transaction updated", UpdateTransaction.this);

                    } else {
                        showDialogView(getResources().getString(R.string.error),
                                getTransactionResponse.getResponseMessage(), UpdateTransaction.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), "Web Service error!", UpdateTransaction.this);
                }
            }
        }.execute();
    }

    private DatePickerDialog.OnDateSetListener datePickerListenerStart =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    fld_order_date.setText(new StringBuilder().append(selectedMonth + 1)
                            .append("/").append(selectedDay).append("/").append(selectedYear)
                            .append(" "));
                }
            };

}
