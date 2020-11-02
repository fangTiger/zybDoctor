package com.zuojianyou.zybdoctor.api;


import com.zuojianyou.zybdoctor.beans.EbmMenuInfo;
import com.zuojianyou.zybdoctor.model.response.HttpResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface NetApis {

    /**
     * 医学库获取树形结构
     * @return
     */
    @GET("/appDoc/diagnose/getDiaTreeList")
    Flowable<HttpResponse<List<EbmMenuInfo>>> getDiaTreeList();
}
