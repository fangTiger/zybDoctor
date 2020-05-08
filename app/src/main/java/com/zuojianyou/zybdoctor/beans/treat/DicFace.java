package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicFace {

    private List<DicSick> lkFaceColorObj;
    private List<DicSick> lkSkinObj;

    public List<DicSick> getLkFaceColorObj() {
        return lkFaceColorObj;
    }

    public void setLkFaceColorObj(List<DicSick> lkFaceColorObj) {
        this.lkFaceColorObj = lkFaceColorObj;
    }

    public List<DicSick> getLkSkinObj() {
        return lkSkinObj;
    }

    public void setLkSkinObj(List<DicSick> lkSkinObj) {
        this.lkSkinObj = lkSkinObj;
    }
}
