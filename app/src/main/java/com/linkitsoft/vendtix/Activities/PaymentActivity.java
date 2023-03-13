package com.linkitsoft.vendtix.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.linkitsoft.vendtix.Adapters.ReceiptItemAdapter;
import com.linkitsoft.vendtix.Models.ReceiptModel;
import com.linkitsoft.vendtix.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaymentActivity extends AppCompatActivity {

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
                                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
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
                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
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
        sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE);

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
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
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


    ImageButton btnNayax;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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


        btnNayax = findViewById(R.id.imageButton9);
        back = findViewById(R.id.imageButton8);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNayax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThankyou();
            }
        });
    }

    private void showThankyou() {
        final Dialog thankyouDialog = new Dialog(this);
        thankyouDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        thankyouDialog.setContentView(R.layout.thankyou_layout);
        thankyouDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnReceipt;
        btnReceipt = thankyouDialog.findViewById(R.id.button);

        btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thankyouDialog.dismiss();
//                ReceiptFragment receiptFragment = new ReceiptFragment();
//                receiptFragment.show(getSupportFragmentManager(),"ReceiptFragment");

                final Dialog receiptDialog = new Dialog(PaymentActivity.this);
                receiptDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                receiptDialog.setContentView(R.layout.receiptlayout);
                receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button btnFinish;
                RecyclerView receiptRecyclerView;
                List<ReceiptModel> receiptModelList;
                ReceiptItemAdapter receiptItemAdapter;
                btnFinish = receiptDialog.findViewById(R.id.button);
                receiptRecyclerView = receiptDialog.findViewById(R.id.recyclerView3);

                receiptModelList = new ArrayList<ReceiptModel>();
                receiptModelList.add(new ReceiptModel("M&MS peanut chocolate candies","250 cal","1","$3.25","1"));
                receiptModelList.add(new ReceiptModel("M&MS peanut chocolate candies","250 cal","1","$3.25","2"));
                receiptModelList.add(new ReceiptModel("M&MS peanut chocolate candies","250 cal","1","$3.25","3"));
                receiptModelList.add(new ReceiptModel("M&MS peanut chocolate candies","250 cal","1","$3.25","4"));
                receiptModelList.add(new ReceiptModel("M&MS peanut chocolate candies","250 cal","1","$3.25","5"));
                receiptModelList.add(new ReceiptModel("M&MS peanut chocolate candies","250 cal","1","$3.25","6"));
                receiptItemAdapter = new ReceiptItemAdapter(receiptModelList,PaymentActivity.this);
                receiptRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                receiptRecyclerView.setAdapter(receiptItemAdapter);
                receiptRecyclerView.setHasFixedSize(true);



                btnFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiptDialog.dismiss();
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                    }
                });


                receiptDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                receiptDialog.show();


            }
        });


        thankyouDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        thankyouDialog.show();
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