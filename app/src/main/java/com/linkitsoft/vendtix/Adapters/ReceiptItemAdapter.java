package com.linkitsoft.vendtix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkitsoft.vendtix.Models.ReceiptModel;
import com.linkitsoft.vendtix.R;

import java.util.List;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.ViewHolder> {

    public List<ReceiptModel> receiptList;
    public Context context;

    public ReceiptItemAdapter(List<ReceiptModel> receiptList, Context context) {
        this.receiptList = receiptList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReceiptItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiptitem, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptItemAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        String name = receiptList.get(position).getProductName();
        String calories = receiptList.get(position).getProdCal();
        String price = receiptList.get(position).getTotalPrice();
        String index = receiptList.get(position).getIndexNo();
        String quantity = receiptList.get(position).getQuantity();

        holder.setData(name,calories,price,index,quantity);


    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        TextView tvDescription,tvIndex,tvQuantity,tvPrice,tvCalories;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvDescription = mView.findViewById(R.id.textView6);
            tvIndex = mView.findViewById(R.id.textView8);
            tvQuantity = mView.findViewById(R.id.textView9);
            tvPrice = mView.findViewById(R.id.textView10);
            tvCalories = mView.findViewById(R.id.textView11);
        }

        public void setData(String name, String calories, String price, String index, String quantity) {
            tvDescription.setText(name);
            tvIndex.setText(index);
            tvQuantity.setText(quantity);
            tvCalories.setText(calories);
            tvPrice.setText(price);
        }
    }
}
