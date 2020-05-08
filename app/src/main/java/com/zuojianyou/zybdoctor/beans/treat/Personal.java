package com.zuojianyou.zybdoctor.beans.treat;

import java.util.ArrayList;
import java.util.List;

public class Personal {

    private String marry;//婚姻状况 1未婚 2已婚
    private String child;//生育状况 1无子 2有子
    private List<String> habit;//传染病

    public Personal() {
        habit = new ArrayList<>();
    }

    public String getMarry() {
        return marry;
    }

    public void setMarry(String marry) {
        this.marry = marry;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public List<String> getHabit() {
        return habit;
    }

    public void setHabit(List<String> habit) {
        this.habit = habit;
    }
}
