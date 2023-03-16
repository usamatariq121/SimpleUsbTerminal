package com.linkitsoft.beepvending.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkitsoft.beepvending.Adapters.MenuItemAdpater;
import com.linkitsoft.beepvending.Helper.ActivityRequest;
import com.linkitsoft.beepvending.Helper.UIHelper;
import com.linkitsoft.beepvending.Models.Product;
import com.linkitsoft.beepvending.R;
import com.linkitsoft.beepvending.Utils.LocalDataManager;
import com.linkitsoft.beepvending.databinding.ActivityPaymentBinding;
import com.linkitsoft.beepvending.databinding.ActivitySelectProductBinding;
import com.linkitsoft.beepvending.databinding.ProductDetailLayoutBinding;
import com.linkitsoft.beepvending.databinding.ReceiptlayoutBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SelectProduct extends BaseActivity {


    private View core_view;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            core_view.setSystemUiVisibility(hide_system_bars());
        }
    }

    private int hide_system_bars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }


    private RecyclerView recyclerView;
    private List<Product> productList;
    ConstraintLayout bottom;
    Button cancel;
    Button checkout;
    TextView totalamt;
    MenuItemAdpater menuItemAdpater;
    ImageButton imgCart;
    TextView tvQuantity;


    View core_views;

    int prodQuantityCount = 1;
    double amount = 0;
    int count = 0;
    public double totalPrices = 0.0;


    public static ArrayList<Product> cartList = new ArrayList<>();

    ActivitySelectProductBinding activitySelectProductBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySelectProductBinding = ActivitySelectProductBinding.inflate(getLayoutInflater());
        View view = activitySelectProductBinding.getRoot();
        setContentView(view);

        core_view = getWindow().getDecorView();
        core_view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    core_view.setSystemUiVisibility(hide_system_bars());
                }
            }
        });



        bottom = findViewById(R.id.constraintLayout8);


        cancel = findViewById(R.id.button6);
        checkout = findViewById(R.id.button7);
        totalamt = findViewById(R.id.textView19);

        imgCart = findViewById(R.id.cart);
        tvQuantity = findViewById(R.id.cartQuantity);

        cartList = new ArrayList<>();


        clickListener();





        productList = new ArrayList<Product>();
        productList.add(new Product("test", 1, false, 1, "India’s Magic Lays", 3.98,R.drawable.prod1));
        productList.add(new Product("test", 1, false, 2, "Heat Beat lays", 3.21,R.drawable.p5));
        productList.add(new Product("test", 1, false, 3, "Lays Classic ver ", 2.98,R.drawable.p6));
        productList.add(new Product("test", 1, false, 4, "Hot Cup Tomyum ", 1.98,R.drawable.p7));


        menuItemAdpater = new MenuItemAdpater(productList, SelectProduct.this);
        menuItemAdpater.setOnItemClickListener(onCartItemClickListener);
        activitySelectProductBinding.recproduct.setLayoutManager(new GridLayoutManager(SelectProduct.this, 3));
        activitySelectProductBinding.recproduct.setAdapter(menuItemAdpater);
        activitySelectProductBinding.recproduct.setHasFixedSize(true);


    }

    private void clickListener() {
        activitySelectProductBinding.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(SelectProduct.this, CartActivity.class);
                next.putExtra("totalItems", Integer.parseInt(tvQuantity.getText().toString()));
                startActivityForResult(next, ActivityRequest.REQUEST_ADD_TO_CART);
            }
        });

        activitySelectProductBinding.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(SelectProduct.this, CartActivity.class);
                next.putExtra("totalItems", Integer.parseInt(tvQuantity.getText().toString()));
                startActivityForResult(next, ActivityRequest.REQUEST_ADD_TO_CART);
            }
        });

        activitySelectProductBinding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(SelectProduct.this, MainActivity.class);
                startActivity(next);
                finish();
            }
        });
    }


    private MenuItemAdpater.OnItemClickListener onCartItemClickListener = new MenuItemAdpater.OnItemClickListener() {

        @Override
        public void onAddToCart(View view, int position, long id, int prodQuantity, double itemprice, String itemname, int image, int pos) {
            Log.d("ajayLis", "added to cart");
            ImageView itemAdded = view.findViewById(R.id.imageView7);
            showProductDetail(itemAdded, position, prodQuantity ,itemprice , itemname,image);
        }
    };

    private void showProductDetail(ImageView addedImage, int position , int prodQuantity , double price , String itemname,int image) {

        ProductDetailLayoutBinding productDetailLayoutBinding;
        final Dialog productDialog = new Dialog(this);
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        productDetailLayoutBinding = ProductDetailLayoutBinding.inflate(getLayoutInflater());
        View view1 = productDetailLayoutBinding.getRoot();
        productDialog.setContentView(view1);


        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        core_views = productDialog.getWindow().getDecorView();
        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E3C3939")));

        TextView Ingredient;
        TextView Description;

//        Ingredient = productDialog.findViewById(R.id.textView20);
//        Description = productDialog.findViewById(R.id.textView31);



        Picasso.get().load(image).into(productDetailLayoutBinding.prodImg);
        productDetailLayoutBinding.prodName.setText(itemname);
        productDetailLayoutBinding.prodPrice.setText("$" + String.format("%,.2f", price) + "");

//        Ingredient.setText(ing);
//        Description.setText(des);


        prodQuantityCount = 1;
        productDetailLayoutBinding.btnaddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productDialog.cancel();
                productList.get(position).setIsselected(true);


                amount = 0;
                amount = prodQuantityCount * price;

                menuItemAdpater.notifyDataSetChanged();
                activitySelectProductBinding.constraintLayout8.setVisibility(View.VISIBLE);
                count = count + prodQuantityCount;
                activitySelectProductBinding.cartQuantity.setText(count + "");

                activitySelectProductBinding.cart.setVisibility(View.VISIBLE);
                activitySelectProductBinding.cartQuantity.setVisibility(View.VISIBLE);

                cartList.add(new Product(price,itemname,image,prodQuantityCount,position));

                totalPrices = amount + totalPrices;
                LocalDataManager.getInstance().putDouble("TotalPrice", totalPrices);


                totalamt.setText("$"+String.format("%,.2f", totalPrices));

            }
        });


        productDetailLayoutBinding.btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prodQuantityCount++;
                if (prodQuantityCount > 0) {
                        productDetailLayoutBinding.textView8.setText("" + prodQuantityCount);
                    } else {
                        prodQuantityCount++;
                       productDetailLayoutBinding.textView8.setText("" + prodQuantityCount);
                    }
            }
        });


        productDetailLayoutBinding.btnminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prodQuantityCount--;
                if (prodQuantityCount > 0) {
                    productDetailLayoutBinding.textView8.setText("" + prodQuantityCount);
                } else {
                    prodQuantityCount = 1;
                    productDialog.dismiss();
                }
            }
        });

        productDetailLayoutBinding.btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productDialog.cancel();
            }
        });


        productDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        productDialog.show();
        productDialog.setCancelable(false);
        core_views.setSystemUiVisibility(hide_system_bars());
    }

    public void showdialog(String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(this, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }


}