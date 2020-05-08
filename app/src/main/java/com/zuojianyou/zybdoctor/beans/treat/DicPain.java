package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicPain {

    private String dicName;
    private List<DicSick> painPartObj;
    private List<DicSick> painSenObj;
    private List<DicSick> painHalfObj;

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public List<DicSick> getPainPartObj() {
        return painPartObj;
    }

    public void setPainPartObj(List<DicSick> painPartObj) {
        this.painPartObj = painPartObj;
    }

    public List<DicSick> getPainSenObj() {
        return painSenObj;
    }

    public void setPainSenObj(List<DicSick> painSenObj) {
        this.painSenObj = painSenObj;
    }

    public List<DicSick> getPainHalfObj() {
        return painHalfObj;
    }

    public void setPainHalfObj(List<DicSick> painHalfObj) {
        this.painHalfObj = painHalfObj;
    }
}
