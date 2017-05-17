package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.worldpay.library.beans.BillAddress;
import com.worldpay.library.domain.Customer;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.customers.CreateCustomerRequest;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.customers.UpdateCustomerRequest;
import com.worldpay.library.webservices.tasks.CustomerCreateTask;
import com.worldpay.library.webservices.tasks.CustomerUpdateTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CreateCustomer extends WorldBaseActivity implements View.OnClickListener {
    Button btn_create;
    WPFormEditText field_customer_id, field_first_name, field_last_name, field_phone_number, field_email_address, field_notes;
    WPFormEditText field_street_address, field_city, zip, field_company;
    WPFormEditText field_user_defined1, field_user_defined2, field_user_defined3, field_user_defined4;
    //    WPFormEditText spinner_state;
    private WPForm validateAlls;
    private CheckBox check_mail;

    MaterialBetterSpinner spinner_states;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);

        mappingViews();

        if (getIntent().getExtras() != null) {

            String responseFromIntent = getIntent().getExtras().getString("response");
            Gson gson = new Gson();
            CustomerResponse customerResponse = gson.fromJson(responseFromIntent, CustomerResponse.class);
            settingFields(customerResponse);
            btn_create.setText("Update");
            setActivityTitle(CreateCustomer.this, "Update");
        } else {
            setActivityTitle(CreateCustomer.this, "Create");
        }

    }

    public void mappingViews() {

        btn_create = (Button) findViewById(R.id.btn_create);
        // btn_cancel = (Button) findViewById(btn_cancel);

        btn_create.setOnClickListener(this);
        //   btn_cancel.setOnClickListener(this);

        validateAlls = new WPForm();

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

        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);

        field_first_name = (WPFormEditText) findViewById(R.id.field_first_name);
        field_first_name.addValidator(new WPNotEmptyValidator("First Name is required!"));
//        validateAlls.addItem(field_first_name);

        field_last_name = (WPFormEditText) findViewById(R.id.field_last_name);
        field_last_name.addValidator(new WPNotEmptyValidator("Last Name is required!"));
//        validateAlls.addItem(field_last_name);

        field_phone_number = (WPFormEditText) findViewById(R.id.field_phone_number);
        field_phone_number.addValidator(new WPNotEmptyValidator("Phone Number is required!"));
//        validateAlls.addItem(field_phone_number);

        field_email_address = (WPFormEditText) findViewById(R.id.field_email_address);
        field_email_address.addValidator(new WPNotEmptyValidator("Email is required!"));
        validateAlls.addItem(field_email_address);

        check_mail = (CheckBox) findViewById(R.id.check_mail);

        field_notes = (WPFormEditText) findViewById(R.id.field_notes);
//        field_notes.addValidator(new WPNotEmptyValidator("Note is required!"));
//        validateAlls.addItem(field_notes);
//

        field_street_address = (WPFormEditText) findViewById(R.id.field_street_address);
        field_street_address.addValidator(new WPNotEmptyValidator("Line1 is required!"));
        validateAlls.addItem(field_street_address);

        field_city = (WPFormEditText) findViewById(R.id.field_city);
        field_city.addValidator(new WPNotEmptyValidator("City is required!"));
        validateAlls.addItem(field_city);

        field_company = (WPFormEditText) findViewById(R.id.field_company);
        field_company.addValidator(new WPNotEmptyValidator("Company is required!"));
        //   validateAlls.addItem(field_company);


        zip = (WPFormEditText) findViewById(R.id.zip);
        // zip.addValidator(new WPNotEmptyValidator("Zip Code is invalid!"));
        //  validateAlls.addItem(zip);

        field_user_defined1 = (WPFormEditText) findViewById(R.id.field_user_defined1);
        field_user_defined2 = (WPFormEditText) findViewById(R.id.field_user_defined2);
        field_user_defined3 = (WPFormEditText) findViewById(R.id.field_user_defined3);
        field_user_defined4 = (WPFormEditText) findViewById(R.id.field_user_defined4);

//        spinner_state = (WPFormEditText) findViewById(spinner_state);
//        spinner_state.addValidator(new WPNotEmptyValidator("State is required!"));
//        validateAlls.addItem(spinner_state);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_create:
                KeyboardUtility.closeKeyboard(this, view);
                if (btn_create.getText().equals("Update")) {
                    updateCustomer();
                } else {
                    createCustomer();
                }
                break;

//            case btn_cancel:
//                KeyboardUtility.closeKeyboard(this, view);
//                finish();
//                break;

            default:
                break;
        }
    }


    public void createCustomer() {

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();

        if (validateAlls.validateAll()) {

            createCustomerRequest.setId("" + field_customer_id.getValue());
            createCustomerRequest.setFirstName("" + field_first_name.getValue());
            createCustomerRequest.setLastName("" + field_last_name.getValue());
            createCustomerRequest.setEmail("" + field_email_address.getValue());
            createCustomerRequest.setPhone("" + field_phone_number.getValue());
            createCustomerRequest.setNotes("" + field_notes.getValue());
            createCustomerRequest.setCompany("" + field_company.getValue());

            if (check_mail.isChecked()) {
                createCustomerRequest.setSendEmailReceipts(true);
            } else {
                createCustomerRequest.setSendEmailReceipts(false);
            }
            TokenUtility.populateRequestHeaderFields(createCustomerRequest, this);

            BillAddress address = new BillAddress();
            address.setCountry("US");
            address.setLine1("" + field_street_address.getValue());
            address.setCity("" + field_city.getValue());


            if (zip != null && !zip.equals("")) {
                address.setZip("" + zip.getValue());
            } else {
                address.setZip("");
            }
            address.setPhone("" + field_phone_number.getValue());

//            address.setState("" + new TokenUtility().getKeyByValue(new TokenUtility().getStates(), spinner_states.getValue()));           //  address.setCompany("" + field_company.getValue());
            address.setState("" + state);
            createCustomerRequest.setAddress(address);

            try {
                HashMap<String, String> hashMap = new HashMap<String, String>();

                if (field_user_defined1.getValue() != null) {
                    hashMap.put("UDF1", field_user_defined1.getValue());
                }
                if (field_user_defined2.getValue() != null) {
                    hashMap.put("UDF2", field_user_defined2.getValue());
                }
                if (field_user_defined3.getValue() != null) {
                    hashMap.put("UDF3", field_user_defined3.getValue());
                }
                if (field_user_defined4.getValue() != null) {
                    hashMap.put("UDF4", field_user_defined4.getValue());
                }
                createCustomerRequest.setUserDefinedFields(hashMap);
            } catch (Exception e) {
            }
            createCustomerTask(createCustomerRequest);

        }
    }

    public void createCustomerTask(CreateCustomerRequest createCustomerRequest) {

        new CustomerCreateTask(createCustomerRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreateCustomer.this);
                startProgressBar(progressDialog, "Creating...");
            }

            @Override
            protected void onPostExecute(CustomerResponse customerResponse) {

                if (customerResponse != null) {
                    if (customerResponse.getResponseCode() == ResponseCode.APPROVED) {
                        createdDialog(customerResponse.getResult(), customerResponse, CreateCustomer.this);
                    } else {
                        showDialogView(getResources().getString(R.string.error), "" + customerResponse.getResponseMessage(), CreateCustomer.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), "Web service error!", CreateCustomer.this);
                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }


    public void createdDialog(String titleStr, final CustomerResponse response, final Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.master_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        TextView message = (TextView) dialogSignature.findViewById(R.id.message);

        title.setText("" + titleStr);
        message.setText("" + response.getResponseMessage());

        Button dialog_btn_negative = (Button) dialogSignature.findViewById(R.id.dialog_btn_negative);
        final Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);

        if (titleStr.equals("APPROVED")) {
            title.setTextColor(Color.parseColor("#007867"));
            dialog_btn_negative.setText("" + getResources().getString(R.string.details));
            dialog_btn_positive.setText("" + getResources().getString(R.string.done));
        } else {
            title.setTextColor(Color.parseColor("#f11e15"));
            dialog_btn_negative.setVisibility(View.GONE);
            dialog_btn_positive.setText("OK");
        }
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent retrieve = new Intent(CreateCustomer.this, CustomerDetailsActivity.class);
                Gson gson = new Gson();
                String responseStr = gson.toJson(response);
                retrieve.putExtra("response", responseStr);
                startActivity(retrieve);
                finish();
            }
        });

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if (dialog_btn_positive.getText().equals("OK")) {
                } else {
                    Intent navigation = new Intent(CreateCustomer.this, VaultOperations.class);
                    startActivity(navigation);
                    finish();
                }
            }
        });
    }

    public void settingFields(CustomerResponse response) {

        //Customer OVERVIEW
        field_customer_id.setText("" + response.getCustomerId());
        field_first_name.setText("" + response.getFirstName());
        field_last_name.setText("" + response.getLastName());
        field_email_address.setText("" + response.getEmail());
        field_company.setText("" + response.getCompany());

        if (response.getPhone() != null) {
            field_phone_number.setText("" + response.getPhone());
        }
        field_notes.setText("" + response.getNotes());

        if (response.getAddress() != null) {
            if (response.getAddress().getLine1() != null) {
                field_street_address.setText("" + response.getAddress().getLine1());
            }
            if (response.getAddress().getCity() != null) {
                field_city.setText("" + response.getAddress().getCity());
            }

//            if (auto_state.getText() != null) {
//
//
//                if (response.getAddress().getState() != null) {
//                    original = getStatesValues.get(response.getAddress().getState());
//                    keyState = response.getAddress().getState();
//                    auto_state.setText("" + original);
//                }
//            }
            if (response.getAddress().getZip() != null) {
                zip.setText("" + response.getAddress().getZip());
            }
        }
        if (response.isSendEmailReceipts()) {
            check_mail.setChecked(true);
        } else {
            check_mail.setChecked(false);
        }

        HashMap<String, String> map = response.getUserDefinedFields();
        if (map.get("UDF1") != null && !map.get("UDF1").equals("")) {
            field_user_defined1.setText(map.get("UDF1"));
        }
        if (map.get("UDF2") != null && !map.get("UDF2").equals("")) {
            field_user_defined2.setText(map.get("UDF2"));
        }
        if (map.get("UDF3") != null && !map.get("UDF3").equals("")) {
            field_user_defined3.setText(map.get("UDF3"));
        }
        if (map.get("UDF4") != null && !map.get("UDF4").equals("")) {
            field_user_defined4.setText(map.get("UDF4"));
        }
    }

    public void updateCustomer() {

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        updateCustomerRequest.setId(field_customer_id.getValue());

        if (validateAlls.validateAll()) {

            Customer customer = new Customer();

            customer.setFirstName("" + field_first_name.getValue());
            customer.setLastName("" + field_last_name.getValue());
            customer.setEmail("" + field_email_address.getValue());
            customer.setPhone("" + field_phone_number.getValue());
            customer.setNotes("" + field_notes.getValue());

            if (check_mail.isChecked()) {
                customer.setSendEmailReceipts(true);
            } else {
                customer.setSendEmailReceipts(false);
            }

            TokenUtility.populateRequestHeaderFields(updateCustomerRequest, this);

            BillAddress address = new BillAddress();
            address.setCountry("US");
            address.setLine1("" + field_street_address.getValue());
            address.setCity("" + field_city.getValue());

            address.setZip("" + zip.getValue());
            address.setPhone("" + field_phone_number.getValue());

            HashMap<String, String> map = new HashMap<String, String>();

            if (field_user_defined1.getValue() != null) {
                map.put("UDF1", field_user_defined1.getValue());
            }
            if (field_user_defined2.getValue() != null) {
                map.put("UDF2", field_user_defined2.getValue());
            }
            if (field_user_defined3.getValue() != null) {
                map.put("UDF3", field_user_defined3.getValue());
            }
            if (field_user_defined4.getValue() != null) {
                map.put("UDF4", field_user_defined4.getValue());
            }


            if (map != null) {
                customer.setUserDefinedFields(map);
            }

            //   address.setState("" + new TokenUtility().getKeyByValue(new TokenUtility().getStates(), spinner_states.getValue()));
            address.setState("" + state);
            customer.setAddress(address);
            updateCustomerRequest.setCustomer(customer);
            updateCustomerTask(updateCustomerRequest);

        }
    }

    public void updateCustomerTask(UpdateCustomerRequest createCustomerRequest) {

        new CustomerUpdateTask(createCustomerRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreateCustomer.this);
                startProgressBar(progressDialog, "Updating Customer...");
            }

            @Override
            protected void onPostExecute(CustomerResponse customerResponse) {

                if (customerResponse != null) {
                    dismissProgressBar(progressDialog);

                    if (customerResponse.getResponseCode() == ResponseCode.APPROVED) {
                        dismissProgressBar(progressDialog);
                        Intent intent = new Intent(CreateCustomer.this, CustomerDetailsActivity.class);
                        intent.putExtra("customer_id", field_customer_id.getValue());
                        startActivity(intent);
                    } else if (customerResponse.getResponseCode() == ResponseCode.ERROR) {
                        showDialogView(getResources().getString(R.string.error), customerResponse.getResponseMessage(), CreateCustomer.this);

                    } else {
                        showDialogView(getResources().getString(R.string.error), customerResponse.getMessage(), CreateCustomer.this);
                    }
                } else {
                    dismissProgressBar(progressDialog);
                    showDialogView(getResources().getString(R.string.error), "Service error!", CreateCustomer.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }

}
