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
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.worldpay.library.domain.TransactionData;
import com.worldpay.library.enums.CaptureMode;
import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.enums.TransactionType;
import com.worldpay.library.utils.WPLogger;
import com.worldpay.library.views.WPCurrencyTextWatcher;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPTextView;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.ui.TransactionDialogFragment;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.Navigation;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.activities.refundvoid.RefundVoidViewActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import static com.worldpayment.demoapp.Navigation.swiper;
import static com.worldpayment.demoapp.activities.refundvoid.RefundVoidViewActivity.count;

public class CreditDebitActivity extends WorldBaseActivity
        implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {

    public static String TAG = CreditDebitActivity.class.getSimpleName();

    public static String PREF_AUTH_TOKEN = "auth_token";

    public static TransactionResponse responseTransactionDetails;

    private Button btn_start_transaction;
    private Button btn_no_card, btn_card, btn_vault_pay;
    LinearLayout vault_layout, address_layout, extended_layout, user_define_layout;
    LinearLayout extended_info_LL;
    private Spinner spn_transaction_types;

    private WPFormEditText dialog_field_transaction_amount, field_customer_id, field_payment_id;
    private WPFormEditText order_date, purchase_order_no, notes;
    LinearLayout checkVaultLayout;
    CheckBox addToVaultCheckBox;
    private WPCurrencyTextWatcher transactionAmountTextWatcher;
    private String authToken;
    private TransactionType transactionType;

    WPTextView tv_extended_info;
    //Date picker
    static final int DATE_PICKER_ID = 1111;
    private int year;
    private int month;
    private int day;

    private TransactionDialogFragment transactionDialogFragment;

    private Bitmap bitmap;
    LinearLayout linearLayout;
    public static File Directory;
    View view;
    File file;
    private EditText field_name;
    Button dialog_clear, dialog_save;

    WPForm validating, validatingIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debit_credit);
        setActivity(CreditDebitActivity.this);
        authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
        initComponents();

    }

    private void initComponents() {

        count = 1;
        validating = new WPForm();
        validatingIDs = new WPForm();

        dialog_field_transaction_amount = (WPFormEditText) findViewById(R.id.dialog_field_transaction_amount);
        dialog_field_transaction_amount.addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        transactionAmountTextWatcher = new WPCurrencyTextWatcher(dialog_field_transaction_amount, Locale.US,
                new BigDecimal("999999.99"), true, true);
        dialog_field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validating.addItem(dialog_field_transaction_amount);
        validatingIDs.addItem(dialog_field_transaction_amount);

        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        field_customer_id.addValidator(new WPNotEmptyValidator("Customer ID required!"));
        validatingIDs.addItem(field_customer_id);

        field_payment_id = (WPFormEditText) findViewById(R.id.field_payment_id);
        field_payment_id.addValidator(new WPNotEmptyValidator("Payment ID required!"));
        validatingIDs.addItem(field_payment_id);

        order_date = (WPFormEditText) findViewById(R.id.order_date);
        order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        tv_extended_info = (WPTextView) findViewById(R.id.tv_extended_info);

        purchase_order_no = (WPFormEditText) findViewById(R.id.purchase_order_no);
        notes = (WPFormEditText) findViewById(R.id.notes);

        btn_no_card = (Button) findViewById(R.id.btn_no_card);
        btn_no_card.setOnClickListener(this);

        btn_card = (Button) findViewById(R.id.btn_card);
        btn_card.setOnClickListener(this);

        btn_vault_pay = (Button) findViewById(R.id.btn_vault_pay);
        btn_vault_pay.setOnClickListener(this);

        vault_layout = (LinearLayout) findViewById(R.id.vault_layout);

        extended_layout = (LinearLayout) findViewById(R.id.extended_layout);
        extended_layout.setOnClickListener(this);
        extended_info_LL = (LinearLayout) findViewById(R.id.extended_info_LL);

        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);


        checkVaultLayout = (LinearLayout) findViewById(R.id.checkVaultLayout);
        checkVaultLayout.setOnClickListener(this);
        addToVaultCheckBox = (CheckBox) findViewById(R.id.addToVaultCheckBox);
        //Swiper Type Spinner
        //spn_swiper_types = (Spinner) findViewById(spn_swiper_types);
//        spn_swiper_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////              Swiper swiper = Swiper.fromDescription("Shuttle");
////
////                                PreferenceManager.getDefaultSharedPreferences(CreditDebitActivity.this).edit()
////                        .putString(PREF_BUNDLE_SWIPER, swiper).apply();
//
//                //  authenticate();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

//        spn_swiper_types.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
//                com.worldpay.library.BuildConfig.COMPATIBLE_SWIPERS));
        //  spnSwiperTypesPos = spn_swiper_types.getSelectedItemPosition();

        //Transaction Type Spinner
        spn_transaction_types = (Spinner) findViewById(R.id.spn_transaction_types);
        spn_transaction_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactionType = (TransactionType) spn_transaction_types.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                transactionType = null;
            }
        });
        spn_transaction_types.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                TransactionType.values()));
        spn_transaction_types.setSelection(1);

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
                RefundVoidViewActivity.buttonEnabled(btn_no_card, btn_card, count);
                RefundVoidViewActivity.buttonEnabled(btn_no_card, btn_vault_pay, count);

                break;

            case R.id.btn_card:
                KeyboardUtility.closeKeyboard(this, view);
                count = 1;
                vault_layout.setVisibility(View.GONE);
                RefundVoidViewActivity.buttonEnabled(btn_card, btn_no_card, count);
                RefundVoidViewActivity.buttonEnabled(btn_card, btn_vault_pay, count);

                break;

            case R.id.btn_vault_pay:
                KeyboardUtility.closeKeyboard(this, view);
                count = 2;
                vault_layout.setVisibility(View.VISIBLE);
                RefundVoidViewActivity.buttonEnabled(btn_vault_pay, btn_no_card, count);
                RefundVoidViewActivity.buttonEnabled(btn_vault_pay, btn_card, count);
                break;

            case R.id.checkVaultLayout:
                KeyboardUtility.closeKeyboard(this, view);
                if (addToVaultCheckBox.isChecked()) {
                    addToVaultCheckBox.setChecked(false);
                } else {
                    addToVaultCheckBox.setChecked(true);
                }
                break;

            case R.id.extended_layout:
                KeyboardUtility.closeKeyboard(this, view);
                visibleInvisible(extended_info_LL, (ImageView) findViewById(R.id.extended_image));
                break;
            default:
                break;
        }
    }

    public void visibleInvisible(LinearLayout linearLayout, ImageView imageView) {
        if (linearLayout.getVisibility() == View.GONE) {
            linearLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_minus));
        } else {
            linearLayout.setVisibility(View.GONE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));
        }
    }

    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {

        if (paymentResponse != null) {
            Log.d("onTransactionComplete",
                    "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                            paymentResponse.toJson());
            Log.d("getHttpStatusCode() =  ", "" + paymentResponse.getHttpStatusCode());
            Log.d("getMessage() =  ", "" + paymentResponse.getMessage());
        }
        switch (result) {
            case APPROVED:
                if (paymentResponse != null && paymentResponse.getTransactionResponse() != null) {
                    if (addToVaultCheckBox.isChecked())
                        openApprovedDialog(result.toString(), paymentResponse.getTransactionResponse(), this);
                    else
                        openApprovedDialog(result.toString(), paymentResponse.getTransactionResponse(), this);
                }
                break;
            case AMOUNT_REJECTED:
                showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + result, CreditDebitActivity.this);
                break;
            case CANCELED:
                showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + result, CreditDebitActivity.this);
                break;
            case NOT_EMV:
                break;
            case EMV_CARD_REMOVED:
                break;
            case CARD_NOT_SUPPORTED:
                break;
            case READER_ERROR:
                showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + result, CreditDebitActivity.this);
                break;
            case AUTHENTICATION_FAILURE:
                showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + result, CreditDebitActivity.this);
                break;
            case UNKNOWN_ERROR:
                break;
            case DECLINED_CALL_ISSUER:
                break;
            case DECLINED_PIN_ERROR:
                break;
            case DECLINED_WITH_REFUND:
                break;
            case REVERSAL_FAILED:
                break;
            case DECLINED_REVERSAL_FAILED:
                break;
            case DECLINED:
                Log.d("Inside DECLINDEDC ", "DECLINED");
                if (paymentResponse != null && paymentResponse.getTransactionResponse() != null) {
                    Log.d("Inside DECLINDEDC ", ""+paymentResponse.toJson());

                    if (addToVaultCheckBox.isChecked())
                        openApprovedDialog(result.toString(), paymentResponse.getTransactionResponse(), this);
                    else
                        openApprovedDialog(result.toString(), paymentResponse.getTransactionResponse(), this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onTransactionError(@NonNull TransactionDialogFragment.TransactionError error,
                                   @Nullable String message) {
        WPLogger.d(TAG, "onTransactionError :: error=" + error + ";message=" + message);
    }

    @Override
    public void onTransactionReversalFailed(ReversalRequest reversalRequest) {
        WPLogger.d(TAG, "onTransactionReversalFailed :: reversalType=" + reversalRequest.toString());
    }


    //APPROVED POP UP
    public static void openApprovedDialog(String messageStr, final TransactionResponse response, final Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.approved_layout, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        TextView message = (TextView) dialogSignature.findViewById(R.id.message);
        TextView transaction_id = (TextView) dialogSignature.findViewById(R.id.transaction_id);
        LinearLayout transaction_layout = (LinearLayout) dialogSignature.findViewById(R.id.transaction_layout);

        final Button dialog_btn_negative = (Button) dialogSignature.findViewById(R.id.dialog_btn_negative);
        final Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);

        dialog_btn_negative.setText("" + context.getResources().getString(R.string.details));
        dialog_btn_positive.setText("" + context.getResources().getString(R.string.done));

        title.setTextColor(Color.parseColor("#007867"));
        title.setText("" + messageStr);
        if (messageStr.equals("APPROVED")) {
            transaction_id.setText("" + response.getId());
            message.setText("" + response.getResponseText());
        } else if (messageStr.equals("DECLINED")) {
            transaction_layout.setVisibility(View.GONE);
            message.setText("" + response.getResponseText());
            dialog_btn_negative.setVisibility(View.GONE);
            dialog_btn_positive.setText("OK");
        }
        responseTransactionDetails = response;

        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transactionDetails = new Intent(context, TransactionDetails.class);
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

    //SIGNATURE POP UP
    public void openSignatureDialog() {

        final Dialog dialog = new Dialog(CreditDebitActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.activity_take_signature);

        Directory = new File(Environment.getExternalStorageDirectory() + "/WorldPay/" + getResources().getString(R.string.external_dir) + "/");
        Log.d("Directory", Directory.toString());
        if (!Directory.exists()) {
            Directory.mkdirs();
        }
        linearLayout = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        final Signature signatureClass = new Signature(this, null);
        signatureClass.setBackgroundColor(Color.WHITE);
        linearLayout.addView(signatureClass, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view = linearLayout;
        field_name = (EditText) dialog.findViewById(R.id.field_name);
        dialog_clear = (Button) dialog.findViewById(R.id.dialog_clear);
        dialog_save = (Button) dialog.findViewById(R.id.dialog_save);

        dialog_save.setClickable(false);
        dialog_save.setEnabled(false);
        dialog_save.setTextColor(getResources().getColor(R.color.white));
        dialog_save.setBackgroundResource(R.drawable.button_disable);

        dialog_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureClass.clear();
                dialog_save.setClickable(false);
                dialog_save.setEnabled(false);
                dialog_save.setTextColor(getResources().getColor(R.color.white));
                dialog_save.setBackgroundResource(R.drawable.button_disable);
            }
        });
        dialog_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = captureSignature();
                if (!error) {
                    view.setDrawingCacheEnabled(true);
                    signatureClass.save(view);
                }
            }
        });

        dialog.show();


    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";


        if (field_name.getText().toString().equalsIgnoreCase("")) {
            errorMessage = errorMessage + "Please enter your Name\n";
            error = true;
        }

        if (error) {
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }

        return error;
    }

    public class Signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            Date d = new Date();
            CharSequence s = DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
            file = new File(Directory, s.toString() + "_" + field_name.getText().toString() + ".jpg");
            if (file.exists()) {
                file.delete();
            }
            try {
                Toast.makeText(CreditDebitActivity.this, getResources().getString(R.string.signedSuccess), Toast.LENGTH_LONG).show();
                FileOutputStream mFileOutStream = new FileOutputStream(file);

                v.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();


            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
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
            KeyboardUtility.closeKeyboard(getApplicationContext(), view);

            float eventX = event.getX();
            float eventY = event.getY();

            dialog_save.setClickable(true);
            dialog_save.setEnabled(true);
            dialog_save.setTextColor(getResources().getColor(R.color.white));
            dialog_save.setBackgroundResource(R.drawable.button_shap);

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
                    debug("Ignored touch event: " + event.toString());
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

        private void debug(String string) {
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

            if (transactionDialogFragment == null) {
                transactionDialogFragment = TransactionDialogFragment.newInstance();
            }
            if (transactionDialogFragment.isVisible()) return;
            TransactionData transactionData = new TransactionData();
            if (validating.validateAll()) {
                if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    if (!transactionAmount.toString().equals("0.00")) {
                        transactionData.setAmount(transactionAmount);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.greaterThanZero), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (addToVaultCheckBox.isChecked()) {
                    transactionData.setAddCardToVault(true);
                } else {
                    transactionData.setAddCardToVault(false);
                }

                transactionData.setTransactionNotes("" + notes.getValue());
                if (order_date.getValue() != null && !order_date.getValue().equals("")) {
                    Date orderDate = new Date(order_date.getValue());
                    transactionData.setOrderDate(orderDate);
                }
                transactionData.setPurchaseOrderNumber("" + purchase_order_no.getValue());
                transactionData.setTransactionNotes("" + notes.getValue());
                transactionDialogFragment.setTransactionData(transactionData);
                transactionDialogFragment.setApplicationVersion(BuildConfig.VERSION_NAME);
                transactionDialogFragment.setTransactionType(transactionType);
                transactionDialogFragment.setSwiper(swiper);

                transactionDialogFragment.setMerchantId(BuildConfig.MERCHANT_ID);
                transactionDialogFragment.setMerchantKey(BuildConfig.MERCHANT_KEY);
                transactionDialogFragment.setAuthToken(authToken);
                transactionDialogFragment.setDeveloperId(BuildConfig.DEVELOPER_ID);
                transactionDialogFragment.setCaptureMode(CaptureMode.MANUAL);
                transactionDialogFragment.show(getSupportFragmentManager(), TransactionDialogFragment.TAG);

            }
        }
    }

    //Start Transaction
    private void showTransactionFragment() {

        if (transactionDialogFragment == null) {
            transactionDialogFragment = TransactionDialogFragment.newInstance();
        }
        if (transactionDialogFragment.isVisible()) return;

        BigDecimal transactionAmount = BigDecimal.ZERO;
        BigDecimal cashBackAmount = BigDecimal.ZERO;

        transactionDialogFragment.setCaptureMode(CaptureMode.SWIPE_TAP_INSERT);
        transactionDialogFragment.setSwiper(swiper);

        transactionDialogFragment.setTransactionType(transactionType);


        transactionDialogFragment.setMerchantId(BuildConfig.MERCHANT_ID);
        transactionDialogFragment.setMerchantKey(BuildConfig.MERCHANT_KEY);
        transactionDialogFragment.setAuthToken(authToken);
        transactionDialogFragment.setDeveloperId(BuildConfig.DEVELOPER_ID);
        transactionDialogFragment.setApplicationVersion(BuildConfig.VERSION_NAME);

        TransactionData transactionData = new TransactionData();

        transactionData.setTransactionNotes("" + notes.getValue());
        if (order_date.getValue() != null && !order_date.getValue().equals("")) {
            Date orderDate = new Date(order_date.getValue());
            transactionData.setOrderDate(orderDate);
        }
        transactionData.setPurchaseOrderNumber("" + purchase_order_no.getValue());
        transactionData.setTransactionNotes("" + notes.getValue());

        if (count == 1) {
            if (validating.validateAll()) {
                if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                    transactionAmount = new BigDecimal(dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    transactionData.setAmount(transactionAmount);
                    transactionData.setCashBackAmount(cashBackAmount);
                    transactionData.setId("115029855");
                    if (addToVaultCheckBox.isChecked()) {
                        transactionData.setAddCardToVault(true);
                    } else {
                        transactionData.setAddCardToVault(false);
                    }
                    transactionDialogFragment.setTransactionData(transactionData);
                    transactionDialogFragment.show(getSupportFragmentManager(), TransactionDialogFragment.TAG);
                }
            }
        } else if (count == 2) {
            if (validatingIDs.validateAll()) {
                if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                    transactionAmount = new BigDecimal(
                            dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    transactionData.setAmount(transactionAmount);
                }

                if (!TextUtils.isEmpty(field_customer_id.getValue())) {
                    String customer_id = field_customer_id.getValue();
                    transactionData.setCustomerId(customer_id);
                }

                if (!TextUtils.isEmpty(field_payment_id.getValue())) {
                    String payment_id = field_payment_id.getValue();
                    transactionData.setPaymentToken(payment_id);
                }

                transactionDialogFragment.setTransactionData(transactionData);
                transactionDialogFragment.show(getSupportFragmentManager(), TransactionDialogFragment.TAG);
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
}