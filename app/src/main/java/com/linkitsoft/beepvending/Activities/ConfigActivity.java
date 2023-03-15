package com.linkitsoft.beepvending.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.linkitsoft.beepvending.R;
import com.linkitsoft.beepvending.databinding.ActivityConfigBinding;
import com.linkitsoft.beepvending.databinding.ActivityMainBinding;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfigActivity extends BaseActivity {


    Button save;
    Button cancel;
    Button setting;

    ActivityConfigBinding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityConfigBinding.inflate(getLayoutInflater());
        View view = Binding.getRoot();
        setContentView(view);






        clickListener();

    }

    private void clickListener() {

        Binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        Binding.buttonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void showdialog(String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(this, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }


}