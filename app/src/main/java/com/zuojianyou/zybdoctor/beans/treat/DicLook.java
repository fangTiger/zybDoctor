package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicLook {

    private DicTongue lkTongueObj;
    private DicFace lkFaceObj;
    private DicBody lkBodyObj;
    private List<DicSick> handObj;

    public List<DicSick> getHandObj() {
        return handObj;
    }

    public void setHandObj(List<DicSick> handObj) {
        this.handObj = handObj;
    }

    public DicTongue getLkTongueObj() {
        return lkTongueObj;
    }

    public void setLkTongueObj(DicTongue lkTongueObj) {
        this.lkTongueObj = lkTongueObj;
    }

    public DicFace getLkFaceObj() {
        return lkFaceObj;
    }

    public void setLkFaceObj(DicFace lkFaceObj) {
        this.lkFaceObj = lkFaceObj;
    }

    public DicBody getLkBodyObj() {
        return lkBodyObj;
    }

    public void setLkBodyObj(DicBody lkBodyObj) {
        this.lkBodyObj = lkBodyObj;
    }
}
