package com.zuojianyou.zybdoctor.beans.treat;

import java.util.ArrayList;
import java.util.List;

public class TreatParameter {

    private SickHis sickHisObj;//
    private List<Report> reportArr;//*
    private List<Record> recordArr;//*
    private BasicInfo basicInfoObj;//*
    private List<Med> medList;//*
    private List<Med> medArr;//*
    private Revisit revisitObj;//*
    private List<Med> boxMedArr;//*

    public TreatParameter() {
        sickHisObj = new SickHis();
        reportArr = new ArrayList<>();
        recordArr = new ArrayList<>();
        medList = new ArrayList<>();
        medArr = new ArrayList<>();
        boxMedArr = new ArrayList<>();
        revisitObj = new Revisit();
        basicInfoObj = new BasicInfo();
    }

    public SickHis getSickHisObj() {
        return sickHisObj;
    }

    public void setSickHisObj(SickHis sickHisObj) {
        this.sickHisObj = sickHisObj;
    }

    public List<Report> getReportArr() {
        return reportArr;
    }

    public void setReportArr(List<Report> reportArr) {
        this.reportArr = reportArr;
    }

    public List<Record> getRecordArr() {
        return recordArr;
    }

    public void setRecordArr(List<Record> recordArr) {
        this.recordArr = recordArr;
    }

    public BasicInfo getBasicInfoObj() {
        return basicInfoObj;
    }

    public void setBasicInfoObj(BasicInfo basicInfoObj) {
        this.basicInfoObj = basicInfoObj;
    }

    public List<Med> getMedList() {
        return medList;
    }

    public void setMedList(List<Med> medList) {
        this.medList = medList;
    }

    public List<Med> getMedArr() {
        return medArr;
    }

    public void setMedArr(List<Med> medArr) {
        this.medArr = medArr;
    }

    public Revisit getRevisitObj() {
        return revisitObj;
    }

    public void setRevisitObj(Revisit revisitObj) {
        this.revisitObj = revisitObj;
    }

    public List<Med> getBoxMedArr() {
        return boxMedArr;
    }

    public void setBoxMedArr(List<Med> boxMedArr) {
        this.boxMedArr = boxMedArr;
    }
}
