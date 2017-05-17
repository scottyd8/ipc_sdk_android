package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.worldpay.library.domain.Card;
import com.worldpay.library.domain.Check;
import com.worldpay.library.enums.CardSourceType;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPCreditCardHelper;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPFormValidator;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPSimpleFormSpinner;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.paymentmethods.CreatePaymentMethodRequest;
import com.worldpay.library.webservices.services.paymentmethods.PaymentMethodResponse;
import com.worldpay.library.webservices.tasks.PaymentMethodCreateTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatePaymentMethod extends WorldBaseActivity implements View.OnClickListener {

    private Button btn_create;
    private RadioGroup radioPaymentType;
    private RadioButton radioButton;
    private LinearLayout card_layout, check_layout;
    int radio;
    private WPForm cardValidation, checkValidation;
    private WPFormEditText customer_id;
    private WPFormEditText payment_id;
    private WPFormEditText card_first_name, check_first_name;
    private WPFormEditText card_last_name, check_last_name, check_type;
    private WPFormEditText card_number, check_number, routing_number, account_number;
    private WPFormEditText card_cvv;
    private WPFormEditText card_expiry_month;
    private WPFormEditText card_expiry_year;
    private WPFormEditText pinBlock;
    private WPFormEditText card_email_address, check_email_address;
    private Spinner mSpinnerCreditCardType;
    String creditCardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_payment_account);
        setActivity(CreatePaymentMethod.this);
        mappingViews();

        if (getIntent().getExtras() != null) {
            String createResponse = getIntent().getExtras().getString("createResponse");
            Gson gson = new Gson();
            CustomerResponse customerResponse = gson.fromJson(createResponse, CustomerResponse.class);
            setResponseData(customerResponse);
        }
    }

    public void setResponseData(CustomerResponse response) {
        customer_id.setText("" + response.getCustomerId());
        customer_id.setEnabled(false);

        card_first_name.setText("" + response.getFirstName());
        card_last_name.setText("" + response.getLastName());

        card_email_address.setText("" + response.getEmail());
    }

    public void mappingViews() {

        cardValidation = new WPForm();
        checkValidation = new WPForm();
        customer_id = (WPFormEditText) findViewById(R.id.customer_id);
        customer_id.addValidator(new WPNotEmptyValidator("CustomerId is required!"));
        cardValidation.addItem(customer_id);

        payment_id = (WPFormEditText) findViewById(R.id.payment_id);
        payment_id.addValidator(new WPNotEmptyValidator("PaymentId is required!"));
      //  cardValidation.addItem(payment_id);

        card_first_name = (WPFormEditText) findViewById(R.id.card_first_name);
        card_first_name.addValidator(new WPNotEmptyValidator("First Name is required!"));
        // cardValidation.addItem(card_first_name);

        card_last_name = (WPFormEditText) findViewById(R.id.card_last_name);
        card_last_name.addValidator(new WPNotEmptyValidator("Last Name is required!"));
        // cardValidation.addItem(card_last_name);

        card_number = (WPFormEditText) findViewById(R.id.card_number);
        card_number.addValidator(new WPNotEmptyValidator("Card Number is required!"));
        card_number.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String s) {
                return WPCreditCardHelper.isNumberValid(s);
            }

            @Override
            public String getMessage() {
                return "Invalid Card Number!";
            }
        });
        cardValidation.addItem(card_number);


        check_first_name = (WPFormEditText) findViewById(R.id.check_first_name);
        check_first_name.addValidator(new WPNotEmptyValidator("First Name is required!"));
        //  checkValidation.addItem(check_first_name);

        check_last_name = (WPFormEditText) findViewById(R.id.check_last_name);
        check_last_name.addValidator(new WPNotEmptyValidator("Last Name is required!"));
        //  checkValidation.addItem(check_last_name);

        check_type = (WPFormEditText) findViewById(R.id.check_type);
        check_type.addValidator(new WPNotEmptyValidator("Check Type is required!"));
        checkValidation.addItem(check_type);

        check_number = (WPFormEditText) findViewById(R.id.check_number);
        check_number.addValidator(new WPNotEmptyValidator("Check Number is required!"));
        check_number.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String s) {
                return WPCreditCardHelper.isNumberValid(s);
            }

            @Override
            public String getMessage() {
                return "Invalid Check Number!";
            }
        });
        checkValidation.addItem(check_number);

        account_number = (WPFormEditText) findViewById(R.id.check_account_number);
        account_number.addValidator(new WPNotEmptyValidator("Account Number is required!"));
        account_number.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String s) {
                return WPCreditCardHelper.isNumberValid(s);
            }

            @Override
            public String getMessage() {
                return "Invalid Account Number!";
            }
        });
        checkValidation.addItem(account_number);

        routing_number = (WPFormEditText) findViewById(R.id.check_routing_number);
        routing_number.addValidator(new WPNotEmptyValidator("Routing Number is required!"));
        routing_number.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String s) {
                return WPCreditCardHelper.isNumberValid(s);
            }

            @Override
            public String getMessage() {
                return "Invalid Routing Number!";
            }
        });
        checkValidation.addItem(routing_number);

        card_expiry_month = (WPFormEditText) findViewById(R.id.card_month);
        card_expiry_month.addValidator(new WPNotEmptyValidator("Card expiration month is required!"));
        card_expiry_month.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String var1) {
                try {
                    int inputMonth = Integer.parseInt(var1);
                    return inputMonth > 0 && inputMonth < 13;
                } catch (Exception e) {
                    Log.e("Exception : ", "" + e);
                    return false;
                }
            }

            @Override
            public String getMessage() {
                return "Invalid expiration month!";
            }
        });
        cardValidation.addItem(card_expiry_month);

        card_expiry_year = (WPFormEditText) findViewById(R.id.card_year);
        card_expiry_year
                .addValidator(new WPNotEmptyValidator("Card expiration year is required."));
        card_expiry_year.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String var1) {
                if (var1.length() < 4) {
                    return false;
                }

                try {
                    int inputYear = Integer.parseInt(var1);
                    int currentYear = Integer.parseInt(
                            new SimpleDateFormat("yyyy", Locale.US).format(new Date()));
                    return currentYear <= inputYear;
                } catch (Exception e) {
                    Log.e("Exception : ", "" + e);
                    return false;
                }
            }

            @Override
            public String getMessage() {
                return "Invalid expiration year!";
            }
        });
        cardValidation.addItem(card_expiry_year);

        card_cvv = (WPFormEditText) findViewById(R.id.card_cvv);
        card_cvv.addValidator(new WPNotEmptyValidator("CVV is required!"));
        card_cvv.addValidator(new WPFormValidator() {
            @Override
            public boolean isValid(String s) {
                return WPCreditCardHelper.isCvnValid(s, card_number.getText().toString());
            }

            @Override
            public String getMessage() {
                return "Invalid CVV!";
            }
        });
        cardValidation.addItem(card_cvv);

        pinBlock = (WPFormEditText) findViewById(R.id.card_pinBlock);
        pinBlock.addValidator(new WPNotEmptyValidator("Pin Block is required!"));
        cardValidation.addItem(pinBlock);

        card_email_address = (WPFormEditText) findViewById(R.id.card_email_address);
        card_email_address.addValidator(new WPNotEmptyValidator("Email address is required!"));
        cardValidation.addItem(card_email_address);

        radioPaymentType = (RadioGroup) findViewById(R.id.radioPaymentType);
        radioPaymentType.setOnClickListener(this);

        card_layout = (LinearLayout) findViewById(R.id.card_layout);
        check_layout = (LinearLayout) findViewById(R.id.check_layout);

        mSpinnerCreditCardType = (WPSimpleFormSpinner) findViewById(R.id.spn_credit_types);
        List<String> cardTypes = new ArrayList<String>();
        cardTypes.add("VISA");
        cardTypes.add("DISCOVER");
        cardTypes.add("MASTER CARD");
        cardTypes.add("AMERICAN EXPRESS");
        mSpinnerCreditCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                creditCardType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpinnerCreditCardType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                cardTypes));
        mSpinnerCreditCardType.setSelection(0);

        radioPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton) group.findViewById(checkedId);
                if (null != radioButton && checkedId > -1) {
                    if (radioButton.getText().equals("Card")) {
                        card_layout.setVisibility(View.VISIBLE);
                        check_layout.setVisibility(View.GONE);
                        radio = 0;

                    } else if (radioButton.getText().equals("Check")) {
                        check_layout.setVisibility(View.VISIBLE);
                        card_layout.setVisibility(View.GONE);
                        radio = 1;

                    }
                }
            }
        });
        btn_create = (Button) findViewById(R.id.btn_create);
//        btn_cancel = (Button) findViewById(btn_cancel);

        btn_create.setOnClickListener(this);
//        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_create:

                KeyboardUtility.closeKeyboard(this, v);
                if (cardValidation.validateAll() || checkValidation.validateAll()) {

                    if (radio == 0) {

                        CreatePaymentMethodRequest createPaymentMethodRequest = new CreatePaymentMethodRequest();
                        TokenUtility.populateRequestHeaderFields(createPaymentMethodRequest, this);
                        createPaymentMethodRequest.setCustomerId("" + customer_id.getValue());
                        createPaymentMethodRequest.setId("" + payment_id.getValue());
                        Card card = new Card();
                        card.setFallbackIndicator(false);
                        card.setIccCardSwiped(false);
                        card.setSwiperType(null);
                        card.setDebit(false);

                        card.setNumber("" + card_number.getValue());
                        card.setCvv("" + card_cvv.getValue());
                        card.setFirstName("" + card_first_name.getValue());
                        card.setLastName("" + card_last_name.getValue());
                        card.setEmail("" + card_email_address.getValue());
                        if (card_expiry_month.getValue() != null && !card_expiry_month.getValue().equals(""))
                            card.setExpirationMonth(Integer.parseInt(card_expiry_month.getValue()));
                        if (card_expiry_year.getValue() != null && !card_expiry_year.getValue().equals(""))
                            card.setExpirationYear(Integer.parseInt(card_expiry_year.getValue()));
                        card.setPinBlock("" + pinBlock.getValue());

                        card.setSourceType(CardSourceType.CREDIT_MANUAL);

//                        Address address = new Address();
//                        address.setPhone("" + card_phone_number.getValue());
//                        address.setLine1("Line 1 Test");
//                        address.setCity("Austin");
//                        address.setState("NY");
//                        address.setZip("56453");
//                        address.setCountry("US");
//                        card.setAddress(address);

                        createPaymentMethodRequest.setCard(card);
                        creatingPaymentMethod(createPaymentMethodRequest);

                    } else if (radio == 1) {
                        CreatePaymentMethodRequest createPaymentMethodRequest = new CreatePaymentMethodRequest();
                        TokenUtility.populateRequestHeaderFields(createPaymentMethodRequest, this);
                        createPaymentMethodRequest.setCustomerId("" + customer_id.getValue());
                        createPaymentMethodRequest.setId("" + payment_id.getValue());


                        Check check = new Check();
                        check.setCheckNumber("" + check_number.getValue());
                        check.setAccountNumber("" + account_number.getValue());
                        check.setFirstName("" + check_first_name.getValue());
                        check.setLastName("" + check_last_name.getValue());
                        check.setEmail("" + check_email_address.getValue());
                        check.setRoutingNumber("" + routing_number.getValue());

                        //                      Address address = new Address();
//                        address.setPhone("" + card_phone_number.getValue());
//                        address.setLine1("Line 1 Test");
//                        address.setCity("Austin");
//                        address.setState("NY");
//                        address.setZip("56453");
//                        address.setCountry("US");
                        //                      check.setAddress(address);

                        createPaymentMethodRequest.setCheck(check);
                        creatingPaymentMethod(createPaymentMethodRequest);
                    }
                }

                break;

//            case btn_cancel:
//                KeyboardUtility.closeKeyboard(this, v);
//                finish();

            default:
                break;
        }
    }


    public void creatingPaymentMethod(CreatePaymentMethodRequest createPaymentMethodRequest) {

        new PaymentMethodCreateTask(createPaymentMethodRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreatePaymentMethod.this);
                startProgressBar(progressDialog, "Creating Method...");
            }

            @Override
            protected void onPostExecute(PaymentMethodResponse paymentMethodResponse) {

                if (paymentMethodResponse != null) {
                    Log.d("Payment RESPONSE : ", "" + paymentMethodResponse.toJson());
                    if (paymentMethodResponse.getResponseCode() == ResponseCode.APPROVED) {
                        showDialogView(getResources().getString(R.string.success), paymentMethodResponse.getResponseMessage(), CreatePaymentMethod.this);
                    } else {
                        showDialogView(getResources().getString(R.string.error), paymentMethodResponse.getResponseMessage(), CreatePaymentMethod.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.nullResponse), CreatePaymentMethod.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
