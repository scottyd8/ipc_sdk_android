package com.worldpayment.demoapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.worldpayment.demoapp.DebitCreditActivity;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.activities.ActivitySettlement;
import com.worldpayment.demoapp.activities.RefundVoidViewActivity;


public class HomeFragment extends Fragment implements View.OnClickListener {

    View root;
    Button debit_credit_button, refund_void_button, settlement_button;// check_button, manual_button, vault_customer_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        mappingViews();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return root;
    }


    public void mappingViews() {

        debit_credit_button = (Button) root.findViewById(R.id.debit_credit_button);
        refund_void_button = (Button) root.findViewById(R.id.refund_void_button);
        settlement_button = (Button) root.findViewById(R.id.settlement_button);

        debit_credit_button.setOnClickListener(this);
        refund_void_button.setOnClickListener(this);
        settlement_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.debit_credit_button:
                Intent credit = new Intent(getActivity(), DebitCreditActivity.class);
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

            default:
                break;
        }
    }
}
