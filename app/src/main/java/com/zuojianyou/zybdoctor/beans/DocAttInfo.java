package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class DocAttInfo {

    private String name;
    private int age;
    private String birthday;
    private String idFace;
    private String idBack;
    private String practicing;
    private String qualification;
    private String skill;
    private String otherSkill;
    private String signPath;
    private DictionaryBean sexObj;
    private DictionaryBean provinceObj;
    private DictionaryBean cityObj;
    private DictionaryBean countryObj;
    private String profess;
    private String shPic;
    private String goodAt;
    private String goodAtSelf;
    private String goodAtSelfId;
    private String wdesc;
    private String wdescPic;
    private String wdescVideo;
    private String docTypeName;
    private String docOffiName;
    private String practAddress;
    private String practAgent;
    private List<DocProfessInfo> professArr;

    public String getPractAgent() {
        return practAgent;
    }

    public void setPractAgent(String practAgent) {
        this.practAgent = practAgent;
    }

    public String getWdescPic() {
        return wdescPic;
    }

    public void setWdescPic(String wdescPic) {
        this.wdescPic = wdescPic;
    }

    public String getWdescVideo() {
        return wdescVideo;
    }

    public void setWdescVideo(String wdescVideo) {
        this.wdescVideo = wdescVideo;
    }

    public String getPractAddress() {
        return practAddress;
    }

    public void setPractAddress(String practAddress) {
        this.practAddress = practAddress;
    }

    public String getOtherSkill() {
        return otherSkill;
    }

    public void setOtherSkill(String otherSkill) {
        this.otherSkill = otherSkill;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignPath() {
        return signPath;
    }

    public void setSignPath(String signPath) {
        this.signPath = signPath;
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

    public String getIdFace() {
        return idFace;
    }

    public void setIdFace(String idFace) {
        this.idFace = idFace;
    }

    public String getIdBack() {
        return idBack;
    }

    public void setIdBack(String idBack) {
        this.idBack = idBack;
    }

    public String getPracticing() {
        return practicing;
    }

    public void setPracticing(String practicing) {
        this.practicing = practicing;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public DictionaryBean getSexObj() {
        return sexObj;
    }

    public void setSexObj(DictionaryBean sexObj) {
        this.sexObj = sexObj;
    }

    public DictionaryBean getProvinceObj() {
        return provinceObj;
    }

    public void setProvinceObj(DictionaryBean provinceObj) {
        this.provinceObj = provinceObj;
    }

    public DictionaryBean getCityObj() {
        return cityObj;
    }

    public void setCityObj(DictionaryBean cityObj) {
        this.cityObj = cityObj;
    }

    public DictionaryBean getCountryObj() {
        return countryObj;
    }

    public void setCountryObj(DictionaryBean countryObj) {
        this.countryObj = countryObj;
    }

    public String getProfess() {
        return profess;
    }

    public void setProfess(String profess) {
        this.profess = profess;
    }

    public String getShPic() {
        return shPic;
    }

    public void setShPic(String shPic) {
        this.shPic = shPic;
    }

    public String getGoodAtSelf() {
        return goodAtSelf;
    }

    public void setGoodAtSelf(String goodAtSelf) {
        this.goodAtSelf = goodAtSelf;
    }

    public String getGoodAtSelfId() {
        return goodAtSelfId;
    }

    public void setGoodAtSelfId(String goodAtSelfId) {
        this.goodAtSelfId = goodAtSelfId;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public String getWdesc() {
        return wdesc;
    }

    public void setWdesc(String wdesc) {
        this.wdesc = wdesc;
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public String getDocOffiName() {
        return docOffiName;
    }

    public void setDocOffiName(String docOffiName) {
        this.docOffiName = docOffiName;
    }

    public List<DocProfessInfo> getProfessArr() {
        return professArr;
    }

    public void setProfessArr(List<DocProfessInfo> professArr) {
        this.professArr = professArr;
    }
}
