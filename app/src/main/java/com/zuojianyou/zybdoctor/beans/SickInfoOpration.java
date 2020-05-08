package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class SickInfoOpration {

    private List<String> other;
    private List<String> allergy;
    private List<String> food;

    public List<String> getOther() {
        return other;
    }

    public void setOther(List<String> other) {
        this.other = other;
    }

    public List<String> getAllergy() {
        return allergy;
    }

    public void setAllergy(List<String> allergy) {
        this.allergy = allergy;
    }

    public List<String> getFood() {
        return food;
    }

    public void setFood(List<String> food) {
        this.food = food;
    }
}
