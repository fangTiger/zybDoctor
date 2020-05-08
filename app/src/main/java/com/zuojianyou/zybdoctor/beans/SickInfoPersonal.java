package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class SickInfoPersonal {

    private DictionaryBean marry;//婚姻状况 1未婚 2已婚
    private DictionaryBean child;//生育状况 1无子 2有子
    private List<String> habit;//传染病

    public DictionaryBean getMarry() {
        return marry;
    }

    public void setMarry(DictionaryBean marry) {
        this.marry = marry;
    }

    public DictionaryBean getChild() {
        return child;
    }

    public void setChild(DictionaryBean child) {
        this.child = child;
    }

    public List<String> getHabit() {
        return habit;
    }

    public void setHabit(List<String> habit) {
        this.habit = habit;
    }
}
