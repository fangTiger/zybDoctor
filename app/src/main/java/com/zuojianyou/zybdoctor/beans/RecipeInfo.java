package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class RecipeInfo {

    private String name;
    private String sickName;
    private String western;
    private String repiceId;
    private List<MedicineInfo> medList;
    private String crtTime;
    private String doctorAdvice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSickName() {
        return sickName;
    }

    public void setSickName(String sickName) {
        this.sickName = sickName;
    }

    public String getWestern() {
        return western;
    }

    public void setWestern(String western) {
        this.western = western;
    }

    public String getRepiceId() {
        return repiceId;
    }

    public void setRepiceId(String repiceId) {
        this.repiceId = repiceId;
    }

    public List<MedicineInfo> getMedList() {
        return medList;
    }

    public void setMedList(List<MedicineInfo> medList) {
        this.medList = medList;
    }

    public String getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(String crtTime) {
        this.crtTime = crtTime;
    }

    public String getDoctorAdvice() {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }
}
