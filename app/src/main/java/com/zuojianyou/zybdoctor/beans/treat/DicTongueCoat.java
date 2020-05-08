package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicTongueCoat {

    private String dicName;
    private List<DicSick> tongueQuObj;
    private List<DicSick> tongueCoObj;

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public List<DicSick> getTongueQuObj() {
        return tongueQuObj;
    }

    public void setTongueQuObj(List<DicSick> tongueQuObj) {
        this.tongueQuObj = tongueQuObj;
    }

    public List<DicSick> getTongueCoObj() {
        return tongueCoObj;
    }

    public void setTongueCoObj(List<DicSick> tongueCoObj) {
        this.tongueCoObj = tongueCoObj;
    }
}
