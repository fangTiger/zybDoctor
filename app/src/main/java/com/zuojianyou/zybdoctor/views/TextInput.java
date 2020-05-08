package com.zuojianyou.zybdoctor.views;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zuojianyou.zybdoctor.R;

public class TextInput {

    private View contentView;
    private PopupWindow popupWindow;
    private TextView textView;

    private TextView tvTitle, tvTip, tvNum;
    private EditText etInput;

    private int maxLength;
    private String title;

    public TextInput(String title, TextView textView) {
        this.title = title;
        this.textView = textView;
        contentView = LayoutInflater.from(textView.getContext()).inflate(R.layout.popup_input_text, null);
        init();
    }

    private void init() {
        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(etInput.getText());
                popupWindow.dismiss();
            }
        });

        tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvTip = contentView.findViewById(R.id.tv_tip);
        tvNum = contentView.findViewById(R.id.tv_input_num);
        etInput = contentView.findViewById(R.id.et_input_text);
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setInputType(int inputType) {
        etInput.setInputType(inputType);
    }

    public void setMaxLength(int max) {
        this.maxLength = max;
        tvNum.setText(String.valueOf(maxLength));
        InputFilter[] filters = {new InputFilter.LengthFilter(max)};
        etInput.setFilters(filters);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNum.setText(String.valueOf(maxLength - s.length()));
            }
        });
    }

    public void setTip(String tip) {
        tvTip.setText(tip);
    }

    public void show() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView, -1, -1, true);
        }
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(textView.getRootView(), Gravity.CENTER, 0, 0);
    }
}
