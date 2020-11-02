package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.DoctorInfo;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.utils.DocAuthStateUtils;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * fragment
 * 我的
 */
public class MainFragmentMine extends Fragment {

    ImageView ivPhoto;
    TextView tvName, tvUnit, tvOffice, tvLevel, tvScore, tvNum, tvAuth;

    SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_mine, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = view.findViewById(R.id.refresh_layout_frag_mine);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                httpGetMineInfo();
            }
        });

        ivPhoto = view.findViewById(R.id.photo);
        tvName = view.findViewById(R.id.name);
        tvUnit = view.findViewById(R.id.unit);
        tvOffice = view.findViewById(R.id.office);
        tvLevel = view.findViewById(R.id.level);
        tvScore = view.findViewById(R.id.score);
        tvNum = view.findViewById(R.id.num);
        tvAuth = view.findViewById(R.id.tv_attestation);

        TextView tvAppVersion = view.findViewById(R.id.tv_app_version);
        tvAppVersion.setText("v" + MainActivity.getVersionName(getContext()));

        view.findViewById(R.id.setting_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getContext(), SettingActivity.class));

            }
        });

        view.findViewById(R.id.ll_fragment_mine_btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.httpUpdate(false);
            }
        });

        view.findViewById(R.id.ll_fragment_mine_btn_recipe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getContext(), MyRecipeActivity.class);
                    intent.putExtra("openType", MyRecipeActivity.OPEN_TYPE_SCAN);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            tvAuth.setText("已认证");
                            tvAuth.setTextColor(0xff00ff00);
                            Intent intent = new Intent(getContext(), MyRecipeActivity.class);
                            intent.putExtra("openType", MyRecipeActivity.OPEN_TYPE_SCAN);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });

        view.findViewById(R.id.ll_fragment_mine_btn_pwd_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyPwdEditActivity.class);
                getActivity().startActivity(intent);
            }
        });

        view.findViewById(R.id.btn_frag_mine_my_earn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), MyEarnListActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            tvAuth.setText("已认证");
                            tvAuth.setTextColor(0xff00ff00);
                            Intent intent = new Intent(getActivity(), MyEarnListActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });
        view.findViewById(R.id.btn_frag_mine_my_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), MyOrderListActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            tvAuth.setText("已认证");
                            tvAuth.setTextColor(0xff00ff00);
                            Intent intent = new Intent(getActivity(), MyOrderListActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });
        view.findViewById(R.id.btn_frag_mine_my_blog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), MyBlogActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            tvAuth.setText("已认证");
                            tvAuth.setTextColor(0xff00ff00);
                            Intent intent = new Intent(getActivity(), MyBlogActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });
        view.findViewById(R.id.ll_fragment_mine_btn_attestation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttestationActivity.class);
                getActivity().startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_frag_mine_work_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), WorkSetActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            tvAuth.setText("已认证");
                            tvAuth.setTextColor(0xff00ff00);
                            Intent intent = new Intent(getActivity(), WorkSetActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });
        view.findViewById(R.id.btn_frag_mine_my_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollectListActivity.class);
                getActivity().startActivity(intent);
            }
        });
        view.findViewById(R.id.ll_fragment_mine_btn_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ServiceHelpActivity.class);
                getActivity().startActivity(intent);
            }
        });

        view.findViewById(R.id.btn_frag_mine_my_article).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), MyArticleActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            tvAuth.setText("已认证");
                            tvAuth.setTextColor(0xff00ff00);
                            Intent intent = new Intent(getActivity(), MyArticleActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });

        httpGetMineInfo();
    }

    public void httpGetMineInfo() {
        String url = ServerAPI.getDocInfoUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                DoctorInfo info = JSONObject.parseObject(data, DoctorInfo.class);
                SpData.setAuthFlag(info.getAuthFlag());
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setDocPhoto(ServerAPI.FILL_DOMAIN + info.getShPic());
                Glide.with(getContext())
                        .load(ServerAPI.FILL_DOMAIN + info.getShPic())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivPhoto);
                tvName.setText(info.getName());
                tvUnit.setText(info.getHospName());
                tvOffice.setText(info.getDocOffiName());
                tvLevel.setText(info.getDocTypeName());
                if (info.getAuthFlag().equals("9")) {
                    tvAuth.setText("已认证");
                    tvAuth.setTextColor(0xff00ff00);
                } else if (info.getAuthFlag().equals("1")) {
                    tvAuth.setText("认证中");
                    tvAuth.setTextColor(0xff999999);
                } else if (info.getAuthFlag().equals("2")) {
                    tvAuth.setText("未通过，再次认证");
                    tvAuth.setTextColor(0xff990000);
                    View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
                    PopupWindow popupWindow = new PopupWindow(view, -1, -1);
                    TextView tv = view.findViewById(R.id.tv_alert_msg);
                    String text1 = "很抱歉，您的医师验证未通过，原因如下：";
                    String text2 = "请修改后再次申请。";
                    String text = text1 + info.getUnAuthReason() + text2;
                    SpannableStringBuilder style = new SpannableStringBuilder(text);
                    style.setSpan(new ForegroundColorSpan(Color.RED), text1.length(), text.length() - text2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv.setText(style);
                    view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.showAtLocation(tvAuth.getRootView(), Gravity.CENTER, 0, 0);
                } else if (info.getAuthFlag().equals("8")) {
                    tvAuth.setText("修改认证");
                    tvAuth.setTextColor(0xff00ff00);
                } else {
                    tvAuth.setText("去认证");
                    tvAuth.setTextColor(0xff999999);
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
                refreshLayout.setRefreshing(false);
            }
        }));
    }

}
