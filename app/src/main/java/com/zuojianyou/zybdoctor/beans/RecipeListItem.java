package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class RecipeListItem {

    private String name;
    private String sickName;
    private String western;
    private String repiceId;
    private List<MedicineInfo> medList;
    private String crtTime;
    private String doctorAdvice;
    private String dsickName;
    private String dsickId;
    private String symptom;
    private String pathogeny;
    private String therapies;
    private String westernDesc;
    private String tisane;
    private int officeId;
    private String officeName;
    private int offsickId;
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

    public String getDsickName() {
        return dsickName;
    }

    public void setDsickName(String dsickName) {
        this.dsickName = dsickName;
    }

    public String getDsickId() {
        return dsickId;
    }

    public void setDsickId(String dsickId) {
        this.dsickId = dsickId;
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

    public String getWesternDesc() {
        return westernDesc;
    }

    public void setWesternDesc(String westernDesc) {
        this.westernDesc = westernDesc;
    }

    public String getTisane() {
        return tisane;
    }

    public void setTisane(String tisane) {
        this.tisane = tisane;
    }
}
