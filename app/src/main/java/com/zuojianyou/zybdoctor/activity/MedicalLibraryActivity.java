package com.zuojianyou.zybdoctor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.api.ApiManager;
import com.zuojianyou.zybdoctor.api.CommonSubscriber;
import com.zuojianyou.zybdoctor.api.RxUtil;
import com.zuojianyou.zybdoctor.beans.EbmMenuInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author: weiwei
 * @date: 2020/8/19
 * @description:
 */
public class MedicalLibraryActivity extends BaseActivity {
    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.option_tv)
    TextView optionTv;
    @BindView(R.id.keyword_et)
    EditText keywordEt;
    @BindView(R.id.search_btn)
    Button searchBtn;
    @BindView(R.id.search_ll)
    LinearLayout searchLl;
    @BindView(R.id.traditional_medicine_rb)
    RadioButton traditionalMedicineRb;
    @BindView(R.id.western_medicine_rb)
    RadioButton westernMedicineRb;
    @BindView(R.id.menu_recycler_view)
    RecyclerView menuRecyclerView;
    @BindView(R.id.menu_ll)
    LinearLayout menuLl;
    @BindView(R.id.western_sick_tv)
    TextView westernSickTv;
    @BindView(R.id.traditional_diagnose_tv)
    TextView traditionalDiagnoseTv;
    @BindView(R.id.western_diagnose_tv)
    TextView westernDiagnoseTv;
    @BindView(R.id.ll_emb_sick_info_block)
    LinearLayout llEmbSickInfoBlock;
    @BindView(R.id.sick_recyclerview)
    RecyclerView sickRecyclerview;
    @BindView(R.id.sick_ll)
    LinearLayout sickLl;
    @BindView(R.id.no_data_tv)
    TextView noDataTv;

    private Unbinder mUnBinder;
    private List<EbmMenuInfo> mMenuInfoList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_library);
        mUnBinder = ButterKnife.bind(this);
        titleTv.setText("中医循证医学库");


        getDiaTreeList();
    }

    @OnClick({R.id.back_btn,R.id.option_tv})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.back_btn:
                if (menuLl.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    menuLl.setVisibility(View.VISIBLE);
                    sickLl.setVisibility(View.GONE);
                    optionTv.setVisibility(View.GONE);
                    noDataTv.setVisibility(View.GONE);
                    titleTv.setText("中医循证医学库");
                }

                break;
            case R.id.option_tv:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    private void getDiaTreeList(){
        ApiManager.getInstance().getDiaTreeList()
                .compose(RxUtil.rxSchedulerHelper())
                .compose(RxUtil.handleMyResults())
                .subscribeWith(new CommonSubscriber<List<EbmMenuInfo>>(this) {
                    @Override
                    public void onSuccess(List<EbmMenuInfo> ebmMenuInfos) {
                        mMenuInfoList.addAll(ebmMenuInfos);
                    }
                });
    }

    private void initData(List<EbmMenuInfo> ebmMenuInfos){

    }

}
