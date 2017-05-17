package com.worldpayment.demoapp.activities.vaultcustomers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.worldpay.library.domain.VaultPaymentMethod;
import com.worldpay.library.views.WPEditText;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

public class UpdatePaymentMethod extends WorldBaseActivity implements View.OnClickListener {

    WPEditText tv_customer_id, tv_payment_id, tv_card_no, tv_cvv, tv_expiration_month, tv_expiration_year, tv_pin_block;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_payment_method);
        setActivity(UpdatePaymentMethod.this);
        mappingViews();
        if (getIntent().getExtras() != null) {
            String cardItem = getIntent().getExtras().getString("cardItem");
            Log.d("cardItem", "He = " + cardItem);
            Gson gson = new Gson();
            VaultPaymentMethod response = gson.fromJson(cardItem, VaultPaymentMethod.class);
            setFields(response);
        }
    }

    public void mappingViews() {

        btn_save = (Button) findViewById(R.id.btn_save);
//        btn_cancel = (Button) findViewById(btn_cancel);

        btn_save.setOnClickListener(this);
//        btn_cancel.setOnClickListener(this);

        tv_customer_id = (WPEditText) findViewById(R.id.tv_customer_id);
        tv_payment_id = (WPEditText) findViewById(R.id.tv_payment_id);
        tv_card_no = (WPEditText) findViewById(R.id.tv_card_no);
        tv_cvv = (WPEditText) findViewById(R.id.tv_cvv);
        tv_expiration_month = (WPEditText) findViewById(R.id.tv_expiration_month);
        tv_expiration_year = (WPEditText) findViewById(R.id.tv_expiration_year);
        tv_pin_block = (WPEditText) findViewById(R.id.tv_pin_block);
    }

    public void setFields(VaultPaymentMethod response) {

        tv_customer_id.setText("" + response.getmCustomerId());
        tv_payment_id.setText("" + response.getmId());
        tv_card_no.setText("" + response.getmVaultCard().getMaskedNumber());
        //Cvv missing in VaultPaymentMethod
//        tv_cvv.setText(""+response.getmVaultCard().get);\
        tv_expiration_month.setText("" + response.getmVaultCard().getExpirationMonth());
        tv_expiration_year.setText("" + response.getmVaultCard().getExpirationYear());
        //Pin Block missing in VaultPaymentMethod
        tv_pin_block.setText("" + response.getmVaultCard().getLastFourDigits());

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                break;
//
//            case btn_cancel:
//                finish();
//                break;

            default:
                break;
        }

    }
}
