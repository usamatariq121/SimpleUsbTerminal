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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.linkitsoft.beepvending.databinding.ActivityCartBinding;
import com.linkitsoft.beepvending.databinding.ActivityPaymentBinding;
import com.linkitsoft.beepvending.databinding.ReceiptlayoutBinding;

import com.linkitsoft.beepvending.databinding.ThankyouLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaymentActivity extends BaseActivity {

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



    Double totalPrice ;
    ActivityPaymentBinding activityPaymentBinding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPaymentBinding = ActivityPaymentBinding.inflate(getLayoutInflater());
        View view = activityPaymentBinding.getRoot();
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





        totalPrice = LocalDataManager.getInstance().getDouble("TotalPrice");
        totalPrice = totalPrice + 1.00;
        activityPaymentBinding.txtTotalprice.setText("$"+ CommonUtils.formatTwoDecimal(totalPrice));

        clickListener();



    }

    private void clickListener() {

        activityPaymentBinding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityPaymentBinding.btnTapPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThankyou();
            }
        });

        activityPaymentBinding.btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThankyou();
            }
        });

    }

    private void showThankyou() {
        ThankyouLayoutBinding thankyouBinding;

        final Dialog thankyouDialog = new Dialog(this);
        thankyouDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        thankyouBinding = ThankyouLayoutBinding.inflate(getLayoutInflater());
        View view = thankyouBinding.getRoot();
        thankyouDialog.setContentView(view);

        View core_view = thankyouDialog.getWindow().getDecorView();
        thankyouDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        Spannable wordtoSpan = new SpannableString( thankyouBinding.txtPaymenttext.getText().toString());
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#382633")), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        thankyouBinding.txtPaymenttext.setText(wordtoSpan);



        thankyouBinding.txtTotal.setText("$"+ CommonUtils.formatTwoDecimal(totalPrice));

        thankyouBinding.btnreciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thankyouDialog.dismiss();


                ReceiptlayoutBinding receiptlayoutBinding;
                final Dialog receiptDialog = new Dialog(PaymentActivity.this);
                receiptDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                receiptlayoutBinding = ReceiptlayoutBinding.inflate(getLayoutInflater());
                View view1 = receiptlayoutBinding.getRoot();
                receiptDialog.setContentView(view1);



                View  core_view = receiptDialog.getWindow().getDecorView();
                receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                ReceiptItemAdapter receiptItemAdapter;

                Date d = new Date();
                String pattern = "dd-MM-yyyy";
                String dateInString =new SimpleDateFormat(pattern).format(new Date());
                SimpleDateFormat dateFormatprev1 = new SimpleDateFormat("hh:mm:ss aa");
                String currentTime = dateFormatprev1.format(d.getTime());


                receiptlayoutBinding.txtDate.setText(dateInString+"/"+currentTime);
                totalPrice = totalPrice;
                receiptlayoutBinding.txtTotalprice.setText("$"+CommonUtils.formatTwoDecimal(totalPrice)+"");



                receiptItemAdapter = new ReceiptItemAdapter(cartList,PaymentActivity.this);
                receiptlayoutBinding.recReciept.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                receiptlayoutBinding.recReciept.setAdapter(receiptItemAdapter);
                receiptlayoutBinding.recReciept.setHasFixedSize(true);



                receiptlayoutBinding.btnFinish.setOnClickListener(new View.OnClickListener() {
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


}