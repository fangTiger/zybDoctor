package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.treat.DicSick;

import java.util.List;

/**
 * 指标
 */
public class DicRuleDialog extends Dialog {

    private TextView etShow;
    private List<DicSick> list;
    private LinearLayout llContent;

    public DicRuleDialog(Context context, List<DicSick> list, TextView etShow) {
        super(context, R.style.AlertDialog);
        this.etShow = etShow;
        this.list = list;

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_treat_rule);

        findViewById(R.id.btn_dialog_treat_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShow.setText(getInputText());
                dismiss();
            }
        });
        findViewById(R.id.btn_dialog_treat_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        llContent = findViewById(R.id.ll_dialog_treat_content);
        for (int i = 0; i < list.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.item_dialog_treat_rule, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
            layoutParams.setMargins(0, 40, 0, 0);
            view.setLayoutParams(layoutParams);
            TextView tvTag = view.findViewById(R.id.tv_dialog_treat_item_title);
            tvTag.setText(list.get(i).getDataName() + ":");
            llContent.addView(view);
        }
    }

    private String getInputText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            View view = llContent.getChildAt(i);
            EditText etContent = view.findViewById(R.id.et_dialog_treat_item_content);
            if (!TextUtils.isEmpty(etContent.getText())) {
                sb.append(list.get(i).getDataName());
                sb.append(":");
                sb.append(etContent.getText().toString().trim());
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
