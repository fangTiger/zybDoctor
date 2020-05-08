package com.zuojianyou.zybdoctor.beans.treat;

import java.util.ArrayList;
import java.util.List;

public class SickHis {

    private Personal personal;//个人史
    private Opration opration;//既往史
    private List<String> heredity;//家族史


    public SickHis() {
        personal = new Personal();
        opration = new Opration();
        heredity = new ArrayList<>();
    }

    public Opration getOpration() {
        return opration;
    }

    public void setOpration(Opration opration) {
        this.opration = opration;
    }

    public List<String> getHeredity() {
        return heredity;
    }

    public void setHeredity(List<String> heredity) {
        this.heredity = heredity;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }
}
