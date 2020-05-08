package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class SickInfo {

    private String sickId;
    private String name;
    private String symptom;//中医辨证依据
    private String pathogeny;//
    private String therapies;//
    private String western;//
    private String repiceId;//
    private String tisane;//煎药/服药方法
    private String doctorAdvice;//医嘱
    private String repiceName;//验方名称
    private List<MedicineInfo> medList;//药品列表
    private boolean isExpand;
    private int officeId;//若返回0则意味当前病症未关联科室
    private String officeName;
    private int offsickId;//若返回0则意味当前病症未关联科室
    private String offsickName;

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public int getOffsickId() {
        return offsickId;
    }

    public void setOffsickId(int offsickId) {
        this.offsickId = offsickId;
    }

    public String getOffsickName() {
        return offsickName;
    }

    public void setOffsickName(String offsickName) {
        this.offsickName = offsickName;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getSickId() {
        return sickId;
    }

    public void setSickId(String sickId) {
        this.sickId = sickId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getPathogeny() {
        return pathogeny;
    }

    public void setPathogeny(String pathogeny) {
        this.pathogeny = pathogeny;
    }

    public String getTherapies() {
        return therapies;
    }

    public void setTherapies(String therapies) {
        this.therapies = therapies;
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

    public String getTisane() {
        return tisane;
    }

    public void setTisane(String tisane) {
        this.tisane = tisane;
    }

    public String getDoctorAdvice() {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }

    public String getRepiceName() {
        return repiceName;
    }

    public void setRepiceName(String repiceName) {
        this.repiceName = repiceName;
    }

    public List<MedicineInfo> getMedList() {
        return medList;
    }

    public void setMedList(List<MedicineInfo> medList) {
        this.medList = medList;
    }
}
