package com.zuojianyou.zybdoctor.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zuojianyou.zybdoctor.R;

public class SexSelector {

    private View contentView;
    private PopupWindow popupWindow;
    private Context context;
    private TextView textView;
    private View btnClose, btnMan, btnWoman, btnSpecial;
    private boolean isShowSpecial;

    public SexSelector(TextView textView, boolean isShowSpecial) {
        this.isShowSpecial = isShowSpecial;
        this.context = textView.getContext();
        this.textView = textView;
        contentView = LayoutInflater.from(context).inflate(R.layout.popup_sex_selector, null);
        init();
    }

    public void showSelector() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView, -1, -1, true);
        }
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(textView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void init() {
        btnClose = contentView.findViewById(R.id.btn_cancel);
        btnClose.setOnClickListener(onMenuClick);

        btnMan = contentView.findViewById(R.id.btn_sex_man);
        btnMan.setOnClickListener(onMenuClick);
        btnWoman = contentView.findViewById(R.id.btn_sex_woman);
        btnWoman.setOnClickListener(onMenuClick);
        btnSpecial = contentView.findViewById(R.id.btn_sex_special);
        if (isShowSpecial) {
            btnSpecial.setOnClickListener(onMenuClick);
        } else {
            btnSpecial.setVisibility(View.GONE);
        }

    }

    View.OnClickListener onMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_sex_man:
                    textView.setText("男");
                    textView.setTag(1);
                    break;
                case R.id.btn_sex_woman:
                    textView.setText("女");
                    textView.setTag(2);
                    break;
                case R.id.btn_sex_special:
                    textView.setText("保密");
                    textView.setTag(3);
                    break;
            }
            popupWindow.dismiss();
        }
    };
}
