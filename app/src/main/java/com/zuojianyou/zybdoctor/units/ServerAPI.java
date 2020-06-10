package com.zuojianyou.zybdoctor.units;

import com.zuojianyou.zybdoctor.data.SpData;

import org.xutils.http.RequestParams;

public class ServerAPI {

    public static void addHeader(RequestParams entity) {
        entity.addHeader("Content-Type", "application/json;charset=utf-8");
        entity.addHeader("client", "1");
        entity.addHeader("token", SpData.getToken());
    }

    public static final String FILL_DOMAIN = "https://www.yimall1688.com/img";
//    public static final String FILL_DOMAIN = "http://62.234.109.94:8189/img";

    public static final String BASE_DOMAIN = "https://www.yimall1688.com";

//    public static final String BASE_DOMAIN = "http://62.234.109.94:8189";

    public static final String ATICLE_SHARE_URL = "/p/joined/article.html?articleId=";
    public static final String AUTH_STATE_URL = "/appDoc/common/getIsAuthed";
    public static final String SMS_CODE_URL = "/appDoc/sms/postDocSmsCode";
    public static final String LOGIN_ACCOUNT_URL = "/appDoc/common/postAppLogin";
    public static final String LOGIN_MOBILE_URL = "/appDoc/sms/postDocSmsAppLogin";
    public static final String USER_REGISTER_URL = "/appDoc/sms/postDocRegist";
    public static final String PWD_UPD_URL = "/appDoc/mine/postUpdPwd";
    public static final String RECIPE_URL = "/appDoc/diagnose/postDocRecipeList";
    public static final String RECIPE_ADD_URL = "/appDoc/diagnose/postDocRecipe";
    public static final String RECIPE_DEL_URL = "/appDoc/diagnose/deleteDocRecipe/";
    public static final String SUB_REG_URL = "/appDoc/diagnose/postRegistConfirm";
    public static final String ASK_LIST_URL = "/appDoc/diagnose/postRegistList";
    public static final String REVIEW_LIST_URL = "/appDoc/diagnose/postRevistList";
    public static final String TREAT_SUBMIT_URL = "/appDoc/diagnose/postdiaConfirm";
    public static final String TREAT_COST_URL = "/appDoc/diagnose/postDiagnoseFee";
    public static final String TAKE_MED_URL = "/appDoc/pharmacy/postTakeMedList";
    public static final String EXECUTE_OUT_MED_URL = "/appDoc/pharmacy/postInsOutMed";
    public static final String MY_ARTICLE_LIST_URL = "/appDoc/mine/postArticleList";
    public static final String ALL_ARTICLE_LIST_URL = "/appDoc/mine/postAllArticleList";
    public static final String ARTICLE_COMMIT_URL = "/appDoc/mine/postArticleInfo";
    public static final String MY_COLLECT_LIST_URL = "/appDoc/mine/postMineFavArticleList";
    public static final String MY_EARN_LIST_URL = "/appDoc/mine/postMinePayList";
    public static final String MY_EARN_DETAIL_URL = "/appDoc/mine/postMinePayDetail";
    public static final String MY_ORDER_LIST_URL = "/appDoc/mine/postMineOrderList";
    public static final String DOC_SIGN_URL = "/appDoc/diagnose/postDiagEleFlag";
    public static final String MY_SICK_SAMPLE = "/appDoc/diagnose/postDocDiagList";
    public static final String MY_DIAG_CURE = "/appDoc/diagnose/postDiagCure";
    public static final String MY_AUTH_INFO = "/appDoc/mine/getDocAuthDetail";
    public static final String MY_AUTH_COMMIT = "/appDoc/mine/postUpdDocAuth";
    public static final String WEST_SICK_NAME_URL = "/appDoc/diagnoseSec/getWestObj/";

    public static final String FILE_UPLOAD_URL = "/appDoc/upload/fileUpload";
    public static final String FILE_DELETE_URL = "/appDoc/upload/delFileUpload";
    public static final String AI_PHOTO_UPLOAD_URL = "/third/renlian/toRecognize";
    public static final String GET_WORK_SET_URL = "/appDoc/mine/getDocSettint";
    public static final String DOC_WORK_SET_URL = "/appDoc/mine/postUpdDocSettint";
    public static final String DOC_FEED_BACK_URL = "/appDoc/mine/postDocFeedBack";

    public static final String OLD_USER_URL = "/appDoc/diagnose/getOldMbrList";
    public static final String TREAT_MBR_URL = "/appDoc/diagnose/getSickHisInfo/";
    public static final String TREAT_SOURCE_URL = "/appDoc/common/getDiagnoseResource/";
    public static final String ASK_NUM_URL = "/appDoc/diagnose/getPredictDate/";
    public static final String REVIEW_NUM_URL = "/appDoc/diagnose/getRevistDate/";
    public static final String EBM_MENU_URL = "/appDoc/diagnose/getDiaTreeList";
    public static final String EBM_LIST_URL = "/appDoc/diagnose/getDiaDataList";
    public static final String EBM_SICK_URL = "/appDoc/diagnose/getSickListByKeywd";
    public static final String PATENT_URL = "/appDoc/diagnose/getMedListByKeywd";//medType 1草药 2成品药
    public static final String MEDICINE_URL = "/appDoc/diagnose/postDocCenterMedList";
    public static final String DIAGNOSE_URL = "/appDoc/diagnose/getDiagnoseDetail/";
    public static final String UPDATE_URL = "/appDoc/common/getNewVDocApk";
    public static final String VISIT_ORDER_URL = "/appDoc/diagnose/getDiagInqInfo/";
    public static final String RECIPE_ORDER_URL = "/appDoc/diagnose/getDiagEleInfo/";
    public static final String PAY_CODE_WX_URL = "/pay/wxpay/getWxScanCode/";
    public static final String PAY_CODE_ZFB_URL = "/pay/alipay/getAliScanCode/";
    public static final String REG_FEE_URL = "/appDoc/diagnose/getHospDistrict";
    public static final String PAY_STATE_URL = "/pay/commonPay/getCommonPayResult/";
    public static final String REMAND_REVIST_URL = "/appDoc/diagnose/getRemandRevistMbr/";
    public static final String OUT_MED_URL = "/appDoc/pharmacy/getTakoutMedList/";
    public static final String DOC_INFO_URL = "/appDoc/mine/getDocInfo";
    public static final String DEL_ARTICLE_URL = "/appDoc/mine/deleteArticleInfo/";
    public static final String COLLECT_ARTICLE_URL = "/appDoc/mine/getFavUArticle";
    public static final String LIKE_ARTICLE_URL = "/appDoc/mine/getLikeUArticle";
    public static final String ARTICLE_MENU_URL = "/appDoc/common/getAllColumnList";
    public static final String DISPATCH_LIST_URL = "/appDoc/common/getAllCenterList";
    public static final String DOC_DISPATCH_LIST_URL = "/appDoc/diagnose/getDocCenterList";
    public static final String RECIPE_DETAIL_URL = "/appDoc/diagnose/getSinRecipe/";
    public static final String DEL_ASK_URL = "/appDoc/diagnose/delRegistInfo/";
    public static final String DOC_AUTH_URL = "/appDoc/mine/postUpdDocAuth";

    public static final String MY_RECIPE_TREE_URL = "/appDoc/sick/getDocTreeList";
    public static final String MY_RECIPE_ADD_URL = "/appDoc/sick/postAddUpdDocTree";
    public static final String MY_RECIPE_DEL_URL = "/appDoc/sick/delDocTreeById/";
    public static final String MY_RECIPE_CON_URL = "/appDoc/diagnose/postDocRecipeList";

    public static final String OFFICE_LIST_URL = "/appDoc/sick/getOfficeList";
    public static final String OFFICE_SICK_LIST_URL = "/appDoc/sick/getOffSickByOfficeId";

    public static final String WX_DOC_APP_PAY = "/pay/wxpay/getWxDocAppPay";

    public static final String ALI_DOC_APP_PAY = "/pay/alipay/getAliDocAppPay";

    public static String getOfficeListUrl() {
        return BASE_DOMAIN + OFFICE_LIST_URL;
    }

    public static String getOfficeSickListUrl() {
        return BASE_DOMAIN + OFFICE_SICK_LIST_URL;
    }

    public static String getAticleShareUrl(String articleId) {
        return BASE_DOMAIN + ATICLE_SHARE_URL + articleId;
    }

    public static String getAuthStateUrl() {
        return BASE_DOMAIN + AUTH_STATE_URL;
    }

    public static String getWestSickNameUrl(String repiceId) {
        return BASE_DOMAIN + WEST_SICK_NAME_URL + repiceId;
    }

    public static String getSmsCodeUrl() {
        return BASE_DOMAIN + SMS_CODE_URL;
    }

    public static String getLoginAccountUrl() {
        return BASE_DOMAIN + LOGIN_ACCOUNT_URL;
    }

    public static String getLoginMobileUrl() {
        return BASE_DOMAIN + LOGIN_MOBILE_URL;
    }

    public static String getUserRegisterUrl() {
        return BASE_DOMAIN + USER_REGISTER_URL;
    }

    public static String getPwdUpdUrl() {
        return BASE_DOMAIN + PWD_UPD_URL;
    }

    public static String getRecipeUrl() {
        return BASE_DOMAIN + RECIPE_URL;
    }

    public static String getRecipeAddUrl() {
        return BASE_DOMAIN + RECIPE_ADD_URL;
    }

    public static String getRecipeDelUrl(String id) {
        return BASE_DOMAIN + RECIPE_DEL_URL + id;
    }

    public static String getOldUserUrl() {
        return BASE_DOMAIN + OLD_USER_URL;
    }

    public static String getSubRegUrl() {
        return BASE_DOMAIN + SUB_REG_URL;
    }

    public static String getAskListUrl() {
        return BASE_DOMAIN + ASK_LIST_URL;
    }

    public static String getReviewListUrl() {
        return BASE_DOMAIN + REVIEW_LIST_URL;
    }

    public static String getTreatSubmitUrl() {
        return BASE_DOMAIN + TREAT_SUBMIT_URL;
    }

    public static String getTreatCostUrl() {
        return BASE_DOMAIN + TREAT_COST_URL;
    }

    public static String getMySickSample() {
        return BASE_DOMAIN + MY_SICK_SAMPLE;
    }

    public static String getMyRecipeConUrl() {
        return BASE_DOMAIN + MY_RECIPE_CON_URL;
    }

    public static String getMyDiagCure() {
        return BASE_DOMAIN + MY_DIAG_CURE;
    }

    public static String getMyAuthInfo() {
        return BASE_DOMAIN + MY_AUTH_INFO;
    }

    public static String getMyAuthCommit() {
        return BASE_DOMAIN + MY_AUTH_COMMIT;
    }

    public static String getTreatMbrUrl(String mbrId) {
        return BASE_DOMAIN + TREAT_MBR_URL + mbrId;
    }

    public static String getTreatSourceUrl(String type) {
        //type 1进货 2出货
        return BASE_DOMAIN + TREAT_SOURCE_URL + type;
    }

    public static String getAskNumUrl(String date) {
        return BASE_DOMAIN + ASK_NUM_URL + date;
    }

    public static String getReviewNumUrl(String date) {
        return BASE_DOMAIN + REVIEW_NUM_URL + date;
    }

    public static String getEbmMenuUrl() {
        return BASE_DOMAIN + EBM_MENU_URL;
    }

    public static String getEbmListUrl() {
        return BASE_DOMAIN + EBM_LIST_URL;
    }

    public static String getEbmSickUrl() {
        return BASE_DOMAIN + EBM_SICK_URL;
    }

    public static String getPatentUrl() {
        return BASE_DOMAIN + PATENT_URL;
    }

    public static String getMedicineUrl() {
        return BASE_DOMAIN + MEDICINE_URL;
    }

    public static String getDiagnoseUrl(String id) {
        return BASE_DOMAIN + DIAGNOSE_URL + id;
    }

    public static String getUpdateUrl() {
        return BASE_DOMAIN + UPDATE_URL;
    }

    public static String getVisitOrderUrl(String id) {
        return BASE_DOMAIN + VISIT_ORDER_URL + id;
    }

    public static String getPayCodeWxUrl(String id) {
        return BASE_DOMAIN + PAY_CODE_WX_URL + id;
    }

    public static String getPayCodeZfbUrl(String id) {
        return BASE_DOMAIN + PAY_CODE_ZFB_URL + id;
    }

    public static String getPayStateUrl(String id) {
        return BASE_DOMAIN + PAY_STATE_URL + id;
    }

    public static String getRecipeOrderUrl(String id) {
        return BASE_DOMAIN + RECIPE_ORDER_URL + id;
    }

    public static String getRecipeDetailUrl(String id) {
        return BASE_DOMAIN + RECIPE_DETAIL_URL + id;
    }

    public static String getDelAskUrl(String id) {
        return BASE_DOMAIN + DEL_ASK_URL + id;
    }

    public static String getFileUploadUrl() {
        return BASE_DOMAIN + FILE_UPLOAD_URL;
    }

    public static String getFileDeleteUrl() {
        return BASE_DOMAIN + FILE_DELETE_URL;
    }

    public static String getAiPhotoUploadUrl() {
        return BASE_DOMAIN + AI_PHOTO_UPLOAD_URL;
    }

    public static String getWorkSetUrl() {
        return BASE_DOMAIN + GET_WORK_SET_URL;
    }

    public static String getDocWorkSetUrl() {
        return BASE_DOMAIN + DOC_WORK_SET_URL;
    }

    public static String getDocFeedBackUrl() {
        return BASE_DOMAIN + DOC_FEED_BACK_URL;
    }

    public static String getRegFeeUrl() {
        return BASE_DOMAIN + REG_FEE_URL;
    }

    public static String getRemandRevistUrl(String id) {
        return BASE_DOMAIN + REMAND_REVIST_URL + id;
    }

    public static String getOutMedUrl(String id) {
        return BASE_DOMAIN + OUT_MED_URL + id;
    }

    public static String getTakeMedUrl() {
        return BASE_DOMAIN + TAKE_MED_URL;
    }

    public static String getExecuteOutMedUrl() {
        return BASE_DOMAIN + EXECUTE_OUT_MED_URL;
    }

    public static String getDocInfoUrl() {
        return BASE_DOMAIN + DOC_INFO_URL;
    }

    public static String getMyArticleListUrl() {
        return BASE_DOMAIN + MY_ARTICLE_LIST_URL;
    }

    public static String getAllArticleListUrl() {
        return BASE_DOMAIN + ALL_ARTICLE_LIST_URL;
    }

    public static String getArticleCommitUrl() {
        return BASE_DOMAIN + ARTICLE_COMMIT_URL;
    }

    public static String getDelArticleUrl(String id) {
        return BASE_DOMAIN + DEL_ARTICLE_URL + id;
    }

    public static String getMyCollectListUrl() {
        return BASE_DOMAIN + MY_COLLECT_LIST_URL;
    }

    public static String getCollectArticleUrl() {
        return BASE_DOMAIN + COLLECT_ARTICLE_URL;
    }

    public static String getLikeArticleUrl() {
        return BASE_DOMAIN + LIKE_ARTICLE_URL;
    }

    public static String getMyEarnListUrl() {
        return BASE_DOMAIN + MY_EARN_LIST_URL;
    }

    public static String getMyEarnDetailUrl() {
        return BASE_DOMAIN + MY_EARN_DETAIL_URL;
    }

    public static String getMyOrderListUrl() {
        return BASE_DOMAIN + MY_ORDER_LIST_URL;
    }

    public static String getArticleMenuUrl() {
        return BASE_DOMAIN + ARTICLE_MENU_URL;
    }

    public static String getDispatchListUrl() {
        return BASE_DOMAIN + DISPATCH_LIST_URL;
    }

    public static String getDocDispatchListUrl() {
        return BASE_DOMAIN + DOC_DISPATCH_LIST_URL;
    }

    public static String getDocSignUrl() {
        return BASE_DOMAIN + DOC_SIGN_URL;
    }

    public static String getMyRecipeTreeUrl() {
        return BASE_DOMAIN + MY_RECIPE_TREE_URL;
    }

    public static String getMyRecipeAddUrl() {
        return BASE_DOMAIN + MY_RECIPE_ADD_URL;
    }

    public static String getMyRecipeDelUrl(String sickId) {
        return BASE_DOMAIN + MY_RECIPE_DEL_URL + sickId;
    }

    public static String getDocAuthUrl() {
        return BASE_DOMAIN + DOC_AUTH_URL;
    }


}
