package com.linkitsoft.beepvending.Helper;

import android.content.Context;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UIHelper {

    public static void showErrorDialog(Context context,String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }


    public static void showSuccessDialog(Context context,String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }

    public static Toast showLongToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }
}
