package com.linkitsoft.beepvending.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.linkitsoft.beepvending.Helper.UIHelper;
import com.linkitsoft.beepvending.R;
import com.linkitsoft.beepvending.Utils.LocalDataManager;
import com.linkitsoft.beepvending.databinding.ActivityMainBinding;
import com.linkitsoft.beepvending.databinding.PinPopupBinding;
import com.linkitsoft.beepvending.databinding.ThankyouLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View core_view;
    List<SlideModel> imageList = new ArrayList<SlideModel>();
    String video_path = null;
    int count = 0;

    Button btnSubmit, btnCancel;
    EditText etPin;
    String pin = "9856";


    ActivityMainBinding activityMainBinding;

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

    ImageButton start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        core_view = getWindow().getDecorView();

        LocalDataManager.createInstance(this);

//        **************************************Storage Permission**************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
//        **************************************Storage Permission**************************************************************



        video_path = Environment.getExternalStorageDirectory().getPath() + "/Advideo/vendtrix.mp4";
        //video_path = Environment.getExternalStorageDirectory().getPath() + "/GestureVideo/2 video.mp4";

        imageList.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));

        activityMainBinding.videoImageSlider.setImageList(imageList);
//        videoPlay();


        core_view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    core_view.setSystemUiVisibility(hide_system_bars());
                }
            }
        });

        clickListener();



    }

    private void clickListener() {

        activityMainBinding.mainlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count ==7)
                {
                    showPinDialog();
                    count =0;
                }
                else
                {
                    count++;
                }
            }
        });

        activityMainBinding.btntouchtoBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent next = new Intent(MainActivity.this, SelectProduct.class);
                startActivity(next);
            }
        });

    }

    private void showPinDialog() {

        PinPopupBinding pinPopupBinding;

        final Dialog pinDialog = new Dialog(this);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        pinPopupBinding = PinPopupBinding.inflate(getLayoutInflater());
        View view = pinPopupBinding.getRoot();
        pinDialog.setContentView(view);



        pinPopupBinding.btncancalPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinDialog.dismiss();
            }
        });

        pinPopupBinding.btnSubmitPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pinPopupBinding.pass.getText().toString().length() > 0 && !pinPopupBinding.pass.getText().toString().equals("")){
                    if (pinPopupBinding.pass.getText().toString().equals(pin)){
                        pinDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        finish();
                    }else {
                        UIHelper.showErrorDialog(MainActivity.this,getString(R.string.error),"Invalid pin",1);
                    }
                }else {
                    UIHelper.showErrorDialog(MainActivity.this,getString(R.string.error),"Pin is required",1);
                }
            }
        });


        pinDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        pinDialog.setCancelable(false);
        pinDialog.show();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       // videoPlay();
    }
}