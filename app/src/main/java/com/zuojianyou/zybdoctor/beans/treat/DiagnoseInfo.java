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
    private PulseObj pulseObj;
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

    public PulseObj getPulseObj() {
        return pulseObj;
    }

    public void setPulseObj(PulseObj pulseObj) {
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

    public static class PulseObj{
        private List<DicSick> lecunPulseObj;
        private List<DicSick> leguPulseObj;
        private List<DicSick> lechiPulseObj;
        private List<DicSick> ricunPulseObj;
        private List<DicSick> riguPulseObj;
        private List<DicSick> richiPulseObj;

        public List<DicSick> getLecunPulseObj() {
            return lecunPulseObj;
        }

        public void setLecunPulseObj(List<DicSick> lecunPulseObj) {
            this.lecunPulseObj = lecunPulseObj;
        }

        public List<DicSick> getLeguPulseObj() {
            return leguPulseObj;
        }

        public void setLeguPulseObj(List<DicSick> leguPulseObj) {
            this.leguPulseObj = leguPulseObj;
        }

        public List<DicSick> getLechiPulseObj() {
            return lechiPulseObj;
        }

        public void setLechiPulseObj(List<DicSick> lechiPulseObj) {
            this.lechiPulseObj = lechiPulseObj;
        }

        public List<DicSick> getRicunPulseObj() {
            return ricunPulseObj;
        }

        public void setRicunPulseObj(List<DicSick> ricunPulseObj) {
            this.ricunPulseObj = ricunPulseObj;
        }

        public List<DicSick> getRiguPulseObj() {
            return riguPulseObj;
        }

        public void setRiguPulseObj(List<DicSick> riguPulseObj) {
            this.riguPulseObj = riguPulseObj;
        }

        public List<DicSick> getRichiPulseObj() {
            return richiPulseObj;
        }

        public void setRichiPulseObj(List<DicSick> richiPulseObj) {
            this.richiPulseObj = richiPulseObj;
        }
    }
}
