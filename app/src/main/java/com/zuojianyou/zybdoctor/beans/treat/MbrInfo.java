package com.zuojianyou.zybdoctor.beans.treat;

import java.io.Serializable;

public class MbrInfo implements Serializable {
    private String personid;
    private String mbrId;
    private String name;
    private String sex;
    private int age;
    private String idNumber;
    private String phone;
    private String birthCounty;
    private String birthProvince;
    private String birthCity;
    private String province;
    private String city;
    private String country;
    private String address;
    private String priceType;
    private String personimg;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }


    public String getBirthCounty() {
        return birthCounty;
    }

    public void setBirthCounty(String birthCounty) {
        this.birthCounty = birthCounty;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}
