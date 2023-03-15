package com.linkitsoft.beepvending.Activities;

import static com.linkitsoft.beepvending.Activities.SelectProduct.cartList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkitsoft.beepvending.Adapters.CartItemAdapter;
import com.linkitsoft.beepvending.Helper.ActivityRequest;
import com.linkitsoft.beepvending.Helper.CommonUtils;
import com.linkitsoft.beepvending.Models.Product;
import com.linkitsoft.beepvending.R;
import com.linkitsoft.beepvending.Utils.LocalDataManager;
import com.linkitsoft.beepvending.databinding.ActivityCartBinding;
import com.linkitsoft.beepvending.databinding.ActivityConfigBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartActivity extends BaseActivity {




    private RecyclerView recyclerView;
    private List<Product> productList;
    ConstraintLayout bottom;
    ImageButton back;
    Button checkout;
    TextView totalamt;
    CartItemAdapter cartItemAdapter;
    private int prodQuantity = 0;
    Double totalPrice=0.00;

    double totalAmount = 0;



    double finalTotalAmount = 0;
    double taxAmount = 0.0;
    int totalItems = 0;
    TextView tvTotalAmount, tvCartTotal, tvTotalItems, tvCartTotalAfterTax, tvTaxAmount;


    ActivityCartBinding activityCartBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartBinding = ActivityCartBinding.inflate(getLayoutInflater());
        View view = activityCartBinding.getRoot();
        setContentView(view);







        clickListener();



        if(cartList!=null){
            cartItemAdapter = new CartItemAdapter(cartList,this);
            cartItemAdapter.setOnItemClickListener(onItemClickListener);
            activityCartBinding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
            activityCartBinding.recyclerView2.setAdapter(cartItemAdapter);
            activityCartBinding.recyclerView2.setHasFixedSize(true);
        }


        Double totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
        activityCartBinding.textView15.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
        totalPrice = totalPrice + 1.00;
        activityCartBinding.textView19.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));

    }

    private void clickListener() {
        activityCartBinding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(CartActivity.this, PaymentActivity.class);
                startActivity(next);
            }
        });

        activityCartBinding.imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private CartItemAdapter.OnItemClickListener onItemClickListener = new CartItemAdapter.OnItemClickListener() {
        @Override
        public void onPlusClick(View view, int position, long id, double price, int quantity , CartItemAdapter.ViewHolder viewHolder) {


            prodQuantity = cartList.get(position).getQty() + 1;
            cartList.get(position).setQty(prodQuantity);
            cartList.get(position).setPrice(price);
            cartList.get(position).setQty(prodQuantity);
            cartList.get(position).setPrice(price);
            totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
            totalPrice = price+totalPrice;
            cartItemAdapter.notifyDataSetChanged();



            LocalDataManager.getInstance().putDouble("TotalPrice",totalPrice);
            showAmounts(totalItems, totalPrice, finalTotalAmount);

        }

        @Override
        public void onMinusClick(View view, int position, long id, double price, int quantity,CartItemAdapter.ViewHolder viewHolder) {


            prodQuantity = cartList.get(position).getQty() - 1;
            if(prodQuantity>=1){
                totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
                Log.d("usama1" , String.valueOf(totalPrice));
                totalPrice = totalPrice-price;
                totalItems = totalItems - 1;

                LocalDataManager.getInstance().putDouble("TotalPrice",totalPrice);

                cartList.get(position).setQty(prodQuantity);
                Double pr = price*prodQuantity;
                Log.d("usama" , String.valueOf(totalPrice));

                cartList.get(position).setQty(prodQuantity);
                cartList.get(position).setPrice(price);

                showAmounts(totalItems, totalPrice, finalTotalAmount);
                cartItemAdapter.notifyDataSetChanged();


            }else{


                totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
                cartList.remove(position);
                totalPrice = totalPrice-price;
                LocalDataManager.getInstance().putDouble("TotalPrice",totalPrice);
                showAmounts(totalItems, totalPrice, finalTotalAmount);
                cartItemAdapter.notifyItemRemoved(position);
                cartItemAdapter.notifyItemRangeChanged(position, cartList.size());
                cartItemAdapter.notifyDataSetChanged();
            }

            if(cartList.size()==0){
                cartList = new ArrayList<>();
                Intent intent = new Intent();
                intent.putExtra("totalAmountAC", 0.0);
                intent.putExtra("totalItemAC", 0);
                setResult(ActivityRequest.REQUEST_ADD_TO_CART, intent);
                Intent i = new Intent(CartActivity.this,SelectProduct.class);
                startActivity(i);
            }


                // finish();





        }

        @Override
        public void onRemoveClick(View view, int position, long id, double price, int quantity,CartItemAdapter.ViewHolder viewHolder) {




            cartList.remove(position);

            totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
            price = price * quantity;
            totalPrice = totalPrice-price;
            LocalDataManager.getInstance().putDouble("TotalPrice",totalPrice);
            totalItems = totalItems - quantity;
            showAmounts(totalItems, totalPrice, finalTotalAmount);

            cartItemAdapter.notifyItemRemoved(position);
            cartItemAdapter.notifyItemRangeChanged(position, cartList.size());

            if(cartList.size()==0){
                totalItems =0;
                totalAmount = 0.00;
                finalTotalAmount = 0.00;
                LocalDataManager.getInstance().putDouble("TotalAmount",0.00);
                showAmounts( totalAmount, finalTotalAmount);
                Intent i = new Intent(CartActivity.this,SelectProduct.class);
                startActivity(i);
                cartList = new ArrayList<>();

            }
            cartItemAdapter.notifyDataSetChanged();






        }

    };


    private void showAmounts(double totalPrice, double finalTotalPrice) {

        activityCartBinding.textView12.setText("Total: " + CommonUtils.formatTwoDecimal(totalPrice) + " $");
        tvCartTotal.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
        totalPrice = totalPrice + 1.00;
        activityCartBinding.textView19.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));

    }



    private void showAmounts(int qty, double totalPrice, double finalTotalPrice) {

        activityCartBinding.textView12.setText("Total: " + CommonUtils.formatTwoDecimal(totalPrice) + " $");
        tvCartTotal.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
        LocalDataManager.getInstance().putDouble("TotalPrice",totalPrice);
        totalPrice = totalPrice + 1.00;
        activityCartBinding.textView12.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
    }


    public void showdialog(String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(this, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }


}