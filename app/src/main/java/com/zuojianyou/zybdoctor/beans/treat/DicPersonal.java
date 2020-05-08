package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicPersonal {

    private List<DicSick> marryObj;
    private List<DicSick> childObj;

    private List<DicSick> liveEnObj;
    private List<DicSick> normalObj;
    private List<DicSick> mensesObj;

    public List<DicSick> getMarryObj() {
        return marryObj;
    }

    public void setMarryObj(List<DicSick> marryObj) {
        this.marryObj = marryObj;
    }

    public List<DicSick> getChildObj() {
        return childObj;
    }

    public void setChildObj(List<DicSick> childObj) {
        this.childObj = childObj;
    }

    public List<DicSick> getLiveEnObj() {
        return liveEnObj;
    }

    public void setLiveEnObj(List<DicSick> liveEnObj) {
        this.liveEnObj = liveEnObj;
    }

    public List<DicSick> getNormalObj() {
        return normalObj;
    }

    public void setNormalObj(List<DicSick> normalObj) {
        this.normalObj = normalObj;
    }

    public List<DicSick> getMensesObj() {
        return mensesObj;
    }

    public void setMensesObj(List<DicSick> mensesObj) {
        this.mensesObj = mensesObj;
    }
}
