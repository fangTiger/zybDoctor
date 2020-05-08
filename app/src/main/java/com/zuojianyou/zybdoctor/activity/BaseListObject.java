package com.zuojianyou.zybdoctor.activity;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListObject {

    protected RecyclerView.LayoutManager layoutManager;

    protected abstract RecyclerView.LayoutManager createLayoutManager();

    protected RecyclerView.Adapter adapter;

    protected abstract RecyclerView.Adapter createAdapter();

    protected RecyclerView.ItemDecoration itemDecoration;

    protected String requestUrl;
    protected Object requestBody;
    protected Class clazz;
    protected List list;

    public BaseListObject() {
        this.layoutManager = createLayoutManager();
        this.adapter = createAdapter();
        list=new ArrayList();
    }
}
