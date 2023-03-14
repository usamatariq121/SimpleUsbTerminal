package com.linkitsoft.beepvending.Activities;

import static com.linkitsoft.beepvending.Activities.SelectProduct.cartList;

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
import android.widget.TextView;

import com.linkitsoft.beepvending.Adapters.ReceiptItemAdapter;
import com.linkitsoft.beepvending.Helper.CommonUtils;
import com.linkitsoft.beepvending.Models.ReceiptModel;
import com.linkitsoft.beepvending.R;
import com.linkitsoft.beepvending.Utils.LocalDataManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    TextView totalam;
    Double totalPrice ;

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
        totalam= findViewById(R.id.textView22);

        totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
        totalam.setText("$"+ CommonUtils.formatTwoDecimal(totalPrice));



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
        core_view = thankyouDialog.getWindow().getDecorView();

        thankyouDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnReceipt;
        TextView thankyoutotal;
        btnReceipt = thankyouDialog.findViewById(R.id.button);
        thankyoutotal = thankyouDialog.findViewById(R.id.textView28);


        thankyoutotal.setText("$"+ CommonUtils.formatTwoDecimal(totalPrice));

        btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thankyouDialog.dismiss();
//                ReceiptFragment receiptFragment = new ReceiptFragment();
//                receiptFragment.show(getSupportFragmentManager(),"ReceiptFragment");

                final Dialog receiptDialog = new Dialog(PaymentActivity.this);
                receiptDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                receiptDialog.setContentView(R.layout.receiptlayout);
                core_view = receiptDialog.getWindow().getDecorView();
                receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button btnFinish;
                RecyclerView receiptRecyclerView;
                List<ReceiptModel> receiptModelList;
                TextView cartTotal;
                TextView DateTime;
                ReceiptItemAdapter receiptItemAdapter;
                btnFinish = receiptDialog.findViewById(R.id.button);
                DateTime = receiptDialog.findViewById(R.id.textView26);
                cartTotal = receiptDialog.findViewById(R.id.textView28);


                receiptRecyclerView = receiptDialog.findViewById(R.id.recyclerView3);
                Date d = new Date();
                String pattern = "dd-MM-yyyy";
                String dateInString =new SimpleDateFormat(pattern).format(new Date());
                SimpleDateFormat dateFormatprev1 = new SimpleDateFormat("hh:mm:ss aa");
                String currentTime = dateFormatprev1.format(d.getTime());
                DateTime.setText(dateInString+"/"+currentTime);

                totalPrice = totalPrice;
                cartTotal.setText("$"+CommonUtils.formatTwoDecimal(totalPrice)+"");



                receiptItemAdapter = new ReceiptItemAdapter(cartList,PaymentActivity.this);
                receiptRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                receiptRecyclerView.setAdapter(receiptItemAdapter);
                receiptRecyclerView.setHasFixedSize(true);



                btnFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiptDialog.dismiss();
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        cartList = new ArrayList<>();
                        startActivity(intent);
                    }
                });


                receiptDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                receiptDialog.show();
                receiptDialog.setCancelable(false);
                core_view.setSystemUiVisibility(hide_system_bars());


            }
        });


        thankyouDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        thankyouDialog.show();
        thankyouDialog.setCancelable(false);
        core_view.setSystemUiVisibility(hide_system_bars());
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