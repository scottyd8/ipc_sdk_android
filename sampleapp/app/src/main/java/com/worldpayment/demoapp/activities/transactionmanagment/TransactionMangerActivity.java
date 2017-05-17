package com.worldpayment.demoapp.activities.transactionmanagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

public class TransactionMangerActivity extends WorldBaseActivity implements View.OnClickListener {

    private Button get_transactions, search_transaction, update_transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_manger_actvity);
        setActivity(TransactionMangerActivity.this);
        mappingViews();
    }

    public void mappingViews() {
        get_transactions = (Button) findViewById(R.id.get_transactions);
        search_transaction = (Button) findViewById(R.id.search_transaction);
        update_transaction = (Button) findViewById(R.id.update_transaction);

        get_transactions.setOnClickListener(this);
        search_transaction.setOnClickListener(this);
        update_transaction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.get_transactions:
                Intent getIntent = new Intent(this, GetTransactions.class);
                startActivity(getIntent);
                break;

            case R.id.search_transaction:
                Intent searchIntent = new Intent(this, SearchTransaction.class);
                startActivity(searchIntent);
                break;

            case R.id.update_transaction:
                Intent updateIntent = new Intent(this, UpdateTransaction.class);
                startActivity(updateIntent);
                break;
        }
    }
}
