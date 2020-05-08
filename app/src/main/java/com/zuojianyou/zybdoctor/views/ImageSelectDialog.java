package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.activity.ArticleCreateActivity;

public class ImageSelectDialog extends Dialog {

    OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ImageSelectDialog(Context context) {
        super(context, R.style.AlertDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_select);
        findViewById(R.id.btn_dialog_image_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.onCancelClick();
                }
                dismiss();
            }
        });

        findViewById(R.id.btn_dialog_image_select_glide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.onGlideClick();
                } else {
                    Intent intent = new Intent(getContext(), ArticleCreateActivity.class);
                    intent.putExtra("type", 1);
                    getContext().startActivity(intent);
                }
                dismiss();
            }
        });

        findViewById(R.id.btn_dialog_image_select_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.onCameraClick();
                } else {
                    Intent intent = new Intent(getContext(), ArticleCreateActivity.class);
                    intent.putExtra("type", 2);
                    getContext().startActivity(intent);
                }
                dismiss();
            }
        });
    }

    public interface OnItemClick {
        void onCancelClick();

        void onCameraClick();

        void onGlideClick();
    }
}
