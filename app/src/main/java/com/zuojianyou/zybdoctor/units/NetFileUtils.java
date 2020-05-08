package com.zuojianyou.zybdoctor.units;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;
import org.xutils.x;

public class NetFileUtils {

    public void del(String... strings) {
        if (strings == null) return;
        String url = ServerAPI.getFileDeleteUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileStr", getParams(strings));
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, null);
    }

    public String getParams(String... strings) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            sb.append(";");
        }
        return sb.toString();
    }
}
