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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View core_view;
    private ImageSlider videoImageSlider;
    List<SlideModel> imageList = new ArrayList<SlideModel>();
    String video_path = null;
    ArrayList<String> video = new ArrayList<>();
    VideoView bannerImageVideo;
    ImageView mainlogo;
    int count = 0;

    Button btnSubmit, btnCancel;
    EditText etPin;
    String pin = "9856";

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
        setContentView(R.layout.activity_main);

        core_view = getWindow().getDecorView();
        start = findViewById(R.id.imageButton);
        videoImageSlider = findViewById(R.id.videoImageSlider);
        mainlogo = findViewById(R.id.mainlogo);

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

        videoImageSlider.setImageList(imageList);
//        videoPlay();


        mainlogo.setOnClickListener(new View.OnClickListener() {
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



        core_view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    core_view.setSystemUiVisibility(hide_system_bars());
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent next = new Intent(MainActivity.this, SelectProduct.class);
                startActivity(next);
            }
        });
    }

    private void videoPlay(){
        try {

            final MediaController mediaController = new MediaController(MainActivity.this);
            bannerImageVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                            mediaController.setAnchorView(bannerImageVideo);
                            mediaPlayer.setLooping(true);
                            mediaPlayer.setScreenOnWhilePlaying(true);
                        }
                    });
                }
            });
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                bannerImageVideo.setVideoPath(video_path);
                bannerImageVideo.start();
                bannerImageVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.d("video", "setOnErrorListener ");
                        return true;
                    }
                });
                bannerImageVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        bannerImageVideo.start();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showPinDialog() {

        final Dialog pinDialog = new Dialog(this);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setContentView(R.layout.pin_popup);
        pinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        etPin = pinDialog.findViewById(R.id.pass);
        btnSubmit = pinDialog.findViewById(R.id.button7);
        btnCancel = pinDialog.findViewById(R.id.button6);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinDialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPin.getText().toString().length() > 0 && !etPin.getText().toString().equals("")){
                    if (etPin.getText().toString().equals(pin)){
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