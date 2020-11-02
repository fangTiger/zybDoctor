package com.zuojianyou.zybdoctor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.beans.RecipeInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 个人处方
 */
public class PersonalRecipeActivity extends BaseActivity {

    ListView lvRecipe;
    List<RecipeInfo> recipeList;
    RecipeAdapter adapter;
    String cnName,enName,sickId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_recipe);
        cnName = getIntent().getStringExtra("cnName");
        enName = getIntent().getStringExtra("enName");
        sickId = getIntent().getStringExtra("sickId");
        findViewById(R.id.btn_personal_recipe_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.btn_personal_recipe_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecipeAddActivity.class);
                intent.putExtra("cnName", cnName);
                intent.putExtra("enName", enName);
                intent.putExtra("sickId", sickId);
                startActivityForResult(intent, 123);
            }
        });

        lvRecipe = findViewById(R.id.lv_personal_recipe);
        View empty = findViewById(R.id.tv_personal_recipe_empty);
        lvRecipe.setEmptyView(empty);
        adapter = new RecipeAdapter();
        lvRecipe.setAdapter(adapter);
        lvRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                RecipeInfo recipe = (RecipeInfo) adapter.getItem(position);
                data.putExtra("medicines", JSONObject.toJSONString(recipe));
                setResult(RESULT_OK, data);
                finish();
            }
        });

        httpGetRecipe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            httpGetRecipe();
        }
    }

    class RecipeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recipeList == null ? 0 : recipeList.size();
        }

        @Override
        public Object getItem(int position) {
            return recipeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_personal_recipe_list, parent, false);
            TextView name = view.findViewById(R.id.name);
            TextView cnName = view.findViewById(R.id.cn_name);
            TextView enName = view.findViewById(R.id.en_name);
            TextView content = view.findViewById(R.id.content);
            TextView time = view.findViewById(R.id.time);
            ImageView btnDel = view.findViewById(R.id.btn_del);
            RecipeInfo recipe = recipeList.get(position);
            name.setText(recipe.getName());
            cnName.setText(recipe.getSickName());
            enName.setText(recipe.getWestern());
            content.setText(getContent(recipe.getMedList()));
            time.setText(recipe.getCrtTime());
            btnDel.setTag(position);
            btnDel.setOnClickListener(btnDelClick);
            convertView = view;
            return convertView;
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

    private void httpGetRecipe() {
        showLoadView();
        String url = ServerAPI.getRecipeUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", "1000");
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject json = JSONObject.parseObject(data);
                recipeList = json.getJSONArray("list").toJavaList(RecipeInfo.class);
                adapter.notifyDataSetChanged();
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

    private void httpDelRecipe(final int position) {
        showLoadView();
        String url = ServerAPI.getRecipeDelUrl(recipeList.get(position).getRepiceId());
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().request(HttpMethod.DELETE, entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                recipeList.remove(position);
                adapter.notifyDataSetChanged();
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
