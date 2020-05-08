package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.zuojianyou.zybdoctor.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.AlertDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }
}
