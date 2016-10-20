package com.worldpayment.demoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.worldpayment.demoapp.R;

public class VaultOperations extends AppCompatActivity implements View.OnClickListener {

    Button create_customer_button, retrieve_customer_button, create_payment_account_button;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_operations);

        mappingViews();
    }

    public void mappingViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vault");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
