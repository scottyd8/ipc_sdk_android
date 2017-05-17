package com.worldpayment.demoapp.activities.debitcredit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.worldpay.library.beans.BillAddress;
import com.worldpay.library.domain.MailOrTelephoneOrderInfo;
import com.worldpay.library.domain.TransactionData;
import com.worldpay.library.enums.CaptureMode;
import com.worldpay.library.enums.MailOrTelephoneOrder;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.enums.StatusType;
import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.enums.TransactionType;
import com.worldpay.library.utils.WPLogger;
import com.worldpay.library.views.WPCurrencyTextWatcher;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPSimpleFormSpinner;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.ui.TransactionDialogFragment;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.Navigation;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.worldpayment.demoapp.Navigation.swiper;
import static com.worldpayment.demoapp.R.string.error;

public class CreditDebitActivity extends WorldBaseActivity
        implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {

    public static String TAG = CreditDebitActivity.class.getSimpleName();

    public static String PREF_AUTH_TOKEN = "auth_token";

    private LinearLayout master_extended;
    private WPFormEditText order_date, purchase_order_no, duty_amount, freight_amount, retail_lane_no, tax_amount;//, notes;
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
    private Button btn_start_transaction;
    private Button btn_no_card, btn_card, btn_vault_pay;
    LinearLayout vault_layout, address_layout, switches;

    private WPFormEditText field_address, field_city, field_zip;
    private WPFormEditText dialog_field_transaction_amount, gratitude_amount, field_customer_id, field_payment_id;
    LinearLayout checkVaultLayout;
    LinearLayout manualonTerminalLayout;
    CheckBox addToVaultCheckBox;
    CheckBox manualonTerminalCheckBox;
    private WPCurrencyTextWatcher transactionAmountTextWatcher;
    private TransactionType transactionType;
    Switch gratitude_switch, cash_back_switch;

    //Date picker
    static final int DATE_PICKER_ID = 1111;
    int positionGlobal;

    public int count = 1;
    WPForm validating, validatingIDs, validatieAddress;
    WPSimpleFormSpinner spn_transaction_types;

    //State
    MaterialBetterSpinner spinner_states;
    String state;
    List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debit_credit);
        setActivity(CreditDebitActivity.this);
        initComponents();

        if (categories.size() == 3)
            categories.add("Verify");
        dynamicTransactionType(categories);
        suitableForRequest(spn_transaction_types.getItemAtPosition(positionGlobal).toString());

    }


    public String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("response.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void initComponents() {

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreditDebitActivity.this, datePickerListener,
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

        spn_transaction_types = (WPSimpleFormSpinner) findViewById(R.id.spn_transaction_types);

        count = 1;
        validating = new WPForm();
        validatingIDs = new WPForm();
        validatieAddress = new WPForm();

        field_address = (WPFormEditText) findViewById(R.id.field_address);
        field_address.addValidator(new WPNotEmptyValidator("Line1 is required!"));
        validatieAddress.addItem(field_address);

        field_city = (WPFormEditText) findViewById(R.id.field_city);
        field_city.addValidator(new WPNotEmptyValidator("City is required!"));
        validatieAddress.addItem(field_city);

        // States Spinner
        HashMap<String, String> getStatesValues = new TokenUtility().getStates();

        TreeMap<String, String> sortedMapState = new TreeMap<String, String>();
        for (Map.Entry entry : getStatesValues.entrySet()) {
            sortedMapState.put((String) entry.getKey(), (String) entry.getValue());
        }
        final String[] keysState = new String[sortedMapState.size()];
        String[] valuesState = new String[sortedMapState.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : sortedMapState.entrySet()) {
            keysState[i] = entry.getKey();
            valuesState[i] = entry.getValue();
            i++;
        }

        spinner_states = (MaterialBetterSpinner) findViewById(R.id.spinner_states);
        spinner_states.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                valuesState));

        // Select State  Spinner
        spinner_states.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state = keysState[position];
            }
        });


        field_zip = (WPFormEditText) findViewById(R.id.field_zip);
        field_zip.addValidator(new WPNotEmptyValidator("Zip is required!"));
        validatieAddress.addItem(field_zip);

        gratitude_switch = (Switch) findViewById(R.id.gratitude_switch);
        cash_back_switch = (Switch) findViewById(R.id.cash_back_switch);

        gratitude_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (cash_back_switch.isChecked())
                    cash_back_switch.setChecked(false);

            }
        });


        cash_back_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (gratitude_switch.isChecked())
                    gratitude_switch.setChecked(false);

            }
        });

        address_layout = (LinearLayout) findViewById(R.id.address_layout);
        switches = (LinearLayout) findViewById(R.id.switches);

        dialog_field_transaction_amount = (WPFormEditText) findViewById(R.id.dialog_field_transaction_amount);
        dialog_field_transaction_amount.addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(dialog_field_transaction_amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        dialog_field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validating.addItem(dialog_field_transaction_amount);
        validatingIDs.addItem(dialog_field_transaction_amount);
        //   addressValidate.addItem(dialog_field_transaction_amount);

        gratitude_amount = (WPFormEditText) findViewById(R.id.gratitude_amount);
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(gratitude_amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        gratitude_amount.addTextChangedListener(transactionAmountTextWatcher);

        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        field_customer_id.addValidator(new WPNotEmptyValidator("Customer Id required!"));
        validatingIDs.addItem(field_customer_id);

        field_payment_id = (WPFormEditText) findViewById(R.id.field_payment_id);
        field_payment_id.addValidator(new WPNotEmptyValidator("Payment Id required!"));
        validatingIDs.addItem(field_payment_id);


        btn_no_card = (Button) findViewById(R.id.btn_no_card);
        btn_no_card.setOnClickListener(this);

        btn_card = (Button) findViewById(R.id.btn_card);
        btn_card.setOnClickListener(this);


        btn_vault_pay = (Button) findViewById(R.id.btn_vault_pay);
        btn_vault_pay.setOnClickListener(this);

        vault_layout = (LinearLayout) findViewById(R.id.vault_layout);

        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);

        checkVaultLayout = (LinearLayout) findViewById(R.id.checkVaultLayout);
        checkVaultLayout.setOnClickListener(this);
        addToVaultCheckBox = (CheckBox) findViewById(R.id.addToVaultCheckBox);

        manualonTerminalLayout = (LinearLayout) findViewById(R.id.manualonTerminalLayout);
        manualonTerminalLayout.setOnClickListener(this);
        manualonTerminalCheckBox = (CheckBox) findViewById(R.id.manualonTerminalCheckBox);


        //Transaction Type Spinner
        categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.authorized));
        categories.add(getResources().getString(R.string.charge));
        categories.add(getResources().getString(R.string.credit));
        categories.add(getResources().getString(R.string.verify));


        dynamicTransactionType(categories);

    }

    public void suitableForRequest(String type) {

        switch (type) {

            case "Verify":

                transactionType = TransactionType.VERIFY;
                invisible(gratitude_amount);
                invisible(switches);
                if (count == 1) {
                    visible(address_layout);
                } else {
                    invisible(address_layout);
                }
                break;

            case "Charge":
                transactionType = TransactionType.CHARGE;
                if (count == 0) {
                    visible(gratitude_amount);
//                    visible(cash_back_amount);
                } else {
                    invisible(gratitude_amount);
                }
                if (count == 1) {
                    visible(switches);
                } else {
                    invisible(switches);
                }
                invisible(address_layout);
                break;

            case "Authorize":
                transactionType = TransactionType.AUTHORIZE;

                invisible(gratitude_amount);
                invisible(address_layout);

                if (count == 1) {
                    visible(switches);
                } else {
                    invisible(switches);
                }
                break;

            case "Credit":
                transactionType = TransactionType.CREDIT;

                invisible(gratitude_amount);
                invisible(address_layout);
                if (count == 1) {
                    visible(switches);
                } else {
                    invisible(switches);
                }
                break;
        }
    }

    public void invisible(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    public void visible(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_start_transaction:
                KeyboardUtility.closeKeyboard(this, view);
                if (count == 0) {
                    manualTransaction();
                } else {
                    showTransactionFragment();
                }
                break;

            case R.id.btn_no_card:
                KeyboardUtility.closeKeyboard(this, view);
                count = 0;
                vault_layout.setVisibility(View.GONE);
                buttonEnabled(btn_no_card, btn_card);
                buttonEnabled(btn_no_card, btn_vault_pay);
                gratitude_amount.setVisibility(View.GONE);
                switches.setVisibility(View.GONE);
                manualonTerminalLayout.setVisibility(View.VISIBLE);
                if (categories.size() == 3)
                    categories.add(getResources().getString(R.string.verify));
                dynamicTransactionType(categories);
                suitableForRequest(spn_transaction_types.getItemAtPosition(positionGlobal).toString());
                break;

            case R.id.btn_card:
                KeyboardUtility.closeKeyboard(this, view);
                count = 1;
                vault_layout.setVisibility(View.GONE);
                buttonEnabled(btn_card, btn_no_card);
                buttonEnabled(btn_card, btn_vault_pay);
                switches.setVisibility(View.VISIBLE);
                manualonTerminalLayout.setVisibility(View.GONE);
                if (categories.size() == 3)
                    categories.add(getResources().getString(R.string.verify));
                dynamicTransactionType(categories);
                suitableForRequest(spn_transaction_types.getItemAtPosition(positionGlobal).toString());


                break;


            case R.id.btn_vault_pay:
                KeyboardUtility.closeKeyboard(this, view);
                count = 2;
                vault_layout.setVisibility(View.VISIBLE);
                buttonEnabled(btn_vault_pay, btn_no_card);
                buttonEnabled(btn_vault_pay, btn_card);
                switches.setVisibility(View.GONE);
                manualonTerminalLayout.setVisibility(View.GONE);
                if (categories.size() == 4)
                    categories.remove(3);
                dynamicTransactionType(categories);
                suitableForRequest(spn_transaction_types.getItemAtPosition(1).toString());
                break;


            case R.id.checkVaultLayout:
                KeyboardUtility.closeKeyboard(this, view);
                if (addToVaultCheckBox.isChecked()) {
                    addToVaultCheckBox.setChecked(false);
                } else {
                    addToVaultCheckBox.setChecked(true);
                }
                break;

            case R.id.manualonTerminalLayout:
                KeyboardUtility.closeKeyboard(this, view);
                if (manualonTerminalCheckBox.isChecked()) {
                    manualonTerminalCheckBox.setChecked(false);
                } else {
                    manualonTerminalCheckBox.setChecked(true);
                }
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

            default:
                break;
        }
    }


    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {

        if (paymentResponse != null) {
            Log.d("onTransactionComplete",
                    "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                            paymentResponse.toJson());

            switch (result) {
                case APPROVED:
                    if (paymentResponse != null && paymentResponse.getTransactionResponse() != null) {
                        if (paymentResponse.getResponseCode() == ResponseCode.APPROVED) {
                            if (paymentResponse.getTransactionResponse() != null &&
                                    (count == 1 || count == 0) &&
                                    paymentResponse.getTransactionResponse().getPaymentTypeResult().equals("CREDIT_CARD") &&
                                    paymentResponse.isSign()) {
                                openSignatureDialog(this, paymentResponse);
                            } else {
                                openApprovedDialog(paymentResponse.getResult(), paymentResponse, this);
                            }
                        } else {
                            openApprovedDialog(paymentResponse.getResult(), paymentResponse, this);
                        }
                    }
                    break;
                case AMOUNT_REJECTED:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case CANCELED:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case NOT_EMV:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case EMV_CARD_REMOVED:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case CARD_NOT_SUPPORTED:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case READER_ERROR:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case AUTHENTICATION_FAILURE:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case UNKNOWN_ERROR:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case DECLINED_CALL_ISSUER:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case DECLINED_PIN_ERROR:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case DECLINED_WITH_REFUND:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case REVERSAL_FAILED:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case DECLINED_REVERSAL_FAILED:
                    showDialogView(getResources().getString(error), paymentResponse.getResponseMessage(), CreditDebitActivity.this);
                    break;
                case DECLINED:
                    if (paymentResponse != null && paymentResponse.getResponseCode() != null) {
                        openApprovedDialog(result.toString(), paymentResponse, this);
                    }
                    break;

                case NO_SWIPER:
                    showDialogView(getResources().getString(error), getResources().getString(R.string.no_device), CreditDebitActivity.this);
                    break;
            }
        } else {
            Toast.makeText(this, "CONNECTION FAILED!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTransactionError(@NonNull TransactionDialogFragment.TransactionError transactionError,
                                   @Nullable String message) {

        WPLogger.d(TAG, "onTransactionError :: error=" + transactionError + ";message=" + message);
        switch (transactionError) {
            case AUTH_FAILURE:
                showDialogView(getResources().getString(error), "" + message, CreditDebitActivity.this);
                break;

            case NO_DEVICE_CONNECTED:
                showDialogView(getResources().getString(error), "" + message, CreditDebitActivity.this);
                break;

        }
    }

    @Override
    public void onTransactionReversalFailed(ReversalRequest reversalRequest) {
        WPLogger.d(TAG, "onTransactionReversalFailed :: reversalType=" + reversalRequest.toString());
    }


    //APPROVED POP UP
    public static void openApprovedDialog(String messageStr, final PaymentResponse response, final Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.approved_layout, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(view);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView transaction_id = (TextView) view.findViewById(R.id.transaction_id);
        LinearLayout transaction_layout = (LinearLayout) view.findViewById(R.id.transaction_layout);

        final Button dialog_btn_negative = (Button) view.findViewById(R.id.dialog_btn_negative);
        final Button dialog_btn_positive = (Button) view.findViewById(R.id.dialog_btn_positive);

        dialog_btn_negative.setText("" + context.getResources().getString(R.string.details));
        dialog_btn_positive.setText("" + context.getResources().getString(R.string.done));

        title.setText("" + messageStr);
        if (messageStr.equals("APPROVED")) {
            title.setTextColor(Color.parseColor("#007867"));
            transaction_id.setText("" + response.getTransactionResponse().getId());
            message.setText("" + response.getResponseMessage());
        } else if (messageStr.equals("DECLINED")) {
            title.setTextColor(Color.parseColor("#f11e15"));
            transaction_layout.setVisibility(View.GONE);
            message.setText("" + response.getResponseMessage());
            dialog_btn_negative.setVisibility(View.GONE);
            dialog_btn_positive.setText("OK");
        } else if (messageStr.equals("ERROR")) {
            title.setTextColor(Color.parseColor("#f11e15"));
            transaction_layout.setVisibility(View.GONE);
            message.setText("" + response.getResponseMessage());
            dialog_btn_negative.setVisibility(View.GONE);
            dialog_btn_positive.setText("OK");
        }

        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transactionDetails = new Intent(context, TransactionDetails.class);
                transactionDetails.putExtra("from", "approved");
                Gson gson = new Gson();
                String transactionResponse = gson.toJson(response.getTransactionResponse(), TransactionResponse.class);
                transactionDetails.putExtra("approvedResponse", transactionResponse);
                context.startActivity(transactionDetails);
            }
        });

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog_btn_positive.getText().toString().equals("OK")) {
                    alert.dismiss();
                } else {
                    alert.dismiss();
                    Intent navigation = new Intent(context, Navigation.class);
                    context.startActivity(navigation);
                }
            }
        });
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        final File directory = new File(Environment.getExternalStorageDirectory() + "/WorldPay/");
        Log.d("Directory1", directory.toString());
        if (!directory.exists()) {
            directory.mkdirs();
            Log.d("Directory2", directory.toString());
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("mm-dd-yyyy_HH:MM").format(new Date());
        File mediaFile;
        String imageName = timeStamp + ".png";
        mediaFile = new File(directory.getPath() + File.separator + imageName);
        return mediaFile;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    //SIGNATURE POP UP
    public void openSignatureDialog(final Context context, final PaymentResponse paymentResponse) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.activity_take_signature, null);


        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(view);

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        final Button dialog_details = (Button) view.findViewById(R.id.dialog_details);
        final Button dialog_done = (Button) view.findViewById(R.id.dialog_done);
        final Button dialog_clear = (Button) view.findViewById(R.id.dialog_clear);


        final Signature signatureClass = new Signature(this, null, dialog_details, dialog_done);
        signatureClass.setBackgroundColor(Color.WHITE);
        linearLayout.addView(signatureClass, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView transaction_id = (TextView) view.findViewById(R.id.transaction_id);

        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        if (paymentResponse != null && paymentResponse.getTransactionResponse() != null) {
            transaction_id.setText("" + paymentResponse.getTransactionResponse().getId());
        }

        //Clear Signs
        dialog_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureClass.clear();
                dialog_details.setClickable(false);
                dialog_details.setEnabled(false);
                dialog_details.setTextColor(getResources().getColor(R.color.gray));
                dialog_done.setClickable(false);
                dialog_done.setEnabled(false);
                dialog_done.setTextColor(getResources().getColor(R.color.gray));
            }
        });
        //Done , Go to hell
        dialog_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.setDrawingCacheEnabled(true);
                signatureClass.save(linearLayout);
                alert.dismiss();
                Intent navigation = new Intent(context, Navigation.class);
                context.startActivity(navigation);
            }
        });
        //Details , Welcome
        dialog_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.setDrawingCacheEnabled(true);
                signatureClass.save(linearLayout);
                alert.dismiss();

                Intent transactionDetails = new Intent(context, TransactionDetails.class);
                transactionDetails.putExtra("from", "approved");
                Gson gson = new Gson();
                String transactionResponse = gson.toJson(paymentResponse.getTransactionResponse(), TransactionResponse.class);
                transactionDetails.putExtra("approvedResponse", transactionResponse);
                context.startActivity(transactionDetails);
            }
        });
    }


    public class Signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
        private Button dialog_details, dialog_done;
        private Context context;

        public Signature(Context context, AttributeSet attrs, Button dialog_details, Button dialog_done) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);

            this.dialog_details = dialog_details;
            this.dialog_done = dialog_done;

            this.context = context;
        }

        public void save(LinearLayout linearLayout) {
            Bitmap bitmap = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.RGB_565);
            storeImage(bitmap);
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            KeyboardUtility.closeKeyboard(getApplicationContext(), dialog_done);
            KeyboardUtility.closeKeyboard(getApplicationContext(), dialog_details);

            float eventX = event.getX();
            float eventY = event.getY();

            dialog_done.setClickable(true);
            dialog_done.setEnabled(true);
            dialog_done.setTextColor(getResources().getColor(R.color.worldPay_green));

            dialog_details.setClickable(true);
            dialog_details.setEnabled(true);
            dialog_details.setTextColor(getResources().getColor(R.color.worldPay_green));

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    Log.d("Ignored touch event: ", "" + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }

    //Manual Transaction
    private void manualTransaction() {

        if (count == 0) {

            TransactionDialogFragment transactionDialogFragment = TransactionDialogFragment.newInstance();
            if (transactionDialogFragment.isVisible()) return;
            TransactionData transactionData = new TransactionData();
            setTransactionData(transactionData);

            if (validating.validateAll()) {
                if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    if (!transactionAmount.toString().equals("0.00")) {
                        transactionData.setAmount(transactionAmount);
                    } else {
                        Toast.makeText(this,
                                getResources().getString(R.string.greaterThanZero),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                fragmentSetGet(transactionData, transactionDialogFragment);
            }
        }
    }

    public void setTransactionData(TransactionData transactionData) {

        transactionData.setTypeOfGoods("" + type_of_goods.getValue());

        MailOrTelephoneOrderInfo mailOrTelephoneOrderInfo = new MailOrTelephoneOrderInfo();
        mailOrTelephoneOrderInfo.setType("" + mailOrTelephoneType.getValue());
        mailOrTelephoneOrderInfo.setTotalNumberOfInstallments("" + total_installments.getValue());
        mailOrTelephoneOrderInfo.setCurrentInstallment("" + current_installment.getValue());

        transactionData.setMailOrTelephoneOrderInfo(mailOrTelephoneOrderInfo);
        if (switches.getVisibility() == View.VISIBLE) {
            transactionData.setCashBackSwitch(!gratitude_switch.isChecked());
            transactionData.setGratitudeSwitch(gratitude_switch.isChecked());
        }
        if (order_date.getValue() != null && !order_date.getValue().equals("")) {
            if (order_date.getValue().contains("/")) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                Date date = cal.getTime();
                transactionData.setOrderDate(date);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_invalid_date_entry), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        transactionData.setPurchaseOrder("" + purchase_order_no.getValue());
        if (!TextUtils.isEmpty(retail_lane_no.getValue()) && retail_lane_no.getValue().length() > 0)
            transactionData.setLaneNumber(Integer.parseInt(retail_lane_no.getValue()));
        else
            transactionData.setLaneNumber(0);
        if (!TextUtils.isEmpty(duty_amount.getValue())) {
            BigDecimal dutyAmount = new BigDecimal(duty_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setDutyAmount(dutyAmount);
        } else {
            BigDecimal dutyAmount = BigDecimal.ZERO;
            transactionData.setDutyAmount(dutyAmount);
        }

        if (!TextUtils.isEmpty(freight_amount.getValue())) {
            BigDecimal freightAmount = new BigDecimal(freight_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setFreightAmount(freightAmount);
        } else {
            BigDecimal freightAmount = BigDecimal.ZERO;
            transactionData.setFreightAmount(freightAmount);
        }

        if (!TextUtils.isEmpty(tax_amount.getValue())) {
            BigDecimal taxAmount = new BigDecimal(tax_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setTaxAmount(taxAmount);
        } else {
            BigDecimal taxAmount = BigDecimal.ZERO;
            transactionData.setTaxAmount(taxAmount);
        }
        if (statusType == null)
            transactionData.setTaxStatus(StatusType.NOT_INCLUDED);
        else
            transactionData.setTaxStatus(statusType);
        if (!TextUtils.isEmpty(gratitude_amount.getValue())) {
            BigDecimal gratitudeAmount = new BigDecimal(
                    gratitude_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setGratuityAmount(gratitudeAmount);
        } else {
            transactionData.setGratuityAmount(BigDecimal.ZERO);
        }

        transactionData.setAddCardToVault(addToVaultCheckBox.isChecked());

        if (order_date.getValue() != null && !order_date.getValue().equals("")) {
            Date orderDate = new Date(order_date.getValue());
            transactionData.setOrderDate(orderDate);
        }

        transactionData.setPurchaseOrder("" + purchase_order_no.getValue());
        if (retail_lane_no.getValue() != null && retail_lane_no.getValue().length() > 1) {
            transactionData.setRetailLaneNumber(Integer.parseInt(retail_lane_no.getValue()));
        }

        if (!TextUtils.isEmpty(duty_amount.getValue())) {
            BigDecimal dutyAmount = new BigDecimal(duty_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setDutyAmount(dutyAmount);
        } else {
            BigDecimal dutyAmount = BigDecimal.ZERO;
            transactionData.setDutyAmount(dutyAmount);
        }

        if (!TextUtils.isEmpty(freight_amount.getValue())) {
            BigDecimal freightAmount = new BigDecimal(freight_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setFreightAmount(freightAmount);
        } else {
            BigDecimal freightAmount = BigDecimal.ZERO;
            transactionData.setFreightAmount(freightAmount);
        }

        if (!TextUtils.isEmpty(tax_amount.getValue())) {
            BigDecimal taxAmount = new BigDecimal(tax_amount.getValue().replaceAll("[^\\d.]", ""));
            transactionData.setTaxAmount(taxAmount);
        } else {
            BigDecimal taxAmount = BigDecimal.ZERO;
            transactionData.setTaxAmount(taxAmount);
        }
    }

    //Start Transaction
    private void showTransactionFragment() {

        TransactionDialogFragment transactionDialogFragment = TransactionDialogFragment.newInstance();
        if (transactionDialogFragment.isVisible()) return;

        TransactionData transactionData = new TransactionData();
        setTransactionData(transactionData);

        if (count == 1) {

            transactionData.setCashBackSwitch(!(gratitude_switch.isChecked()));
            transactionData.setGratitudeSwitch(gratitude_switch.isChecked());
            transactionData.setAddCardToVault(addToVaultCheckBox.isChecked());

            if (address_layout.getVisibility() == View.VISIBLE) {
                if (validatieAddress.validateAll()) {

                    if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                        BigDecimal transactionAmount = new BigDecimal(dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                        transactionData.setAmount(transactionAmount);
                    } else {
                        BigDecimal transactionAmount = BigDecimal.ZERO;
                        transactionData.setAmount(transactionAmount);
                    }

                    BillAddress billAddress = new BillAddress();
                    billAddress.setLine1("" + field_address.getValue());
                    billAddress.setCity("" + field_city.getValue());
                    billAddress.setState(field_city.getValue());
                    billAddress.setState("" + state);
                    billAddress.setZip("" + field_zip.getValue());
                    transactionData.setAddress(billAddress);
                    fragmentSetGet(transactionData, transactionDialogFragment);
                }
            } else {
                if (validating.validateAll()) {
                    if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                        BigDecimal transactionAmount = new BigDecimal(dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                        transactionData.setAmount(transactionAmount);
                    }
                    fragmentSetGet(transactionData, transactionDialogFragment);
                }
            }
        } else if (count == 2) {

            if (validatingIDs.validateAll()) {
                if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    transactionData.setAmount(transactionAmount);
                }
                if (!TextUtils.isEmpty(field_customer_id.getValue())) {
                    transactionData.setCustomerId(field_customer_id.getValue());
                } else {
                    Toast.makeText(this, "Invalid Customer Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(field_payment_id.getValue())) {
                    transactionData.setPaymentID(field_payment_id.getValue());
                } else {
                    Toast.makeText(this, "Invalid Payment Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                fragmentSetGet(transactionData, transactionDialogFragment);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, pickerListener, year, day, month);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            // Show selected date
            order_date.setText(new StringBuilder().append(month + 1)
                    .append("/").append(day).append("/").append(year)
                    .append(" "));
        }
    };

    public void dynamicTransactionType(List<String> categories) {

        spn_transaction_types.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                categories));
        spn_transaction_types.setSelection(1);
        spn_transaction_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("spn_transaction_types", "" + parent.getItemAtPosition(position).toString());
                suitableForRequest(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void fragmentSetGet(TransactionData transactionData, TransactionDialogFragment transactionDialogFragment) {
        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
        transactionDialogFragment.setTransactionData(transactionData);
        transactionDialogFragment.setApplicationVersion(BuildConfig.VERSION_NAME);
        transactionDialogFragment.setTransactionType(transactionType);
        transactionDialogFragment.setSwiper(swiper);
        transactionDialogFragment.setMerchantId(BuildConfig.MERCHANT_ID);
        transactionDialogFragment.setMerchantKey(BuildConfig.MERCHANT_KEY);
        transactionDialogFragment.setAuthToken(authToken);
        transactionDialogFragment.setDeveloperId(BuildConfig.DEVELOPER_ID);
        if (count == 0) {
            if(manualonTerminalCheckBox.isChecked())
                transactionDialogFragment.setCaptureMode(CaptureMode.MANUAL_TERMINAL);
            else
                transactionDialogFragment.setCaptureMode(CaptureMode.MANUAL);

        } else {
            transactionDialogFragment.setCaptureMode(CaptureMode.SWIPE);
        }
        transactionDialogFragment.show(getSupportFragmentManager(), TransactionDialogFragment.TAG);
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