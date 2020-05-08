package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DiagnoseInfo {

    private DicLook lookObj;
    private DicAsk askObj;
    private DicPicType picTypeObj;
    private DicListen listenObj;
    private DicPersonal personalObj;
    private DicOpration oprationObj;
    private List<DicSick> heredityObj;
    private List<DicSick> pulseObj;
    private List<DicSick> dealTypeObj;

    public DicLook getLookObj() {
        return lookObj;
    }

    public void setLookObj(DicLook lookObj) {
        this.lookObj = lookObj;
    }

    public DicAsk getAskObj() {
        return askObj;
    }

    public void setAskObj(DicAsk askObj) {
        this.askObj = askObj;
    }

    public DicPicType getPicTypeObj() {
        return picTypeObj;
    }

    public void setPicTypeObj(DicPicType picTypeObj) {
        this.picTypeObj = picTypeObj;
    }

    public DicListen getListenObj() {
        return listenObj;
    }

    public void setListenObj(DicListen listenObj) {
        this.listenObj = listenObj;
    }

    public DicPersonal getPersonalObj() {
        return personalObj;
    }

    public void setPersonalObj(DicPersonal personalObj) {
        this.personalObj = personalObj;
    }

    public List<DicSick> getHeredityObj() {
        return heredityObj;
    }

    public void setHeredityObj(List<DicSick> heredityObj) {
        this.heredityObj = heredityObj;
    }

    public List<DicSick> getPulseObj() {
        return pulseObj;
    }

    public void setPulseObj(List<DicSick> pulseObj) {
        this.pulseObj = pulseObj;
    }

    public DicOpration getOprationObj() {
        return oprationObj;
    }

    public void setOprationObj(DicOpration oprationObj) {
        this.oprationObj = oprationObj;
    }

    public List<DicSick> getDealTypeObj() {
        return dealTypeObj;
    }

    public void setDealTypeObj(List<DicSick> dealTypeObj) {
        this.dealTypeObj = dealTypeObj;
    }
}
