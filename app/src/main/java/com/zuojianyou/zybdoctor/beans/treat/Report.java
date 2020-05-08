package com.zuojianyou.zybdoctor.beans.treat;

public class Report {

    private String typ;//* 1-实验室检测 2-影像检测 3-望诊
    private String isNorm;//* 1-指标 2-图片
    private String normVal;//仅用于实验室检测->指标
    private String position;//仅用于图片 静态数据中dataValue值
    private String positionName;//仅用于图片 静态数据中dataName值
    private String url;//用户与实验室检测、影像检测、望诊的图片相对路径

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getIsNorm() {
        return isNorm;
    }

    public void setIsNorm(String isNorm) {
        this.isNorm = isNorm;
    }

    public String getNormVal() {
        return normVal;
    }

    public void setNormVal(String normVal) {
        this.normVal = normVal;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
