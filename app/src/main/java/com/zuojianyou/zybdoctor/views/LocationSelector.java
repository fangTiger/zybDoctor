package com.zuojianyou.zybdoctor.views;

import android.content.Context;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.AreaInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LocationSelector {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_NO_COUNTRY = 1;
    public static final int TYPE_NO_AREA = 2;

    private int type;
    private View contentView;
    private PopupWindow popupWindow;
    private Context context;
    private TextView textView;
    private View btnClose;
    private RadioButton rbCountry, rbProvince, rbCity, rbArea;
    private RecyclerView rvArea;
    private LocationAdapter adapter;

    List<AreaInfo> countryList;
    List<AreaInfo> provinceList;
    List<AreaInfo> cityList;
    List<AreaInfo> areaList;

    private int countryId, provinceId, cityId, areaId;

    public LocationSelector(TextView textView, int type) {
        this.context = textView.getContext();
        this.textView = textView;
        this.type = type;
        contentView = LayoutInflater.from(context).inflate(R.layout.popup_location_selector, null);
        init();
    }

    public LocationSelector(TextView textView) {
        this(textView, TYPE_NORMAL);
    }

    public void showSelector() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView, -1, -1, true);
        }
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(textView.getRootView(), Gravity.CENTER, 0, 0);
        if (textView.getTag(R.id.tag_country_id) != null) {
            countryId = getCityIdByTag(R.id.tag_country_id);
            rbCountry.setText(getCityByCode(countryId, countryList));
        }
        if (textView.getTag(R.id.tag_province_id) != null) {
            provinceId = getCityIdByTag(R.id.tag_province_id);
            rbProvince.setText(getCityByCode(provinceId, provinceList));
        }
        if (textView.getTag(R.id.tag_city_id) != null) {
            cityId = getCityIdByTag(R.id.tag_city_id);
            rbCity.setText(getCityByCode(cityId, cityList));
        }
        if (textView.getTag(R.id.tag_area_id) != null) {
            areaId = getCityIdByTag(R.id.tag_area_id);
            rbArea.setText(getCityByCode(areaId, areaList));
        }
    }

    public void locationOver() {
        StringBuffer sb = new StringBuffer();
        if (type != TYPE_NO_COUNTRY && countryId != 0) {
            sb.append(rbCountry.getText().toString());
        }
        if (provinceId != 0) {
            sb.append(rbProvince.getText().toString());
        }
        if (cityId != 0) {
            sb.append(rbCity.getText().toString());
        }
        if (type != TYPE_NO_AREA && areaId != 0) {
            sb.append(rbArea.getText().toString());
        }
        textView.setText(sb.toString());
        textView.setTag(R.id.tag_country_id, countryId);
        textView.setTag(R.id.tag_province_id, provinceId);
        textView.setTag(R.id.tag_city_id, cityId);
        textView.setTag(R.id.tag_area_id, areaId);
    }

    private void init() {
        cityList = JSONObject.parseArray(getAreaString("city.json"), AreaInfo.class);
        countryList = JSONObject.parseArray(getAreaString("country.json"), AreaInfo.class);
        provinceList = JSONObject.parseArray(getAreaString("province.json"), AreaInfo.class);
        areaList = JSONObject.parseArray(getAreaString("area.json"), AreaInfo.class);

        btnClose = contentView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rbCountry = contentView.findViewById(R.id.rb_country);
        rbCountry.setOnClickListener(onMenuClick);
        rbProvince = contentView.findViewById(R.id.rb_province);
        rbProvince.setOnClickListener(onMenuClick);
        rbCity = contentView.findViewById(R.id.rb_city);
        rbCity.setOnClickListener(onMenuClick);
        rbArea = contentView.findViewById(R.id.rb_area);
        rbArea.setOnClickListener(onMenuClick);
        rvArea = contentView.findViewById(R.id.rv_popup_location);
        rvArea.setLayoutManager(new LinearLayoutManager(context));
        adapter = new LocationAdapter();
        rvArea.setAdapter(adapter);

        if (type == TYPE_NO_COUNTRY) {
            rbCountry.setVisibility(View.GONE);
            countryId = 156;
            rbProvince.setText("请选择");
            rbProvince.performClick();
        } else if (type == TYPE_NO_AREA) {
            rbArea.setVisibility(View.GONE);
            rbCountry.setText("请选择");
            rbCountry.performClick();
        } else {
            rbCountry.setText("请选择");
            rbCountry.performClick();
        }

    }

    private int getCityIdByTag(int tagId) {
        int id;
        if (textView.getTag(tagId) instanceof String) {
            String tag = (String) textView.getTag(tagId);
            id = Integer.parseInt(tag);
        } else {
            id = (Integer) textView.getTag(tagId);
        }
        return id;
    }

    private String getCityByCode(int id, List<AreaInfo> list) {
        for (AreaInfo areaInfo : list) {
            if (areaInfo.getAreaId() == id) {
                return areaInfo.getAreaName();
            }
        }
        return null;
    }

    View.OnClickListener onMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextPaint tp = null;
            switch (v.getId()) {
                case R.id.rb_country:
                    tp = rbCountry.getPaint();
                    tp.setFakeBoldText(true);
                    tp = rbProvince.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbCity.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbArea.getPaint();
                    tp.setFakeBoldText(false);
                    adapter.setAreaInfos(countryList);
                    break;
                case R.id.rb_province:
                    tp = rbCountry.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbProvince.getPaint();
                    tp.setFakeBoldText(true);
                    tp = rbCity.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbArea.getPaint();
                    tp.setFakeBoldText(false);
                    adapter.setAreaInfos(provinceList);
                    break;
                case R.id.rb_city:
                    tp = rbCountry.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbProvince.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbCity.getPaint();
                    tp.setFakeBoldText(true);
                    tp = rbArea.getPaint();
                    tp.setFakeBoldText(false);
                    List<AreaInfo> regionList = new ArrayList<>();
                    for (AreaInfo region : cityList) {
                        if (region.getAreaParent() == provinceId) {
                            regionList.add(region);
                        }
                    }
                    adapter.setAreaInfos(regionList);
                    break;
                case R.id.rb_area:
                    tp = rbCountry.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbProvince.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbCity.getPaint();
                    tp.setFakeBoldText(false);
                    tp = rbArea.getPaint();
                    tp.setFakeBoldText(true);
                    List<AreaInfo> regionList2 = new ArrayList<>();
                    for (AreaInfo region : areaList) {
                        if (region.getAreaParent() == cityId) {
                            regionList2.add(region);
                        }
                    }
                    adapter.setAreaInfos(regionList2);
                    break;
            }
        }
    };

    class LocationAdapter extends RecyclerView.Adapter<ItemHolder> {

        List<AreaInfo> areaInfos;

        public void setAreaInfos(List<AreaInfo> areaInfos) {
            this.areaInfos = areaInfos;
            notifyDataSetChanged();
        }

        @NonNull
        @Override

        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(context);
            textView.setPadding(12, 12, 12, 12);
            return new ItemHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            AreaInfo areaInfo = areaInfos.get(position);
            holder.textView.setText(areaInfo.getAreaName());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (areaInfo.getAreaType()) {
                        case 0:
                            rbCountry.setText(areaInfo.getAreaName());
                            countryId = areaInfo.getAreaId();
                            provinceId = 0;
                            cityId = 0;
                            if (areaInfo.getAreaId() == 156) {
                                rbProvince.setText("请选择");
                                rbCity.setText(null);
                                rbProvince.performClick();
                            } else {
                                locationOver();
                                popupWindow.dismiss();
                            }
                            break;
                        case 1:
                            rbProvince.setText(areaInfo.getAreaName());
                            provinceId = areaInfo.getAreaId();
                            cityId = 0;
                            rbCity.setText("请选择");
                            rbCity.performClick();
                            break;
                        case 2:
                            rbCity.setText(areaInfo.getAreaName());
                            cityId = areaInfo.getAreaId();
                            if (type != TYPE_NO_AREA) {
                                rbArea.setText("请选择");
                                rbArea.performClick();
                            } else {
                                locationOver();
                                popupWindow.dismiss();
                            }
                            break;
                        case 3:
                            rbArea.setText(areaInfo.getAreaName());
                            areaId = areaInfo.getAreaId();
                            locationOver();
                            popupWindow.dismiss();
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return areaInfos == null ? 0 : areaInfos.size();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    private String getAreaString(String fileName) {
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String jsonLine;
            while ((jsonLine = reader.readLine()) != null) {
                builder.append(jsonLine);
            }
            reader.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }


}
