package com.linkitsoft.vendtix.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.linkitsoft.vendtix.Activities.SelectProduct;
import com.linkitsoft.vendtix.Models.Product;
import com.linkitsoft.vendtix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuItemAdpater extends RecyclerView.Adapter<MenuItemAdpater.ViewHolder>{

    public List<Product> productList;
    public Context context;
    SelectProduct selectProduct;
    private OnItemClickListener onItemClickListener;
    public MenuItemAdpater(List<Product> productList, SelectProduct selectProduct){

        this.selectProduct = selectProduct;
        this.productList = productList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public MenuItemAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menuitem, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemAdpater.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setIsRecyclable(false);


        String image = productList.get(position).getImage();
        String itemname = productList.get(position).getItemname();
        double itemprice = productList.get(position).getPrice();
        int qty = productList.get(position).getQty();
        int pos = productList.get(position).getPosition();
        Boolean isselected = productList.get(position).getIsselected();

        holder.setdata(itemname,image,qty,itemprice,pos,isselected);

        View.OnClickListener oc = null;

        if (onItemClickListener != null){
             oc = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onAddToCart(view,position,view.getId());

                }
            };
        }

        holder.mainCard.setOnClickListener(oc);
        holder.price.setOnClickListener(oc);
        holder.mView.setOnClickListener(oc);
        holder.name.setOnClickListener(oc);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView name;
        private TextView price;
        private TextView position;
        private ImageView selecticon;
        private ImageView prodimage;
        private CardView mainCard;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.textView5);
            price = mView.findViewById(R.id.textView3);
            position = mView.findViewById(R.id.textView4);
            selecticon = mView.findViewById(R.id.imageView7);
            prodimage = mView.findViewById(R.id.imageView6);
            mainCard = mView.findViewById(R.id.cardView);
        }
        public void setdata( String itemname, String itemimage, int qty, double pricee, int pos, Boolean isselected){

            name.setText(itemname);
            price.setText("$"+pricee);
            position.setText(""+pos);
            if(isselected){
                selecticon.setVisibility(View.VISIBLE);
            }
            Picasso.get().load(R.drawable.p5).into(prodimage);

        }
    }

    public interface OnItemClickListener{
        public void onAddToCart(View view, int position, long id);
    }
}
