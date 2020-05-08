package com.zuojianyou.zybdoctor.beans;

public class OrderListInfo {

    private String diagnoseId;
    private String diagnoseTime;
    private String sickName;
    private String name;
    private int age;
    private String phone;
    private String idNumber;
    private DictionaryBean sexObj;
    private String province;
    private String city;
    private String country;
    private String address;
    private DictionaryBean sourceObj;
    private DictionaryBean diagFlagObj;

    public String getDiagnoseId() {
        return diagnoseId;
    }

    public void setDiagnoseId(String diagnoseId) {
        this.diagnoseId = diagnoseId;
    }

    public String getDiagnoseTime() {
        return diagnoseTime;
    }

    public void setDiagnoseTime(String diagnoseTime) {
        this.diagnoseTime = diagnoseTime;
    }

    public String getSickName() {
        return sickName;
    }

    public void setSickName(String sickName) {
        this.sickName = sickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public DictionaryBean getSexObj() {
        return sexObj;
    }

    public void setSexObj(DictionaryBean sexObj) {
        this.sexObj = sexObj;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DictionaryBean getSourceObj() {
        return sourceObj;
    }

    public void setSourceObj(DictionaryBean sourceObj) {
        this.sourceObj = sourceObj;
    }

    public DictionaryBean getDiagFlagObj() {
        return diagFlagObj;
    }

    public void setDiagFlagObj(DictionaryBean diagFlagObj) {
        this.diagFlagObj = diagFlagObj;
    }
}
