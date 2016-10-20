package com.worldpayment.demoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.activities.TransactionDetails;
import com.worldpay.library.webservices.services.payments.TransactionResponse;

import java.util.List;

import static com.worldpayment.demoapp.DebitCreditActivity.responseTransactionDetails;

public class SettlementAdapter extends RecyclerView.Adapter<SettlementAdapter.RecyclerViewHolders> {

    private List<TransactionResponse> itemList;
    private Context context;

    public SettlementAdapter(Context context, List<TransactionResponse> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction_id, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {

        Log.d("itemList", "" + itemList.size());
        holder.field_transaction_id.setText(itemList.get(position).getId());

        holder.transaction_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             //   Toast.makeText(context, "" + itemList.get(position).getId(), Toast.LENGTH_SHORT).show();
                responseTransactionDetails = itemList.get(position);
                Intent transactionDetails = new Intent(context, TransactionDetails.class);
                context.startActivity(transactionDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout transaction_layout;
        TextView field_transaction_id;

        public RecyclerViewHolders(View itemView) {
            super(itemView);

            field_transaction_id = (TextView) itemView.findViewById(R.id.field_transaction_id);
            transaction_layout = (LinearLayout) itemView.findViewById(R.id.transaction_layout);
        }

        @Override
        public void onClick(View view) {

            /*MakeUpFragment makeUpFragment = new MakeUpFragment();
            FragmentTransaction makeUpFragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            makeUpFragmentTransaction.replace(R.id.frame, makeUpFragment);
            ((FragmentActivity)context).setTitle("Make Up");
            makeUpFragmentTransaction.commit();*/
        }
    }
}

