package com.linkitsoft.beepvending.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkitsoft.beepvending.Activities.CartActivity;
import com.linkitsoft.beepvending.Models.Product;
import com.linkitsoft.beepvending.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {


    public List<Product> productList;
    public Context context;
    CartActivity cartActivity;

    public CartItemAdapter(List<Product> productList, CartActivity cartActivity){

        this.cartActivity = cartActivity;
        this.productList = productList;
    }


    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitem, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);


        String image = productList.get(position).getImage();
        String itemname = productList.get(position).getItemname();
        double itemprice = productList.get(position).getPrice();
        int qty = productList.get(position).getQty();
        int pos = productList.get(position).getPosition();
        Boolean isselected = productList.get(position).getIsselected();

        holder.setdata(itemname,image,qty,itemprice,pos,isselected);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView name;
        private TextView price;
        private TextView qty;
        private ImageView prodimage;
        private ImageButton plus;
        private ImageButton minus;
        private ImageButton delete;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            mView = itemView;

            name = mView.findViewById(R.id.textView6);
            price = mView.findViewById(R.id.textView7);
            qty = mView.findViewById(R.id.textView8);
            plus = mView.findViewById(R.id.imageButton4);
            minus = mView.findViewById(R.id.imageButton5);
            delete = mView.findViewById(R.id.imageButton7);
            prodimage = mView.findViewById(R.id.imageView8);

        }
        public void setdata( String itemname, String itemimage, int qtyy, double pricee, int pos, Boolean isselected){

            name.setText(itemname);
            price.setText("$"+pricee);
            qty.setText(""+qtyy);

            Picasso.get().load(R.drawable.p5).into(prodimage);

        }
    }
}
