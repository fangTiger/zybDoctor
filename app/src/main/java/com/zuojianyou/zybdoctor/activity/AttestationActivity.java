package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.DocAttInfo;
import com.zuojianyou.zybdoctor.beans.DocProfessInfo;
import com.zuojianyou.zybdoctor.beans.GoodAtInfo;
import com.zuojianyou.zybdoctor.beans.GoodAtOfficeInfo;
import com.zuojianyou.zybdoctor.constants.BroadcastAction;
import com.zuojianyou.zybdoctor.units.FileUtils;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.ToastUtils;
import com.zuojianyou.zybdoctor.views.ImageGlideDialog;
import com.zuojianyou.zybdoctor.views.LocationSelector;
import com.zuojianyou.zybdoctor.views.ScrollFlexBoxManager;
import com.zuojianyou.zybdoctor.views.SexSelector;
import com.zuojianyou.zybdoctor.views.TextInput;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AttestationActivity extends BaseActivity {

    final int CODE_SIGN_CREATE = 300;
    final int CODE_PHOTO_CREATE = 400;

    Button btnConfirm;
    View firstPageContainer, secondPageContainer;

    RecyclerView rvZYZS, rvZGZS, rvZCZS, rvQTZS, rvIntro;
    CertificateAdapter adapterZYZS, adapterZGZS, adapterZCZS, adapterQTZS, adapterIntro;
    TextView tvLocation, tvSex;

    LocationSelector locationSelector;
    SexSelector sexSelector;
    EditText docWorkNameEt;
    TextView tvDocName, tvDocBirthDay, tvProfess, tvGoodAt, tvDocWorkAdd, tvDocIntro, workText;
    RecyclerView rvGoodAt;
    GoodAtAdapter goodAtAdapter;

    final int IMAGE_TYPE_DOC_PHOTO = 1;
    final int IMAGE_TYPE_CARD_FRONT = 2;
    final int IMAGE_TYPE_CARD_BACK = 3;
    final int IMAGE_TYPE_ZYZS = 4;
    final int IMAGE_TYPE_ZGZS = 5;
    final int IMAGE_TYPE_ZCZS = 6;
    final int IMAGE_TYPE_QTZS = 8;
    final int IMAGE_TYPE_DOC_SIGN = 7;
    final int IMAGE_TYPE_INTRO = 9;

    ImageView ivDocPhoto, ivDocSign, ivCardFront, ivCardBack;
    String docPhotoUrl, docSignUrl, cardFrontUrl, cardBackUrl;
    int imageType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attestation);
        findViewById(R.id.ib_act_base_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        firstPageContainer = findViewById(R.id.ll_first_page_container);
        secondPageContainer = findViewById(R.id.ll_second_page_container);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secondPageContainer.getVisibility() != View.VISIBLE) {
                    if (checkFirstPage()) {
                        firstPageContainer.setVisibility(View.GONE);
                        secondPageContainer.setVisibility(View.VISIBLE);
                        btnConfirm.setText("提交");
                    }
                } else {
                    if (checkSecondPage()) {
                        //submit
                        httpSubmit();
                    }
                }
            }
        });

        rvZYZS = findViewById(R.id.rv_act_att_zyzs);
        rvZYZS.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterZYZS = new CertificateAdapter(IMAGE_TYPE_ZYZS);
        rvZYZS.setAdapter(adapterZYZS);

        rvZGZS = findViewById(R.id.rv_act_att_zgzs);
        rvZGZS.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterZGZS = new CertificateAdapter(IMAGE_TYPE_ZGZS);
        rvZGZS.setAdapter(adapterZGZS);

        rvZCZS = findViewById(R.id.rv_act_att_zczs);
        rvZCZS.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterZCZS = new CertificateAdapter(IMAGE_TYPE_ZCZS);
        rvZCZS.setAdapter(adapterZCZS);

        rvQTZS = findViewById(R.id.rv_act_att_qtzs);
        rvQTZS.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterQTZS = new CertificateAdapter(IMAGE_TYPE_QTZS);
        rvQTZS.setAdapter(adapterQTZS);

        rvIntro = findViewById(R.id.rv_act_att_intro);
        rvIntro.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterIntro = new CertificateAdapter(IMAGE_TYPE_INTRO);
        rvIntro.setAdapter(adapterIntro);

        tvLocation = findViewById(R.id.tv_doc_position);
        locationSelector = new LocationSelector(tvLocation, LocationSelector.TYPE_NO_COUNTRY);
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSelector.showSelector();
            }
        });

        tvSex = findViewById(R.id.tv_doc_sex);
        sexSelector = new SexSelector(tvSex, false);
        tvSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexSelector.showSelector();
            }
        });

        docWorkNameEt = findViewById(R.id.doc_work_name_et);
        workText = findViewById(R.id.work_text);
        SpannableStringBuilder ss = new SpannableStringBuilder("请填写主要执业机构名称");
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_tag_marked_yellow)),3,11, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        workText.setText(ss);
        tvDocName = findViewById(R.id.tv_doc_name);
        tvDocName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("医师姓名", tvDocName);
                textInput.setMaxLength(6);
                textInput.show();
            }
        });

        tvDocWorkAdd = findViewById(R.id.tv_doc_work_add);
        tvDocWorkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("主要执业地点", tvDocWorkAdd);
                textInput.setMaxLength(30);
                textInput.show();
            }
        });
        tvDocWorkAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tvDocWorkAdd.setText(R.string.doc_work_add_example);
                    tvDocWorkAdd.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
                } else {
                    tvDocWorkAdd.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
                }
            }
        });

        tvDocIntro = findViewById(R.id.tv_doc_intro);
        tvDocIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("简介", tvDocIntro);
                textInput.setMaxLength(300);
                textInput.show();
            }
        });

        tvDocBirthDay = findViewById(R.id.tv_doc_birthday);
        tvDocBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        tvProfess = findViewById(R.id.tv_doc_profess);
        tvProfess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupProfess();
            }
        });

        tvGoodAt = findViewById(R.id.tv_doc_good_at);
        tvGoodAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupGoodAt();
            }
        });
        rvGoodAt = findViewById(R.id.rv_act_att_good_at);
        rvGoodAt.setNestedScrollingEnabled(false);
        rvGoodAt.setHasFixedSize(true);
//        FlexboxLayoutManager flm = new FlexboxLayoutManager(getContext());
//        flm.setFlexDirection(FlexDirection.ROW);
//        flm.setFlexWrap(FlexWrap.WRAP);
//        flm.setAlignItems(AlignItems.STRETCH);
//        flm.setJustifyContent(JustifyContent.FLEX_START);
        LinearLayoutManager flm = new LinearLayoutManager(getContext());
        flm.setOrientation(RecyclerView.VERTICAL);
        rvGoodAt.setLayoutManager(flm);
        goodAtAdapter = new GoodAtAdapter();
        rvGoodAt.setAdapter(goodAtAdapter);
        itemTouchHelper.attachToRecyclerView(rvGoodAt);

        ivDocPhoto = findViewById(R.id.iv_doc_photo);
        ivDocSign = findViewById(R.id.iv_doc_sign);
        ivCardFront = findViewById(R.id.iv_card_photo_front);
        ivCardBack = findViewById(R.id.iv_card_photo_back);

        ivDocPhoto.setOnClickListener(onImageClicked);
        ivDocSign.setOnClickListener(onImageClicked);
        ivCardFront.setOnClickListener(onImageClicked);
        ivCardBack.setOnClickListener(onImageClicked);

        httpGetAuthInfo();
    }

    View.OnClickListener onImageClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_doc_photo:
                    showImageSelect(IMAGE_TYPE_DOC_PHOTO);
                    break;
                case R.id.iv_doc_sign:
                    showImageSelect(IMAGE_TYPE_DOC_SIGN);
                    break;
                case R.id.iv_card_photo_front:
                    showImageSelect(IMAGE_TYPE_CARD_FRONT);
                    break;
                case R.id.iv_card_photo_back:
                    showImageSelect(IMAGE_TYPE_CARD_BACK);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (firstPageContainer.getVisibility() != View.VISIBLE) {
            firstPageContainer.setVisibility(View.VISIBLE);
            secondPageContainer.setVisibility(View.GONE);
            btnConfirm.setText("下一步");
            return;
        }
        super.onBackPressed();
    }

    class CertificateAdapter extends RecyclerView.Adapter<CertificateHolder> {

        List<String> imgUrls;
        int type;

        public CertificateAdapter(int type) {
            imgUrls = new ArrayList<>();
            this.type = type;
        }

        @NonNull
        @Override
        public CertificateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_certificate, parent, false);
            return new CertificateHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CertificateHolder holder, int position) {
            if (position < imgUrls.size()) {
                holder.btnDel.setVisibility(View.VISIBLE);
                holder.btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgUrls.remove(position);
                        notifyDataSetChanged();
                    }
                });
                holder.tvTip.setVisibility(View.GONE);
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + imgUrls.get(position))
                        .thumbnail(0.6f).into(holder.ivPhoto);
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageGlideDialog(getContext(), ServerAPI.FILL_DOMAIN + imgUrls.get(position), "").show();
                    }
                });
            } else {
                holder.btnDel.setVisibility(View.GONE);
                if (type == IMAGE_TYPE_INTRO) {
                    holder.tvTip.setVisibility(View.GONE);
                } else {
                    holder.tvTip.setVisibility(View.VISIBLE);
                }
                holder.ivPhoto.setImageResource(R.mipmap.ic_btn_add);
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImageSelect(type);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return imgUrls == null ? 1 : imgUrls.size() + 1;
        }
    }

    class CertificateHolder extends RecyclerView.ViewHolder {

        ImageView btnDel;
        ImageView ivPhoto;
        TextView tvTip;

        public CertificateHolder(@NonNull View itemView) {
            super(itemView);
            btnDel = itemView.findViewById(R.id.btn_del);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvTip = itemView.findViewById(R.id.tv_tip);
        }
    }

    private void httpGetAuthInfo() {
        showLoadView();
        String url = ServerAPI.getMyAuthInfo();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject dataJson = JSONObject.parseObject(data);
                DocAttInfo docAttInfo = dataJson.getJSONObject("docObj").toJavaObject(DocAttInfo.class);
                onDocInfoReturn(docAttInfo);
                List<GoodAtInfo> goodAtArr = dataJson.getJSONArray("goodAtArr").toJavaList(GoodAtInfo.class);

                professAdapter = new ProfessAdapter(docAttInfo.getProfessArr());
                DocProfessInfo professInfo = getSelectedProfess(docAttInfo.getProfessArr());
                if (professInfo != null) {
                    String value = professInfo.getValue();
                    tvProfess.setTag(value.substring(0, value.indexOf('_')));
                    tvProfess.setText(professInfo.getKey());
                }

                goodAtSelectAdapter = new GoodAtSelectAdapter(goodAtArr);
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

    private void onDocInfoReturn(DocAttInfo docAttInfo) {
        if (docAttInfo.getShPic() != null && docAttInfo.getShPic().length() > 0) {
            docPhotoUrl = docAttInfo.getShPic();
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + docAttInfo.getShPic()).into(ivDocPhoto);
        }
        tvDocName.setText(docAttInfo.getName());
        if (docAttInfo.getBirthday() != null) {
            Calendar calendar = getTime(docAttInfo.getBirthday());
            tvDocBirthDay.setText(getTime(calendar));
        }
        tvDocWorkAdd.setText(docAttInfo.getPractAddress());
        docWorkNameEt.setText(docAttInfo.getPractAgent());
        tvDocIntro.setText(docAttInfo.getWdesc());
        tvSex.setText(docAttInfo.getSexObj().getKeyName());
        tvSex.setTag(docAttInfo.getSexObj().getKeyValue());

        tvLocation.setText(docAttInfo.getProvinceObj().getKeyName()
                + docAttInfo.getCityObj().getKeyName()
                + docAttInfo.getCountryObj().getKeyName());
        tvLocation.setTag(R.id.tag_area_id, docAttInfo.getCountryObj().getKeyValue());
        tvLocation.setTag(R.id.tag_province_id, docAttInfo.getProvinceObj().getKeyValue());
        tvLocation.setTag(R.id.tag_city_id, docAttInfo.getCityObj().getKeyValue());

        if (docAttInfo.getGoodAtSelfId() != null && docAttInfo.getGoodAtSelfId().length() > 0) {
            String[] arrName = docAttInfo.getGoodAtSelf().split("\\;");
            String[] arrId = docAttInfo.getGoodAtSelfId().split("\\;");
            List<GoodAtBean> list = new ArrayList<>();
            for (int i = 0; i < arrId.length; i++) {
                GoodAtBean bean = new GoodAtBean();
                bean.setName(arrName[i]);
                bean.setId(arrId[i]);
                list.add(bean);
            }
            goodAtAdapter.updateArr(list);
        }

        if (docAttInfo.getIdFace() != null && docAttInfo.getIdFace().length() > 0) {
            cardFrontUrl = docAttInfo.getIdFace();
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + docAttInfo.getIdFace())
                    .thumbnail(0.4f).into(ivCardFront);
        }
        if (docAttInfo.getIdBack() != null && docAttInfo.getIdBack().length() > 0) {
            cardBackUrl = docAttInfo.getIdFace();
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + docAttInfo.getIdBack())
                    .thumbnail(0.4f).into(ivCardBack);
        }
        if (docAttInfo.getSignPath() != null && docAttInfo.getSignPath().length() > 0) {
            docSignUrl = docAttInfo.getSignPath();
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + docAttInfo.getSignPath()).into(ivDocSign);
        }

        if (docAttInfo.getPracticing() != null && docAttInfo.getPracticing().length() > 0) {
            String[] arr = docAttInfo.getPracticing().split("\\;");
            adapterZYZS.imgUrls = new ArrayList<>(Arrays.asList(arr));
            adapterZYZS.notifyDataSetChanged();
        }

        if (docAttInfo.getQualification() != null && docAttInfo.getQualification().length() > 0) {
            String[] arr = docAttInfo.getQualification().split("\\;");
            adapterZGZS.imgUrls = new ArrayList<>(Arrays.asList(arr));
            adapterZGZS.notifyDataSetChanged();
        }

        if (docAttInfo.getSkill() != null && docAttInfo.getSkill().length() > 0) {
            String[] arr = docAttInfo.getSkill().split("\\;");
            adapterZCZS.imgUrls = new ArrayList<>(Arrays.asList(arr));
            adapterZCZS.notifyDataSetChanged();
        }

        if (docAttInfo.getOtherSkill() != null && docAttInfo.getOtherSkill().length() > 0) {
            String[] arr = docAttInfo.getOtherSkill().split("\\;");
            adapterQTZS.imgUrls = new ArrayList<>(Arrays.asList(arr));
            adapterQTZS.notifyDataSetChanged();
        }

        if (docAttInfo.getWdescPic() != null && docAttInfo.getWdescPic().length() > 0) {
            String[] arr = docAttInfo.getWdescPic().split("\\;");
            adapterIntro.imgUrls = new ArrayList<>(Arrays.asList(arr));
            adapterIntro.notifyDataSetChanged();
        }
    }

    private DocProfessInfo getSelectedProfess(List<DocProfessInfo> professArr) {
        for (DocProfessInfo info : professArr) {
            String value = info.getValue();
            if (value.substring(value.length() - 1).equals("1")) {
                return info;
            }
        }
        return null;
    }

    //-------------------------职称选择------------------------
    View professSelectView;
    PopupWindow popupProfessSelect;
    ProfessAdapter professAdapter;

    private void showPopupProfess() {
        if (popupProfessSelect == null) {
            professSelectView = getLayoutInflater().inflate(R.layout.popup_profess_selector, null);
            professSelectView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopupProfess();
                }
            });
            RecyclerView rvProfess = professSelectView.findViewById(R.id.rv_popup_profess);
            rvProfess.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProfess.setAdapter(professAdapter);
            popupProfessSelect = new PopupWindow(professSelectView, -1, -1, true);
        }
        if (!popupProfessSelect.isShowing())
            popupProfessSelect.showAtLocation(tvProfess.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void dismissPopupProfess() {
        popupProfessSelect.dismiss();
    }

    class ProfessAdapter extends RecyclerView.Adapter<ProfessHolder> {

        List<DocProfessInfo> professArr;

        public ProfessAdapter(List<DocProfessInfo> professArr) {
            this.professArr = professArr;
        }

        @NonNull
        @Override
        public ProfessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(getContext());
            tv.setPadding(32, 24, 32, 24);
            tv.setBackgroundColor(0xffffffff);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
            lp.setMargins(0, 2, 0, 0);
            tv.setLayoutParams(lp);
            return new ProfessHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ProfessHolder holder, int position) {
            holder.textView.setText(professArr.get(position).getKey());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = professArr.get(position).getValue();
                    tvProfess.setTag(value.substring(0, value.indexOf('_')));
                    tvProfess.setText(professArr.get(position).getKey());
                    dismissPopupProfess();
                }
            });
        }

        @Override
        public int getItemCount() {
            return professArr == null ? 0 : professArr.size();
        }
    }

    class ProfessHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ProfessHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    //------------------------擅长显示-----------------------

    private List<GoodAtBean> getSelectedGoodAt(List<GoodAtInfo> goodAtArr) {
        if (goodAtArr == null || goodAtArr.size() == 0) return null;
        List<GoodAtBean> list = new ArrayList<>();
        for (int i = 0; i < goodAtArr.size(); i++) {
            GoodAtInfo goodAtInfo = goodAtArr.get(i);
            List<GoodAtOfficeInfo> officeInfos = goodAtInfo.getSmsOfficeSickList();
            for (int j = 0; j < officeInfos.size(); j++) {
                if (officeInfos.get(j).getIsChose() == 1) {
                    if (goodAtInfo.getIsChose() != 1)
                        goodAtInfo.setIsChose(1);
                    GoodAtBean item = new GoodAtBean();
                    item.setName(officeInfos.get(j).getOffsickName());
                    item.setId(goodAtInfo.getOfficeId() + "-" + officeInfos.get(j).getOffsickId());
                    list.add(item);
                }
            }
        }
        return list;
    }

    class GoodAtAdapter extends RecyclerView.Adapter<GoodAtHolder> {

        List<GoodAtBean> professArr;

        public GoodAtAdapter() {
            professArr = new ArrayList<>();
        }

        public void updateArr(List<GoodAtBean> list) {
            if (list == null || list.size() == 0) {
                if (professArr.size() > 0) {
                    professArr.clear();
                    notifyDataSetChanged();
                }
                return;
            }
            if (professArr.size() == 0) {
                professArr.addAll(list);
            } else {
                for (int i = professArr.size() - 1; i >= 0; i--) {
                    int position = isContainStr(list, professArr.get(i).getName());
                    if (position != -1) {
                        list.remove(position);
                        list.add(0, professArr.get(i));
                    }
                }
                professArr.clear();
                professArr.addAll(list);
            }
            notifyDataSetChanged();
        }

        private int isContainStr(List<GoodAtBean> list, String str) {
            int position = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(str)) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        @NonNull
        @Override
        public GoodAtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(getContext());
            tv.setPadding(32, 16, 32, 16);
            tv.setBackgroundResource(R.drawable.bg_att_good_at);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
            FlexboxLayoutManager.LayoutParams lp = new FlexboxLayoutManager.LayoutParams(-2, -2);
            lp.setMargins(12, 12, 12, 12);
            tv.setLayoutParams(lp);
            return new GoodAtHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull GoodAtHolder holder, int position) {
            holder.textView.setText(professArr.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return professArr == null ? 0 : professArr.size();
        }
    }

    class GoodAtHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public GoodAtHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    class GoodAtBean {
        private String name;
        private String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlag;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (llm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else {
                    dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                }
            } else {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(goodAtAdapter.professArr, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(goodAtAdapter.professArr, i, i - 1);
                }
            }
            goodAtAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                GoodAtHolder holder = (GoodAtHolder) viewHolder;
                holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            GoodAtHolder holder = (GoodAtHolder) viewHolder;
            holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
            goodAtAdapter.notifyDataSetChanged();
        }
    });

//--------------------擅长选择------------------------

    View goodAtView;
    PopupWindow popupGoodAt;
    GoodAtSelectAdapter goodAtSelectAdapter;

    private void showPopupGoodAt() {
        if (popupGoodAt == null) {
            goodAtView = getLayoutInflater().inflate(R.layout.popup_good_at, null);
            goodAtView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopupGoodAt();
                }
            });
            goodAtView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopupGoodAt();
                }
            });
            RecyclerView rvProfess = goodAtView.findViewById(R.id.rv_popup_good_at);
            rvProfess.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProfess.setNestedScrollingEnabled(false);
            rvProfess.setHasFixedSize(true);
            rvProfess.setAdapter(goodAtSelectAdapter);
            popupGoodAt = new PopupWindow(goodAtView, -1, -1, true);
            popupGoodAt.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    goodAtAdapter.updateArr(getSelectedGoodAt(goodAtSelectAdapter.getGoodAtInfos()));
                }
            });
        }
        if (!popupGoodAt.isShowing())
            popupGoodAt.showAtLocation(tvGoodAt.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void dismissPopupGoodAt() {
        popupGoodAt.dismiss();
    }

    class GoodAtSelectAdapter extends RecyclerView.Adapter<GoodAtSelectHolder> {

        List<GoodAtInfo> goodAtInfos;

        public GoodAtSelectAdapter(List<GoodAtInfo> goodAtInfos) {
            this.goodAtInfos = goodAtInfos;
        }

        public List<GoodAtInfo> getGoodAtInfos() {
            return goodAtInfos;
        }

        @NonNull
        @Override
        public GoodAtSelectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_good_at_select, parent, false);
            return new GoodAtSelectHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GoodAtSelectHolder holder, int position) {
            holder.tvTitle.setText(goodAtInfos.get(position).getOfficeName());
            FlexboxLayoutManager flm = new ScrollFlexBoxManager(getContext());
//            FlexboxLayoutManager flm = new FlexboxLayoutManager();
            flm.setFlexDirection(FlexDirection.ROW);
            flm.setFlexWrap(FlexWrap.WRAP);
            flm.setAlignItems(AlignItems.STRETCH);
            holder.recyclerView.setLayoutManager(flm);
            holder.recyclerView.setNestedScrollingEnabled(false);
            holder.recyclerView.setHasFixedSize(true);
            GoodAtItemAdapter adapter = new GoodAtItemAdapter(goodAtInfos.get(position).getSmsOfficeSickList());
            holder.recyclerView.setAdapter(adapter);
        }

        @Override
        public int getItemCount() {
            return goodAtInfos == null ? 0 : goodAtInfos.size();
        }
    }

    class GoodAtSelectHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        RecyclerView recyclerView;

        public GoodAtSelectHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            recyclerView = itemView.findViewById(R.id.rv_content);
        }
    }

    class GoodAtItemAdapter extends RecyclerView.Adapter<GoodAtItemHolder> {

        List<GoodAtOfficeInfo> goodAtInfos;

        public GoodAtItemAdapter(List<GoodAtOfficeInfo> goodAtInfos) {
            this.goodAtInfos = goodAtInfos;
        }

        @NonNull
        @Override
        public GoodAtItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setPadding(32, 16, 32, 16);
            FlexboxLayoutManager.LayoutParams lp = new FlexboxLayoutManager.LayoutParams(-2, -2);
            lp.setMargins(12, 12, 12, 12);
            textView.setLayoutParams(lp);
            return new GoodAtItemHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull GoodAtItemHolder holder, int position) {
            GoodAtOfficeInfo info = goodAtInfos.get(position);
            holder.textView.setText(info.getOffsickName());
            if (info.getIsChose() == 1) {
                holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextWhite));
                holder.textView.setBackgroundResource(R.drawable.bg_att_good_at_item_select);
            } else {
                holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
                holder.textView.setBackgroundResource(R.drawable.bg_att_good_at_item_normal);
            }
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.getIsChose() == 1) {
                        info.setIsChose(0);
                        notifyDataSetChanged();
                    } else {
                        List list = getSelectedGoodAt(goodAtSelectAdapter.getGoodAtInfos());
                        if (list == null || list.size() < 5) {
                            info.setIsChose(1);
                            notifyDataSetChanged();
                        } else {
                            ToastUtils.show(getContext(), "最多选取五个！");
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return goodAtInfos == null ? 0 : goodAtInfos.size();
        }
    }

    class GoodAtItemHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public GoodAtItemHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    //TODO 验证签名
    //-----------------------图片选择---------------------------
    View imageSelectView;
    PopupWindow popupImageSelect;

    private void showImageSelect(int imageType) {
        this.imageType = imageType;
        if (popupImageSelect == null) {
            imageSelectView = getLayoutInflater().inflate(R.layout.dialog_image_select, null);
            imageSelectView.findViewById(R.id.btn_dialog_image_select_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissImageSelect();
                }
            });
            imageSelectView.findViewById(R.id.btn_dialog_image_select_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissImageSelect();
                    takePhoto();
                }
            });
            imageSelectView.findViewById(R.id.btn_dialog_image_select_glide).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissImageSelect();
                    choosePic();
                }
            });
            popupImageSelect = new PopupWindow(imageSelectView, -1, -1, true);
        }
        if (!popupImageSelect.isShowing())
            popupImageSelect.showAtLocation(btnConfirm.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void dismissImageSelect() {
        popupImageSelect.dismiss();
    }


    public static final int OPEN_CAMERA_CODE = 204;//拍照
    public static final int OPEN_GALLERY_CODE = 205;//相册

    private String mTempPhotoPath;
    private Uri imageUri;

    private void takePhoto() {
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(getContext().getFilesDir() + File.separator + "images" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File photoFile = new File(fileDir, System.currentTimeMillis() + ".jpeg");
        mTempPhotoPath = photoFile.getAbsolutePath();
        imageUri = FileProvider.getUriForFile(this, getContext().getPackageName() + ".android7.FileProvider", photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, OPEN_CAMERA_CODE);
    }

    private void choosePic() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, OPEN_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SIGN_CREATE || requestCode == CODE_PHOTO_CREATE) {
            if (resultCode == RESULT_OK) {
                String signImagePath = data.getStringExtra("imagePath");
                httpImageUpload(signImagePath);
            }
            return;
        }
        if (resultCode == RESULT_OK) {
            String filePath = null;
            switch (requestCode) {
                case OPEN_CAMERA_CODE:
                    filePath = mTempPhotoPath;
                    break;
                case OPEN_GALLERY_CODE:
                    Uri uri = data.getData();
                    filePath = FileUtils.getFilePathByUri(this, uri);
                    break;
            }
            //TODO
            if (imageType == IMAGE_TYPE_DOC_SIGN) {
                Intent intent = new Intent(getContext(), ImageCreateActivity.class);
                intent.putExtra("imgPath", filePath);
                intent.putExtra("aspectRatio", 0.65f);
                startActivityForResult(intent, CODE_SIGN_CREATE);
            } else if (imageType == IMAGE_TYPE_DOC_PHOTO) {
                Intent intent = new Intent(getContext(), ImageCreateActivity.class);
                intent.putExtra("imgPath", filePath);
                intent.putExtra("aspectRatio", 1f);
                startActivityForResult(intent, CODE_PHOTO_CREATE);
            } else {
                httpImageUpload(filePath);
            }
        }
    }

    private void httpImageUpload(String imgPath) {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        File file = new File(imgPath);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", ""));
        mList.add(new KeyValue("path", "docAuth"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("httpImageUpload", result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    String resultPath = jsonObject.getJSONObject("data").getString("url");
                    onImageUploadSuccess(imageType, resultPath);
                } else {
                    Toast.makeText(getContext(), jsonObject.getString("errMsg"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("httpImageUpload", String.valueOf(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                showLoadView();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d("httpImageUpload", current + "/" + total);
            }
        });

    }

    private void onImageUploadSuccess(int type, String url) {
        switch (type) {
            case IMAGE_TYPE_DOC_PHOTO:
                docPhotoUrl = url;
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + url).into(ivDocPhoto);
                break;
            case IMAGE_TYPE_DOC_SIGN:
                docSignUrl = url;
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + url).into(ivDocSign);
                break;
            case IMAGE_TYPE_CARD_BACK:
                cardBackUrl = url;
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + url)
                        .thumbnail(0.4f).into(ivCardBack);
                break;
            case IMAGE_TYPE_CARD_FRONT:
                cardFrontUrl = url;
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + url)
                        .thumbnail(0.4f).into(ivCardFront);
                break;
            case IMAGE_TYPE_ZYZS:
                adapterZYZS.imgUrls.add(url);
                adapterZYZS.notifyDataSetChanged();
                break;
            case IMAGE_TYPE_ZGZS:
                adapterZGZS.imgUrls.add(url);
                adapterZGZS.notifyDataSetChanged();
                break;
            case IMAGE_TYPE_ZCZS:
                adapterZCZS.imgUrls.add(url);
                adapterZCZS.notifyDataSetChanged();
                break;
            case IMAGE_TYPE_QTZS:
                adapterQTZS.imgUrls.add(url);
                adapterQTZS.notifyDataSetChanged();
                break;
            case IMAGE_TYPE_INTRO:
                adapterIntro.imgUrls.add(url);
                adapterIntro.notifyDataSetChanged();
                break;
        }
    }

    //--------------------提交---------------------------
    private boolean checkFirstPage() {
        if (docPhotoUrl == null) {
            ToastUtils.show(getContext(), "请上传正面照片！");
            return false;
        }
        if (TextUtils.isEmpty(tvDocName.getText())) {
            ToastUtils.show(getContext(), "请输入姓名！");
            return false;
        }
        if (TextUtils.isEmpty(tvDocBirthDay.getText())) {
            ToastUtils.show(getContext(), "请输入出生日期！");
            return false;
        }
        if (tvProfess.getTag() == null) {
            ToastUtils.show(getContext(), "请选择职称！");
            return false;
        }
        if (rvGoodAt.getAdapter().getItemCount() == 0) {
            ToastUtils.show(getContext(), "请选择擅长治疗的疾病！");
            return false;
        }
        if (tvLocation.getTag(R.id.tag_area_id) == null) {
            ToastUtils.show(getContext(), "请选择地区！");
            return false;
        }
        return true;
    }

    private boolean checkSecondPage() {
        if (cardFrontUrl == null) {
            ToastUtils.show(getContext(), "请上传身份证正面图片！");
            return false;
        }
        if (cardBackUrl == null) {
            ToastUtils.show(getContext(), "请上传身份证反面图片！");
            return false;
        }
        if (tvDocWorkAdd.getText().toString().equals(getString(R.string.doc_work_add_example))) {
            ToastUtils.show(getContext(), "请填写主要执业地点！");
            return false;
        }
        if (docSignUrl == null) {
            ToastUtils.show(getContext(), "请上传签名图片！");
            return false;
        }
        if (adapterZYZS.imgUrls.size() == 0) {
            ToastUtils.show(getContext(), "请上传医师执业证书图片！");
            return false;
        }
        if (adapterZGZS.imgUrls.size() == 0) {
            ToastUtils.show(getContext(), "请上传医师资格证书图片！");
            return false;
        }
        return true;
    }

    private JSONObject createParams() {
        JSONObject jsonObject = new JSONObject();
        if (docPhotoUrl != null) jsonObject.put("shPic", docPhotoUrl);
        jsonObject.put("name", tvDocName.getText().toString().trim());
        jsonObject.put("practAddress", tvDocWorkAdd.getText().toString().trim());
        jsonObject.put("practAgent", docWorkNameEt.getText().toString().trim());
        jsonObject.put("birthday", tvDocBirthDay.getText().toString().replaceAll("-", ""));
        jsonObject.put("sex", tvSex.getTag());
        jsonObject.put("doctorTyp", tvProfess.getTag());
        jsonObject.put("provinceId", tvLocation.getTag(R.id.tag_province_id));
        jsonObject.put("cityId", tvLocation.getTag(R.id.tag_city_id));
        jsonObject.put("countryId", tvLocation.getTag(R.id.tag_area_id));
        jsonObject.put("idFace", cardFrontUrl);
        jsonObject.put("idBack", cardBackUrl);
        jsonObject.put("signPath", docSignUrl);
        List<String> listName=new ArrayList<>();
        List<String> listId=new ArrayList<>();
        for(GoodAtBean bean:goodAtAdapter.professArr){
            listName.add(bean.getName());
            listId.add(bean.getId());
        }
        jsonObject.put("goodAtSelf", getStringArr(listName));
        jsonObject.put("goodAtSelfId", getStringArr(listId));
        jsonObject.put("goodAtArr", goodAtSelectAdapter.getGoodAtInfos());
        jsonObject.put("practicing", getStringArr(adapterZYZS.imgUrls));
        jsonObject.put("qualification", getStringArr(adapterZGZS.imgUrls));
        jsonObject.put("skill", getStringArr(adapterZCZS.imgUrls));
        jsonObject.put("otherSkill", getStringArr(adapterQTZS.imgUrls));
        if (!tvDocIntro.getText().toString().equals(""))
            jsonObject.put("wdesc", tvDocIntro.getText().toString().trim());
        jsonObject.put("wdescPic", getStringArr(adapterIntro.imgUrls));
        jsonObject.put("wdescVideo", "");
        return jsonObject;
    }

    private String getStringArr(List<String> list) {
        if (list == null || list.size() == 0) return "";
        StringBuffer sb = new StringBuffer();
        for (String str : list) {
            sb.append(str);
            sb.append(";");
        }
        return sb.toString();
    }

    private void httpSubmit() {
        String url = ServerAPI.getDocAuthUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setBodyContent(createParams().toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.show(getContext(), "提交成功，请等待审核！");
                Intent intent = new Intent(BroadcastAction.ACTION_AUTH_SUBMIT);
                sendBroadcast(intent);
                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

//    private void showDatePicker() {
//        Calendar calendar;
//        if (TextUtils.isEmpty(tvDocBirthDay.getText())) {
//            calendar = Calendar.getInstance();
//        } else {
//            calendar = getTime(tvDocBirthDay.getText().toString());
//        }
//        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar mCalendar = Calendar.getInstance();
//                mCalendar.set(year, month, dayOfMonth);
//                tvDocBirthDay.setText(getTime(mCalendar));
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
//    }

    private void showDatePicker() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                tvDocBirthDay.setText(format.format(date));
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setContentTextSize(18)//滚轮文字大小
                .setTitleSize(18)//标题文字大小
                .setTitleText("选择出生日期")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(getResources().getColor(R.color.color_ask_title_black))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.color_ask_title_red))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.color_ask_title_red))//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
//                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
//                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年","月","日","","","")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();

        pvTime.show();
    }



    private String getTime(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        String yearStr = String.valueOf(year);
        int month = calendar.get(Calendar.MONTH) + 1;//获取月份
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        int day = calendar.get(Calendar.DATE);//获取日
        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        return String.format("%s-%s-%s", yearStr, monthStr, dayStr);
    }

    private Calendar getTime(String time) {
        if (time == null || time.length() < 8) return Calendar.getInstance();
        time = time.replaceAll("\\-", "");
        time = time.replaceAll("\\/", "");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }
}
