package com.zuojianyou.zybdoctor.beans.treat;

public class Revisit {

    private String repiceId;
    private String repiceName;
    private String useNum;
    private String dayNum;
    private String instruction;
    private String dealFlag;//* 0不代煎 1代煎
    private String dealPrice;//* 代煎费用
    private String dealName;
    private int typ;//1中医循证 2或其他个人处方
    private String manager;//1平台药品 2门诊药品

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getRepiceId() {
        return repiceId;
    }

    public void setRepiceId(String repiceId) {
        this.repiceId = repiceId;
    }

    public String getRepiceName() {
        return repiceName;
    }

    public void setRepiceName(String repiceName) {
        this.repiceName = repiceName;
    }

    public String getUseNum() {
        return useNum;
    }

    public void setUseNum(String useNum) {
        this.useNum = useNum;
    }

    public String getDayNum() {
        return dayNum;
    }

    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDealFlag() {
        return dealFlag;
    }

    public void setDealFlag(String dealFlag) {
        this.dealFlag = dealFlag;
    }

    public String getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(String dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
    }
}
