package com.zuojianyou.zybdoctor.beans;

import java.util.List;

public class RecipeTreeItem {

    private String dsickId;
    private String dsickName;
    private String psickId;
    private boolean spread;
    private int level;
    private boolean isLeaf;
    private boolean isSelect;
    private List<RecipeTreeItem> children;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getDsickId() {
        return dsickId;
    }

    public void setDsickId(String dsickId) {
        this.dsickId = dsickId;
    }

    public String getDsickName() {
        return dsickName;
    }

    public void setDsickName(String dsickName) {
        this.dsickName = dsickName;
    }

    public String getPsickId() {
        return psickId;
    }

    public void setPsickId(String psickId) {
        this.psickId = psickId;
    }

    public boolean isSpread() {
        return spread;
    }

    public void setSpread(boolean spread) {
        this.spread = spread;
    }

    public List<RecipeTreeItem> getChildren() {
        return children;
    }

    public void setChildren(List<RecipeTreeItem> children) {
        this.children = children;
    }
}
