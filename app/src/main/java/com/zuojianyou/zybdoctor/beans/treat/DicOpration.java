package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicOpration {

    private List<DicSick> normalObj;
    private List<DicSick> sickHisObj;
    private List<DicSick> drugAllergyObj;
    private List<DicSick> foodAllergyObj;
    private List<DicSick> infectionHisObj;

    public List<DicSick> getNormalObj() {
        return normalObj;
    }

    public void setNormalObj(List<DicSick> normalObj) {
        this.normalObj = normalObj;
    }

    public List<DicSick> getSickHisObj() {
        return sickHisObj;
    }

    public void setSickHisObj(List<DicSick> sickHisObj) {
        this.sickHisObj = sickHisObj;
    }

    public List<DicSick> getDrugAllergyObj() {
        return drugAllergyObj;
    }

    public void setDrugAllergyObj(List<DicSick> drugAllergyObj) {
        this.drugAllergyObj = drugAllergyObj;
    }

    public List<DicSick> getFoodAllergyObj() {
        return foodAllergyObj;
    }

    public void setFoodAllergyObj(List<DicSick> foodAllergyObj) {
        this.foodAllergyObj = foodAllergyObj;
    }

    public List<DicSick> getInfectionHisObj() {
        return infectionHisObj;
    }

    public void setInfectionHisObj(List<DicSick> infectionHisObj) {
        this.infectionHisObj = infectionHisObj;
    }
}
