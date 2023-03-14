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
import com.linkitsoft.beepvending.Helper.UIHelper;
import com.linkitsoft.beepvending.Models.Product;
import com.linkitsoft.beepvending.R;
import com.linkitsoft.beepvending.Utils.LocalDataManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SelectProduct extends AppCompatActivity {

    //********************************** TIMER **********************************************************
    Boolean isuserpaying = false;
    Boolean threadintrupt = false;
    Boolean oncreate = false;
    SweetAlertDialog sweetAlertDialog;
    wait30 w30;


    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt) {

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final CountDownTimer[] ct = new CountDownTimer[1];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ct[0] = new CountDownTimer(10000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    if (!isuserpaying) {
                                        if (millisUntilFinished > 0) {
                                            sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
                                        } else {
                                            threadintrupt = true;
                                            try {
                                                sweetAlertDialog.dismissWithAnimation();
                                            } catch (Exception ex) {
                                            }
                                            Intent intent = new Intent(SelectProduct.this, MainActivity.class);
                                            startActivity(intent);
                                            ct[0].cancel();
                                        }
                                    }
                                }

                                public void onFinish() {

                                    try {
                                        sweetAlertDialog.dismissWithAnimation();
                                    } catch (Exception ex) {
                                    }
                                    threadintrupt = true;
                                    ct[0].cancel();
                                    Intent intent = new Intent(SelectProduct.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            };

                            if (!isuserpaying) {
                                showsweetalerttimeout(ct);
                                ct[0].start();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
    }

    void showsweetalerttimeout(final CountDownTimer[] ct) {
        sweetAlertDialog = new SweetAlertDialog(SelectProduct.this, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText("Press anywhere on screen to continue");
        sweetAlertDialog.setContentText("This session will end in 10");
        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                threadintrupt = true;
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
                Intent intent = new Intent(SelectProduct.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });

        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sweetAlertDialog.dismissWithAnimation();
                ct[0].cancel();
            }
        });
        sweetAlertDialog.show();
    }

    //********************************** TIMER **********************************************************

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        core_view = getWindow().getDecorView();
        core_view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    core_view.setSystemUiVisibility(hide_system_bars());
                }
            }
        });

        w30 = new wait30();
        w30.start();
        oncreate = true;


        bottom = findViewById(R.id.constraintLayout8);
        recyclerView = findViewById(R.id.recyclerView);

        cancel = findViewById(R.id.button6);
        checkout = findViewById(R.id.button7);
        totalamt = findViewById(R.id.textView19);

        imgCart = findViewById(R.id.cart);
        tvQuantity = findViewById(R.id.cartQuantity);

        cartList = new ArrayList<>();




        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(SelectProduct.this, CartActivity.class);
                startActivity(next);
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(SelectProduct.this, CartActivity.class);
                startActivity(next);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        productList = new ArrayList<Product>();

        productList.add(new Product("test", 1, false, 1, "India’s Magic Lays", 3.98,R.drawable.prod1));
        productList.add(new Product("test", 1, false, 2, "Heat Beat lays", 3.21,R.drawable.p5));
        productList.add(new Product("test", 1, false, 3, "Lays Classic ver ", 2.98,R.drawable.p6));
        productList.add(new Product("test", 1, false, 4, "Hot Cup Tomyum ", 1.98,R.drawable.p7));


        menuItemAdpater = new MenuItemAdpater(productList, SelectProduct.this);
        menuItemAdpater.setOnItemClickListener(onCartItemClickListener);
        recyclerView.setLayoutManager(new GridLayoutManager(SelectProduct.this, 3));
        recyclerView.setAdapter(menuItemAdpater);
        recyclerView.setHasFixedSize(true);


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
        final Dialog productDialog = new Dialog(this);
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productDialog.setContentView(R.layout.product_detail_layout);
        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        core_views = productDialog.getWindow().getDecorView();

        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E3C3939")));
        Button addToCart, close;
        ImageButton btnPlus;
        TextView tvTotalProduct;
        ImageButton btnMinus;
        ImageView imgProd;
        TextView Ingredient;
        TextView Description;
        TextView tvProdPrice;
        TextView tvProdName;
        imgProd = productDialog.findViewById(R.id.imageView8);
        addToCart = productDialog.findViewById(R.id.imageButton3);
        close = productDialog.findViewById(R.id.imageButton2);
        btnPlus = productDialog.findViewById(R.id.imageButton4);
        btnMinus = productDialog.findViewById(R.id.imageButton5);
        tvTotalProduct = productDialog.findViewById(R.id.textView8);
        tvProdName = productDialog.findViewById(R.id.textView6);
        tvProdPrice = productDialog.findViewById(R.id.textView7);
//        Ingredient = productDialog.findViewById(R.id.textView20);
//        Description = productDialog.findViewById(R.id.textView31);



        Picasso.get().load(image).into(imgProd);
        tvProdName.setText(itemname);
        tvProdPrice.setText("$" + String.format("%,.2f", price) + "");
//        Ingredient.setText(ing);
//        Description.setText(des);


        prodQuantityCount = 1;
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productDialog.cancel();
                productList.get(position).setIsselected(true);


                amount = 0;
                amount = prodQuantityCount * price;

                menuItemAdpater.notifyDataSetChanged();
                bottom.setVisibility(View.VISIBLE);
                count = count + prodQuantityCount;
                tvQuantity.setText(count + "");

                imgCart.setVisibility(View.VISIBLE);
                tvQuantity.setVisibility(View.VISIBLE);

                cartList.add(new Product(price,itemname,image,prodQuantityCount,position));

                totalPrices = amount + totalPrices;
                LocalDataManager.getInstance().putDouble("TotalPrice", totalPrices);


                totalamt.setText("$"+String.format("%,.2f", totalPrices));

            }
        });


        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prodQuantityCount++;
                if (prodQuantityCount > 0) {
                        tvTotalProduct.setText("" + prodQuantityCount);
                    } else {
                        prodQuantityCount++;
                        tvTotalProduct.setText("" + prodQuantityCount);
                    }
            }
        });


        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prodQuantityCount--;
                if (prodQuantityCount > 0) {
                    tvTotalProduct.setText("" + prodQuantityCount);
                } else {
                    prodQuantityCount = 1;
                    productDialog.dismiss();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        threadintrupt = false;
        isuserpaying = false;
        if (!oncreate) {
            new wait30().start();
        } else {
            oncreate = false;
        }

    }
}