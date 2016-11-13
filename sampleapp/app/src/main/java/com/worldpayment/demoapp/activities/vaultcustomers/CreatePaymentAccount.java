package com.worldpayment.demoapp.activities.vaultcustomers;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

public class CreatePaymentAccount extends WorldBaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btn_create, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_payment_account);
        setActivity(CreatePaymentAccount.this);
        mappingViews();
    }

    public void mappingViews() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_create:
                break;

            case R.id.btn_cancel:
                finish();

            default:
                break;
        }
    }
}
