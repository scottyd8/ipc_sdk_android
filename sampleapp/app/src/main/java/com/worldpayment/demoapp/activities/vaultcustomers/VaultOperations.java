package com.worldpayment.demoapp.activities.vaultcustomers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

public class VaultOperations extends WorldBaseActivity implements View.OnClickListener {

    Button create_customer_button, retrieve_customer_button, create_payment_account_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_operations);
        setActivity(VaultOperations.this);
        mappingViews();

    }

    public void mappingViews() {

        create_customer_button = (Button) findViewById(R.id.create_customer_button);
        retrieve_customer_button = (Button) findViewById(R.id.retrieve_customer_button);
        create_payment_account_button = (Button) findViewById(R.id.create_payment_account_button);

        create_customer_button.setOnClickListener(this);
        retrieve_customer_button.setOnClickListener(this);
        create_payment_account_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.create_customer_button:
                Intent createCustomer = new Intent(VaultOperations.this, CreateCustomer.class);
                startActivity(createCustomer);
                break;

            case R.id.retrieve_customer_button:
                Intent retrieveCustomer = new Intent(VaultOperations.this, RetrieveCustomer.class);
                startActivity(retrieveCustomer);
                break;

            case R.id.create_payment_account_button:
                Intent createPaymentAccount = new Intent(VaultOperations.this, CreatePaymentAccount.class);
                startActivity(createPaymentAccount);
                break;

            default:
                break;
        }
    }
}
