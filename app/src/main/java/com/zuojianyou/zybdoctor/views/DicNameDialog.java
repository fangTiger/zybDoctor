package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.treat.DicSick;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 问诊中图片名字选择
 */
public class DicNameDialog extends Dialog {

    String imgPath;
    String id;//挂号id用于上传图片
    List<DicSick> list;
    OnNameSelectListener onNameSelectListener;

    String resultPath = null;

    public DicNameDialog(Context context, String imgPath, String id, List<DicSick> list) {
        super(context, R.style.AlertDialog);
        this.imgPath = imgPath;
        this.list = list;
        this.id = id;
    }

    public void setOnNameSelectListener(OnNameSelectListener listener) {
        onNameSelectListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dic_name_select);

        ImageView imageView = findViewById(R.id.iv_img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
        ListView listView = findViewById(R.id.lv_position_name);
        listView.setAdapter(new NameAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (resultPath == null) {
                    Toast.makeText(getContext(), "图片上传地址未返回！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onNameSelectListener != null) {
                    onNameSelectListener.onNameSelected(list.get(position),resultPath);
                    dismiss();
                }
            }
        });

        httpImageUpload();
    }

    class NameAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setPadding(12, 12, 12, 12);
            textView.setText(list.get(position).getDataName());
            convertView = textView;
            return convertView;
        }
    }

    public interface OnNameSelectListener {
        void onNameSelected(DicSick dicSick,String resultPath);
    }

    private void httpImageUpload() {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        File file = new File(imgPath);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", id));
        mList.add(new KeyValue("path", "inquiry/images"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("httpImageUpload", result);
                findViewById(R.id.ll_progress).setVisibility(View.GONE);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    resultPath = jsonObject.getJSONObject("data").getString("url");
                } else {
                    Toast.makeText(getContext(), jsonObject.getString("errMsg"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("httpImageUpload", ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                findViewById(R.id.ll_progress).setVisibility(View.GONE);
            }

            @Override
            public void onWaiting() {
                findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d("httpImageUpload", current + "/" + total);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                long progress = current * 100 / total;
                progressBar.setProgress((int) progress);
            }
        });


    }
}
