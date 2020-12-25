package com.zuojianyou.zybdoctor.beans;

public class AskListInfo {
    private String personid;
    private String mbrId;
    private String name;
    private String age;
    private String phone;
    private String province;
    private String city;
    private String country;
    private String birthProvince;
    private String birthCity;
    private String address;
    private String idNumber;
    private String registrationId;
    private String wstate;
    private String diagnosePredict;
    private String diagnoseId;
    private String personimg;
    private double fee;
    private String regTyp; // 1-主医生  2-协助医生 3-转诊 只有为2才不能修改数据
    private String accId; // 返回上一次协助/专家 的用户ID
    private DictionaryBean sourceObj;
    private DictionaryBean sexObj;
    private DictionaryBean diagObj;
    private DictionaryBean payObj;
    private DictionaryBean takeMedObj;
    private DictionaryBean birthCountyObj;

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getRegTyp() {
        return regTyp;
    }

    public void setRegTyp(String regTyp) {
        this.regTyp = regTyp;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getPersonimg() {
        return personimg;
    }

    public void setPersonimg(String personimg) {
        this.personimg = personimg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthProvince() {
        return birthProvince;
    }

    public void setBirthProvince(String birthProvince) {
        this.birthProvince = birthProvince;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public DictionaryBean getBirthCountyObj() {
        return birthCountyObj;
    }

    public void setBirthCountyObj(DictionaryBean birthCountyObj) {
        this.birthCountyObj = birthCountyObj;
    }

    public String getDiagnoseId() {
        return diagnoseId;
    }

    public void setDiagnoseId(String diagnoseId) {
        this.diagnoseId = diagnoseId;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getWstate() {
        return wstate;
    }

    public void setWstate(String wstate) {
        this.wstate = wstate;
    }

    public String getDiagnosePredict() {
        return diagnosePredict;
    }

    public void setDiagnosePredict(String diagnosePredict) {
        this.diagnosePredict = diagnosePredict;
    }

    public DictionaryBean getSourceObj() {
        return sourceObj;
    }

    public void setSourceObj(DictionaryBean sourceObj) {
        this.sourceObj = sourceObj;
    }

    public DictionaryBean getSexObj() {
        return sexObj;
    }

    public void setSexObj(DictionaryBean sexObj) {
        this.sexObj = sexObj;
    }

    public DictionaryBean getDiagObj() {
        return diagObj;
    }

    public void setDiagObj(DictionaryBean diagObj) {
        this.diagObj = diagObj;
    }

    public DictionaryBean getPayObj() {
        return payObj;
    }

    public void setPayObj(DictionaryBean payObj) {
        this.payObj = payObj;
    }

    public DictionaryBean getTakeMedObj() {
        return takeMedObj;
    }

    public void setTakeMedObj(DictionaryBean takeMedObj) {
        this.takeMedObj = takeMedObj;
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
}
