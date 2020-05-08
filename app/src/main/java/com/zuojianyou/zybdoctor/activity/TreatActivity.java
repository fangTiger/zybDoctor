package com.zuojianyou.zybdoctor.activity;

import android.app.DatePickerDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dds.java.voip.CallSingleActivity;
import com.dds.java.voip.VoipEvent;
import com.dds.nodejs.WebrtcUtil;
import com.dds.skywebrtc.SkyEngineKit;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.AddListInfo;
import com.zuojianyou.zybdoctor.beans.DispatchListInfo;
import com.zuojianyou.zybdoctor.beans.GoodAtInfo;
import com.zuojianyou.zybdoctor.beans.GoodAtOfficeInfo;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.beans.RecipeListItem;
import com.zuojianyou.zybdoctor.beans.SickInfo;
import com.zuojianyou.zybdoctor.beans.SickInfoOpration;
import com.zuojianyou.zybdoctor.beans.SickInfoPersonal;
import com.zuojianyou.zybdoctor.beans.SickSampleItem;
import com.zuojianyou.zybdoctor.beans.treat.BasicInfo;
import com.zuojianyou.zybdoctor.beans.treat.DiagnoseInfo;
import com.zuojianyou.zybdoctor.beans.treat.DicSick;
import com.zuojianyou.zybdoctor.beans.treat.MbrInfo;
import com.zuojianyou.zybdoctor.beans.treat.MbrSickInfo;
import com.zuojianyou.zybdoctor.beans.treat.Med;
import com.zuojianyou.zybdoctor.beans.treat.Opration;
import com.zuojianyou.zybdoctor.beans.treat.Personal;
import com.zuojianyou.zybdoctor.beans.treat.Record;
import com.zuojianyou.zybdoctor.beans.treat.Report;
import com.zuojianyou.zybdoctor.beans.treat.Revisit;
import com.zuojianyou.zybdoctor.beans.treat.SickHis;
import com.zuojianyou.zybdoctor.beans.treat.TreatParameter;
import com.zuojianyou.zybdoctor.service.Mp3PlayerService;
import com.zuojianyou.zybdoctor.service.Mp3RecordService;
import com.zuojianyou.zybdoctor.units.FileUtils;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.ToastUtils;
import com.zuojianyou.zybdoctor.views.DicNameDialog;
import com.zuojianyou.zybdoctor.views.DicRuleDialog;
import com.zuojianyou.zybdoctor.views.DicSickDialog;
import com.zuojianyou.zybdoctor.views.ImageGlideDialog;
import com.zuojianyou.zybdoctor.views.ImageSelectDialog;
import com.zuojianyou.zybdoctor.views.RecordListDialog;
import com.zuojianyou.zybdoctor.views.RecordUploadDialog;
import com.zuojianyou.zybdoctor.views.ScrollFlexBoxManager;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 问诊
 */
public class TreatActivity extends BaseActivity {

    public static final int EBM_REQUEST_CODE = 201;//中医循证
    public static final int PATENT_REQUEST_CODE = 202;//成品药
    public static final int RECIPE_REQUEST_CODE = 203;//个人处方
    public static final int CAMERA_LAB_CODE = 204;//实验室图像
    public static final int CAMERA_IMG_CODE = 205;//影像检查
    public static final int CAMERA_SEE_CODE = 206;//望诊
    public static final int PAY_STATE_CODE = 207;//交费状态
    public static final int MED_ADD_CODE = 208;//添加草药
    public static final int SICK_SAMPLE_CODE = 209;//个人病案库

    public static final int MED_RES_DATA = 400;//医学库
    public static final int MED_RES_PER = 401;//个人

    public static final String ACTION_TREAT_BACK = "action_treat_back";//

    String ebmSickInfo = null;
    String mbrId, regId, diaId, sickId, repiceId;
    boolean isPay = false;
    int medRes = MED_RES_DATA;
    DiagnoseInfo diagnoseInfo;

    TextView[] btnJudge = new TextView[4];
    View[] contentJudge = new View[4];

    TextView btnCommonRecipe, btnPersonalRecipe;
    TextView tvTipRecipe;
    View llMedRcpCon, llPsnRcpCon;

    List<MedicineInfo> medicines1, medicines2;
    RecyclerView rvHerbal1, rvHerbal2;
    GridHerbalAdapter herbalAdapter1, herbalAdapter2;

    List<MedicineInfo> patentInfos;
    RecyclerView rvPatent;
    PatentAdapter patentAdapter;

    TextView tvTitle;
    TextView tvReviewTime;
    TextView tvWestSickName;
    RecordThread recordThread;

    List<Report> reportList;
    List<Record> recordList;

    Mp3RecordService mp3Record;
    Mp3PlayerService mp3Player;
    ServiceConnection playerConn;
    ServiceConnection recordConn;

    Spinner spDispatch;
    DispatchAdapter dispatchAdapter;
    List<DispatchListInfo> dispatchList;
    List<DispatchListInfo> dispatchAll;

    PopupWindow popupMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TreatActivity", "onCreate");
        setContentView(R.layout.activity_treat);
        registerCaptureReceiver();
        tvTitle = findViewById(R.id.tv_act_treat_title);
        if (tvTitle != null)
            tvTitle.setTag(0);

        recordConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Mp3RecordService.RecordBind recordBind = (Mp3RecordService.RecordBind) service;
                mp3Record = recordBind.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intentRecord = new Intent(this, Mp3RecordService.class);
        bindService(intentRecord, recordConn, Service.BIND_AUTO_CREATE);

        playerConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Mp3PlayerService.PlayerBind playerBind = (Mp3PlayerService.PlayerBind) service;
                mp3Player = playerBind.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intentPlayer = new Intent(this, Mp3PlayerService.class);
        bindService(intentPlayer, playerConn, Service.BIND_AUTO_CREATE);

        mbrId = getIntent().getStringExtra("mbrId");
        regId = getIntent().getStringExtra("regId");
        diaId = getIntent().getStringExtra("diaId");
        isPay = getIntent().getBooleanExtra("payState", false);
        double fee = getIntent().getDoubleExtra("fee", 0);
        EditText etWenzhenfei = findViewById(R.id.et_treat_content_wenzhenfei);
        etWenzhenfei.setText(String.format("%.2f", fee));
        etWenzhenfei.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                httpCost();
            }
        });
        EditText etNumber = findViewById(R.id.et_treat_content_number);
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText etDay = findViewById(R.id.et_treat_content_days);
                etDay.setText(s);
                httpCost();
            }
        });

        Spinner spRate = findViewById(R.id.sp_treat_content_xishu);
        SpinnerAdapter rateAdapter = new SpinnerAdapter();
        List<DicSick> rateList = new ArrayList<>();
        for (float i = 1f; i <= 3f; i += 0.1f) {
            DicSick dicSick = new DicSick();
            dicSick.setDataName(String.format("%.1f", i));
            dicSick.setDataValue(String.format("%.1f", i));
            rateList.add(dicSick);
        }
        rateAdapter.setData(rateList);
        spRate.setAdapter(rateAdapter);
        spRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                httpCost();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.ib_treat_btn_record).setTag("start");
        findViewById(R.id.ib_treat_btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                String tag = (String) imageView.getTag();
                if (tag.equals("start")) {
                    imageView.setImageResource(R.mipmap.ic_btn_record_stop);
                    imageView.setTag("stop");
                    recordThread = new RecordThread();
                    recordThread.start();
                    mp3Record.startRecord();
                } else {
                    imageView.setImageResource(R.mipmap.ic_btn_record_start);
                    imageView.setTag("start");
                    recordThread.breakUp();
                    recordThread = null;
                    if (mp3Record.stopRecord() == 0) {
                        String filePath = mp3Record.getFilePath();
                        RecordUploadDialog uploadDialog = new RecordUploadDialog(getContext(), filePath, regId);
                        uploadDialog.setCanceledOnTouchOutside(false);
                        uploadDialog.setOnUploadSuccessListener(new RecordUploadDialog.OnUploadSuccessListener() {
                            @Override
                            public void onUploadSuccess(String url) {
                                if (recordList == null) recordList = new ArrayList<>();
                                Record record = new Record();
                                record.setWurl(url);
                                recordList.add(record);
                            }
                        });
                        uploadDialog.show();
                    }
                }
            }
        });

        findViewById(R.id.ll_treat_btn_open_record_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordListDialog recordListDialog = new RecordListDialog(getContext(), mp3Player, recordList);
                recordListDialog.show();
            }
        });

        findViewById(R.id.btn_act_treat_open_ebm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TreatEbmActivity.class);
                startActivityForResult(intent, EBM_REQUEST_CODE);
            }
        });

        findViewById(R.id.btn_act_treat_open_patent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TreatPatentActivity.class);
                startActivityForResult(intent, PATENT_REQUEST_CODE);
            }
        });

        findViewById(R.id.btn_act_treat_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_act_treat_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpSubmit(true, 0);
            }
        });

        findViewById(R.id.btn_act_treat_visit_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpSubmit(false, 1);
            }
        });

        findViewById(R.id.btn_act_treat_recipe_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpSubmit(false, 2);
            }
        });

        findViewById(R.id.ib_treat_btn_add_see_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(CAMERA_SEE_CODE);
            }
        });

        findViewById(R.id.ib_treat_btn_add_img_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(CAMERA_IMG_CODE);
            }
        });

        findViewById(R.id.ib_treat_btn_add_lab_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(CAMERA_LAB_CODE);
            }
        });

        findViewById(R.id.tv_treat_sick_west_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupGoodAt();
            }
        });

        tvWestSickName = findViewById(R.id.tv_treat_sick_west_name);
        tvReviewTime = findViewById(R.id.tv_treat_btn_review_time);
        tvReviewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strMonth = month + 1 < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                        String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                        tvReviewTime.setText(year + "-" + strMonth + "-" + strDay);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        rvHerbal1 = findViewById(R.id.rv_act_treat_herbal_1);
        rvHerbal1.setHasFixedSize(true);
        rvHerbal1.setNestedScrollingEnabled(false);
        GridLayoutManager glm1 = new GridLayoutManager(getContext(), 2);
        rvHerbal1.setLayoutManager(glm1);
        rvHerbal1.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 1;
                if (parent.getChildAdapterPosition(view) % 2 == 0) {
                    outRect.right = 1;
                }
            }
        });
        herbalAdapter1 = new GridHerbalAdapter();
        rvHerbal1.setAdapter(herbalAdapter1);

        rvHerbal2 = findViewById(R.id.rv_act_treat_herbal_2);
        rvHerbal2.setHasFixedSize(true);
        rvHerbal2.setNestedScrollingEnabled(false);
        GridLayoutManager glm2 = new GridLayoutManager(getContext(), 2);
        rvHerbal2.setLayoutManager(glm2);
        rvHerbal2.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 1;
                if (parent.getChildAdapterPosition(view) % 2 == 0) {
                    outRect.right = 1;
                }
            }
        });
        herbalAdapter2 = new GridHerbalAdapter();
        rvHerbal2.setAdapter(herbalAdapter2);

        rvPatent = findViewById(R.id.rv_treat_patent_list);
        rvPatent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPatent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 1;
            }
        });
        rvPatent.setNestedScrollingEnabled(false);
        rvPatent.setHasFixedSize(true);
        patentInfos = new ArrayList<>();
        patentAdapter = new PatentAdapter();
        rvPatent.setAdapter(patentAdapter);

        initJudge();
        initRecipe();
        initDispatch();

        if (mbrId != null) {
            httpMbrInfo();
        } else {
            httpDiagnose(diaId);
        }
        httpGetAuthInfo();
    }

    private String mTempPhotoPath;
    private Uri imageUri;

    private void takePhoto(int requestCode) {
        ImageSelectDialog imageSelectDialog = new ImageSelectDialog(getContext());
        imageSelectDialog.setOnItemClick(new ImageSelectDialog.OnItemClick() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onCameraClick() {
                Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File fileDir = new File(getContext().getFilesDir() + File.separator + "images" + File.separator);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                File photoFile = new File(fileDir, System.currentTimeMillis() + ".jpeg");
                mTempPhotoPath = photoFile.getAbsolutePath();
                imageUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".android7.FileProvider", photoFile);
                intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intentToTakePhoto, requestCode);
            }

            @Override
            public void onGlideClick() {
                mTempPhotoPath = null;
                Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
                intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intentToPickPic, requestCode);
            }
        });
        imageSelectDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //中医循证返回
        if (requestCode == EBM_REQUEST_CODE) {
            // TODO: 中医循证返显
            if (resultCode == RESULT_OK) {
                ebmSickInfo = data.getStringExtra("sickInfo");
                setEbmResult(ebmSickInfo);
            }
        } else if (requestCode == PATENT_REQUEST_CODE) {
            // TODO: 添加成品药返显
            if (resultCode == RESULT_OK) {
                List<MedicineInfo> patentList = JSONObject.parseArray(data.getStringExtra("result"), MedicineInfo.class);
                if (patentInfos.size() > 0) {
                    for (int i = 0; i < patentList.size(); i++) {
                        boolean isExit = false;
                        MedicineInfo newMed = patentList.get(i);
                        if (!newMed.getMedicineId().equals("")) {
                            for (int j = 0; j < patentInfos.size(); j++) {
                                MedicineInfo oldMed = patentInfos.get(j);
                                if (oldMed.getMedicineId().equals(newMed.getMedicineId())) {
                                    int useNum = Integer.parseInt(oldMed.getUseNum());
                                    int addNum = Integer.parseInt(newMed.getUseNum());
                                    int sumNum = useNum + addNum;
                                    oldMed.setUseNum(String.valueOf(sumNum));
                                    isExit = true;
                                    break;
                                }
                            }
                        }
                        if (!isExit) {
                            patentInfos.add(newMed);
                        }
                    }
                } else {
                    patentInfos.addAll(patentList);
                }
                patentAdapter.notifyDataSetChanged();
                httpCost();
            }
        } else if (requestCode == RECIPE_REQUEST_CODE) {
            // TODO: 个人处方返显
            if (resultCode == RESULT_OK) {
                RecipeListItem recipe = JSONObject.parseObject(data.getStringExtra("medicines"), RecipeListItem.class);
                repiceId = recipe.getRepiceId();
                setViewText(R.id.tv_treat_sick_cn_name, recipe.getSickName());
                setViewText(R.id.tv_treat_sick_west_name, recipe.getOffsickName());
                setViewText(R.id.tv_treat_sick_reason, recipe.getPathogeny());//发病机理
                setViewText(R.id.tv_treat_sick_therapies, recipe.getTherapies());//治法
                setViewText(R.id.et_treat_content_yizhu, recipe.getDoctorAdvice());//医嘱
                setViewText(R.id.et_treat_content_yongyaoshuoming, recipe.getTisane());//服用方法
                tvWestSickName.setTag(recipe.getOfficeId() + "-" + recipe.getOffsickId());
                medicines2 = recipe.getMedList();
                herbalAdapter2.setData(medicines2);
                httpCost();
                filterDispatch(recipe.getMedList().get(0).getCenterId());
            }
        } else if (requestCode == SICK_SAMPLE_CODE) {
            // TODO: 个人病案库返显
            if (resultCode == RESULT_OK) {
                SickSampleItem recipe = JSONObject.parseObject(data.getStringExtra("medicines"), SickSampleItem.class);
//                repiceId = recipe.getRepiceId();
                setViewText(R.id.tv_treat_sick_cn_name, recipe.getSickName());
                setViewText(R.id.tv_treat_sick_west_name, recipe.getOffsickName());
                setViewText(R.id.tv_treat_sick_reason, recipe.getPathogeny());//发病机理
                setViewText(R.id.tv_treat_sick_therapies, recipe.getTherapy());//治法
                setViewText(R.id.et_treat_content_yizhu, recipe.getDoctorAdvice());//医嘱
                setViewText(R.id.et_treat_content_yongyaoshuoming, recipe.getInstruction());//服用方法
                tvWestSickName.setTag(recipe.getOfficeId() + "-" + recipe.getOffsickId());
                medicines2 = recipe.getMedList();
                herbalAdapter2.setData(medicines2);
                httpCost();
                filterDispatch(recipe.getMedList().get(0).getCenterId());
            }
        } else if (requestCode == MED_ADD_CODE) {
            //TODO: 添加草药显示
            if (resultCode == RESULT_OK) {
                List<MedicineInfo> medicines = JSONObject.parseArray(data.getStringExtra("medicines"), MedicineInfo.class);
                filterDispatch(medicines.get(0).getCenterId());
                if (medRes == MED_RES_DATA) {
                    if (medicines1 == null) {
                        medicines1 = new ArrayList<>();
                    }
                    if (medicines1.size() > 0) {
                        for (int i = 0; i < medicines.size(); i++) {
                            boolean isExit = false;
                            MedicineInfo newMed = medicines.get(i);
                            for (int j = 0; j < medicines1.size(); j++) {
                                MedicineInfo oldMed = medicines1.get(j);
                                if (oldMed.getMedicineId().equals(newMed.getMedicineId())) {
                                    int useNum = Integer.parseInt(oldMed.getUseNum());
                                    int addNum = Integer.parseInt(newMed.getUseNum());
                                    int sumNum = useNum + addNum;
                                    oldMed.setUseNum(String.valueOf(sumNum));
                                    isExit = true;
                                    break;
                                }
                            }
                            if (!isExit) {
                                medicines1.add(newMed);
                            }
                        }
                    } else {
                        medicines1.addAll(medicines);
                    }
//                    medicines1.addAll(medicines);
                    herbalAdapter1.setData(medicines1);
                } else {
                    if (medicines2 == null) {
                        medicines2 = new ArrayList<>();
                    } else if (medicines2.size() > 0) {
                        if (!medicines2.get(0).getCenterId().equals(medicines.get(0).getCenterId())) {
                            medicines2.clear();
                        }
                    }
                    if (medicines2.size() > 0) {
                        for (int i = 0; i < medicines.size(); i++) {
                            boolean isExit = false;
                            MedicineInfo newMed = medicines.get(i);
                            for (int j = 0; j < medicines2.size(); j++) {
                                MedicineInfo oldMed = medicines2.get(j);
                                if (oldMed.getMedicineId().equals(newMed.getMedicineId())) {
                                    int useNum = Integer.parseInt(oldMed.getUseNum());
                                    int addNum = Integer.parseInt(newMed.getUseNum());
                                    int sumNum = useNum + addNum;
                                    oldMed.setUseNum(String.valueOf(sumNum));
                                    isExit = true;
                                    break;
                                }
                            }
                            if (!isExit) {
                                medicines2.add(newMed);
                            }
                        }
                    } else {
                        medicines2.addAll(medicines);
                    }
//                    medicines2.addAll(medicines);
                    herbalAdapter2.setData(medicines2);
                }
                httpCost();
            }
        } else if (requestCode == CAMERA_LAB_CODE) {
            //TODO:实验室图片
            if (resultCode == RESULT_OK) {
                if (mTempPhotoPath == null) {
                    Uri uri = data.getData();
                    mTempPhotoPath = FileUtils.getFilePathByUri(this, uri);
                }
                DicNameDialog dicNameDialog = new DicNameDialog(getContext(),
                        mTempPhotoPath, regId, diagnoseInfo.getPicTypeObj().getLaboratoryObj());
                dicNameDialog.setOnNameSelectListener(new DicNameDialog.OnNameSelectListener() {
                    @Override
                    public void onNameSelected(DicSick dicSick, String resultPath) {
                        addReportItem(dicSick, "1", resultPath);
                        addImageToContent("1", dicSick.getDataName(), resultPath);
                    }
                });
                dicNameDialog.show();
            }
        } else if (requestCode == CAMERA_IMG_CODE) {
            //TODO:影像
            if (resultCode == RESULT_OK) {
                if (mTempPhotoPath == null) {
                    Uri uri = data.getData();
                    mTempPhotoPath = FileUtils.getFilePathByUri(this, uri);
                }
                DicNameDialog dicNameDialog = new DicNameDialog(getContext(),
                        mTempPhotoPath, regId, diagnoseInfo.getPicTypeObj().getImagingPicObj());
                dicNameDialog.setOnNameSelectListener(new DicNameDialog.OnNameSelectListener() {
                    @Override
                    public void onNameSelected(DicSick dicSick, String resultPath) {
                        addReportItem(dicSick, "2", resultPath);
                        addImageToContent("2", dicSick.getDataName(), resultPath);
                    }
                });
                dicNameDialog.show();
            }
        } else if (requestCode == CAMERA_SEE_CODE) {
            //TODO:望诊
            if (resultCode == RESULT_OK) {
                if (mTempPhotoPath == null) {
                    Uri uri = data.getData();
                    mTempPhotoPath = FileUtils.getFilePathByUri(this, uri);
                }
                DicNameDialog dicNameDialog = new DicNameDialog(getContext(),
                        mTempPhotoPath, regId, diagnoseInfo.getPicTypeObj().getDiagnosePicObj());
                dicNameDialog.setOnNameSelectListener(new DicNameDialog.OnNameSelectListener() {
                    @Override
                    public void onNameSelected(DicSick dicSick, String resultPath) {
                        addReportItem(dicSick, "3", resultPath);
                        addImageToContent("3", dicSick.getDataName(), resultPath);
                    }
                });
                dicNameDialog.show();
            }
        } else if (requestCode == PAY_STATE_CODE) {
            if (resultCode == RESULT_OK) {
                isPay = true;
                onPaySuccess();
            }
        }
    }

    private void setEbmResult(String data) {
        SickInfo sickInfo = JSONObject.parseObject(data, SickInfo.class);
        sickId = sickInfo.getSickId();
        repiceId = sickInfo.getRepiceId();
        TextView tvSickReason, tvSickCnName, tvSickWestName, tvSickTherapies, tvMedicineName, tvMedicineCode;

        tvSickReason = findViewById(R.id.tv_treat_sick_reason);
        tvSickCnName = findViewById(R.id.tv_treat_sick_cn_name);
        tvSickWestName = findViewById(R.id.tv_treat_sick_west_name);
        tvSickTherapies = findViewById(R.id.tv_treat_sick_therapies);
        tvMedicineName = findViewById(R.id.tv_treat_medicine_name);
        tvMedicineCode = findViewById(R.id.tv_treat_medicine_code);
        setViewText(R.id.et_treat_content_yizhu, sickInfo.getDoctorAdvice());
        setViewText(R.id.et_treat_content_yongyaoshuoming, sickInfo.getTisane());

        tvSickReason.setText(sickInfo.getPathogeny());
        tvSickCnName.setText(sickInfo.getName());
        tvSickWestName.setText(sickInfo.getOffsickName());
        tvSickWestName.setTag(sickInfo.getOfficeId() + "-" + sickInfo.getOffsickId());
        tvSickTherapies.setText(sickInfo.getTherapies());
        tvMedicineName.setText(sickInfo.getRepiceName());

        for (int i = sickInfo.getMedList().size() - 1; i >= 0; i--) {
            MedicineInfo medicine = sickInfo.getMedList().get(i);
            if (medicine.getTyp().equals("1")) {
                tvMedicineCode.setTag(medicine);
                tvMedicineCode.setText(medicine.getGdName());
                sickInfo.getMedList().remove(medicine);
                break;
            }
        }
        medicines1 = sickInfo.getMedList();
        herbalAdapter1.setData(medicines1);
        httpCost();
        filterDispatch(medicines1.get(0).getCenterId());
    }

    private void addReportItem(DicSick dicSick, String type, String imagePath) {
        if (reportList == null) reportList = new ArrayList<>();
        Report report = new Report();
        report.setTyp(type);
        report.setIsNorm("2");
        report.setPosition(dicSick.getDataValue());
        report.setPositionName(dicSick.getDataName());
        report.setUrl(imagePath);
        reportList.add(report);
    }

    private void addImageToContent(String type, String name, String imagePath) {
        final LinearLayout ll;
        if (type.equals("1")) {
            ll = findViewById(R.id.ll_treat_lab);
        } else if (type.equals("2")) {
            ll = findViewById(R.id.ll_treat_img);
        } else {
            ll = findViewById(R.id.ll_treat_see);
        }
        final View view = getLayoutInflater().inflate(R.layout.item_treat_img, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(0, 0, 12, 0);
        view.setLayoutParams(layoutParams);
        view.setTag(imagePath);
        ImageView imageView = view.findViewById(R.id.iv_img);
        Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + imagePath).into(imageView);
        imageView.setTag(R.id.tag_image_name, name);
        imageView.setTag(R.id.tag_image_path, ServerAPI.FILL_DOMAIN + imagePath);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgName = (String) v.getTag(R.id.tag_image_name);
                String imgPath = (String) v.getTag(R.id.tag_image_path);
                new ImageGlideDialog(getContext(), imgPath, imgName).show();
            }
        });
        TextView tvName = view.findViewById(R.id.tv_position_name);
        tvName.setText(name);
        View btnDel = view.findViewById(R.id.btn_del);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < ll.getChildCount() - 1; i++) {
                    if (ll.getChildAt(i) == view) {
                        ll.removeViewAt(i);
                    }
                }
                String tag = (String) view.getTag();
                for (int i = 0; i < reportList.size(); i++) {
                    if (reportList.get(i).getUrl().equals(tag)) {
                        reportList.remove(i);
                        break;
                    }
                }
            }
        });
        if (isPay) btnDel.setVisibility(View.GONE);
        ll.addView(view, ll.getChildCount() - 1);
    }

    private void showMbrInfo(MbrInfo mbrInfo) {
        if (mbrInfo.getPriceType().equals("1")) {
            findViewById(R.id.ll_act_treat_xishu).setVisibility(View.INVISIBLE);
        }
        TextView tvName = findViewById(R.id.tv_treat_mbr_name);
        addText(tvName, mbrInfo.getName());
        TextView tvSex = findViewById(R.id.tv_treat_mbr_sex);
        String sex = mbrInfo.getSex().equals("1") ? "男" : "女";
        addText(tvSex, sex);
        TextView tvAge = findViewById(R.id.tv_treat_mbr_age);
        addText(tvAge, mbrInfo.getAge() + "岁");
        TextView tvMobile = findViewById(R.id.tv_treat_mbr_mobile);
        addText(tvMobile, mbrInfo.getPhone());
        TextView tvCard = findViewById(R.id.tv_treat_mbr_card);
        addText(tvCard, mbrInfo.getIdNumber());
        TextView tvAdd = findViewById(R.id.tv_treat_mbr_add);
        String add = mbrInfo.getBirthCounty() + mbrInfo.getBirthProvince() + mbrInfo.getBirthCity();
        if (mbrInfo.getBirthCounty() != null && mbrInfo.getBirthCounty().equals("中国")) {
            add = mbrInfo.getBirthProvince() + mbrInfo.getBirthCity();
        }
        addText(tvAdd, add);
        TextView tvAddDetail = findViewById(R.id.tv_treat_mbr_add_detail);
        String addDetail = mbrInfo.getProvince() + mbrInfo.getCity() + mbrInfo.getCountry() + mbrInfo.getAddress();
        addText(tvAddDetail, addDetail);
        ImageView ivPhoto = findViewById(R.id.iv_treat_mbr_photo);
        Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + mbrInfo.getPersonimg()).into(ivPhoto);

        findViewById(R.id.btn_act_treat_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkyEngineKit.init(new VoipEvent(TreatActivity.this));
                if (!BuildConfig.DEBUG)
                    CallSingleActivity.openActivity(TreatActivity.this, mbrId, true, false);
                else
                    CallSingleActivity.openActivity(TreatActivity.this, "111111", true, false);
            }
        });
    }

    private void addText(TextView tv, String text) {
        if (text == null) return;
        tv.setText(tv.getText() + text);
    }

    private void showMbrSickInfo(MbrSickInfo sickInfo) {
        TextView tvJws = findViewById(R.id.tv_treat_sick_jiwangshi);
        tvJws.setText(getSickText(sickInfo.getOpration()));
        Opration opration = new Opration();
        opration.setOther(sickInfo.getOpration().getOther());
        opration.setFood(sickInfo.getOpration().getFood());
        opration.setAllergy(sickInfo.getOpration().getAllergy());
        tvJws.setTag(opration);

        TextView tvGrs = findViewById(R.id.tv_treat_sick_gerenshi);
        tvGrs.setText(getSickText(sickInfo.getPersonal()));
        Personal personal = new Personal();
        personal.setHabit(sickInfo.getPersonal().getHabit());
        if (sickInfo.getPersonal().getChild() != null)
            personal.setChild(sickInfo.getPersonal().getChild().getKeyValue());
        if (sickInfo.getPersonal().getMarry() != null)
            personal.setMarry(sickInfo.getPersonal().getMarry().getKeyValue());
        tvGrs.setTag(personal);

        TextView tvJzs = findViewById(R.id.tv_treat_sick_jiazushi);
        tvJzs.setText(getSickText(sickInfo.getHeredity()));
    }

    private void initAddAdapter(List<AddListInfo> addList) {
        AddListInfo emptyInfo = new AddListInfo();
        addList.add(0, emptyInfo);
        Spinner spReceiver = findViewById(R.id.sp_treat_content_receiver);
        ReceiverAdapter adapter = new ReceiverAdapter();
        spReceiver.setAdapter(adapter);
        adapter.setData(addList);
        spReceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDiaAdd) {
                    isDiaAdd = false;
                } else {
                    ReceiverAdapter rateAdapter = (ReceiverAdapter) parent.getAdapter();
                    List<AddListInfo> rateList = rateAdapter.areaList;
                    AddListInfo addInfo = rateList.get(position);
                    TextView tvAdd = findViewById(R.id.et_treat_content_receiver);
                    if (addInfo.getReName() != null)
                        tvAdd.setText(addInfo.getProvinceName() + addInfo.getCityName() + addInfo.getCountryName() + addInfo.getAddress());
                    else
                        tvAdd.setText(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getSickText(List<String> list) {
        if (list == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            sb.append(";");
        }
        return sb.toString();
    }

    private String getAllergyText(List<String> list) {
        if (list == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            sb.append("过敏;");
        }
        return sb.toString();
    }

    private String getSickText(SickInfoPersonal personal) {
        StringBuilder sb = new StringBuilder();
        if (personal.getMarry() != null && personal.getMarry().getKeyName() != null) {
            sb.append(personal.getMarry().getKeyName());
            sb.append(";");
        }
        if (personal.getChild() != null && personal.getChild().getKeyName() != null) {
            sb.append(personal.getChild().getKeyName());
            sb.append(";");
        }
        sb.append(getSickText(personal.getHabit()));
        return sb.toString();
    }

    private String getSickText(SickInfoOpration opration) {
        StringBuilder sb = new StringBuilder();
        sb.append(getAllergyText(opration.getAllergy()));
        sb.append(getAllergyText(opration.getFood()));
        sb.append(getSickText(opration.getOther()));
        return sb.toString();
    }

    //初始化问诊按钮
    private void initJudge() {
        btnJudge[0] = findViewById(R.id.tv_treat_btn_judge_0);
        btnJudge[1] = findViewById(R.id.tv_treat_btn_judge_1);
        btnJudge[2] = findViewById(R.id.tv_treat_btn_judge_2);
        btnJudge[3] = findViewById(R.id.tv_treat_btn_judge_3);
        contentJudge[0] = findViewById(R.id.ll_treat_judge_0);
        contentJudge[1] = findViewById(R.id.ll_treat_judge_1);
        contentJudge[2] = findViewById(R.id.ll_treat_judge_2);
        contentJudge[3] = findViewById(R.id.ll_treat_judge_3);
        for (int i = 0; i < btnJudge.length; i++) {
            btnJudge[i].setTag(i);
            btnJudge[i].setOnClickListener(btnJudgeClicked);
        }
        btnJudge[0].performClick();

    }

    View.OnClickListener btnJudgeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            for (int i = 0; i < btnJudge.length; i++) {
                if (i == index) {
                    btnJudge[i].setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_red));
                    btnJudge[i].setTypeface(Typeface.DEFAULT_BOLD);
                    contentJudge[i].setVisibility(View.VISIBLE);
                } else {
                    btnJudge[i].setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_black));
                    btnJudge[i].setTypeface(Typeface.DEFAULT);
                    contentJudge[i].setVisibility(View.GONE);
                }
            }
        }
    };

    private void initRecipe() {
        btnCommonRecipe = findViewById(R.id.tv_treat_btn_common_recipe);
        btnPersonalRecipe = findViewById(R.id.tv_treat_btn_personal_recipe);
        tvTipRecipe = findViewById(R.id.tv_treat_tip_recipe);
        llMedRcpCon = findViewById(R.id.ll_medicine_recipe_content);
        llPsnRcpCon = findViewById(R.id.ll_personal_recipe_content);
        btnCommonRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (popupMenu != null && popupMenu.isShowing()) {
//                    popupMenu.dismiss();
//                    popupMenu = null;
//                }
                medRes = MED_RES_DATA;
                btnCommonRecipe.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_red));
                btnCommonRecipe.setTypeface(Typeface.DEFAULT_BOLD);
                btnPersonalRecipe.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_black));
                btnPersonalRecipe.setTypeface(Typeface.DEFAULT);
                tvTipRecipe.setText(R.string.tip_act_treat_recipe_common);
                llMedRcpCon.setVisibility(View.VISIBLE);
                llPsnRcpCon.setVisibility(View.GONE);
                if (findViewById(R.id.btn_act_treat_save_recipe) != null) {
                    findViewById(R.id.btn_act_treat_open_sample).setVisibility(View.GONE);
                    findViewById(R.id.btn_act_treat_open_recipe).setVisibility(View.GONE);
                    findViewById(R.id.btn_act_treat_save_recipe).setVisibility(View.GONE);
                    findViewById(R.id.btn_act_treat_reset_med).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_act_treat_no_med).setVisibility(View.VISIBLE);
                }
                herbalAdapter1.setData(medicines1);
                if (medicines1 != null && medicines1.size() > 0) {
                    filterDispatch(medicines1.get(0).getCenterId());
                } else {
                    filterDispatch(null);
                }
                httpCost();
            }
        });
        btnPersonalRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (popupMenu != null && popupMenu.isShowing()) {
//                    popupMenu.dismiss();
//                    popupMenu = null;
//                }
                medRes = MED_RES_PER;
                btnPersonalRecipe.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_red));
                btnPersonalRecipe.setTypeface(Typeface.DEFAULT_BOLD);
                btnCommonRecipe.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_black));
                btnCommonRecipe.setTypeface(Typeface.DEFAULT);
                tvTipRecipe.setText(R.string.tip_act_treat_recipe_personal);
                llMedRcpCon.setVisibility(View.GONE);
                llPsnRcpCon.setVisibility(View.VISIBLE);
                if (findViewById(R.id.btn_act_treat_save_recipe) != null) {
                    findViewById(R.id.btn_act_treat_open_sample).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_act_treat_open_recipe).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_act_treat_save_recipe).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_act_treat_reset_med).setVisibility(View.GONE);
                    findViewById(R.id.btn_act_treat_no_med).setVisibility(View.GONE);
                }
                herbalAdapter2.setData(medicines2);
                if (medicines2 != null && medicines2.size() > 0) {
                    filterDispatch(medicines2.get(0).getCenterId());
                } else {
                    filterDispatch(null);
                }
                httpCost();
            }
        });
        if (findViewById(R.id.btn_act_treat_save_recipe) != null) {
            findViewById(R.id.btn_act_treat_save_recipe).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tvCnName = findViewById(R.id.tv_treat_sick_cn_name);
                    if (TextUtils.isEmpty(tvCnName.getText())) {
                        ToastUtils.show(getContext(), "请填写中医诊断名称！");
                        return;
                    }
                    if (medicines2 == null || medicines2.size() == 0) {
                        ToastUtils.show(getContext(), "请配置自己处方！");
                        return;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cnName", getViewText(R.id.tv_treat_sick_cn_name));
                    jsonObject.put("westName", getViewText(R.id.tv_treat_sick_west_name));
                    jsonObject.put("therapies", getViewText(R.id.tv_treat_sick_therapies));
                    jsonObject.put("pathogeny", getViewText(R.id.tv_treat_sick_reason));
                    jsonObject.put("tisane", getViewText(R.id.et_treat_content_yongyaoshuoming));
                    jsonObject.put("doctorAdvice", getViewText(R.id.et_treat_content_yizhu));
                    jsonObject.put("sickId", tvWestSickName.getTag());
                    jsonObject.put("medList", medicines2);
                    Intent intent = new Intent(getContext(), MyRecipeAddActivity.class);
                    intent.putExtra("type", MyRecipeAddActivity.TYPE_SAVE);
                    intent.putExtra("data", jsonObject.toJSONString());
                    startActivity(intent);
                }
            });
            findViewById(R.id.btn_act_treat_reset_med).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ebmSickInfo != null) setEbmResult(ebmSickInfo);
                }
            });
            findViewById(R.id.btn_act_treat_no_med).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tvMedicineName = findViewById(R.id.tv_treat_medicine_name);
                    TextView tvMedicineCode = findViewById(R.id.tv_treat_medicine_code);
                    tvMedicineName.setText(null);
                    tvMedicineCode.setText(null);
                    tvMedicineCode.setTag(null);
                    if (medicines1 != null && medicines1.size() > 0) {
                        medicines1.clear();
                        herbalAdapter1.notifyDataSetChanged();
                    }
                    setViewText(R.id.et_treat_content_number, "0");
                    Spinner sp = findViewById(R.id.sp_treat_boil_type);
                    sp.setSelection(1);
                    findViewById(R.id.ll_act_treat_dispatch).setVisibility(View.GONE);
                }
            });
            findViewById(R.id.btn_act_treat_open_recipe).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MyRecipeActivity.class);
                    intent.putExtra("openType", MyRecipeActivity.OPEN_TYPE_RESULT);
                    startActivityForResult(intent, RECIPE_REQUEST_CODE);
                }
            });
            findViewById(R.id.btn_act_treat_open_sample).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SickSampleActivity.class);
                    startActivityForResult(intent, SICK_SAMPLE_CODE);
                }
            });
        } else {
            findViewById(R.id.btn_act_treat_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (popupMenu != null && popupMenu.isShowing()) {
//                        popupMenu.dismiss();
//                        popupMenu = null;
//                        return;
//                    }
                    View view;
                    if (medRes == MED_RES_DATA) {
                        view = getLayoutInflater().inflate(R.layout.menu_treat_ebm, null);
                        view.findViewById(R.id.btn_act_treat_reset_med).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ebmSickInfo != null) setEbmResult(ebmSickInfo);
                                popupMenu.dismiss();
                            }
                        });
                        view.findViewById(R.id.btn_act_treat_no_med).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView tvMedicineName = findViewById(R.id.tv_treat_medicine_name);
                                TextView tvMedicineCode = findViewById(R.id.tv_treat_medicine_code);
                                tvMedicineName.setText(null);
                                tvMedicineCode.setText(null);
                                tvMedicineCode.setTag(null);
                                if (medicines1 != null && medicines1.size() > 0) {
                                    medicines1.clear();
                                    herbalAdapter1.notifyDataSetChanged();
                                }
                                setViewText(R.id.et_treat_content_number, "0");
                                Spinner sp = findViewById(R.id.sp_treat_boil_type);
                                sp.setSelection(1);
                                findViewById(R.id.ll_act_treat_dispatch).setVisibility(View.GONE);
                                popupMenu.dismiss();
                            }
                        });
                    } else {
                        view = getLayoutInflater().inflate(R.layout.menu_treat_user, null);
                        view.findViewById(R.id.btn_act_treat_save_recipe).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView tvCnName = findViewById(R.id.tv_treat_sick_cn_name);
                                if (TextUtils.isEmpty(tvCnName.getText())) {
                                    ToastUtils.show(getContext(), "请填写中医诊断名称！");
                                    return;
                                }
                                if (medicines2 == null || medicines2.size() == 0) {
                                    ToastUtils.show(getContext(), "请配置自己处方！");
                                    return;
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("cnName", getViewText(R.id.tv_treat_sick_cn_name));
                                jsonObject.put("westName", getViewText(R.id.tv_treat_sick_west_name));
                                jsonObject.put("therapies", getViewText(R.id.tv_treat_sick_therapies));
                                jsonObject.put("pathogeny", getViewText(R.id.tv_treat_sick_reason));
                                jsonObject.put("tisane", getViewText(R.id.et_treat_content_yongyaoshuoming));
                                jsonObject.put("doctorAdvice", getViewText(R.id.et_treat_content_yizhu));
                                jsonObject.put("sickId", tvWestSickName.getTag());
                                jsonObject.put("medList", medicines2);
                                Intent intent = new Intent(getContext(), MyRecipeAddActivity.class);
                                intent.putExtra("type", MyRecipeAddActivity.TYPE_SAVE);
                                intent.putExtra("data", jsonObject.toJSONString());
                                startActivity(intent);
                                popupMenu.dismiss();
                            }
                        });
                        view.findViewById(R.id.btn_act_treat_open_recipe).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), MyRecipeActivity.class);
                                intent.putExtra("openType", MyRecipeActivity.OPEN_TYPE_RESULT);
                                startActivityForResult(intent, RECIPE_REQUEST_CODE);
                                popupMenu.dismiss();
                            }
                        });
                        view.findViewById(R.id.btn_act_treat_open_sample).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), SickSampleActivity.class);
                                startActivityForResult(intent, SICK_SAMPLE_CODE);
                                popupMenu.dismiss();
                            }
                        });
                    }
                    popupMenu = new PopupWindow(view, -2, -2, true);
                    popupMenu.showAsDropDown(v);
                }
            });
        }
        findViewById(R.id.btn_act_treat_add_med1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repiceId == null) {
                    ToastUtils.show(getContext(), "请先选择中医循证验方！");
                    return;
                }
                Intent intent = new Intent(getContext(), MedicineAddActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("repiceId", repiceId);
                startActivityForResult(intent, MED_ADD_CODE);
            }
        });
        findViewById(R.id.btn_act_treat_add_med2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MedicineAddActivity.class);
                intent.putExtra("type", 2);
                if (medicines2 != null && medicines2.size() > 0) {
                    intent.putExtra("centerId", medicines2.get(0).getCenterId());
                }
                startActivityForResult(intent, MED_ADD_CODE);
            }
        });
        btnCommonRecipe.performClick();
    }

    private void initDispatch() {
        dispatchList = new ArrayList<>();
        DispatchListInfo emptyInfo = new DispatchListInfo();
        dispatchList.add(emptyInfo);
        spDispatch = findViewById(R.id.sp_treat_content_peisong);
        dispatchAdapter = new DispatchAdapter();
        dispatchAdapter.setData(dispatchList);
        spDispatch.setAdapter(dispatchAdapter);
        spDispatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                httpCost();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        httpGetDispatchList();
    }

    private void filterDispatch(String centerId) {
        findViewById(R.id.ll_act_treat_dispatch).setVisibility(View.VISIBLE);
        if (centerId == null || centerId.equals("")) {
            if (dispatchList == null) return;
            dispatchList.clear();
            dispatchList.add(new DispatchListInfo());
            dispatchAdapter.notifyDataSetChanged();
            spDispatch.setSelection(0);
            return;
        }
        String customerId = "7517865838033954000";
        if (dispatchAll != null) {
            dispatchList.clear();
            dispatchList.add(getDispatchById(centerId));
            if (!centerId.equals(customerId)) {
                dispatchList.add(getDispatchById(customerId));
            }
            dispatchAdapter.notifyDataSetChanged();
            spDispatch.setSelection(0);
        }
    }

    private DispatchListInfo getDispatchById(String id) {
        for (int i = 0; i < dispatchAll.size(); i++) {
            if (dispatchAll.get(i).getCenterId().equals(id)) {
                return dispatchAll.get(i);
            }
        }
        return null;
    }

    private void onSickReturn() {
        findViewById(R.id.ib_treat_btn_jiwangshi).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_gerenshi).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_jiazushi).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_xianbingshi).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_tizheng).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_mianbu).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_shetou).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_shouzhang).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_qiwei).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_shengyin).setOnClickListener(btnAddClicked);
        findViewById(R.id.ib_treat_btn_maixiang).setOnClickListener(btnAddClicked);

        findViewById(R.id.ib_treat_btn_rule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvRule = findViewById(R.id.tv_treat_content_rule);
                new DicRuleDialog(getContext(), diagnoseInfo.getPicTypeObj().getIndicatorObj(), tvRule).show();
            }
        });

        Spinner spBoilType = findViewById(R.id.sp_treat_boil_type);
        SpinnerAdapter rateAdapter = new SpinnerAdapter();
        DicSick dicSick = new DicSick();
        dicSick.setDataName("不煎药");
        dicSick.setDataValue("0");
        dicSick.setDataDesc("0");
        dicSick.setDicId("0");
        diagnoseInfo.getDealTypeObj().add(0, dicSick);
        DicSick dicEmpty = new DicSick();
        dicEmpty.setDataName("");
        dicEmpty.setDataValue("-1");
        dicEmpty.setDataDesc("-1");
        dicEmpty.setDicId("-1");
        diagnoseInfo.getDealTypeObj().add(0, dicEmpty);
        rateAdapter.setData(diagnoseInfo.getDealTypeObj());
        spBoilType.setAdapter(rateAdapter);
        spBoilType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                httpCost();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    View.OnClickListener btnAddClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = "";
            TextView textView = null;
            Object object = null;
            switch (v.getId()) {
                case R.id.ib_treat_btn_jiwangshi:
                    title = "既往史";
                    textView = findViewById(R.id.tv_treat_sick_jiwangshi);
                    object = diagnoseInfo.getOprationObj();
                    break;
                case R.id.ib_treat_btn_gerenshi:
                    title = "个人史";
                    textView = findViewById(R.id.tv_treat_sick_gerenshi);
                    object = diagnoseInfo.getPersonalObj();
                    break;
                case R.id.ib_treat_btn_jiazushi:
                    title = "家族史";
                    textView = findViewById(R.id.tv_treat_sick_jiazushi);
                    object = diagnoseInfo.getHeredityObj();
                    break;
                case R.id.ib_treat_btn_xianbingshi:
                    title = "现病史";
                    textView = findViewById(R.id.tv_treat_content_xianbingshi);
                    object = diagnoseInfo.getAskObj();
                    break;
                case R.id.ib_treat_btn_tizheng:
                    title = "体征";
                    textView = findViewById(R.id.tv_treat_content_tizheng);
                    object = diagnoseInfo.getLookObj().getLkBodyObj();
                    break;
                case R.id.ib_treat_btn_mianbu:
                    title = "面部";
                    textView = findViewById(R.id.tv_treat_content_mianbu);
                    object = diagnoseInfo.getLookObj().getLkFaceObj();
                    break;
                case R.id.ib_treat_btn_shetou:
                    title = "舌头";
                    textView = findViewById(R.id.tv_treat_content_shetou);
                    object = diagnoseInfo.getLookObj().getLkTongueObj();
                    break;
                case R.id.ib_treat_btn_shouzhang:
                    title = "手掌";
                    textView = findViewById(R.id.tv_treat_content_shouzhang);
                    object = diagnoseInfo.getLookObj().getHandObj();
                    break;
                case R.id.ib_treat_btn_qiwei:
                    title = "气味";
                    textView = findViewById(R.id.tv_treat_content_qiwei);
                    object = diagnoseInfo.getListenObj().getLiOdorObj();
                    break;
                case R.id.ib_treat_btn_shengyin:
                    title = "声音";
                    textView = findViewById(R.id.tv_treat_content_shengyin);
                    object = diagnoseInfo.getListenObj().getLiVoiceObj();
                    break;
                case R.id.ib_treat_btn_maixiang:
                    title = "脉象";
                    textView = findViewById(R.id.tv_treat_content_maixiang);
                    object = diagnoseInfo.getPulseObj();
                    break;
            }
            new DicSickDialog(getContext(), title, object, textView).show();
        }
    };

    class GridHerbalAdapter extends RecyclerView.Adapter<HerbalHolder> {

        private List<MedicineInfo> mList = null;

        public void setData(List<MedicineInfo> list) {
            mList = list;
            notifyDataSetChanged();
        }

        public List<MedicineInfo> getList() {
            return mList;
        }

        @NonNull
        @Override
        public HerbalHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_treat_herbal_list, viewGroup, false);
            return new HerbalHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HerbalHolder herbalHolder, int i) {
            herbalHolder.setIsRecyclable(false);
            herbalHolder.setState(i);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    class HerbalHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvValue, tvUnit;
        ImageButton btnDel;

        public HerbalHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_treat_med_item_name);
            tvValue = itemView.findViewById(R.id.tv_treat_med_item_value);
            tvUnit = itemView.findViewById(R.id.tv_treat_med_item_unit);
            btnDel = itemView.findViewById(R.id.ib_treat_med_item_del);
        }

        public void setState(int position) {
            final MedicineInfo medicine = getHerbalAdapter().getList().get(position);
            tvName.setText(medicine.getGdName());
            tvValue.setText(medicine.getUseNum());
            tvValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s)) {
                        medicine.setUseNum("0");
                        httpCost();
                    } else if (!s.toString().equals(medicine.getUseNum())) {
                        medicine.setUseNum(s.toString());
                        httpCost();
                    }
                }
            });

            tvUnit.setText(medicine.getMedUnit());
            btnDel.setTag(position);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    getHerbalAdapter().getList().remove(index);
                    getHerbalAdapter().notifyDataSetChanged();
                    httpCost();
                }
            });
            if (isPay) {
                tvValue.setEnabled(false);
                btnDel.setVisibility(View.INVISIBLE);
            } else {
                tvValue.setEnabled(true);
                btnDel.setVisibility(View.VISIBLE);
            }
        }
    }

    class SpinnerAdapter extends BaseAdapter {

        List<DicSick> areaList;

        public void setData(List<DicSick> areaList) {
            this.areaList = areaList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return areaList == null ? 0 : areaList.size();
        }

        @Override
        public Object getItem(int position) {
            return areaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(TreatActivity.this);
            textView.setPadding(12, 6, 12, 6);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_text_tag));
            textView.setText(areaList.get(position).getDataName());
            convertView = textView;
            return convertView;
        }
    }

    //获取病人信息
    private void httpMbrInfo() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getTreatMbrUrl(mbrId);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.d("TreatActivity", "mbrResult=" + result);
                JSONObject json = JSONObject.parseObject(result);
                int code = json.getIntValue("code");
                if (code == 0) {
                    MbrInfo mbrInfo = json.getJSONObject("data").getJSONObject("mbrObj").toJavaObject(MbrInfo.class);
                    showMbrInfo(mbrInfo);
                    MbrSickInfo mbrSickInfo = json.getJSONObject("data").getJSONObject("mbrSickObj").toJavaObject(MbrSickInfo.class);
                    showMbrSickInfo(mbrSickInfo);
                    List<AddListInfo> addList = json.getJSONObject("data").getJSONArray("addList").toJavaList(AddListInfo.class);
                    initAddAdapter(addList);
                    httpDiagnoseSource(mbrInfo.getPriceType());
                } else {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(TreatActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    boolean isLoadOver = false;

    //获取诊断源数据
    private void httpDiagnoseSource(String type) {
        String url = ServerAPI.getTreatSourceUrl(type);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                diagnoseInfo = JSONObject.parseObject(data, DiagnoseInfo.class);
                onSickReturn();
                if (diaId != null) {
                    httpDiagnose(diaId);
                } else {
                    isLoadOver = true;
                    httpCost();
                    hiddenLoadView();
                }
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

    class PatentAdapter extends RecyclerView.Adapter<PatentHolder> {

        @NonNull
        @Override
        public PatentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_treat_patent_list, viewGroup, false);
            return new PatentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PatentHolder patentHolder, int i) {
            patentHolder.setIsRecyclable(false);
            patentHolder.setState(i);
        }

        @Override
        public int getItemCount() {
            return patentInfos.size();
        }

    }

    class PatentHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvNum, tvUnit;
        View btnDel;

        public PatentHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_patent_item_name);
            tvPrice = itemView.findViewById(R.id.tv_patent_item_price);
            tvNum = itemView.findViewById(R.id.tv_patent_item_num);
            tvUnit = itemView.findViewById(R.id.tv_patent_item_unit);
            btnDel = itemView.findViewById(R.id.ib_patent_item_del);
        }

        public void setState(int position) {
            final MedicineInfo patentInfo = patentInfos.get(position);
            tvName.setText(patentInfo.getGdName());
            tvPrice.setText("单价：" + patentInfo.getRetailPrice() + "元");
            tvNum.setText(patentInfo.getUseNum());
            tvNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s)) {
                        patentInfo.setUseNum("0");
                        httpCost();
                    } else if (!s.toString().equals(patentInfo.getUseNum())) {
                        patentInfo.setUseNum(s.toString());
                        httpCost();
                    }
                }
            });
            tvUnit.setText(patentInfo.getMedUnit());
            btnDel.setTag(position);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (Integer) btnDel.getTag();
                    patentInfos.remove(index);
                    patentAdapter.notifyDataSetChanged();
                    httpCost();
                }
            });
            if (isPay) {
                tvNum.setEnabled(false);
                btnDel.setVisibility(View.INVISIBLE);
            } else {
                tvNum.setEnabled(true);
                btnDel.setVisibility(View.VISIBLE);
            }
        }
    }

    //问诊信息
    //------------------------------------------------------------------
    private void httpDiagnose(String id) {
        String url = ServerAPI.getDiagnoseUrl(id);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Log.d("TreatActivity_diagnose", data);
                TreatParameter parameter = JSONObject.parseObject(data, TreatParameter.class);
                if (mbrId == null) {
                    BasicInfo basicInfo = parameter.getBasicInfoObj();
                    mbrId = basicInfo.getMbrId();
                    regId = basicInfo.getRegistrationId();
                    diaId = basicInfo.getDiagnoseId();
                    httpMbrInfo();
                } else {
                    onDiagnoseReturn(parameter);
                    if (isPay)
                        onPaySuccess();
                    else {
                        httpCost();
                    }
                    isLoadOver = true;
//                    httpCost();
                }
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

    boolean isDiaAdd = false;

    private void onDiagnoseReturn(TreatParameter parameter) {
        BasicInfo basicInfo = parameter.getBasicInfoObj();
        setViewText(R.id.tv_treat_content_zhusu, basicInfo.getComplaint());
        setViewText(R.id.tv_treat_content_xianbingshi, basicInfo.getPresent());
        setViewText(R.id.tv_treat_content_tizheng, basicInfo.getRepresentation());
        setViewText(R.id.tv_treat_content_mianbu, basicInfo.getFace());
        setViewText(R.id.tv_treat_content_shetou, basicInfo.getFurredTongue());
        setViewText(R.id.tv_treat_content_shouzhang, basicInfo.getHand());
        setViewText(R.id.et_treat_content_more1, basicInfo.getSeeDesc());
        setViewText(R.id.tv_treat_content_qiwei, basicInfo.getSmell());
        setViewText(R.id.tv_treat_content_shengyin, basicInfo.getVoice());
        setViewText(R.id.et_treat_content_more2, basicInfo.getVoiceDesc());
        setViewText(R.id.tv_treat_content_maixiang, basicInfo.getPulse());
        setViewText(R.id.et_treat_content_more3, basicInfo.getPulseDesc());
        sickId = basicInfo.getSickId();
        setViewText(R.id.tv_treat_sick_cn_name, basicInfo.getSickName());
        setViewText(R.id.tv_treat_sick_west_name, basicInfo.getWestSickName());
        tvWestSickName.setTag(basicInfo.getWestSickId());
        setViewText(R.id.tv_treat_sick_reason, basicInfo.getPathogeny());
        setViewText(R.id.tv_treat_sick_therapies, basicInfo.getTherapy());
        setViewText(R.id.et_treat_content_yizhu, basicInfo.getDoctorAdvice());
        String reviewDate = basicInfo.getRevistDate();
        if (reviewDate != null && reviewDate.length() > 0) {
            String date = reviewDate.substring(0, 4) + "-" + reviewDate.substring(4, 6) + "-" + reviewDate.substring(6);
            setViewText(R.id.tv_treat_btn_review_time, date);
        }
        setViewText(R.id.tv_treat_content_yaofei, basicInfo.getMedicineFee() + "元");
        setViewText(R.id.tv_treat_content_jianyaofei, basicInfo.getDealFee() + "元");
        setViewText(R.id.tv_treat_content_chengpinyaofei, basicInfo.getBoxMedicineFee() + "元");
        setViewText(R.id.tv_treat_content_feiyongheji, "费用合计：" + basicInfo.getSumFee() + "元");
        Spinner spRate = findViewById(R.id.sp_treat_content_xishu);
        SpinnerAdapter rateAdapter = (SpinnerAdapter) spRate.getAdapter();
        List<DicSick> rateList = rateAdapter.areaList;
        for (int i = 0; i < rateList.size(); i++) {
            if (rateList.get(i).getDataValue().equals(basicInfo.getDiagRadio())) {
                spRate.setSelection(i);
                break;
            }
        }

        filterDispatch(basicInfo.getCenterId());
        Spinner spReceiver = findViewById(R.id.sp_treat_content_receiver);
        ReceiverAdapter receiverAdapter = (ReceiverAdapter) spReceiver.getAdapter();
        List<AddListInfo> addList = receiverAdapter.areaList;
        for (int i = 1; i < addList.size(); i++) {
            if (addList.get(i).getReName().equals(basicInfo.getReName())) {
                spReceiver.setSelection(i);
                break;
            }
        }
        isDiaAdd = true;
        setViewText(R.id.et_treat_content_receiver, basicInfo.getSendAddress());
        //---------------------------------------------------------------------
        Revisit revisit = parameter.getRevisitObj();
        setViewText(R.id.et_treat_content_number, revisit.getUseNum());
        setViewText(R.id.et_treat_content_days, revisit.getDayNum());
        setViewText(R.id.et_treat_content_yongyaoshuoming, revisit.getInstruction());
        Spinner spBoil = findViewById(R.id.sp_treat_boil_type);
        if (spBoil.getAdapter() instanceof SpinnerAdapter) {
            SpinnerAdapter boilAdapter = (SpinnerAdapter) spBoil.getAdapter();
            List<DicSick> boilList = boilAdapter.areaList;
            for (int i = 0; i < boilList.size(); i++) {
                if (boilList.get(i).getDataValue().equals(revisit.getDealFlag())) {
                    spBoil.setSelection(i);
                    break;
                }
            }
        }
        //---------------------------------------------------------------------
        List<Med> boxMedList = parameter.getBoxMedArr();
        if (boxMedList != null && boxMedList.size() > 0) {
            List<MedicineInfo> boxMedInfos = new ArrayList<>();
            for (Med med : boxMedList) {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedicineId(med.getMedicineId());
                medInfo.setGdName(med.getGdName());
                medInfo.setUseNum(med.getUseNum());
                medInfo.setMedUnit(med.getMedUnit());
                medInfo.setTyp(med.getTyp());
                medInfo.setRetailPrice(med.getPrice());
                medInfo.setXrPice(med.getXrpice());
                boxMedInfos.add(medInfo);
            }
            patentInfos.addAll(boxMedInfos);
            patentAdapter.notifyDataSetChanged();
        }
        //---------------------------------------------------------------------
        List<Med> medList = parameter.getMedArr();
        if (medList != null && medList.size() > 0) {
            List<MedicineInfo> medInfos = new ArrayList<>();
            for (Med med : medList) {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedicineId(med.getMedicineId());
                medInfo.setGdName(med.getGdName());
                medInfo.setUseNum(med.getUseNum());
                medInfo.setMedUnit(med.getMedUnit());
                medInfo.setTyp(med.getTyp());
                medInfo.setRetailPrice(med.getPrice());
                medInfo.setXrPice(med.getXrpice());
                medInfo.setCenterId(med.getCenterId());
                medInfos.add(medInfo);
            }
            if (revisit.getTyp() == 1) {
                if (medInfos.size() > 0) {
                    for (int i = medInfos.size() - 1; i >= 0; i--) {
                        MedicineInfo medicine = medInfos.get(i);
                        if (medicine.getTyp().equals("1")) {
                            TextView tvCode = findViewById(R.id.tv_treat_medicine_code);
                            tvCode.setTag(medicine);
                            tvCode.setText(medicine.getGdName());
                            medInfos.remove(medicine);
                            break;
                        }
                    }
                }
                setViewText(R.id.tv_treat_medicine_name, revisit.getRepiceName());
                repiceId = revisit.getRepiceId();
                medicines1 = medInfos;
                btnCommonRecipe.performClick();
            } else {
                medicines2 = medInfos;
                btnPersonalRecipe.performClick();
            }
        }
        //--------------------------------------------------------------------
        reportList = parameter.getReportArr();
        for (int i = 0; i < reportList.size(); i++) {
            Report report = reportList.get(i);
            if (report.getIsNorm().equals("2")) {
                addImageToContent(report.getTyp(), report.getPositionName(), report.getUrl());
            } else {
                TextView tvNorm = findViewById(R.id.tv_treat_content_rule);
                tvNorm.setText(tvNorm.getText() + report.getNormVal() + ";");
            }
        }
        //--------------------------------------------------------------------
        recordList = parameter.getRecordArr();
    }

    //确定问诊
    //------------------------------------------------------------------
    //TODO 参数检查
    private boolean isFeasible() {
        TextView tvName = findViewById(R.id.tv_treat_sick_cn_name);
        if (TextUtils.isEmpty(tvName.getText())) {
            Toast.makeText(getContext(), "请填写中医诊断名称！", Toast.LENGTH_SHORT).show();
            return false;
        }
        TextView tvWestName = findViewById(R.id.tv_treat_sick_west_name);
        if (TextUtils.isEmpty(tvWestName.getText())) {
            Toast.makeText(getContext(), "请填写西医病名名称！", Toast.LENGTH_SHORT).show();
            return false;
        }
        List<MedicineInfo> medInfos = getHerbalAdapter().getList();
        if (medInfos != null && medInfos.size() > 0) {
            for (MedicineInfo med : medInfos) {
                if (med.getUseNum().equals("0")) {
                    ToastUtils.show(getContext(), "您有药引剂量未填写！");
                    return false;
                }
            }
        }
        if (patentInfos != null && patentInfos.size() > 0) {
            for (MedicineInfo boxMed : patentInfos) {
                if (boxMed.getUseNum().equals("0")) {
                    ToastUtils.show(getContext(), "您有成品药剂量未填写！");
                    return false;
                }
            }
        }
        EditText etNum = findViewById(R.id.et_treat_content_number);
        if (TextUtils.isEmpty(etNum.getText())) {
            ToastUtils.show(getContext(), "请输入用药副数！");
            return false;
        } else {
            List list = getHerbalAdapter().getList();
            if (list != null && list.size() > 0 && etNum.getText().toString().trim().equals("0")) {
                ToastUtils.show(getContext(), "请输入用药副数！");
                return false;
            }
        }
        EditText etDays = findViewById(R.id.et_treat_content_days);
        if (TextUtils.isEmpty(etDays.getText())) {
            ToastUtils.show(getContext(), "请输入用药天数！");
            return false;
        }
        Spinner spBoil = findViewById(R.id.sp_treat_boil_type);
        if (spBoil.getAdapter() instanceof SpinnerAdapter) {
            SpinnerAdapter boilAdapter = (SpinnerAdapter) spBoil.getAdapter();
            DicSick type = (DicSick) boilAdapter.getItem(spBoil.getSelectedItemPosition());
            if (type.getDataDesc().equals("-1")) {
                ToastUtils.show(getContext(), "请选择煎药类型！");
                return false;
            }
        }
        if (etNum.getText().toString().trim().equals("0")) {
            return true;
        }
        Spinner spPeisong = findViewById(R.id.sp_treat_content_peisong);
        if (spPeisong.getAdapter() instanceof DispatchAdapter) {
            DispatchAdapter boilAdapter = (DispatchAdapter) spPeisong.getAdapter();
            DispatchListInfo info = (DispatchListInfo) boilAdapter.getItem(spPeisong.getSelectedItemPosition());
            if (info.getCenterId() == null) {
                ToastUtils.show(getContext(), "请选择配送中心！");
                return false;
            }
        }
        Spinner spReceiver = findViewById(R.id.sp_treat_content_receiver);
        if (spReceiver.getAdapter() instanceof ReceiverAdapter) {
            ReceiverAdapter boilAdapter = (ReceiverAdapter) spReceiver.getAdapter();
            AddListInfo info = (AddListInfo) boilAdapter.getItem(spReceiver.getSelectedItemPosition());
            if (info.getReName() == null) {
                ToastUtils.show(getContext(), "请选择收货人！");
                return false;
            }
        }
        EditText etAddress = findViewById(R.id.et_treat_content_receiver);
        if (TextUtils.isEmpty(etAddress.getText())) {
            ToastUtils.show(getContext(), "请输入收货地址！");
            return false;
        }
        return true;
    }

    //TODO 提交
    private void httpSubmit(final boolean isConfirm, final int type) {
        if (!isFeasible()) return;
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getTreatSubmitUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        TreatParameter parameter = createTreatParameter();
        Log.d("TreatActivity_submit", JSONObject.toJSONString(parameter));
        entity.setBodyContent(JSONObject.toJSONString(parameter));
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                if (isConfirm) {
                    Toast.makeText(getContext(), "问诊成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    diaId = data;
                }
                switch (type) {
                    case 1:
                        Intent intentVisit = new Intent(getContext(), VisitOrderActivity.class);
                        intentVisit.putExtra("diaId", diaId);
                        startActivityForResult(intentVisit, PAY_STATE_CODE);
                        break;
                    case 2:
                        Intent intentRecipe = new Intent(getContext(), RecipeOrderActivity.class);
                        intentRecipe.putExtra("diaId", diaId);
                        startActivity(intentRecipe);
                        break;
                }
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

    private TreatParameter createTreatParameter() {
        TreatParameter treatParameter = new TreatParameter();
        //基本信息对象
        BasicInfo basicInfo = treatParameter.getBasicInfoObj();
        basicInfo.setDiagnoseId(diaId == null ? "" : diaId);
        basicInfo.setMbrId(mbrId);
        basicInfo.setRegistrationId(regId);
        basicInfo.setComplaint(getViewText(R.id.tv_treat_content_zhusu));
        basicInfo.setPresent(getViewText(R.id.tv_treat_content_xianbingshi));
        basicInfo.setRepresentation(getViewText(R.id.tv_treat_content_tizheng));
        basicInfo.setFace(getViewText(R.id.tv_treat_content_mianbu));
        basicInfo.setFurredTongue(getViewText(R.id.tv_treat_content_shetou));
        basicInfo.setHand(getViewText(R.id.tv_treat_content_shouzhang));
        basicInfo.setSeeDesc(getViewText(R.id.et_treat_content_more1));
        basicInfo.setSmell(getViewText(R.id.tv_treat_content_qiwei));
        basicInfo.setVoice(getViewText(R.id.tv_treat_content_shengyin));
        basicInfo.setVoiceDesc(getViewText(R.id.et_treat_content_more2));
        basicInfo.setPulse(getViewText(R.id.tv_treat_content_maixiang));
        basicInfo.setPulseDesc(getViewText(R.id.et_treat_content_more3));
        basicInfo.setSickId(sickId);
        basicInfo.setSickName(getViewText(R.id.tv_treat_sick_cn_name));
        basicInfo.setWestSickId((String) tvWestSickName.getTag());
        basicInfo.setPathogeny(getViewText(R.id.tv_treat_sick_reason));//发病机理
        basicInfo.setTherapy(getViewText(R.id.tv_treat_sick_therapies));//治法
        String needMed = getHerbalAdapter().getItemCount() == 0 ? "0" : "1";
        basicInfo.setNeedMedicine(needMed);// 1/0
        String needRevist = TextUtils.isEmpty(tvReviewTime.getText()) ? "0" : "1";
        basicInfo.setRevist(needRevist);// 1/0
        if (!TextUtils.isEmpty(tvReviewTime.getText())) {
            String reviewDate = tvReviewTime.getText().toString();
            basicInfo.setRevistDate(reviewDate.replaceAll("-", ""));//
        }
        basicInfo.setDoctorAdvice(getViewText(R.id.et_treat_content_yizhu));
        basicInfo.setDiagnoseFee(getViewText(R.id.et_treat_content_wenzhenfei));//*问诊费用
        Spinner spRate = findViewById(R.id.sp_treat_content_xishu);
        SpinnerAdapter rateAdapter = (SpinnerAdapter) spRate.getAdapter();
        DicSick rate = (DicSick) rateAdapter.getItem(spRate.getSelectedItemPosition());
        basicInfo.setDiagRadio(rate.getDataValue());//*问诊费用系数

        EditText etNumber = findViewById(R.id.et_treat_content_number);
        if (!etNumber.getText().toString().trim().equals("0")) {
            Spinner spPeisong = findViewById(R.id.sp_treat_content_peisong);
            DispatchAdapter dispatchAdapter = (DispatchAdapter) spPeisong.getAdapter();
            DispatchListInfo dispatchInfo = (DispatchListInfo) dispatchAdapter.getItem(spPeisong.getSelectedItemPosition());
            basicInfo.setCenterId(dispatchInfo.getCenterId());//*配送中心ID

            Spinner spReceiver = findViewById(R.id.sp_treat_content_receiver);
            ReceiverAdapter receiverAdapter = (ReceiverAdapter) spReceiver.getAdapter();
            AddListInfo addInfo = (AddListInfo) receiverAdapter.getItem(spReceiver.getSelectedItemPosition());
            basicInfo.setReName(addInfo.getReName());//*收货人姓名

            TextView tvAddress = findViewById(R.id.et_treat_content_receiver);
            basicInfo.setSendAddress(tvAddress.getText().toString().trim());
        } else {
            basicInfo.setCenterId("");//*配送中心ID
            basicInfo.setReName("");//*收货人姓名
            basicInfo.setSendAddress("");
        }
        //既往史、个人史、家庭史
        SickHis sickHis = treatParameter.getSickHisObj();
        sickHis.setHeredity(getViewArr(R.id.tv_treat_sick_jiazushi));

        Opration opration = sickHis.getOpration();
        if (findViewById(R.id.tv_treat_sick_jiwangshi).getTag() != null) {
            opration = (Opration) findViewById(R.id.tv_treat_sick_jiwangshi).getTag();
        }
        sickHis.setOpration(opration);

        Personal personal = sickHis.getPersonal();
        if (findViewById(R.id.tv_treat_sick_gerenshi).getTag() != null) {
            personal = (Personal) findViewById(R.id.tv_treat_sick_gerenshi).getTag();
        }
        sickHis.setPersonal(personal);

        //实验室、影像检测、望诊
        if (reportList != null && reportList.size() > 0) {
            for (int i = reportList.size() - 1; i >= 0; i--) {
                if (reportList.get(i).getIsNorm() != null && reportList.get(i).getIsNorm().equals("1")) {
                    reportList.remove(i);
                }
            }
            treatParameter.getReportArr().addAll(reportList);
        }
        TextView tvNorm = findViewById(R.id.tv_treat_content_rule);
        if (!TextUtils.isEmpty(tvNorm.getText())) {
            String[] norms = tvNorm.getText().toString().split("\\;");
            for (String norm : norms) {
                Report report = new Report();
                report.setTyp("1");
                report.setIsNorm("1");
                report.setNormVal(norm);
                treatParameter.getReportArr().add(report);
            }
        }

        //录音信息
        if (recordList != null) {
            treatParameter.getRecordArr().addAll(recordList);
        }

        //用药剂量与复诊
        Revisit revisit = treatParameter.getRevisitObj();
        revisit.setRepiceId(repiceId);
        revisit.setDayNum(getViewText(R.id.et_treat_content_days));
//        EditText etNumber = findViewById(R.id.et_treat_content_number);
        String number = "0";
        if (!TextUtils.isEmpty(etNumber.getText())) {
            number = etNumber.getText().toString().trim();
        }
        revisit.setUseNum(number);
        revisit.setInstruction(getViewText(R.id.et_treat_content_yongyaoshuoming));
        Spinner spBoilType = findViewById(R.id.sp_treat_boil_type);
        if (spBoilType.getAdapter() instanceof SpinnerAdapter) {
            SpinnerAdapter boilAdapter = (SpinnerAdapter) spBoilType.getAdapter();
            DicSick boil = (DicSick) boilAdapter.getItem(spBoilType.getSelectedItemPosition());
            revisit.setDealFlag(boil.getDataValue());
            revisit.setDealPrice(boil.getDataDesc());
        } else {
            revisit.setDealFlag("0");
            revisit.setDealPrice("0");
        }

        //中医循证/自己处方药品
        List<Med> medList = treatParameter.getMedList();
        List<MedicineInfo> medInfos = getHerbalAdapter().getList();
        if (medInfos != null && medInfos.size() > 0) {
            for (MedicineInfo info : medInfos) {
                Med med = new Med();
                med.setMedicineId(info.getMedicineId());
                med.setUseNum(info.getUseNum());
                med.setTyp(info.getTyp());
                med.setPrice(info.getRetailPrice());
                med.setXprice(info.getXrPice());
                med.setCenterId(info.getCenterId());
                medList.add(med);
            }
        }
        if (medRes == MED_RES_DATA) {
            TextView tvCode = findViewById(R.id.tv_treat_medicine_code);
            if (tvCode.getTag() != null) {
                MedicineInfo info = (MedicineInfo) tvCode.getTag();
                Med med = new Med();
                med.setMedicineId(info.getMedicineId());
                med.setUseNum(info.getUseNum());
                med.setTyp(info.getTyp());
                med.setPrice(info.getRetailPrice());
                med.setXprice(info.getXrPice());
                med.setCenterId(info.getCenterId());
                medList.add(med);
            }
        }

        //成品药与产品
        List<Med> boxMedList = treatParameter.getBoxMedArr();
        if (patentInfos != null && patentInfos.size() > 0) {
            for (MedicineInfo info : patentInfos) {
                Med med = new Med();
                med.setMedicineId(info.getMedicineId());
                med.setGdName(info.getGdName());
                med.setMedUnit(info.getMedUnit());
                med.setUseNum(info.getUseNum());
                med.setTyp(info.getTyp());
                med.setPrice(info.getRetailPrice());
                med.setXprice(info.getXrPice());
                boxMedList.add(med);
            }
        }
        return treatParameter;
    }

    private String getViewText(int id) {
        TextView tv = findViewById(id);
        if (TextUtils.isEmpty(tv.getText())) {
            return "";
        }
        return tv.getText().toString();
    }

    private List<String> getViewArr(int id) {
        TextView tv = findViewById(id);
        if (TextUtils.isEmpty(tv.getText())) {
            return null;
        }
        String[] text = tv.getText().toString().split(";");
        List<String> strings = Arrays.asList(text);
        return strings;
    }

    //问诊费
    //-----------------------------------------------------------------
    private void httpCost() {
        if (!isLoadOver) return;
        String url = ServerAPI.getTreatCostUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        JSONArray medArr = new JSONArray();
        if (getHerbalAdapter().getList() != null && getHerbalAdapter().getList().size() > 0) {
            for (MedicineInfo medInfo : getHerbalAdapter().getList()) {
                JSONObject jsonMed = new JSONObject();
                jsonMed.put("useNum", medInfo.getUseNum());
                jsonMed.put("typ", medInfo.getTyp());
                jsonMed.put("price", medInfo.getRetailPrice());
                medArr.add(jsonMed);
            }
        }
        if (medRes == MED_RES_DATA) {
            TextView tvCode = findViewById(R.id.tv_treat_medicine_code);
            if (tvCode.getTag() != null) {
                MedicineInfo info = (MedicineInfo) tvCode.getTag();
                JSONObject jsonMed = new JSONObject();
                jsonMed.put("useNum", info.getUseNum());
                jsonMed.put("typ", info.getTyp());
                jsonMed.put("price", info.getRetailPrice());
                medArr.add(jsonMed);
            }
        }
        jsonObject.put("medList", medArr);
        //-----------------------------------------------------------
        JSONArray boxMedArr = new JSONArray();
        if (patentInfos != null && patentInfos.size() > 0) {
            for (MedicineInfo medInfo : patentInfos) {
                JSONObject jsonMed = new JSONObject();
                jsonMed.put("useNum", medInfo.getUseNum());
                jsonMed.put("price", medInfo.getRetailPrice());
                jsonMed.put("typ", medInfo.getTyp());
                boxMedArr.add(jsonMed);
            }
        }
        jsonObject.put("boxMedList", boxMedArr);
        //-----------------------------------------------------
        EditText etWenzhenfei = findViewById(R.id.et_treat_content_wenzhenfei);
        String wenzhenfei = "0";
        if (!TextUtils.isEmpty(etWenzhenfei.getText())) {
            wenzhenfei = etWenzhenfei.getText().toString().trim();
        }
        jsonObject.put("diagnoseFee", wenzhenfei);
        //---------------------------------------------------------
        EditText etNumber = findViewById(R.id.et_treat_content_number);
        String number = "0";
        if (!TextUtils.isEmpty(etNumber.getText())) {
            number = etNumber.getText().toString().trim();
        }
        jsonObject.put("useNum", number);
        //-----------------------------------------------------
        Spinner spRate = findViewById(R.id.sp_treat_content_xishu);
        SpinnerAdapter rateAdapter = (SpinnerAdapter) spRate.getAdapter();
        DicSick rate = (DicSick) rateAdapter.getItem(spRate.getSelectedItemPosition());
        jsonObject.put("diagRadio", rate.getDataValue());
        //--------------------------------------------------------
        Spinner spBoil = findViewById(R.id.sp_treat_boil_type);
        if (spBoil.getAdapter() instanceof SpinnerAdapter) {
            SpinnerAdapter boilAdapter = (SpinnerAdapter) spBoil.getAdapter();
            DicSick type = (DicSick) boilAdapter.getItem(spBoil.getSelectedItemPosition());
            if (!type.getDataDesc().equals("-1")) {
                jsonObject.put("dealFee", type.getDataDesc());
            } else {
                jsonObject.put("dealFee", "0");
            }
        } else {
            jsonObject.put("dealFee", "0");
        }
        //------------------------------------------------------
        Spinner spPeisong = findViewById(R.id.sp_treat_content_peisong);
        if (spPeisong.getAdapter() instanceof DispatchAdapter) {
            DispatchAdapter dispatchAdapter = (DispatchAdapter) spPeisong.getAdapter();
            DispatchListInfo dispatchInfo = (DispatchListInfo) dispatchAdapter.getItem(spPeisong.getSelectedItemPosition());
            String centerId = dispatchInfo.getCenterId();//*配送中心ID
            if (centerId != null)
                jsonObject.put("centerId", centerId);
        }
        //-------------------------------------------------------
        Log.d("TreatActivity_cost", jsonObject.toJSONString());
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject json = JSONObject.parseObject(data);
                String medicineFee = json.getString("medicineFee");
                String dealFee = json.getString("dealFee");
                String diagnoseFee = json.getString("diagnoseFee");
                String boxMedicineFee = json.getString("boxMedicineFee");
                String tecFee = json.getString("tecFee");
                String expensFee = json.getString("expensFee");
                String sumFee = json.getString("sumFee");
                String sumFeeChUpper = json.getString("sumFeeChUpper");
                setViewText(R.id.tv_treat_content_yaofei, medicineFee + "元");
                setViewText(R.id.tv_treat_content_jianyaofei, dealFee + "元");
                setViewText(R.id.tv_treat_content_chengpinyaofei, boxMedicineFee + "元");
                setViewText(R.id.tv_treat_content_jishufuwufei, tecFee + "元");
                setViewText(R.id.tv_treat_content_yunfei, expensFee + "元");
                setViewText(R.id.tv_treat_content_feiyongheji, "费用合计：" + sumFee + "元");
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

    private void setViewText(int id, String text) {
        TextView tv = findViewById(id);
        if (text == null) {
            tv.setText(null);
        } else {
            tv.setText(Html.fromHtml(text).toString());
        }
    }

    //-------------------录音时间显示------------------------
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int time = msg.what;
            TextView tvTime = findViewById(R.id.tv_treat_record_time);
            int hour = time / 3600;
            String sHour = hour <= 9 ? "0" + hour : "" + hour;
            int munites = time % 3600 / 60;
            String sMunites = munites <= 9 ? "0" + munites : "" + munites;
            int second = time % 60;
            String sSecond = second <= 9 ? "0" + second : "" + second;
            tvTime.setText(sHour + ":" + sMunites + ":" + sSecond);
        }
    };

    class RecordThread extends Thread {

        private int time = 0;
        private boolean isBreak = false;

        public void breakUp() {
            isBreak = true;
        }

        @Override
        public void run() {
            super.run();
            while (!isBreak) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;
                handler.sendEmptyMessage(time);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(recordConn);
        unbindService(playerConn);
        unregisterCaptureReceiver();
        if (recordThread != null) recordThread.breakUp();

        Intent intent = new Intent();
        intent.setAction(ACTION_TREAT_BACK);
        sendBroadcast(intent);
    }

    private void onPaySuccess() {

        findViewById(R.id.tv_treat_content_zhusu).setEnabled(false);
        findViewById(R.id.tv_treat_content_tizheng).setEnabled(false);
        findViewById(R.id.tv_treat_content_mianbu).setEnabled(false);
        findViewById(R.id.tv_treat_content_shetou).setEnabled(false);
        findViewById(R.id.tv_treat_content_shouzhang).setEnabled(false);
        findViewById(R.id.et_treat_content_more1).setEnabled(false);
        findViewById(R.id.tv_treat_content_qiwei).setEnabled(false);
        findViewById(R.id.tv_treat_content_shengyin).setEnabled(false);
        findViewById(R.id.et_treat_content_more2).setEnabled(false);
        findViewById(R.id.tv_treat_content_maixiang).setEnabled(false);
        findViewById(R.id.et_treat_content_more3).setEnabled(false);
        findViewById(R.id.tv_treat_sick_reason).setEnabled(false);
        findViewById(R.id.tv_treat_sick_cn_name).setEnabled(false);
        findViewById(R.id.tv_treat_sick_west_name).setEnabled(false);
        findViewById(R.id.tv_treat_sick_therapies).setEnabled(false);
        findViewById(R.id.et_treat_content_number).setEnabled(false);
        findViewById(R.id.et_treat_content_days).setEnabled(false);
        findViewById(R.id.sp_treat_boil_type).setEnabled(false);
        findViewById(R.id.et_treat_content_yongyaoshuoming).setEnabled(false);
        findViewById(R.id.et_treat_content_yizhu).setEnabled(false);
        findViewById(R.id.et_treat_content_wenzhenfei).setEnabled(false);
        findViewById(R.id.sp_treat_content_xishu).setEnabled(false);
        findViewById(R.id.tv_treat_btn_review_time).setEnabled(false);

        findViewById(R.id.sp_treat_content_peisong).setEnabled(false);
        findViewById(R.id.sp_treat_content_receiver).setEnabled(false);
        findViewById(R.id.et_treat_content_receiver).setEnabled(false);

        findViewById(R.id.btn_act_treat_submit).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_act_treat_recipe_order).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_act_treat_visit_order).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_add_img_pic).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_add_lab_pic).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_rule).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_jiazushi).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_gerenshi).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_jiwangshi).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_maixiang).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_shengyin).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_qiwei).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_shetou).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_shouzhang).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_mianbu).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_tizheng).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_record).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_xianbingshi).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_treat_btn_add_see_pic).setVisibility(View.INVISIBLE);

        findViewById(R.id.ll_traet_emb_block).setVisibility(View.GONE);
        findViewById(R.id.btn_act_treat_add_med1).setVisibility(View.GONE);
        findViewById(R.id.btn_act_treat_add_med2).setVisibility(View.GONE);
        findViewById(R.id.ll_act_treat_open_patent).setVisibility(View.GONE);
        findViewById(R.id.ll_treat_recipe_menu).setVisibility(View.GONE);

        removeImageDelBtn(R.id.ll_treat_lab);
        removeImageDelBtn(R.id.ll_treat_img);
        removeImageDelBtn(R.id.ll_treat_see);

        herbalAdapter1.notifyDataSetChanged();
        herbalAdapter2.notifyDataSetChanged();
        patentAdapter.notifyDataSetChanged();
    }

    private void removeImageDelBtn(int id) {
        LinearLayout ll = findViewById(id);
        for (int i = 1; i < ll.getChildCount() - 1; i++) {
            View view = ll.getChildAt(i);
            View btnDel = view.findViewById(R.id.btn_del);
            if (btnDel != null) btnDel.setVisibility(View.GONE);
        }
    }

    private GridHerbalAdapter getHerbalAdapter() {
        if (medRes == MED_RES_DATA)
            return herbalAdapter1;
        else
            return herbalAdapter2;
    }

    //获取配送中心
    private void httpGetDispatchList() {
        String url = ServerAPI.getDispatchListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                dispatchAll = JSONObject.parseArray(data, DispatchListInfo.class);
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

    class DispatchAdapter extends BaseAdapter {

        List<DispatchListInfo> areaList;

        public void setData(List<DispatchListInfo> areaList) {
            this.areaList = areaList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return areaList == null ? 0 : areaList.size();
        }

        @Override
        public Object getItem(int position) {
            return areaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(TreatActivity.this);
            textView.setPadding(12, 6, 12, 6);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_text_tag));
            textView.setText(areaList.get(position).getCenterName());
            convertView = textView;
            return convertView;
        }
    }


    class ReceiverAdapter extends BaseAdapter {

        List<AddListInfo> areaList;

        public void setData(List<AddListInfo> areaList) {
            this.areaList = areaList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return areaList == null ? 0 : areaList.size();
        }

        @Override
        public Object getItem(int position) {
            return areaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(TreatActivity.this);
            textView.setPadding(12, 6, 12, 6);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_text_tag));
            textView.setText(areaList.get(position).getReName());
            convertView = textView;
            return convertView;
        }
    }

    CostTimeThread costTimeThread;

    @Override
    protected void onResume() {
        super.onResume();
        if (tvTitle != null && !isPay) {
            costTimeThread = new CostTimeThread();
            costTimeThread.start();
        }
        if (shotInfos.size() > 0) {
            for (ShotInfo shotInfo : shotInfos) {
                DicSick dicSick = createDicSick(shotInfo.picName);
                addReportItem(dicSick, "3", shotInfo.picUrl);
                addImageToContent("3", dicSick.getDataName(), shotInfo.picUrl);
            }
            shotInfos.clear();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (costTimeThread != null) {
            costTimeThread.setStopFlag();
            costTimeThread = null;
        }
    }

    Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (tvTitle != null) {
                int time = (Integer) tvTitle.getTag();
                time++;
                tvTitle.setTag(time);
                tvTitle.setText("问诊 " + getCostTimeStr(time));
            }
        }
    };

    private String getCostTimeStr(int number) {
        String min = String.format("%02d", number / 60);
        String sec = String.format("%02d", number % 60);
        return min + ":" + sec;
    }

    class CostTimeThread extends Thread {

        private boolean stopFlag = false;

        public void setStopFlag() {
            stopFlag = true;
        }

        @Override
        public void run() {
            super.run();
            while (!stopFlag) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeHandler.sendEmptyMessage(0);
            }
        }
    }

    class VideoCaptureReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            String picName = intent.getStringExtra("name");
            httpImageUpload(path, picName, regId);
        }
    }

    VideoCaptureReceiver captureReceiver;

    private void registerCaptureReceiver() {
        IntentFilter intentFilter = new IntentFilter("action_video_capture");
        captureReceiver = new VideoCaptureReceiver();
        registerReceiver(captureReceiver, intentFilter);
    }

    private void unregisterCaptureReceiver() {
        unregisterReceiver(captureReceiver);
    }

    List<ShotInfo> shotInfos = new ArrayList<>();

    class ShotInfo {
        public ShotInfo(String picName, String picUrl) {
            this.picName = picName;
            this.picUrl = picUrl;
        }

        String picName;
        String picUrl;
    }

    private void httpImageUpload(String imgPath, String picName, String id) {
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
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    String resultPath = jsonObject.getJSONObject("data").getString("url");
                    DicSick dicSick = createDicSick(picName);
                    addReportItem(dicSick, "3", resultPath);
                    addImageToContent("3", dicSick.getDataName(), resultPath);
//                    shotInfos.add(new ShotInfo(picName, resultPath));
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

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
            }
        });


    }

    private DicSick createDicSick(String picName) {
        DicSick dicSick = new DicSick();
        dicSick.setDataName(picName);
        if (picName.equals("面部")) {
            dicSick.setDataValue("1");
        } else if (picName.equals("舌质、舌苔")) {
            dicSick.setDataValue("2");
        } else if (picName.equals("手掌")) {
            dicSick.setDataValue("3");
        } else {
            dicSick.setDataValue("4");
        }
        return dicSick;
    }

    //--------------------西医病名选择------------------------

    View goodAtView;
    PopupWindow popupGoodAt;
    GoodAtSelectAdapter goodAtSelectAdapter;

    private void showPopupGoodAt() {
        if (popupGoodAt == null) {
            goodAtView = getLayoutInflater().inflate(R.layout.popup_west_sick_name, null);
            goodAtView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
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
        }
        if (!popupGoodAt.isShowing())
            popupGoodAt.showAtLocation(tvWestSickName.getRootView(), Gravity.CENTER, 0, 0);
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
            flm.setFlexDirection(FlexDirection.ROW);
            flm.setFlexWrap(FlexWrap.WRAP);
            flm.setAlignItems(AlignItems.STRETCH);
            holder.recyclerView.setLayoutManager(flm);
            holder.recyclerView.setNestedScrollingEnabled(false);
            holder.recyclerView.setHasFixedSize(true);
            GoodAtItemAdapter adapter = new GoodAtItemAdapter(goodAtInfos.get(position).getSmsOfficeSickList(),
                    goodAtInfos.get(position).getOfficeId());
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
        String parentId;

        public GoodAtItemAdapter(List<GoodAtOfficeInfo> goodAtInfos, String parentId) {
            this.goodAtInfos = goodAtInfos;
            this.parentId = parentId;
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
//            if (info.getIsChose() == 1) {
//                holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextWhite));
//                holder.textView.setBackgroundResource(R.drawable.bg_att_good_at_item_select);
//            } else {
//                holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
//                holder.textView.setBackgroundResource(R.drawable.bg_att_good_at_item_normal);
//            }
            holder.textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
            holder.textView.setBackgroundResource(R.drawable.bg_att_good_at_item_normal);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvWestSickName.setText(info.getOffsickName());
                    if (parentId == null) {
                        tvWestSickName.setTag(info.getOffsickId());
                    } else {
                        tvWestSickName.setTag(parentId + "-" + info.getOffsickId());
                    }
                    dismissPopupGoodAt();
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

    private void httpGetAuthInfo() {
        String url = ServerAPI.getWestSickNameUrl(repiceId == null ? "" : repiceId);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject dataJson = JSONObject.parseObject(data);
                String goodAtSelf = dataJson.getString("goodAtSelf");
                String goodAtSelfId = dataJson.getString("goodAtSelfId");
                List<GoodAtOfficeInfo> list = getSelectedGoodAt(goodAtSelf, goodAtSelfId);
                List<GoodAtInfo> goodAtArr = dataJson.getJSONArray("goodAtArr").toJavaList(GoodAtInfo.class);
                if (list != null && list.size() > 0) {
                    GoodAtInfo goodAtInfo = new GoodAtInfo();
                    goodAtInfo.setOfficeId(null);
                    goodAtInfo.setOfficeName("擅长");
                    goodAtInfo.setSmsOfficeSickList(list);
                    goodAtArr.add(0, goodAtInfo);
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
            }
        }));
    }

    private List<GoodAtOfficeInfo> getSelectedGoodAt(String name, String ids) {
        if (ids == null || ids.length() == 0) return null;
        List<GoodAtOfficeInfo> list = new ArrayList<>();
        String[] idArr = ids.split("\\;");
        String[] nameArr = name.split("\\;");
        for (int i = 0; i < idArr.length; i++) {
            GoodAtOfficeInfo goodAtOfficeInfo = new GoodAtOfficeInfo();
            goodAtOfficeInfo.setOffsickName(nameArr[i]);
            goodAtOfficeInfo.setOffsickId(idArr[i]);
            list.add(goodAtOfficeInfo);
        }
        return list;
    }

}
