package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;

public class ImageGlideDialog extends Dialog {

    String path, name;

    public ImageGlideDialog(Context context, String path, String name) {
        super(context, R.style.AlertDialog);
        this.path = path;
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_glide);
        findViewById(R.id.ib_image_glide_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tvTitle = findViewById(R.id.tv_image_glide_title);
        tvTitle.setText(name);
        PhotoView ivPhoto = findViewById(R.id.iv_image_glide_photo);
        ivPhoto.enable();
        ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivPhoto.setMaxScale(2f);
        Glide.with(getContext()).load(path).into(ivPhoto);
    }
}
