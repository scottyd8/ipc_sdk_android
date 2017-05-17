package com.worldpayment.demoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.activities.debitcredit.TransactionDetails;

import java.util.List;

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

        holder.field_transaction_id.setText(itemList.get(position).getId());

        holder.field_transaction_id.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                responseTransactionDetails = itemList.get(position);
                Gson gson = new Gson();
                String batchResponse = gson.toJson(itemList.get(position), TransactionResponse.class);
                Intent transactionDetails = new Intent(context, TransactionDetails.class);
                transactionDetails.putExtra("from", "adapter");
                transactionDetails.putExtra("batchResponse", batchResponse);
                context.startActivity(transactionDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView field_transaction_id;

        public RecyclerViewHolders(View itemView) {
            super(itemView);

            field_transaction_id = (TextView) itemView.findViewById(R.id.field_transaction_id);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }

        @Override
        public void onClick(View view) {
        }
    }
}

