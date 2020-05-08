package com.zuojianyou.zybdoctor.beans;

public class ReviewListInfo {

    private String mbrId;
    private String name;
    private String age;
    private String address;
    private String phone;
    private String idNumber;
    private String province;
    private String city;
    private String country;
    private String diagnoseId;
    private String revistDate;
    private String sickName;
    private String diagnoseTime;
    private DictionaryBean sexObj;
    private DictionaryBean revistNoticeObj;
    private DictionaryBean sourceObj;
    private DictionaryBean birthCountyObj;

    public DictionaryBean getBirthCountyObj() {
        return birthCountyObj;
    }

    public void setBirthCountyObj(DictionaryBean birthCountyObj) {
        this.birthCountyObj = birthCountyObj;
    }

    public String getMbrId() {
        return mbrId;
    }

    public void setMbrId(String mbrId) {
        this.mbrId = mbrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDiagnoseId() {
        return diagnoseId;
    }

    public void setDiagnoseId(String diagnoseId) {
        this.diagnoseId = diagnoseId;
    }

    public String getRevistDate() {
        return revistDate;
    }

    public void setRevistDate(String revistDate) {
        this.revistDate = revistDate;
    }

    public String getSickName() {
        return sickName;
    }

    public void setSickName(String sickName) {
        this.sickName = sickName;
    }

    public String getDiagnoseTime() {
        return diagnoseTime;
    }

    public void setDiagnoseTime(String diagnoseTime) {
        this.diagnoseTime = diagnoseTime;
    }

    public DictionaryBean getSexObj() {
        return sexObj;
    }

    public void setSexObj(DictionaryBean sexObj) {
        this.sexObj = sexObj;
    }

    public DictionaryBean getRevistNoticeObj() {
        return revistNoticeObj;
    }

    public void setRevistNoticeObj(DictionaryBean revistNoticeObj) {
        this.revistNoticeObj = revistNoticeObj;
    }

    public DictionaryBean getSourceObj() {
        return sourceObj;
    }

    public void setSourceObj(DictionaryBean sourceObj) {
        this.sourceObj = sourceObj;
    }
}
