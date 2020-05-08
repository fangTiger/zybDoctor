package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class SickSampleItem {

    private String diagnoseId;
    private String sickId;
    private String sickName;
    private String western;
    private String pathogeny;
    private String therapy;
    private String doctorAdvice;
    private String instruction;
    private String mbrName;
    private String age;
    private String phone;
    private String sex;
    private String diagnoseTime;
    private String recipeName;
    private String cureComm;
    private int officeId;
    private String officeName;
    private int offsickId;
    private String offsickName;
    private List<MedicineInfo> medList;

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

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getCureComm() {
        return cureComm;
    }

    public void setCureComm(String cureComm) {
        this.cureComm = cureComm;
    }

    public String getDiagnoseTime() {
        return diagnoseTime;
    }

    public void setDiagnoseTime(String diagnoseTime) {
        this.diagnoseTime = diagnoseTime;
    }

    public String getDiagnoseId() {
        return diagnoseId;
    }

    public void setDiagnoseId(String diagnoseId) {
        this.diagnoseId = diagnoseId;
    }

    public String getSickId() {
        return sickId;
    }

    public void setSickId(String sickId) {
        this.sickId = sickId;
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

    public String getPathogeny() {
        return pathogeny;
    }

    public void setPathogeny(String pathogeny) {
        this.pathogeny = pathogeny;
    }

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public String getDoctorAdvice() {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getMbrName() {
        return mbrName;
    }

    public void setMbrName(String mbrName) {
        this.mbrName = mbrName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<MedicineInfo> getMedList() {
        return medList;
    }

    public void setMedList(List<MedicineInfo> medList) {
        this.medList = medList;
    }
}
