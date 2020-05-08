package com.zuojianyou.zybdoctor.beans.treat;

import java.util.List;

public class DicPicType {

    private List<DicSick> laboratoryObj;
    private List<DicSick> imagingPicObj;
    private List<DicSick> diagnosePicObj;
    private List<DicSick> indicatorObj;

    public List<DicSick> getLaboratoryObj() {
        return laboratoryObj;
    }

    public void setLaboratoryObj(List<DicSick> laboratoryObj) {
        this.laboratoryObj = laboratoryObj;
    }

    public List<DicSick> getImagingPicObj() {
        return imagingPicObj;
    }

    public void setImagingPicObj(List<DicSick> imagingPicObj) {
        this.imagingPicObj = imagingPicObj;
    }

    public List<DicSick> getDiagnosePicObj() {
        return diagnosePicObj;
    }

    public void setDiagnosePicObj(List<DicSick> diagnosePicObj) {
        this.diagnosePicObj = diagnosePicObj;
    }

    public List<DicSick> getIndicatorObj() {
        return indicatorObj;
    }

    public void setIndicatorObj(List<DicSick> indicatorObj) {
        this.indicatorObj = indicatorObj;
    }
}
