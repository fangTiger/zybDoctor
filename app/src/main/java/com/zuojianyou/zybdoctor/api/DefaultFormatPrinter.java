/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zuojianyou.zybdoctor.api;

import android.text.TextUtils;
import android.util.Log;

import com.zuojianyou.zybdoctor.utils.CharacterHandler;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;

/**
 * ================================================
 * 对 OkHttp 的请求和响应信息进行更规范和清晰的打印, 此类为框架默认实现, 以默认格式打印信息, 若觉得默认打印格式
 * 并不能满足自己的需求, 可自行扩展自己理想的打印格式
 *
 * ================================================
 */

public class DefaultFormatPrinter {
    private static final String TAG = "ArtHttpLog";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;

    private static final String[] OMITTED_RESPONSE = { LINE_SEPARATOR, "Omitted response body" };
    private static final String[] OMITTED_REQUEST = { LINE_SEPARATOR, "Omitted request body" };

    private static final String N = "\n";
    private static final String T = "\t";
    private static final String REQUEST_UP_LINE =
        "┌────── Request ────────────────────────────────────────────────────────────────────────";
    private static final String END_LINE =
        "└───────────────────────────────────────────────────────────────────────────────────────";
    private static final String RESPONSE_UP_LINE =
        "┌────── Response ───────────────────────────────────────────────────────────────────────";
    private static final String BODY_TAG = "Body:";
    private static final String URL_TAG = "URL: ";
    private static final String METHOD_TAG = "Method: @";
    private static final String HEADERS_TAG = "Headers:";
    private static final String STATUS_CODE_TAG = "Status Code: ";
    private static final String RECEIVED_TAG = "Received in: ";
    private static final String CORNER_UP = "┌ ";
    private static final String CORNER_BOTTOM = "└ ";
    private static final String CENTER_LINE = "├ ";
    private static final String DEFAULT_LINE = "│ ";

    private static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || N.equals(line) || T.equals(line) || TextUtils.isEmpty(
            line.trim());
    }

    /**
     * 打印网络请求信息, 当网络请求时 {{@link okhttp3.RequestBody}} 可以解析的情况
     */
    public static void printJsonRequest(Request request, String bodyString) {
        final String requestBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyString;
        //final String requestBody = BODY_TAG + LINE_SEPARATOR + bodyString;
        final String tag = getTag(true);
        //Timber.tag(tag).i(REQUEST_UP_LINE);
        //logLines(tag, , false);
        logLines(tag, getOneArray(new String[] { "  ", REQUEST_UP_LINE, URL_TAG + request.url() },
            getRequest(request), requestBody.split(LINE_SEPARATOR)), true);
        //logLines(tag, , true);
        //Timber.tag(tag).i(END_LINE);
    }

    public static String[] getOneArray(String[] a, String[] b, String[] c) {
        String[] d = new String[a.length + b.length + c.length + 1];
        for (int j = 0; j < a.length; ++j) {
            d[j] = a[j];
        }
        for (int j = 0; j < b.length; ++j) {
            d[a.length + j] = b[j];
        }
        for (int j = 0; j < c.length; ++j) {
            d[a.length + b.length + j] = c[j];
        }
        d[d.length - 1] = END_LINE;
        return d;
    }

    /**
     * 打印网络请求信息, 当网络请求时 {{@link okhttp3.RequestBody}} 为 {@code null} 或不可解析的情况
     */
    public static void printFileRequest(Request request) {
        final String tag = getTag(true);
        //Timber.tag(tag).i(REQUEST_UP_LINE);
        //logLines(tag, new String[] { URL_TAG + request.url() }, false);
        //logLines(tag, getRequest(request), true);
        //logLines(tag, OMITTED_REQUEST, true);
        //Timber.tag(tag).i(END_LINE);
        logLines(tag, getOneArray(new String[] { "  ", REQUEST_UP_LINE, URL_TAG + request.url() },
            getRequest(request), OMITTED_REQUEST), true);
    }

    /**
     * 打印网络响应信息, 当网络响应时 {{@link okhttp3.ResponseBody}} 可以解析的情况
     *
     * @param chainMs 服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code 响应码
     * @param headers 请求头
     * @param contentType 服务器返回数据的数据类型
     * @param bodyString 服务器返回的数据(已解析)
     * @param segments 域名后面的资源地址
     * @param message 响应信息
     * @param responseUrl 请求地址
     */
    public static void printJsonResponse(long chainMs, boolean isSuccessful, int code,
        String headers, MediaType contentType, String bodyString, List<String> segments,
        String message, final String responseUrl) {
        bodyString =
                DefaultLogInterceptor.isJson(contentType) ? CharacterHandler.jsonFormat(bodyString)
                : DefaultLogInterceptor.isXml(contentType) ? CharacterHandler.xmlFormat(
                    bodyString) : bodyString;

        final String responseBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyString;
        //String tagAfter = "";
        //if (!TextUtils.isEmpty(responseUrl) && responseUrl.contains("/")) {
        //    String[] strings = responseUrl.split("/");
        //    tagAfter = strings[strings.length - 1];
        //}

        final String tag = getTag(false);
        //final String[] urlLine = { URL_TAG + responseUrl, N };

        //Timber.tag(tag).i(RESPONSE_UP_LINE);
        //logLines(tag, urlLine, true);
        //暂时去掉返回打印header等信息
        //logLines(tag, getResponse(headers, chainMs, code, isSuccessful, segments, message), true);
        //logLines(tag, getResponse(null, chainMs, code, isSuccessful, segments, message), true);
        //logLines(tag, responseBody.split(LINE_SEPARATOR), true);
        //Timber.tag(tag).i(END_LINE);
        logLines(tag, getOneArray(new String[] { "  ", RESPONSE_UP_LINE, URL_TAG + responseUrl },
            getResponse(headers, chainMs, code, isSuccessful, segments, message),
            responseBody.split(LINE_SEPARATOR)), true);
    }

    /**
     * 打印网络响应信息, 当网络响应时 {{@link okhttp3.ResponseBody}} 为 {@code null} 或不可解析的情况
     *
     * @param chainMs 服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code 响应码
     * @param headers 请求头
     * @param segments 域名后面的资源地址
     * @param message 响应信息
     * @param responseUrl 请求地址
     */
    public static void printFileResponse(long chainMs, boolean isSuccessful, int code,
        String headers, List<String> segments, String message, final String responseUrl) {
        final String tag = getTag(false);
        //final String[] urlLine = { URL_TAG + responseUrl, N };

        //Timber.tag(tag).i(RESPONSE_UP_LINE);
        //logLines(tag, urlLine, true);
        //logLines(tag, getResponse(headers, chainMs, code, isSuccessful, segments, message), true);
        //logLines(tag, OMITTED_RESPONSE, true);
        //Timber.tag(tag).i(END_LINE);

        logLines(tag, getOneArray(new String[] { "  ", RESPONSE_UP_LINE, URL_TAG + responseUrl },
            getResponse(headers, chainMs, code, isSuccessful, segments,
                message), OMITTED_RESPONSE), true);
    }

    /**
     * 对 {@code lines} 中的信息进行逐行打印
     *
     * @param withLineSize 为 {@code true} 时, 每行的信息长度不会超过110, 超过则自动换行
     */
    private static void logLines(String tag, String[] lines, boolean withLineSize) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            int lineLength = line.length();
            int MAX_LONG_SIZE = withLineSize ? 110 : lineLength;
            for (int i = 0; i <= lineLength / MAX_LONG_SIZE; i++) {
                int start = i * MAX_LONG_SIZE;
                int end = (i + 1) * MAX_LONG_SIZE;
                end = end > line.length() ? line.length() : end;
                //tags = DEFAULT_LINE + line.substring(start, end)+"/n";

                sb.append("\n" + line.substring(start, end));
                //System.out.println(sb.toString());
                //sb.insert(0, "你都已看不到我们的好，");
                //System.out.println(sb.toString());
                //Timber.tag(resolveTag(tag)).i(DEFAULT_LINE + line.substring(start, end));
            }
        }
        Log.d(resolveTag(tag), sb.toString());
        //Timber.tag(resolveTag(tag)).i(sb.toString());
    }

    private static ThreadLocal<Integer> last = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private static final String[] ART = new String[] { "-A-", "-R-", "-T-" };

    private static String computeKey() {
        if (last.get() >= 3) {
            last.set(0);
        }
        String s = ART[last.get()];
        last.set(last.get() + 1);
        return s;
    }

    /**
     * 此方法是为了解决在 AndroidStudio v3.1 以上 Logcat 输出的日志无法对齐的问题
     * <p>
     * 此问题引起的原因, 据 JessYan 猜测, 可能是因为 AndroidStudio v3.1 以上将极短时间内以相同 tag 输出多次的 log 自动合并为一次输出
     * 导致本来对称的输出日志, 出现不对称的问题
     * AndroidStudio v3.1 此次对输出日志的优化, 不小心使市面上所有具有日志格式化输出功能的日志框架无法正常工作
     * 现在暂时能想到的解决方案有两个: 1. 改变每行的 tag (每行 tag 都加一个可变化的 token) 2. 延迟每行日志打印的间隔时间
     * <p>
     * {@link #resolveTag(String)} 使用第一种解决方案
     */
    private static String resolveTag(String tag) {
        return computeKey() + tag;
    }

    private static String[] getRequest(Request request) {
        String log;
        String header = request.headers().toString();
        log = METHOD_TAG + request.method() + DOUBLE_SEPARATOR + (isEmpty(header) ? ""
            : HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header));
        return log.split(LINE_SEPARATOR);
    }

    private static String[] getResponse(String header, long tookMs, int code, boolean isSuccessful,
        List<String> segments, String message) {
        String log;
        String segmentString = slashSegments(segments);
        log = ((!TextUtils.isEmpty(segmentString) ? segmentString + " - " : "")
            + "is success : "
            + isSuccessful
            + " - "
            + RECEIVED_TAG
            + tookMs
            + "ms"
            + DOUBLE_SEPARATOR
            + STATUS_CODE_TAG
            + code
            + " / "
            + message
            + DOUBLE_SEPARATOR
            + (isEmpty(header) ? "" : HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header)));
        return log.split(LINE_SEPARATOR);
    }

    private static String slashSegments(List<String> segments) {
        StringBuilder segmentString = new StringBuilder();
        for (String segment : segments) {
            segmentString.append("/").append(segment);
        }
        return segmentString.toString();
    }

    /**
     * 对 {@code header} 按规定的格式进行处理
     */
    private static String dotHeaders(String header) {
        String[] headers = header.split(LINE_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        String tag = "─ ";
        if (headers.length > 1) {
            for (int i = 0; i < headers.length; i++) {
                if (i == 0) {
                    tag = CORNER_UP;
                } else if (i == headers.length - 1) {
                    tag = CORNER_BOTTOM;
                } else {
                    tag = CENTER_LINE;
                }
                builder.append(tag).append(headers[i]).append("\n");
            }
        } else {
            for (String item : headers) {
                builder.append(tag).append(item).append("\n");
            }
        }
        return builder.toString();
    }

    private static String getTag(boolean isRequest) {
        if (isRequest) {
            return TAG + "-Request";
        } else {
            return TAG + "-Response";
        }
    }

    private static String getTag(boolean isRequest, String tagAfter) {
        if (isRequest) {
            return TAG + "-Request" + "-" + tagAfter;
        } else {
            return TAG + "-Response" + "-" + tagAfter;
        }
    }
}
