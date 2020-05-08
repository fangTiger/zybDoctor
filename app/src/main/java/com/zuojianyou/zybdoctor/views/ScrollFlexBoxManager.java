package com.zuojianyou.zybdoctor.views;

import android.content.Context;

import com.google.android.flexbox.FlexboxLayoutManager;

public class ScrollFlexBoxManager extends FlexboxLayoutManager {

    public ScrollFlexBoxManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }
}
