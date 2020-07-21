package com.zuojianyou.zybdoctor.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.activity.TreatActivity;
import com.zuojianyou.zybdoctor.beans.treat.DiagnoseInfo;
import com.zuojianyou.zybdoctor.beans.treat.DicAsk;
import com.zuojianyou.zybdoctor.beans.treat.DicBody;
import com.zuojianyou.zybdoctor.beans.treat.DicFace;
import com.zuojianyou.zybdoctor.beans.treat.DicOpration;
import com.zuojianyou.zybdoctor.beans.treat.DicPersonal;
import com.zuojianyou.zybdoctor.beans.treat.DicSick;
import com.zuojianyou.zybdoctor.beans.treat.DicTongue;
import com.zuojianyou.zybdoctor.beans.treat.Opration;
import com.zuojianyou.zybdoctor.beans.treat.Personal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 过往病史选择
 */
public class DicSickDialog extends Dialog {

    private final int DISTANCE = 48;

    private String title;
    private LinearLayout llContent;
    private TextView etShow;
    private Object object;
    private View contentView;
    private int spanCount;
    private TreatActivity activity;

    public DicSickDialog(Activity activity, String title, Object object, TextView etShow) {
        super(activity, R.style.AlertDialog);
        this.title = title;
        this.etShow = etShow;
        this.object = object;
        if (activity instanceof  TreatActivity) {
            this.activity = (TreatActivity) activity;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = getLayoutInflater().inflate(R.layout.dialog_dic_sick, null);
        setContentView(contentView);
        TextView tvTitle = findViewById(R.id.tv_dialog_dic_sick_title);
        tvTitle.setText(title);
        spanCount = Integer.valueOf((String) tvTitle.getTag());
        llContent = findViewById(R.id.ll_dialog_dic_sick_content);
        if (object instanceof DicAsk) {
            DicAsk dicAsk = (DicAsk) object;
            initAskContent(dicAsk);
        } else if (object instanceof DicBody) {
            DicBody dicBody = (DicBody) object;
            initLookBody(dicBody);
        } else if (object instanceof DicFace) {
            DicFace dicFace = (DicFace) object;
            initLookFace(dicFace);
        } else if (object instanceof DicTongue) {
            DicTongue dicTongue = (DicTongue) object;
            initLookTongue(dicTongue);
        } else if (object instanceof List) {
            if (etShow.getId() == R.id.tv_treat_sick_jiazushi) {
                List<DicSick> list = (List<DicSick>) object;
                initHeredity(list);
            } else {
                List<DicSick> list = (List<DicSick>) object;
                initOnlyList(list);
            }
        } else if (object instanceof DicOpration) {
            DicOpration dicOpration = (DicOpration) object;
            initOpration(dicOpration);
        } else if (object instanceof DicPersonal) {
            DicPersonal dicPersonal = (DicPersonal) object;
            initPersonal(dicPersonal);
        } else if (object instanceof DiagnoseInfo.PulseObj) {
            DiagnoseInfo.PulseObj obj = (DiagnoseInfo.PulseObj) object;
            intPulseObj(obj);
        }

        findViewById(R.id.btn_dialog_dic_sick_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void intPulseObj(DiagnoseInfo.PulseObj obj){
        View view = getLayoutInflater().inflate(R.layout.dialog_dic_pulse, null);
        RecyclerView lecunRecyclerView = view.findViewById(R.id.lecun_recycler_view);
        lecunRecyclerView.setNestedScrollingEnabled(false);
        lecunRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        lecunRecyclerView.setAdapter(new SickAdapter(obj.getLecunPulseObj(), 1));
        RecyclerView leguRecyclerView = view.findViewById(R.id.legu_recycler_view);
        leguRecyclerView.setNestedScrollingEnabled(false);
        leguRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        leguRecyclerView.setAdapter(new SickAdapter(obj.getLeguPulseObj(), 1));
        RecyclerView lechiRecyclerView = view.findViewById(R.id.lechi_recycler_view);
        lechiRecyclerView.setNestedScrollingEnabled(false);
        lechiRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        lechiRecyclerView.setAdapter(new SickAdapter(obj.getLechiPulseObj(), 1));
        RecyclerView ricunRecyclerView = view.findViewById(R.id.ricun_recycler_view);
        ricunRecyclerView.setNestedScrollingEnabled(false);
        ricunRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        ricunRecyclerView.setAdapter(new SickAdapter(obj.getRicunPulseObj(), 1));
        RecyclerView riguRecyclerView = view.findViewById(R.id.rigu_recycler_view);
        riguRecyclerView.setNestedScrollingEnabled(false);
        riguRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        riguRecyclerView.setAdapter(new SickAdapter(obj.getRiguPulseObj(), 1));
        RecyclerView richiRecyclerView = view.findViewById(R.id.richi_recycler_view);
        richiRecyclerView.setNestedScrollingEnabled(false);
        richiRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        richiRecyclerView.setAdapter(new SickAdapter(obj.getRichiPulseObj(), 1));

        llContent.addView(view);

        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    activity.updatePulseView();
                }
                dismiss();
            }
        });
    }

    private void initAskContent(DicAsk dicAsk) {
        addTextView(dicAsk.getAskColdHeatObj().get(0).getDicName(), 0);
        addRecyclerView(dicAsk.getAskColdHeatObj(), 2, 0);

        addTextView(dicAsk.getAskSweatObj().get(0).getDicName(), 0);
        addRecyclerView(dicAsk.getAskSweatObj(), 2, 0);

        addTextView(dicAsk.getAskPainObj().getDicName(), 0);

        addTextView(dicAsk.getAskPainObj().getPainPartObj().get(0).getDicName(), 1);
        addRecyclerView(dicAsk.getAskPainObj().getPainPartObj(), 2, 1);

        addTextView(dicAsk.getAskPainObj().getPainSenObj().get(0).getDicName(), 1);
        addRecyclerView(dicAsk.getAskPainObj().getPainSenObj(), 2, 1);

        addTextView(dicAsk.getAskPainObj().getPainHalfObj().get(0).getDicName(), 1);
        addRecyclerView(dicAsk.getAskPainObj().getPainHalfObj(), 2, 1);

        addTextView(dicAsk.getAskDietObj().get(0).getDicName(), 0);
        addRecyclerView(dicAsk.getAskDietObj(), 2, 0);

        addTextView(dicAsk.getAskSleepObj().get(0).getDicName(), 0);
        addRecyclerView(dicAsk.getAskSleepObj(), 2, 0);

        addTextView(dicAsk.getAskErBianObj().get(0).getDicName(), 0);
        addRecyclerView(dicAsk.getAskErBianObj(), 2, 0);

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(dicAsk);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DicAsk dic = (DicAsk) v.getTag();
                etShow.setText(getCheckText(dic));
                dismiss();
            }
        });
    }

    private String getCheckText(DicAsk dicAsk) {
        StringBuilder sb = new StringBuilder();
        sb.append(getStringList(dicAsk.getAskColdHeatObj()));
        sb.append(getStringList(dicAsk.getAskSweatObj()));
        sb.append(getStringList(dicAsk.getAskPainObj().getPainPartObj()));
        sb.append(getStringList(dicAsk.getAskPainObj().getPainSenObj()));
        sb.append(getStringList(dicAsk.getAskPainObj().getPainHalfObj()));
        sb.append(getStringList(dicAsk.getAskDietObj()));
        sb.append(getStringList(dicAsk.getAskSleepObj()));
        sb.append(getStringList(dicAsk.getAskErBianObj()));
        return sb.toString();
    }

    private void initLookBody(DicBody dicBody) {
        addTextView(dicBody.getLkBodyTypeObj().get(0).getDicName(), 0);
        addRecyclerView(dicBody.getLkBodyTypeObj(), 2, 0);

        addTextView(dicBody.getLkSpiritObj().get(0).getDicName(), 0);
        addRecyclerView(dicBody.getLkSpiritObj(), 2, 0);

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(dicBody);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DicBody dic = (DicBody) v.getTag();
                etShow.setText(getCheckText(dic));
                dismiss();
            }
        });
    }

    private String getCheckText(DicBody dicBody) {
        StringBuilder sb = new StringBuilder();
        sb.append(getStringList(dicBody.getLkBodyTypeObj()));
        sb.append(getStringList(dicBody.getLkSpiritObj()));
        return sb.toString();
    }

    private void initLookFace(DicFace dicFace) {
        addTextView(dicFace.getLkFaceColorObj().get(0).getDicName(), 0);
        addRecyclerView(dicFace.getLkFaceColorObj(), 2, 0);

        addTextView(dicFace.getLkSkinObj().get(0).getDicName(), 0);
        addRecyclerView(dicFace.getLkSkinObj(), 2, 0);

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(dicFace);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DicFace dic = (DicFace) v.getTag();
                etShow.setText(getCheckText(dic));
                dismiss();
            }
        });
    }

    private String getCheckText(DicFace dicFace) {
        StringBuilder sb = new StringBuilder();
        sb.append(getStringList(dicFace.getLkFaceColorObj()));
        sb.append(getStringList(dicFace.getLkSkinObj()));
        return sb.toString();
    }

    private void initLookTongue(DicTongue dicTongue) {
        addTextView(dicTongue.getTongueCoatObj().getDicName(), 0);

        addTextView(dicTongue.getTongueCoatObj().getTongueQuObj().get(0).getDicName(), 1);
        addRecyclerView(dicTongue.getTongueCoatObj().getTongueQuObj(), 2, 1);

        addTextView(dicTongue.getTongueCoatObj().getTongueCoObj().get(0).getDicName(), 1);
        addRecyclerView(dicTongue.getTongueCoatObj().getTongueCoObj(), 2, 1);

        addTextView(dicTongue.getTongueNatureObj().getDicName(), 0);

        addTextView(dicTongue.getTongueNatureObj().getTongueColObj().get(0).getDicName(), 1);
        addRecyclerView(dicTongue.getTongueNatureObj().getTongueColObj(), 2, 1);

        addTextView(dicTongue.getTongueNatureObj().getTongueBoObj().get(0).getDicName(), 1);
        addRecyclerView(dicTongue.getTongueNatureObj().getTongueBoObj(), 2, 1);

        addTextView(dicTongue.getTongueNatureObj().getTongueEnObj().get(0).getDicName(), 1);
        addRecyclerView(dicTongue.getTongueNatureObj().getTongueEnObj(), 2, 1);

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(dicTongue);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DicTongue dic = (DicTongue) v.getTag();
                etShow.setText(getCheckText(dic));
                dismiss();
            }
        });
    }

    private String getCheckText(DicTongue dicTongue) {
        StringBuilder sb = new StringBuilder();
        sb.append(getStringList(dicTongue.getTongueCoatObj().getTongueQuObj()));
        sb.append(getStringList(dicTongue.getTongueCoatObj().getTongueCoObj()));
        sb.append(getStringList(dicTongue.getTongueNatureObj().getTongueColObj()));
        sb.append(getStringList(dicTongue.getTongueNatureObj().getTongueBoObj()));
        sb.append(getStringList(dicTongue.getTongueNatureObj().getTongueEnObj()));
        return sb.toString();
    }

    private void initOnlyList(List<DicSick> list) {
        addRecyclerView(list, 2, 0);

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(list);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List mList = (List) v.getTag();
                etShow.setText(getStringList(mList));
                dismiss();
            }
        });
    }

    private void initHeredity(List<DicSick> list) {
        addRecyclerView(list, 2, 0);
        addEditText(0, list.get(0).getDicName());

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(list);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List mList = (List) v.getTag();
                etShow.setText(getStringList(mList));
                dismiss();
            }
        });
    }

    private void initOpration(DicOpration dicOpration) {
        addTextView(dicOpration.getNormalObj().get(0).getDicName(), 0);
        addRecyclerView(dicOpration.getNormalObj(), 2, 0);
        addEditText(0, dicOpration.getNormalObj().get(0).getDicName());

        addTextView(dicOpration.getSickHisObj().get(0).getDicName(), 0);
        addRecyclerView(dicOpration.getSickHisObj(), 2, 0);
        addEditText(0, dicOpration.getSickHisObj().get(0).getDicName());

        addTextView(dicOpration.getFoodAllergyObj().get(0).getDicName(), 0);
        addRecyclerView(dicOpration.getFoodAllergyObj(), 2, 0);
        addEditText(0, dicOpration.getFoodAllergyObj().get(0).getDicName());

        addTextView(dicOpration.getDrugAllergyObj().get(0).getDicName(), 0);
        addRecyclerView(dicOpration.getDrugAllergyObj(), 2, 0);
        addEditText(0, dicOpration.getDrugAllergyObj().get(0).getDicName());

        addTextView(dicOpration.getInfectionHisObj().get(0).getDicName(), 0);
        addRecyclerView(dicOpration.getInfectionHisObj(), 2, 0);
        addEditText(0, dicOpration.getInfectionHisObj().get(0).getDicName());

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(dicOpration);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DicOpration dicOpration = (DicOpration) v.getTag();
                etShow.setText(getCheckText(dicOpration));
                etShow.setTag(getCheckTag(dicOpration));
                dismiss();
            }
        });
    }

    private String getCheckText(DicOpration dicOpration) {
        StringBuilder sb = new StringBuilder();
        sb.append(getStringList(dicOpration.getNormalObj()));
        sb.append(getStringList(dicOpration.getSickHisObj()));
        sb.append(getStringList(dicOpration.getFoodAllergyObj()));
        sb.append(getStringList(dicOpration.getDrugAllergyObj()));
        sb.append(getStringList(dicOpration.getInfectionHisObj()));
        return sb.toString();
    }

    private Object getCheckTag(DicOpration dicOpration) {
        Opration opration = new Opration();

        StringBuilder sbAllergy = new StringBuilder();
        sbAllergy.append(getStringList(dicOpration.getDrugAllergyObj()));
        String[] strAllergy = sbAllergy.toString().split("\\;");
        List<String> allergy = new ArrayList<>(Arrays.asList(strAllergy));
        opration.setAllergy(allergy);

        StringBuilder sbFood = new StringBuilder();
        sbFood.append(getStringList(dicOpration.getFoodAllergyObj()));
        String[] strFood = sbFood.toString().split("\\;");
        List<String> food = new ArrayList<>(Arrays.asList(strFood));
        opration.setFood(food);

        StringBuilder sbOther = new StringBuilder();
        sbOther.append(getStringList(dicOpration.getNormalObj()));
        sbOther.append(getStringList(dicOpration.getSickHisObj()));
        sbOther.append(getStringList(dicOpration.getInfectionHisObj()));
        String[] strOther = sbOther.toString().split("\\;");
        List<String> other = new ArrayList<>(Arrays.asList(strOther));
        opration.setOther(other);

        return opration;
    }

    private void initPersonal(DicPersonal dicPersonal) {
        addTextView(dicPersonal.getMarryObj().get(0).getDicName(), 0);
        addRecyclerView(dicPersonal.getMarryObj(), 1, 0);

        addTextView(dicPersonal.getChildObj().get(0).getDicName(), 0);
        addRecyclerView(dicPersonal.getChildObj(), 1, 0);

        addTextView(dicPersonal.getNormalObj().get(0).getDicName(), 0);
        addRecyclerView(dicPersonal.getNormalObj(), 2, 0);
        addEditText(0, dicPersonal.getNormalObj().get(0).getDicName());

        addTextView(dicPersonal.getLiveEnObj().get(0).getDicName(), 0);
        addRecyclerView(dicPersonal.getLiveEnObj(), 2, 0);
        addEditText(0, dicPersonal.getLiveEnObj().get(0).getDicName());

        addTextView(dicPersonal.getMensesObj().get(0).getDicName(), 0);
        addRecyclerView(dicPersonal.getMensesObj(), 2, 0);
        addEditText(0, dicPersonal.getMensesObj().get(0).getDicName());

        findViewById(R.id.btn_dialog_dic_sick_confirm).setTag(dicPersonal);
        findViewById(R.id.btn_dialog_dic_sick_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DicPersonal dicPersonal = (DicPersonal) v.getTag();
                etShow.setText(getCheckText(dicPersonal));
                etShow.setTag(getCheckTag(dicPersonal));
                dismiss();
            }
        });
    }

    private String getCheckText(DicPersonal dicPersonal) {
        StringBuilder sb = new StringBuilder();
        sb.append(getStringName(dicPersonal.getMarryObj()));
        sb.append(getStringName(dicPersonal.getChildObj()));
        sb.append(getStringList(dicPersonal.getNormalObj()));
        sb.append(getStringList(dicPersonal.getLiveEnObj()));
        sb.append(getStringList(dicPersonal.getMensesObj()));
        return sb.toString();
    }

    private Object getCheckTag(DicPersonal dicPersonal) {
        Personal personal = new Personal();
        String marry = getStringList(dicPersonal.getMarryObj());
        if (marry != null && marry.length() > 0) {
            personal.setMarry(marry.substring(0, 1));
        }
        String child = getStringList(dicPersonal.getChildObj());
        if (child != null && child.length() > 0) {
            personal.setChild(child.substring(0, 1));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getStringList(dicPersonal.getNormalObj()));
        sb.append(getStringList(dicPersonal.getLiveEnObj()));
        sb.append(getStringList(dicPersonal.getMensesObj()));
        String[] strings = sb.toString().split("\\;");
        List<String> mList = new ArrayList<>(Arrays.asList(strings));
        personal.setHabit(mList);
        return personal;
    }

    private void addTextView(String title, int level) {
        View view = getLayoutInflater().inflate(R.layout.dialog_dic_sick_text, null);
        view.setPadding((level + 1) * DISTANCE, 0, 0, 0);
        TextView textView = view.findViewById(R.id.tv_dialog_dic_sick_text);
        textView.setText(title);
        if (llContent.getChildCount() != 0) {
            int top = level == 0 ? 24 : 2;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, top, 0, 0);
            view.setLayoutParams(lp);
        }
        llContent.addView(view);
    }

    private void addEditText(int level, Object tag) {
        View view = getLayoutInflater().inflate(R.layout.dialog_dic_sick_edit, null);
        view.setPadding((level + 1) * DISTANCE, 0, 0, 0);
        EditText editText = view.findViewById(R.id.et_dialog_dic_sick_edit);
        editText.setTag(tag);
        llContent.addView(view);
    }

    //type 1:RadioButton 2:CheckBox
    private void addRecyclerView(List<DicSick> sickList, int type, int level) {
        View view = getLayoutInflater().inflate(R.layout.dialog_dic_sick_recycler, null);
        view.setPadding((level + 1) * DISTANCE, 0, 0, 0);
        RecyclerView recyclerView = view.findViewById(R.id.rv_dialog_dic_sick_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        FlexboxLayoutManager flm = new ScrollFlexBoxManager(getContext());
        flm.setFlexDirection(FlexDirection.ROW);
        flm.setFlexWrap(FlexWrap.WRAP);
        recyclerView.setLayoutManager(flm);
        SickAdapter adapter = new SickAdapter(sickList, type);
        recyclerView.setAdapter(adapter);
        llContent.addView(recyclerView);
    }

    class SickAdapter extends RecyclerView.Adapter<SickHolder> {

        private int type;
        private List<DicSick> mList;

        public SickAdapter(List<DicSick> list, int type) {
            this.mList = list;
            this.type = type;
        }

        @NonNull
        @Override
        public SickHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.dialog_dic_sick_item, null);
            return new SickHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SickHolder sickHolder, final int i) {
            CompoundButton btn = (CompoundButton) sickHolder.itemView;
            FlexboxLayoutManager.LayoutParams layoutParams = new FlexboxLayoutManager.LayoutParams(-2, -2);
            layoutParams.setMargins(12, 12, 12, 12);
            btn.setLayoutParams(layoutParams);
            btn.setText(mList.get(i).getDataName());
            btn.setChecked(mList.get(i).isChecked());
            btn.setTag(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    if (type == 1) {
                        for (int j = 0; j < mList.size(); j++) {
                            if (j == index) {
                                mList.get(j).setChecked(true);
                            } else {
                                mList.get(j).setChecked(false);
                            }
                        }
                    } else {
                        mList.get(index).setChecked(!mList.get(index).isChecked());
                    }
                    SickAdapter.this.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    class SickHolder extends RecyclerView.ViewHolder {

        public SickHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private String getStringList(List<DicSick> list) {
        StringBuilder sb = new StringBuilder();
        for (DicSick dicSick : list) {
            if (dicSick.isChecked()) {
                sb.append(dicSick.getDataValue());
                sb.append(";");
            }
        }
        EditText et = contentView.findViewWithTag(list.get(0).getDicName());
        if (et != null && !TextUtils.isEmpty(et.getText())) {
            String etText = et.getText().toString();
            sb.append(etText);
            if (etText.charAt(etText.length() - 1) != ';') {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private String getStringName(List<DicSick> list) {
        StringBuilder sb = new StringBuilder();
        for (DicSick dicSick : list) {
            if (dicSick.isChecked()) {
                sb.append(dicSick.getDataName());
                sb.append(";");
            }
        }
        return sb.toString();
    }


}
