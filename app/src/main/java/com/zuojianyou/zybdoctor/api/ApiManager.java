package com.zuojianyou.zybdoctor.api;

import android.text.TextUtils;

import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.app.Constants;
import com.zuojianyou.zybdoctor.application.MyApplication;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.beans.EbmMenuInfo;
import com.zuojianyou.zybdoctor.model.response.HttpResponse;
import com.zuojianyou.zybdoctor.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static NetApis netApis;
    private Converter.Factory gsonConverterFactory = GsonConverterFactory.create();

    private static class ApiManageHolder {
        private static final ApiManager INSTANCE = new ApiManager();
    }

    private ApiManager() {}

    public static final ApiManager getInstance() {
        return ApiManageHolder.INSTANCE;
    }

//    public Flowable<HttpResponse<FileUploadResponse>> fileUpload(String filePath, String fileName,String extra, String path) throws Exception{
//        RequestBody requestBody;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            InputStream inputStream = MyApplication.getInstance().getContentResolver().openInputStream(Uri.parse(filePath));
//            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//            requestBody =
//                    RequestBody.create(buffer,MediaType.parse("multipart/form-data"));
//        } else {
//            requestBody =
//                    RequestBody.create(new File(filePath),MediaType.parse("multipart/form-data"));
//        }
//
//        MultipartBody.Part part =
//                MultipartBody.Part.createFormData("file", fileName, requestBody);
//
//        Map<String, RequestBody> map = new HashMap<>();
//        map.put("extra", RequestBody.create(extra, MediaType.parse("multipart/form-data")));
//        map.put("path", RequestBody.create(path, MediaType.parse("multipart/form-data")));
//
//        return getNetApis().fileUpload(map, part);
//    }

    public Flowable<HttpResponse<List<EbmMenuInfo>>> getDiaTreeList(){
        return getNetApis().getDiaTreeList();
    }

    private NetApis getNetApis() {
        if (netApis == null) {
            Retrofit retrofit = new Retrofit.Builder().client(getHttpClient().build())
                                                      .baseUrl(Constants.BASE_DOMAIN)
                                                      .addCallAdapterFactory(
                                                              RxJava2CallAdapterFactory.create())
                                                      .addConverterFactory(gsonConverterFactory)
                                                      .build();
            netApis = retrofit.create(NetApis.class);
        }
        return netApis;
    }

    private OkHttpClient.Builder getHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!SystemUtils.isNetworkConnected(MyApplication.getAppContext())) {
                    throw new ApiException("网络似乎出现了点问题，请检查", "9999");
                }

                Response response = chain.proceed(request);

                if (!response.isSuccessful()) {
                    String str = response.body().string();
                    int errCode = 0;
                    String errMsg = "";
                    if (!TextUtils.isEmpty(str)) {
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            errMsg = jsonObject.getString("message");
                            errCode = jsonObject.getInt("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    throw new ApiException(errMsg, String.valueOf(errCode));
                }

                return response;
            }
        };
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String token = SpData.getToken();
                if (TextUtils.isEmpty(token)) {
                    request = request.newBuilder()
                            .addHeader("client", "1")
                            .build();
                } else {
                    request = request.newBuilder()
                            .addHeader("client", "1")
                            .addHeader("token", token)
                            .build();
                }

                return chain.proceed(request);
            }
        };

        builder.addInterceptor(interceptor);
        //设置缓存
        builder.addNetworkInterceptor(cacheInterceptor);
        builder.addInterceptor(cacheInterceptor);
        //        builder.cache(cache);
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连 true
        builder.retryOnConnectionFailure(true);

        //添加日志拦截器
        if (BuildConfig.DEBUG) {
            DefaultLogInterceptor loggingInterceptor = new DefaultLogInterceptor();
            loggingInterceptor.setPrintLevel(DefaultLogInterceptor.Level.ALL);
            //HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(loggingInterceptor);
        }
        return builder;
    }
}
