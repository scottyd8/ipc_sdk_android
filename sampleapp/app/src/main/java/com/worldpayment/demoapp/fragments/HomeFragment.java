package com.worldpayment.demoapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity;
import com.worldpayment.demoapp.activities.refundvoid.RefundVoidViewActivity;
import com.worldpayment.demoapp.activities.settlement.ActivitySettlement;
import com.worldpayment.demoapp.activities.vaultcustomers.VaultOperations;


public class HomeFragment extends Fragment implements View.OnClickListener {

    View root;
    Button debit_credit_button, refund_void_button, settlement_button, vault_customer_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        mappingViews();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return root;
    }
//115583672

    public void mappingViews() {

        debit_credit_button = (Button) root.findViewById(R.id.debit_credit_button);
        refund_void_button = (Button) root.findViewById(R.id.refund_void_button);
        settlement_button = (Button) root.findViewById(R.id.settlement_button);
        vault_customer_button = (Button) root.findViewById(R.id.vault_customer_button);

        debit_credit_button.setOnClickListener(this);
        refund_void_button.setOnClickListener(this);
        settlement_button.setOnClickListener(this);
        vault_customer_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.debit_credit_button:
                Intent credit = new Intent(getActivity(), CreditDebitActivity.class);
                startActivity(credit);

                break;

            case R.id.refund_void_button:
                Intent refund_void = new Intent(getActivity(), RefundVoidViewActivity.class);
                startActivity(refund_void);
                break;

            case R.id.settlement_button:
                Intent settlement = new Intent(getActivity(), ActivitySettlement.class);
                startActivity(settlement);
                break;

            case R.id.vault_customer_button:
                Intent intent = new Intent(getActivity(), VaultOperations.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

}
