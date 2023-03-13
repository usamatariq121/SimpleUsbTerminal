package com.linkitsoft.beepvending.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.linkitsoft.beepvending.Adapters.CartItemAdapter;
import com.linkitsoft.beepvending.Models.Product;
import com.linkitsoft.beepvending.R;

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

        productList = new ArrayList<Product>();

        productList.add(new Product("test",1,false,1,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,2,"Heat Beat lays Barbecue 250 cal",3.98));
        productList.add(new Product("test",1,false,3,"Lays Classic ver Family Pack 250 cal",3.98));
        productList.add(new Product("test",1,false,4,"Hot Cup Tomyum 250 cal",3.98));
        productList.add(new Product("test",1,false,5,"Snikers Medium Pack 250 cal",3.98));
        productList.add(new Product("test",1,false,6,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,7,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,8,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,9,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,10,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,11,"India’s Magic Lays Masala 250 cal",3.98));
        productList.add(new Product("test",1,false,12,"India’s Magic Lays Masala 250 cal",3.98));

        cartItemAdapter = new CartItemAdapter(productList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(cartItemAdapter);
        recyclerView.setHasFixedSize(true);
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