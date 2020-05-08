package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicBody {

    private List<DicSick> lkBodyTypeObj;
    private List<DicSick> lkSpiritObj;

    public List<DicSick> getLkBodyTypeObj() {
        return lkBodyTypeObj;
    }

    public void setLkBodyTypeObj(List<DicSick> lkBodyTypeObj) {
        this.lkBodyTypeObj = lkBodyTypeObj;
    }

    public List<DicSick> getLkSpiritObj() {
        return lkSpiritObj;
    }

    public void setLkSpiritObj(List<DicSick> lkSpiritObj) {
        this.lkSpiritObj = lkSpiritObj;
    }
}
