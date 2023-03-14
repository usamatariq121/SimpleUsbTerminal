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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartActivity extends AppCompatActivity {

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
                                            Intent intent = new Intent(CartActivity.this, MainActivity.class);
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
                                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
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
        sweetAlertDialog = new SweetAlertDialog(CartActivity.this, SweetAlertDialog.WARNING_TYPE);

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
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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

        recyclerView = findViewById(R.id.recyclerView2);

        back = findViewById(R.id.imageButton6);
        checkout = findViewById(R.id.button6);
        totalamt = findViewById(R.id.textView19);
        tvCartTotal = findViewById(R.id.textView15);
        tvTotalAmount = findViewById(R.id.textView12);
        tvCartTotalAfterTax = findViewById(R.id.textView19);
        tvTotalItems = findViewById(R.id.textView10);
        tvTaxAmount = findViewById(R.id.textView16);



        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(CartActivity.this, PaymentActivity.class);
                startActivity(next);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if(cartList!=null){
            cartItemAdapter = new CartItemAdapter(cartList,this);
            cartItemAdapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(cartItemAdapter);
            recyclerView.setHasFixedSize(true);
        }



        Double totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");

        tvCartTotal.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
        totalPrice = totalPrice + 1.00;
        totalamt.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));

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

        tvTotalAmount.setText("Total: " + CommonUtils.formatTwoDecimal(totalPrice) + " $");
        tvCartTotal.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
        totalPrice = totalPrice + 1.00;
        tvCartTotalAfterTax.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));

    }



    private void showAmounts(int qty, double totalPrice, double finalTotalPrice) {

        tvTotalAmount.setText("Total: " + CommonUtils.formatTwoDecimal(totalPrice) + " $");
        tvCartTotal.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
        LocalDataManager.getInstance().putDouble("TotalPrice",totalPrice);
        totalPrice = totalPrice + 1.00;
        tvCartTotalAfterTax.setText("$" + CommonUtils.formatTwoDecimal(totalPrice));
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