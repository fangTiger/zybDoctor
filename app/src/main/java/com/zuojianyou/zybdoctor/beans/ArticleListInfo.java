package com.zuojianyou.zybdoctor.beans;

public class ArticleListInfo {

    private String paId;
    private String title;
    private String subtitle;
    private String pubTime;
    private String coverPath;
    private String con;
    private int likeNum;
    private int favNum;
    private DictionaryBean likeObj;
    private DictionaryBean favObj;
    private String authorName;
    private String shPic;
    private String pubPic;
    private String uploadTyp;
    private String pubTyp;
    private String beforeTime;

    public String getPubPic() {
        return pubPic;
    }

    public void setPubPic(String pubPic) {
        this.pubPic = pubPic;
    }

    public String getUploadTyp() {
        return uploadTyp;
    }

    public void setUploadTyp(String uploadTyp) {
        this.uploadTyp = uploadTyp;
    }

    public String getPubTyp() {
        return pubTyp;
    }

    public void setPubTyp(String pubTyp) {
        this.pubTyp = pubTyp;
    }

    public String getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(String beforeTime) {
        this.beforeTime = beforeTime;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getFavNum() {
        return favNum;
    }

    public void setFavNum(int favNum) {
        this.favNum = favNum;
    }

    public DictionaryBean getLikeObj() {
        return likeObj;
    }

    public void setLikeObj(DictionaryBean likeObj) {
        this.likeObj = likeObj;
    }

    public DictionaryBean getFavObj() {
        return favObj;
    }

    public void setFavObj(DictionaryBean favObj) {
        this.favObj = favObj;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getShPic() {
        return shPic;
    }

    public void setShPic(String shPic) {
        this.shPic = shPic;
    }
}
