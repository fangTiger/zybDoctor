package com.zuojianyou.zybdoctor;

import com.alibaba.fastjson.JSONObject;

import org.junit.Test;
import org.xutils.common.util.MD5;

import java.text.DecimalFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getFormatTime() {
        String str="2_1";
        System.out.println(str.substring(0,str.indexOf('_')));
        System.out.println(str.substring(str.indexOf('_')+1));
    }
}