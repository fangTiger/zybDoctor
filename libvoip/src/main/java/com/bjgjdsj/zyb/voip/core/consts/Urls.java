package com.bjgjdsj.zyb.voip.core.consts;

/**
 * Created by dds on 2020/4/19.
 * ddssingsong@163.com
 */
public class Urls {

    //    private final static String IP = "192.168.2.111";
    public final static String IP = "www.yimall1688.com/webrtc";

    private final static String HOST = "http://" + IP + "/";

    // 信令地址wss://www.yimall1688.com/webrtc/wss/
    public final static String WS = "wss://" + IP + "/wss";

    // 获取用户列表
    public static String getUserList() {
        return HOST + "userList";
    }

    // 获取房间列表
    public static String getRoomList() {
        return HOST + "roomList";
    }
}
