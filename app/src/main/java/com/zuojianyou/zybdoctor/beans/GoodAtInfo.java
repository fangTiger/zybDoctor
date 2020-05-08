package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class GoodAtInfo {

    private String officeId;
    private String officeName;
    private int intSort;
    private int isChose;
    private List<GoodAtOfficeInfo> smsOfficeSickList;

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public int getIntSort() {
        return intSort;
    }

    public void setIntSort(int intSort) {
        this.intSort = intSort;
    }

    public int getIsChose() {
        return isChose;
    }

    public void setIsChose(int isChose) {
        this.isChose = isChose;
    }

    public List<GoodAtOfficeInfo> getSmsOfficeSickList() {
        return smsOfficeSickList;
    }

    public void setSmsOfficeSickList(List<GoodAtOfficeInfo> smsOfficeSickList) {
        this.smsOfficeSickList = smsOfficeSickList;
    }
}
