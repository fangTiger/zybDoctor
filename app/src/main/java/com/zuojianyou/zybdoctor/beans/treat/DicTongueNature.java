package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicTongueNature {

    private String dicName;
    private List<DicSick> tongueColObj;
    private List<DicSick> tongueBoObj;
    private List<DicSick> tongueEnObj;

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public List<DicSick> getTongueColObj() {
        return tongueColObj;
    }

    public void setTongueColObj(List<DicSick> tongueColObj) {
        this.tongueColObj = tongueColObj;
    }

    public List<DicSick> getTongueBoObj() {
        return tongueBoObj;
    }

    public void setTongueBoObj(List<DicSick> tongueBoObj) {
        this.tongueBoObj = tongueBoObj;
    }

    public List<DicSick> getTongueEnObj() {
        return tongueEnObj;
    }

    public void setTongueEnObj(List<DicSick> tongueEnObj) {
        this.tongueEnObj = tongueEnObj;
    }
}
