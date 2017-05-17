package com.worldpayment.demoapp.activities.refundvoid;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.worldpay.library.domain.ExtendedInformation;
import com.worldpay.library.enums.MailOrTelephoneOrder;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.enums.ReversalType;
import com.worldpay.library.enums.StatusType;
import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.enums.VoidType;
import com.worldpay.library.utils.WPLogger;
import com.worldpay.library.views.WPCurrencyTextWatcher;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPSimpleFormSpinner;
import com.worldpay.library.webservices.services.payments.CaptureRequest;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.tasks.PaymentCaptureTask;
import com.worldpay.library.webservices.tasks.PaymentRefundTask;
import com.worldpay.library.webservices.tasks.PaymentVoidTask;
import com.worldpay.ui.TransactionDialogFragment;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.worldpayment.demoapp.Navigation.swiper;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.openApprovedDialog;

public class RefundVoidViewActivity extends WorldBaseActivity implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {

    private WPFormEditText field_transaction_id, field_transaction_amount, gratitude_amount;
    private LinearLayout gratuity_layout;
    private Button btn_start_transaction;
    private TextView amount_textView;
    public int count;
    WPForm validateRefund, validateVoid, validateCapture;
    private Spinner spn_transaction_types;

    private LinearLayout master_extended;
    private WPFormEditText order_date, purchase_order_no, duty_amount, freight_amount, retail_lane_no, tax_amount;//, notes;
    private WPCurrencyTextWatcher transactionAmountTextWatcher;
    private ImageView extended_image, levelTwoData_image, mail_telephone_order_image, terminal_data_image; //All Extended Data

    //Date with current time
    private int year, month, day;

    //All Extended Data
    private LinearLayout extended_layout, extended_info_LL;
    private WPFormEditText type_of_goods;
    //Level two data
    private LinearLayout levelTwoData_layout, levelTwoData_LL;
    //mail_telephone_order_layout
    private WPSimpleFormSpinner mailOrTelephoneType;
    private LinearLayout mail_telephone_order_layout, mail_telephone_order_LL, installments_LL;
    private WPFormEditText total_installments, current_installment;
    //terminal_data_layout
    private LinearLayout terminal_data_layout, terminalData_LL;
    private WPFormEditText terminal_id, terminal_city, terminal_state, terminal_location, store_number, device_serial_number, pos_terminal_input_capability;
    private Spinner tax_status;
    private StatusType statusType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivity(RefundVoidViewActivity.this);
        setContentView(R.layout.activity_refund_void_view);
        initComponents();
        count = 0;
    }

    public void initComponents() {

        master_extended = (LinearLayout) findViewById(R.id.master_extended);
        //All Extended Data
        extended_layout = (LinearLayout) findViewById(R.id.extended_layout);
        extended_layout.setOnClickListener(this);
        extended_info_LL = (LinearLayout) findViewById(R.id.extended_info_LL);
        extended_image = (ImageView) findViewById(R.id.extended_image);

        type_of_goods = (WPFormEditText) findViewById(R.id.type_of_goods);

        //Level Two Data
        levelTwoData_layout = (LinearLayout) findViewById(R.id.levelTwoData_layout);
        levelTwoData_layout.setOnClickListener(this);
        levelTwoData_LL = (LinearLayout) findViewById(R.id.levelTwoData_LL);
        levelTwoData_image = (ImageView) findViewById(R.id.levelTwoData_image);

        //Mail or Phone Order
        mail_telephone_order_layout = (LinearLayout) findViewById(R.id.mail_telephone_order_layout);
        mail_telephone_order_layout.setOnClickListener(this);
        mail_telephone_order_LL = (LinearLayout) findViewById(R.id.mail_telephone_order_LL);
        mail_telephone_order_image = (ImageView) findViewById(R.id.mail_telephone_order_image);

        mailOrTelephoneType = (WPSimpleFormSpinner) findViewById(R.id.mailOrTelephoneType);
        mailOrTelephoneType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, MailOrTelephoneOrder.values()));
        installments_LL = (LinearLayout) findViewById(R.id.installments_LL);
        mailOrTelephoneType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((MailOrTelephoneOrder) parent.getItemAtPosition(position) == MailOrTelephoneOrder.SINGLE_PURCHASE) {
                    installments_LL.setVisibility(View.GONE);
                } else {
                    installments_LL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        total_installments = (WPFormEditText) findViewById(R.id.total_installments);
        current_installment = (WPFormEditText) findViewById(R.id.current_installment);

        //Terminal Data
        terminal_data_layout = (LinearLayout) findViewById(R.id.terminal_data_layout);
        terminal_data_layout.setOnClickListener(this);
        terminalData_LL = (LinearLayout) findViewById(R.id.terminalData_LL);
        terminal_data_image = (ImageView) findViewById(R.id.terminal_data_image);

        order_date = (WPFormEditText) findViewById(R.id.order_date);
        order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time currDate = new Time(Time.getCurrentTimezone());
                currDate.setToNow();
                DatePickerDialog datePickerDialog = new DatePickerDialog(RefundVoidViewActivity.this, datePickerListener,
                        currDate.year, currDate.month, currDate.monthDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        tax_status = (Spinner) findViewById(R.id.tax_status);
        tax_status.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, StatusType.values()));

        tax_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusType = (StatusType) tax_status.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        duty_amount = (WPFormEditText) findViewById(R.id.duty_amount);
        duty_amount.addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(duty_amount, Locale.US, new BigDecimal("999999.99"), true, true);
        duty_amount.addTextChangedListener(transactionAmountTextWatcher);

        freight_amount = (WPFormEditText) findViewById(R.id.freight_amount);
        freight_amount.addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(freight_amount, Locale.US, new BigDecimal("999999.99"), true, true);
        freight_amount.addTextChangedListener(transactionAmountTextWatcher);

        retail_lane_no = (WPFormEditText) findViewById(R.id.retail_lane_no);
        tax_amount = (WPFormEditText) findViewById(R.id.tax_amount);
        tax_amount.addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(tax_amount, Locale.US, new BigDecimal("999999.99"), true, true);
        tax_amount.addTextChangedListener(transactionAmountTextWatcher);
        purchase_order_no = (WPFormEditText) findViewById(R.id.purchase_order_no);

        //terminal data
        terminal_id = (WPFormEditText) findViewById(R.id.terminal_id);
        terminal_city = (WPFormEditText) findViewById(R.id.terminal_city);
        terminal_state = (WPFormEditText) findViewById(R.id.terminal_state);
        terminal_location = (WPFormEditText) findViewById(R.id.terminal_location);
        store_number = (WPFormEditText) findViewById(R.id.store_number);
        device_serial_number = (WPFormEditText) findViewById(R.id.device_serial_number);
        pos_terminal_input_capability = (WPFormEditText) findViewById(R.id.pos_terminal_input_capability);

        amount_textView = (TextView) findViewById(R.id.amount_textView);
        field_transaction_id = (WPFormEditText) findViewById(R.id.field_transaction_id);
        field_transaction_amount = (WPFormEditText) findViewById(R.id.field_transaction_amount);
        gratitude_amount = (WPFormEditText) findViewById(R.id.gratitude_amount);
        gratuity_layout = (LinearLayout) findViewById(R.id.gratuity_layout);

        validateRefund = new WPForm();
        validateVoid = new WPForm();
        validateCapture = new WPForm();

        field_transaction_id.addValidator(new WPNotEmptyValidator("Transaction Id is required!"));
        validateRefund.addItem(field_transaction_id);
        validateVoid.addItem(field_transaction_id);
        validateCapture.addItem(field_transaction_id);

        field_transaction_amount.addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        WPCurrencyTextWatcher transactionAmountTextWatcher =
                new WPCurrencyTextWatcher(field_transaction_amount, Locale.US, new BigDecimal("999999.99"), true, true);
        field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validateRefund.addItem(field_transaction_amount);
        validateVoid.addItem(field_transaction_amount);
        validateCapture.addItem(field_transaction_amount);


        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);

        //Transaction Type Spinner
        final List<String> types = new ArrayList<String>();
        types.add("REFUND");
        types.add("VOID");
        types.add("CAPTURE");
        spn_transaction_types = (Spinner) findViewById(R.id.spn_transaction_types);
        spn_transaction_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String transactionType = parent.getItemAtPosition(position).toString();
                if (transactionType.equals("REFUND")) {
                    KeyboardUtility.closeKeyboard(RefundVoidViewActivity.this, view);
                    gratuity_layout.setVisibility(View.GONE);
                    master_extended.setVisibility(View.GONE);
                    count = 0;
                } else if (transactionType.equals("VOID")) {
                    KeyboardUtility.closeKeyboard(RefundVoidViewActivity.this, view);
                    gratuity_layout.setVisibility(View.GONE);
                    master_extended.setVisibility(View.GONE);
                    count = 1;
                } else if (transactionType.equals("CAPTURE")) {
                    KeyboardUtility.closeKeyboard(RefundVoidViewActivity.this, view);
                    gratuity_layout.setVisibility(View.VISIBLE);
                    master_extended.setVisibility(View.VISIBLE);
                    count = 2;
                }

                Log.d("transactionType", "" + transactionType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spn_transaction_types.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                types));
    }

    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {

        if (paymentResponse != null) {
            WPLogger.d(CreditDebitActivity.TAG,
                    "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                            paymentResponse);
        }
        switch (result) {
            case APPROVED:
                //   openApprovedDialog();
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
        WPLogger.d(CreditDebitActivity.TAG, "onTransactionError :: error=" + error + ";message=" + message);
    }

    @Override
    public void onTransactionReversalFailed(ReversalRequest reversalRequest) {
        WPLogger.d(CreditDebitActivity.TAG, "onTransactionReversalFailed :: reversalType=" + reversalRequest.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_start_transaction:
                KeyboardUtility.closeKeyboard(this, view);
                showTransactionFragment();
                break;

            case R.id.extended_layout:
                KeyboardUtility.closeKeyboard(this, view);
                visibleInvisible(extended_info_LL, (ImageView) findViewById(R.id.extended_image));
                break;

            case R.id.terminal_data_layout:
                KeyboardUtility.closeKeyboard(this, view);
                visibleInvisible(terminalData_LL, (ImageView) findViewById(R.id.terminal_data_image));
                break;

            case R.id.levelTwoData_layout:
                KeyboardUtility.closeKeyboard(this, view);
                visibleInvisible(levelTwoData_LL, (ImageView) findViewById(R.id.levelTwoData_image));
                break;

            case R.id.mail_telephone_order_layout:
                KeyboardUtility.closeKeyboard(this, view);
                visibleInvisible(mail_telephone_order_LL, (ImageView) findViewById(R.id.mail_telephone_order_image));
                break;

        }
    }

    private void showTransactionFragment() {

        if (count == 0) {
            ReversalRequest reversalRequest = new ReversalRequest();
            TokenUtility.populateRequestHeaderFields(reversalRequest, this);

            if (validateRefund.validateAll()) {

                reversalRequest.setTransactionId(field_transaction_id.getValue());

                if (!TextUtils.isEmpty(field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));

                    if (!transactionAmount.toString().equals("0.00")) {
                        reversalRequest.setAmount(transactionAmount);
                        reversalRequest.setReversalType(ReversalType.REFUND);
                        refundCalling(reversalRequest);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.greaterThanZero), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        } else if (count == 1) {
            ReversalRequest reversalRequest = new ReversalRequest();
            TokenUtility.populateRequestHeaderFields(reversalRequest, this);

            if (validateVoid.validateAll()) {
                if (field_transaction_amount.getValue() != null && !field_transaction_amount.getValue().equals("")) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    if (!transactionAmount.toString().equals("0.00")) {
                        reversalRequest.setAmount(transactionAmount);
                    }
                }
                reversalRequest.setTransactionId(field_transaction_id.getValue());
                reversalRequest.setReversalType(ReversalType.VOID);
                reversalRequest.setVoidType(VoidType.VoidTypeMerchant);
                voidCalling(reversalRequest);
            }
        } else if (count == 2) {

            CaptureRequest captureRequest = new CaptureRequest();
            TokenUtility.populateRequestHeaderFields(captureRequest, this);
            ExtendedInformation extendedInformation = new ExtendedInformation();

            extendedInformation.setTypeOfGoods("" + type_of_goods.getValue());
            ExtendedInformation.MailOrTelephoneOrderData mailOrTelephoneOrderData = extendedInformation.new MailOrTelephoneOrderData();
            mailOrTelephoneOrderData.setType("" + mailOrTelephoneType.getValue());
            mailOrTelephoneOrderData.setTotalNumberOfInstallments("" + total_installments.getValue());
            mailOrTelephoneOrderData.setCurrentInstallment("" + current_installment.getValue());
            extendedInformation.setMailOrTelephoneOrderData(mailOrTelephoneOrderData);

            ExtendedInformation.ServiceData serviceData = extendedInformation.new ServiceData();
            if (!TextUtils.isEmpty(gratitude_amount.getValue())) {
                BigDecimal gratitudeAmount = new BigDecimal(gratitude_amount.getValue().replaceAll("[^\\d.]", ""));
                serviceData.setGratuityAmount(gratitudeAmount);
                extendedInformation.setServiceData(serviceData);
            } else {
                BigDecimal gratitudeAmount = BigDecimal.ZERO;
                serviceData.setGratuityAmount(gratitudeAmount);
                extendedInformation.setServiceData(serviceData);
            }

            ExtendedInformation.LevelTwoData levelTwoData = extendedInformation.new LevelTwoData();
            if (order_date.getValue() != null && !order_date.getValue().equals("")) {
                if (order_date.getValue().contains("/")) {

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    Date date = cal.getTime();
                    levelTwoData.setOrderDate(
                            new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.US)
                                    .format(date));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.error_invalid_date_entry), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            levelTwoData.setPurchaseOrder("" + purchase_order_no.getValue());
            if (!TextUtils.isEmpty(retail_lane_no.getValue()) && retail_lane_no.getValue().length() > 0)
                levelTwoData.setRetailLaneNumber(Integer.parseInt(retail_lane_no.getValue()));
            else
                levelTwoData.setRetailLaneNumber(0);
            if (!TextUtils.isEmpty(duty_amount.getValue())) {
                BigDecimal dutyAmount = new BigDecimal(duty_amount.getValue().replaceAll("[^\\d.]", ""));
                levelTwoData.setDutyAmount(dutyAmount);
            } else {
                BigDecimal dutyAmount = BigDecimal.ZERO;
                levelTwoData.setDutyAmount(dutyAmount);
            }

            if (!TextUtils.isEmpty(freight_amount.getValue())) {
                BigDecimal freightAmount = new BigDecimal(freight_amount.getValue().replaceAll("[^\\d.]", ""));
                levelTwoData.setFreightAmount(freightAmount);
            } else {
                BigDecimal freightAmount = BigDecimal.ZERO;
                levelTwoData.setFreightAmount(freightAmount);
            }

            if (!TextUtils.isEmpty(tax_amount.getValue())) {
                BigDecimal taxAmount = new BigDecimal(tax_amount.getValue().replaceAll("[^\\d.]", ""));
                levelTwoData.setTaxAmount(taxAmount);
            } else {
                BigDecimal taxAmount = BigDecimal.ZERO;
                levelTwoData.setTaxAmount(taxAmount);
            }
            if (statusType == null)
                levelTwoData.setStatus(StatusType.NOT_INCLUDED);
            else
                levelTwoData.setStatus(statusType);

            extendedInformation.setLevelTwoData(levelTwoData);

            //TerminalData inner class
            ExtendedInformation.TerminalData terminalData = extendedInformation.new TerminalData();
            terminalData.setPOSTerminalInputCapabilityInd(pos_terminal_input_capability.getValue());
            terminalData.setLocation(terminal_location.getValue());
            terminalData.setCity(terminal_city.getValue());
            terminalData.setStoreNumber(store_number.getValue());
            terminalData.setState(terminal_state.getValue());
            terminalData.setTerminalId(terminal_id.getValue());

            if (terminalData.getTerminalId() == null) {
                terminalData.setTerminalId(swiper.getTerminalId());
            }
            extendedInformation.setTerminalData(terminalData);
            if (validateCapture.validateAll()) {
                if (field_transaction_amount.getValue() != null && !field_transaction_amount.getValue().equals("")) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    if (!transactionAmount.toString().equals("0.00")) {
                        captureRequest.setAmount(transactionAmount);
                    }
                }
                captureRequest.setTransactionId(field_transaction_id.getValue());
                captureRequest.setExtendedData(extendedInformation);
                Log.d("captureRequest", captureRequest.toJson());
                new PaymentCaptureTask(captureRequest) {
                    ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(RefundVoidViewActivity.this);
                        startProgressBar(progressDialog, "Capturing...");
                    }

                    @Override
                    protected void onPostExecute(PaymentResponse paymentResponse) {
                        if (paymentResponse.hasError()) {
                            dismissProgressBar(progressDialog);
                            return;
                        }
                        if (paymentResponse != null) {
                            if (paymentResponse.getResponseCode() == ResponseCode.APPROVED) {
                                showDialogView(getResources().getString(R.string.success),
                                        "Transaction Id = " + paymentResponse.getTransactionResponse().getId() + " is captured",
                                        RefundVoidViewActivity.this);
                            } else if (paymentResponse.getResponseCode() == ResponseCode.ERROR) {
                                showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                            }
                        } else {
                            showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + " Service Error!", RefundVoidViewActivity.this);
                        }
                        dismissProgressBar(progressDialog);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                startProgressBar(progressDialog, "Paying refund...");
            }

            @Override
            protected void onPostExecute(PaymentResponse paymentResponse) {
                if (paymentResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (paymentResponse != null) {
                    if (paymentResponse.getResponseCode() == ResponseCode.APPROVED) {
                        openApprovedDialog("APPROVED", paymentResponse, RefundVoidViewActivity.this);
                    } else if (paymentResponse.getResponseCode() == ResponseCode.ERROR) {
                        showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                    } else if (paymentResponse.getResponseCode() == ResponseCode.DECLINED) {
                        showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + "Service Error!", RefundVoidViewActivity.this);
                }
                dismissProgressBar(progressDialog);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void voidCalling(ReversalRequest reversalRequest) {

        new PaymentVoidTask(reversalRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(RefundVoidViewActivity.this);
                startProgressBar(progressDialog, "Paying void...");
            }

            @Override
            protected void onPostExecute(PaymentResponse paymentResponse) {
                if (paymentResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }
                if (paymentResponse != null) {
                    if (paymentResponse.getResponseCode() == ResponseCode.APPROVED) {
                        openApprovedDialog("APPROVED", paymentResponse, RefundVoidViewActivity.this);
                    } else if (paymentResponse.getResponseCode() == ResponseCode.ERROR) {
                        showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + " Service Error!", RefundVoidViewActivity.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;
                    order_date.setText(new StringBuilder().append(selectedMonth + 1)
                            .append("/").append(selectedDay).append("/").append(selectedYear)
                            .append(" "));
                }
            };
}