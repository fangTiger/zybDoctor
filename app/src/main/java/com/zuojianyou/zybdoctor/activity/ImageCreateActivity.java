package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.views.ImageCutView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.Nullable;

public class ImageCreateActivity extends BaseActivity {

    Bitmap bitmap;
    ImageCutView imageCutView;
    int orientationDegree = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_create);

        findViewById(R.id.ib_act_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String path = getIntent().getStringExtra("imgPath");
        bitmap = BitmapFactory.decodeFile(path);
        imageCutView = findViewById(R.id.icv_act_sign_create);
        float aspectRatio = getIntent().getFloatExtra("aspectRatio", 1);
        imageCutView.setAspectRatio(aspectRatio);
        imageCutView.setImageBitmap(bitmap);
        findViewById(R.id.btn_act_sign_create_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationDegree += 90;
                Matrix m = new Matrix();
                m.setRotate(orientationDegree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                imageCutView.setImageBitmap(bm);
            }
        });
        findViewById(R.id.btn_act_sign_create_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationDegree = 0;
                imageCutView.setImageBitmap(bitmap);
                imageCutView.reSetCutBox();
            }
        });
        findViewById(R.id.btn_act_sign_create_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap mp = imageCutView.getImageBitmap();
                String path = getFilesDir() + File.separator + "images" + File.separator;
                File dirFile = new File(path);
                if (!dirFile.exists()) dirFile.mkdirs();
                File file = new File(path, System.currentTimeMillis() + ".jpeg");
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    mp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("imagePath", file.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
