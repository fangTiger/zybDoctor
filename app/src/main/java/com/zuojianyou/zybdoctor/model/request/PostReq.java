package com.zuojianyou.zybdoctor.model.request;

/**
 * @author: weiwei
 * @date: 2020/6/6
 * @description:
 */
public class PostReq {
    protected int pageNum;
    protected int pageSize;

    public PostReq(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
