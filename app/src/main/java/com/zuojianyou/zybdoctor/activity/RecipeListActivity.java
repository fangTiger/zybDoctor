package com.zuojianyou.zybdoctor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.beans.RecipeInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.TimeUtils;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class RecipeListActivity extends BaseListActivity {

    String cnName, enName, sickId;
    EditText etKeyWord;
    ImageButton btnSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("个人处方库");
        cnName = getIntent().getStringExtra("cnName");
        enName = getIntent().getStringExtra("enName");
        sickId = getIntent().getStringExtra("sickId");

        ImageButton ivMenu = findViewById(R.id.ib_act_base_list_menu);
        ivMenu.setImageResource(R.mipmap.ic_create_recipe);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecipeAddActivity.class);
                intent.putExtra("cnName", cnName);
                intent.putExtra("enName", enName);
                intent.putExtra("sickId", sickId);
                startActivityForResult(intent, 123);
            }
        });

        LinearLayout contentView = findViewById(R.id.ll_act_base_list_content_view);
        View headerView = getLayoutInflater().inflate(R.layout.activity_recipe_list_header, null);
        contentView.addView(headerView, 1);

        etKeyWord = findViewById(R.id.et_act_recipe_list_key);
        etKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSearch.setImageResource(R.mipmap.ic_common_btn_search);
                btnSearch.setTag("search");
            }
        });
        btnSearch = findViewById(R.id.btn_act_recipe_list_search);
        btnSearch.setTag("search");
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (tag.equals("search") && !TextUtils.isEmpty(etKeyWord.getText())) {
                    btnSearch.setImageResource(R.mipmap.ic_common_btn_delete);
                    btnSearch.setTag("delete");
                } else {
                    etKeyWord.setText(null);
                    btnSearch.setImageResource(R.mipmap.ic_common_btn_search);
                    btnSearch.setTag("search");
                }
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(etKeyWord.getText())) {
                    jsonObject.put("keyWd", etKeyWord.getText().toString().trim());
                }
                baseListObject.requestBody = jsonObject;
                httpGetList(true);
            }
        });

        LinearLayout llListHeader = findViewById(R.id.ll_act_recipe_list_header);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(48, 0, 48, 0);
        llListHeader.setLayoutParams(layoutParams);
        llListHeader.setPadding(0,12,0,12);
        recyclerView.setPadding(48, 0, 48, 0);
    }

    @Override
    public BaseListObject createBaseListObject() {
        baseListObject.requestUrl = ServerAPI.getRecipeUrl();
        baseListObject.clazz = RecipeInfo.class;
        baseListObject.itemDecoration = itemDecoration;
        return baseListObject;
    }

    BaseListObject baseListObject = new BaseListObject() {
        @Override
        protected RecyclerView.LayoutManager createLayoutManager() {
            return new LinearLayoutManager(getContext());
        }

        @Override
        protected RecyclerView.Adapter createAdapter() {
            return new MyAdapter();
        }
    };

    @Override
    public void onRequestOk(String data) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            httpGetList(true);
        }
    }

    RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);
            Paint mPaint = new Paint();
            mPaint.setARGB(255, 235, 235, 235);
            int childCount = parent.getChildCount();
            // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
            for (int i = 0; i < childCount; i++) {
                // 获取每个Item的位置
                final View child = parent.getChildAt(i);
                // 设置矩形(分割线)的宽度为1px
                final int mDivider = 1;
                // 矩形左上顶点 = (ItemView的左边界,ItemView的下边界)
                final int left = child.getLeft();
                final int top = child.getBottom();
                // 矩形右下顶点 = (ItemView的右边界,矩形的下边界)
                final int right = child.getRight();
                final int bottom = top + mDivider;
                // 通过Canvas绘制矩形（分割线）
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = 1;
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_personal_recipe_list, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            if (i % 2 == 0) {
                myHolder.itemView.setBackgroundColor(0xffffffff);
            } else {
                myHolder.itemView.setBackgroundColor(0xfffdfdfd);
            }
            RecipeInfo recipe = (RecipeInfo) baseListObject.list.get(i);
            myHolder.name.setText(recipe.getName());
            myHolder.cnName.setText(recipe.getSickName());
            myHolder.enName.setText(recipe.getWestern());
            myHolder.content.setText(getContent(recipe.getMedList()));
            myHolder.time.setText(TimeUtils.toNormMin(recipe.getCrtTime()));
            myHolder.btnDel.setTag(i);
            myHolder.btnDel.setOnClickListener(btnDelClick);
            myHolder.itemView.setTag(i);
            myHolder.itemView.setOnClickListener(onItemClick);
        }

        @Override
        public int getItemCount() {
            return baseListObject.list == null ? 0 : baseListObject.list.size();
        }

        private String getContent(List<MedicineInfo> list) {
            StringBuilder sb = new StringBuilder();
            if (list != null && list.size() > 0) {
                for (MedicineInfo med : list) {
                    sb.append(med.getGdName());
                    sb.append(med.getUseNum());
                    sb.append(med.getMedUnit());
                    sb.append(";");
                }
            }
            return sb.toString();
        }
    }

    View.OnClickListener btnDelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (Integer) v.getTag();
            new AlertDialog.Builder(getContext())
                    .setTitle("提示").setMessage("确定删除？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            httpDelRecipe(position);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    };

    View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Intent data = new Intent();
            RecipeInfo recipe = (RecipeInfo) baseListObject.list.get(position);
            data.putExtra("medicines", JSONObject.toJSONString(recipe));
            setResult(RESULT_OK, data);
            finish();
        }
    };

    class MyHolder extends RecyclerView.ViewHolder {

        TextView name, cnName, enName, content, time;
        ImageView btnDel;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            cnName = itemView.findViewById(R.id.cn_name);
            enName = itemView.findViewById(R.id.en_name);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            btnDel = itemView.findViewById(R.id.btn_del);
        }
    }

    private void httpDelRecipe(final int position) {
        showLoadView();
        RecipeInfo recipeInfo = (RecipeInfo) baseListObject.list.get(position);
        String url = ServerAPI.getRecipeDelUrl(recipeInfo.getRepiceId());
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().request(HttpMethod.DELETE, entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                baseListObject.list.remove(position);
                baseListObject.adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "删除成功!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }
        }));
    }
}
