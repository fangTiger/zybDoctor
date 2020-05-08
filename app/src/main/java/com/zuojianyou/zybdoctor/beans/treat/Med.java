package com.zuojianyou.zybdoctor.beans.treat;

public class Med {

    private String medicineId;
    private String gdName;
    private String useNum;
    private String typ;
    private String price;
    private String xrpice;
    private String xprice;
    private String medUnit;
    private String manager;
    private String centerId;

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getXrpice() {
        return xrpice;
    }

    public void setXrpice(String xrpice) {
        this.xrpice = xrpice;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getUseNum() {
        return useNum;
    }

    public void setUseNum(String useNum) {
        this.useNum = useNum;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getXprice() {
        return xprice;
    }

    public void setXprice(String xprice) {
        this.xprice = xprice;
    }

    public String getGdName() {
        return gdName;
    }

    public void setGdName(String gdName) {
        this.gdName = gdName;
    }

    public String getMedUnit() {
        return medUnit;
    }

    public void setMedUnit(String medUnit) {
        this.medUnit = medUnit;
    }
}
