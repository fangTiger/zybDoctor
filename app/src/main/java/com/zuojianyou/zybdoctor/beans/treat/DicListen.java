package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicListen {

    private List<DicSick> liVoiceObj;
    private List<DicSick> liOdorObj;

    public List<DicSick> getLiVoiceObj() {
        return liVoiceObj;
    }

    public void setLiVoiceObj(List<DicSick> liVoiceObj) {
        this.liVoiceObj = liVoiceObj;
    }

    public List<DicSick> getLiOdorObj() {
        return liOdorObj;
    }

    public void setLiOdorObj(List<DicSick> liOdorObj) {
        this.liOdorObj = liOdorObj;
    }
}
