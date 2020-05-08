package com.zuojianyou.zybdoctor.beans;

public class DoctorInfo {

    private String name;
    private String hospName;
    private String shPic;
    private String docOffiName;
    private String goodAt;
    private String province;
    private String city;
    private String docTypeName;
    private String wdesc;
    private String authFlag;//0-未认证 1-认证中 2-认证驳回  9-已认证
    private String unAuthReason;

    public String getUnAuthReason() {
        return unAuthReason;
    }

    public void setUnAuthReason(String unAuthReason) {
        this.unAuthReason = unAuthReason;
    }

    public String getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(String authFlag) {
        this.authFlag = authFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospName() {
        return hospName;
    }

    public void setHospName(String hospName) {
        this.hospName = hospName;
    }

    public String getShPic() {
        return shPic;
    }

    public void setShPic(String shPic) {
        this.shPic = shPic;
    }

    public String getDocOffiName() {
        return docOffiName;
    }

    public void setDocOffiName(String docOffiName) {
        this.docOffiName = docOffiName;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public String getWdesc() {
        return wdesc;
    }

    public void setWdesc(String wdesc) {
        this.wdesc = wdesc;
    }
}
