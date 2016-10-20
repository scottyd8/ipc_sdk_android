package com.worldpayment.demoapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.worldpayment.demoapp.activities.RefundVoidViewActivity;
import com.worldpayment.demoapp.activities.TransactionDetails;
import com.worldpay.library.domain.TransactionData;
import com.worldpay.library.enums.CaptureMode;
import com.worldpay.library.enums.Swiper;
import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.enums.TransactionType;
import com.worldpay.library.utils.iM3Logger;
import com.worldpay.library.views.iM3CurrencyTextWatcher;
import com.worldpay.library.views.iM3Form;
import com.worldpay.library.views.iM3FormEditText;
import com.worldpay.library.views.iM3NotEmptyValidator;
import com.worldpay.library.webservices.services.authtokens.AuthTokenCreateRequest;
import com.worldpay.library.webservices.services.authtokens.AuthTokenCreateResponse;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.library.webservices.tasks.CreateAuthTokenTask;
import com.worldpay.ui.TransactionDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.RefundVoidViewActivity.count;

public class DebitCreditActivity extends AppCompatActivity
        implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {

    public static String TAG = DebitCreditActivity.class.getSimpleName();

    public static String PREF_AUTH_TOKEN = "auth_token";
    public static String PREF_SWIPER_DESC = "swiper_desc";

    public static String BUNDLE_TRANSACTION_TYPE = "trans_type";
    public static String BUNDLE_SWIPER = "swiper";
    public static String BUNDLE_AUTH_TOKEN = "auth_token";

    public static TransactionResponse responseTransactionDetails;

    private Button btn_start_transaction;
    private Button btn_no_card, btn_card;
    private Spinner spn_swiper_types;
    private Spinner spn_transaction_types;

    private iM3FormEditText dialog_field_transaction_amount;
    private iM3FormEditText wp_tx_dialog_field_cash_back_amount;

    private iM3CurrencyTextWatcher transactionAmountTextWatcher;


    private String authToken;
    private Swiper swiper;
    private TransactionType transactionType;

    private TransactionDialogFragment transactionDialogFragment;
    private ProgressDialog progressDialog;
    private int spnSwiperTypesPos;
    Toolbar toolbar;

    Spinner wp_tx_dialog_spinner_transaction;

    private Bitmap bitmap;
    LinearLayout linearLayout;
    public static File Directory;
    View view;
    File file;
    private EditText field_name;
    Button dialog_clear, dialog_save;

    iM3Form validating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
        Log.d("authToken", ""+authToken);

        setToolbar();
        initComponents();

        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            transactionType = (TransactionType) savedInstanceState.get(BUNDLE_TRANSACTION_TYPE);
            authToken = (String) savedInstanceState.get(BUNDLE_AUTH_TOKEN);
            swiper = (Swiper) savedInstanceState.get(BUNDLE_SWIPER);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_TRANSACTION_TYPE, transactionType);
        outState.putSerializable(BUNDLE_SWIPER, swiper);
        outState.putString(BUNDLE_AUTH_TOKEN, authToken);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_start_transaction:
                KeyboardUtility.closeKeyboard(this, view);
                if (count == 0) {
                    manualTransaction();
                } else if (count == 1) {
                    showTransactionFragment();
                }
                break;

            case R.id.btn_no_card:
                KeyboardUtility.closeKeyboard(this, view);
                count = 0;
                RefundVoidViewActivity.buttonEnabled(btn_no_card, btn_card, count);

                break;

            case R.id.btn_card:
                KeyboardUtility.closeKeyboard(this, view);
                count = 1;
                RefundVoidViewActivity.buttonEnabled(btn_card, btn_no_card, count);

                break;

            default:
                break;
        }
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Debit/Credit");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }


    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {
        Log.d("onTransactionComplete",
                "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                        paymentResponse);
        switch (result) {
            case APPROVED:
                if (paymentResponse.getTransactionResponse() != null) {
                    openApprovedDialog(paymentResponse.getTransactionResponse(), this);
                }
                break;
            case AMOUNT_REJECTED:
                SplashActivity.showErrorDialog("Transaction failed!\n" + result, DebitCreditActivity.this);
                break;
            case CANCELED:
                SplashActivity.showErrorDialog("Transaction failed!\n" + result, DebitCreditActivity.this);
                break;
            case NOT_EMV:
                break;
            case EMV_CARD_REMOVED:
                break;
            case CARD_NOT_SUPPORTED:
                break;
            case READER_ERROR:
                SplashActivity.showErrorDialog("Transaction failed!\n" + result, DebitCreditActivity.this);
                break;
            case AUTHENTICATION_FAILURE:
                SplashActivity.showErrorDialog("Transaction failed!\n" + result, DebitCreditActivity.this);
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
                SplashActivity.showErrorDialog("Transaction failed!\n" + result, DebitCreditActivity.this);
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

    //APPROVED POP UP
    public static void openApprovedDialog(final TransactionResponse response, final Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogSignature = layoutInflater.inflate(R.layout.dialog_layout, null);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);
        alertDialogBuilder.setView(dialogSignature);
        responseTransactionDetails = response;
        Button dialog_detail = (Button) dialogSignature.findViewById(R.id.dialog_detail);
        dialog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transactionDetails = new Intent(context, TransactionDetails.class);
                context.startActivity(transactionDetails);
            }
        });

        Button dialog_done = (Button) dialogSignature.findViewById(R.id.dialog_done);
        dialog_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigation = new Intent(context, Navigation.class);
                context.startActivity(navigation);
            }
        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //SIGNATURE POP UP
    public void openSignatureDialog() {

        final Dialog dialog = new Dialog(DebitCreditActivity.this); // Context, this, etc.
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
                Toast.makeText(DebitCreditActivity.this, "Signature is saved successfully.", Toast.LENGTH_LONG).show();
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

    private void initComponents() {

        count = 1;
        validating = new iM3Form();

        dialog_field_transaction_amount = (iM3FormEditText) findViewById(R.id.dialog_field_transaction_amount);

        dialog_field_transaction_amount
                .addValidator(new iM3NotEmptyValidator("Transaction amount required!"));
        transactionAmountTextWatcher =
                new iM3CurrencyTextWatcher(dialog_field_transaction_amount, Locale.US,
                        new BigDecimal("999999.99"), true, true);
        dialog_field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validating.addItem(dialog_field_transaction_amount);

        wp_tx_dialog_field_cash_back_amount = (iM3FormEditText) findViewById(R.id.wp_tx_dialog_field_cash_back_amount);

        btn_no_card = (Button) findViewById(R.id.btn_no_card);
        btn_no_card.setOnClickListener(this);

        btn_card = (Button) findViewById(R.id.btn_card);
        btn_card.setOnClickListener(this);

        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);

        //Swiper Type Spinner
        spn_swiper_types = (Spinner) findViewById(R.id.spn_swiper_types);
        spn_swiper_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                swiper = Swiper.fromDescription((String) spn_swiper_types.getAdapter().getItem(position));
                authenticate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                swiper = null;
            }
        });

        spn_swiper_types.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                com.worldpay.library.BuildConfig.COMPATIBLE_SWIPERS));
        spnSwiperTypesPos = spn_swiper_types.getSelectedItemPosition();

        //Transaction Type Spinner
        spn_transaction_types = (Spinner) findViewById(R.id.spn_transaction_types);
        List<Enum> transType = new ArrayList<Enum>();
        transType.add(TransactionType.AUTH);
        transType.add(TransactionType.SALE);
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
                transType));
        spn_transaction_types.setEnabled(false);


        //Dialog Type Spinner
        wp_tx_dialog_spinner_transaction = (Spinner) findViewById(R.id.wp_tx_dialog_spinner_transaction);
        List<String> categories = new ArrayList<String>();
        categories.add("CHARGE");
        categories.add("AUTHORIZE");
        categories.add("CREDIT");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        wp_tx_dialog_spinner_transaction.setAdapter(dataAdapter);
    }

    private void updateComponents() {
        boolean enable = true;

        dialog_field_transaction_amount.setEnabled(enable);
        wp_tx_dialog_field_cash_back_amount.setEnabled(enable);
        spn_transaction_types.setEnabled(enable);
        btn_start_transaction.setEnabled(enable);
    }

    //Manual Transaction
    private void manualTransaction() {

        if (count == 0) {

            authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

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
                        Log.d("transactionAmount", "" + transactionAmount);
                    } else {
                        Toast.makeText(this, "Amount should be greater than zero!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //   transactionData.setAddCardToVault(addToVaultCheckBox.isChecked());
                transactionDialogFragment.setTransactionData(transactionData);
                transactionDialogFragment.setApplicationVersion(BuildConfig.VERSION_NAME);
                transactionDialogFragment.setTransactionType(transactionType);
                transactionDialogFragment.setSwiper(swiper);

                transactionDialogFragment.setMerchantId(MERCHANT_ID);
                transactionDialogFragment.setMerchantKey(MERCHANT_KEY);
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

        iM3Form validates = new iM3Form();

        TransactionData transactionData = new TransactionData();

        if (validates.validateAll()) {
            if (!TextUtils.isEmpty(dialog_field_transaction_amount.getValue())) {
                transactionAmount = new BigDecimal(
                        dialog_field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
            }
        }

        transactionData.setAmount(transactionAmount);
        transactionData.setCashBackAmount(cashBackAmount);
        transactionData.setId("115029855");

        transactionDialogFragment.setTransactionData(transactionData);
        transactionDialogFragment.setCaptureMode(CaptureMode.SWIPE_TAP_INSERT);
        transactionDialogFragment.setTransactionType(transactionType);
        transactionDialogFragment.setSwiper(swiper);

        transactionDialogFragment.setMerchantId(MERCHANT_ID);
        transactionDialogFragment.setMerchantKey(MERCHANT_KEY);
        transactionDialogFragment.setAuthToken(authToken);
        transactionDialogFragment.setDeveloperId(BuildConfig.DEVELOPER_ID);
        transactionDialogFragment.setApplicationVersion(BuildConfig.VERSION_NAME);
        transactionDialogFragment.show(getSupportFragmentManager(), TransactionDialogFragment.TAG);
    }

    private void authenticate() {
        authToken = null;
        updateComponents();
        authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        if (!TextUtils.isEmpty(authToken)) {
            String savedSwiperDesc =
                    PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_SWIPER_DESC, null);

            if (swiper.getDescription().equals(savedSwiperDesc)) {
                updateComponents();
                return;
            }
        }

        AuthTokenCreateRequest authTokenRequestDto = new AuthTokenCreateRequest();
        authTokenRequestDto.setApplicationId(getApplication().getApplicationInfo().packageName);
        authTokenRequestDto.setApplicationModel(Build.MANUFACTURER);
        authTokenRequestDto.setApplicationOs("Android " + Build.VERSION.SDK_INT);
        authTokenRequestDto.setApplicationVersion(BuildConfig.VERSION_NAME);
        authTokenRequestDto.setDeveloperId(BuildConfig.DEVELOPER_ID);
        authTokenRequestDto.setTerminalId(swiper.getTerminalId());
        authTokenRequestDto.setTerminalVendor(swiper.getVendorId());

        switch (BuildConfig.MERCHANT_GATEWAY) {

            case NONE:
                break;

            case SECURENET:
                authTokenRequestDto.setSecureNetId(MERCHANT_ID);
                authTokenRequestDto.setSecureNetKey(MERCHANT_KEY);
                break;

            case MERCHANT_PARTNERS:
                authTokenRequestDto.setMerchantPartnersId(MERCHANT_ID);
                authTokenRequestDto.setMerchantPartnersPin(MERCHANT_KEY);
                break;
        }

        new CreateAuthTokenTask(authTokenRequestDto) {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(DebitCreditActivity.this,
                        getResources().getString(R.string.loading_title),
                        getResources().getString(R.string.loading_data), true);
            }

            @Override
            protected void onPostExecute(AuthTokenCreateResponse authTokenCreateResponse) {
                if (authTokenCreateResponse.hasError()) {
                    updateComponents();
                    closeProgressDialog();
                    Toast.makeText(DebitCreditActivity.this, R.string.error_no_token,
                            Toast.LENGTH_LONG).show();
                    spn_swiper_types.setSelection(spnSwiperTypesPos);
                    return;
                }
                authToken = authTokenCreateResponse.getAuthToken();
                PreferenceManager.getDefaultSharedPreferences(DebitCreditActivity.this).edit()
                        .putString(PREF_AUTH_TOKEN, authToken).apply();
                PreferenceManager.getDefaultSharedPreferences(DebitCreditActivity.this).edit()
                        .putString(PREF_SWIPER_DESC, swiper.getDescription()).apply();
                updateComponents();
                closeProgressDialog();
                spnSwiperTypesPos = spn_swiper_types.getSelectedItemPosition();
            }
        }.execute();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
