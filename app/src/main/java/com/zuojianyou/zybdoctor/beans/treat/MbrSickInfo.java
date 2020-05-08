package com.zuojianyou.zybdoctor.beans.treat;

import com.zuojianyou.zybdoctor.beans.SickInfoOpration;
import com.zuojianyou.zybdoctor.beans.SickInfoPersonal;

import java.util.List;

public class MbrSickInfo {

    private List<String> heredity;//家族史
    private SickInfoPersonal personal;//个人史
    private SickInfoOpration opration;//既往史

    public List<String> getHeredity() {
        return heredity;
    }

    public void setHeredity(List<String> heredity) {
        this.heredity = heredity;
    }

    public SickInfoPersonal getPersonal() {
        return personal;
    }

    public void setPersonal(SickInfoPersonal personal) {
        this.personal = personal;
    }

    public SickInfoOpration getOpration() {
        return opration;
    }

    public void setOpration(SickInfoOpration opration) {
        this.opration = opration;
    }
}
