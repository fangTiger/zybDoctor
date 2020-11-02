package com.zuojianyou.zybdoctor.wxapi;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zuojianyou.zybdoctor.app.Constants;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import java.io.ByteArrayOutputStream;

/**
 * Created by jc.Ren on 2018/8/14
 * e-mail:renjiangchao1989@sina.com
 * 微信分享
 */
public class WXShare {

    public static final String SHARE_TYPE_CHAT = "chat";
    public static final String SHARE_TYPE_BLOG = "blog";
    private static final int THUMB_SIZE = 150;

    private Context context;
    private IWXAPI api;

    public WXShare(Context context) {
        this.context = context;
        api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
    }

    /**
     * @param url       地址
     * @param title     标题
     * @param desc      描述
     * @param thumb     缩略图
     * @param shareType 分享类型（SHARE_TYPE_CHAT 聊天|SHARE_TYPE_BLOG 朋友圈）
     */
    public void shareWeb(String url, String title, String desc, Bitmap thumb, String shareType) {

        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = desc;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, THUMB_SIZE, THUMB_SIZE, true);
        thumb.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp,true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webPage");
        req.message = msg;
        switch (shareType) {
            case SHARE_TYPE_CHAT:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            case SHARE_TYPE_BLOG:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
            default:
                ToastUtils.show(context, "未知的分享类型！");
                return;
        }
        api.sendReq(req);
    }

    private String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
