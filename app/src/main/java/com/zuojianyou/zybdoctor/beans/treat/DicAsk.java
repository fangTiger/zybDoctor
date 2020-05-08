package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicAsk {

    private List<DicSick> askSweatObj;
    private List<DicSick> askDietObj;
    private List<DicSick> askErBianObj;
    private List<DicSick> askSleepObj;
    private List<DicSick> askColdHeatObj;
    private DicPain askPainObj;

    public List<DicSick> getAskSweatObj() {
        return askSweatObj;
    }

    public void setAskSweatObj(List<DicSick> askSweatObj) {
        this.askSweatObj = askSweatObj;
    }

    public List<DicSick> getAskDietObj() {
        return askDietObj;
    }

    public void setAskDietObj(List<DicSick> askDietObj) {
        this.askDietObj = askDietObj;
    }

    public List<DicSick> getAskErBianObj() {
        return askErBianObj;
    }

    public void setAskErBianObj(List<DicSick> askErBianObj) {
        this.askErBianObj = askErBianObj;
    }

    public List<DicSick> getAskSleepObj() {
        return askSleepObj;
    }

    public void setAskSleepObj(List<DicSick> askSleepObj) {
        this.askSleepObj = askSleepObj;
    }

    public List<DicSick> getAskColdHeatObj() {
        return askColdHeatObj;
    }

    public void setAskColdHeatObj(List<DicSick> askColdHeatObj) {
        this.askColdHeatObj = askColdHeatObj;
    }

    public DicPain getAskPainObj() {
        return askPainObj;
    }

    public void setAskPainObj(DicPain askPainObj) {
        this.askPainObj = askPainObj;
    }
}
