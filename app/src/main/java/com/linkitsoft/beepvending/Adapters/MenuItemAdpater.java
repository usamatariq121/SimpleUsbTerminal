package com.linkitsoft.beepvending.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.linkitsoft.beepvending.Activities.SelectProduct;
import com.linkitsoft.beepvending.Models.Product;
import com.linkitsoft.beepvending.R;
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


        //String image = productList.get(position).getImage();
        String itemname = productList.get(position).getItemname();
        double itemprice = productList.get(position).getPrice();
        int qty = productList.get(position).getQty();
        int pos = productList.get(position).getPosition();
        Boolean isselected = productList.get(position).getIsselected();
        int image = productList.get(position).getProdImg();

        holder.setdata(itemname,image,qty,itemprice,pos,isselected);

        View.OnClickListener oc = null;

        if (onItemClickListener != null){
             oc = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onAddToCart(view,position,view.getId(),qty,itemprice,itemname , image, pos);
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
        private ConstraintLayout prodCons;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.textView5);
            price = mView.findViewById(R.id.textView3);
            position = mView.findViewById(R.id.textView4);
            selecticon = mView.findViewById(R.id.imageView7);
            prodimage = mView.findViewById(R.id.imageView6);
            mainCard = mView.findViewById(R.id.cardView);
            prodCons = mView.findViewById(R.id.consProduct);
        }
        public void setdata( String itemname, int itemimage, int qty, double pricee, int pos, Boolean isselected){

            name.setText(itemname);
            price.setText("$"+pricee);
            position.setText(""+pos);
            if(isselected){
                selecticon.setVisibility(View.VISIBLE);
                prodCons.setBackgroundResource(R.drawable.item_border);
            }else{
                prodCons.setBackgroundResource(0);
            }
            Picasso.get().load(itemimage).into(prodimage);

        }
    }

    public interface OnItemClickListener{
        public void onAddToCart(View view, int position, long id , int prodQuantity , double itemprice , String itemname , int image , int pos);
    }
}
