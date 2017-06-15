package exercise2.wangzhengli.test.com.treasure.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/6/12.
 */
//网络核心类
public class NetClient {
    public static final String BASE_URL = "http://admin.syfeicuiedu.com";
    private final Retrofit mRetrofit;
    private static NetClient mNetClient;
    private TreasureApi mTreasureApi;

    private NetClient() {
        //日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //okhttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 设置超时时间
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        // Gson的非严格模式:setLenient
        Gson gson = new GsonBuilder().setLenient().create();
        //retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public static synchronized NetClient getInstance() {
        if (mNetClient == null) {
            mNetClient = new NetClient();
        }
        return mNetClient;
    }

    // 获取实现后的TreasureApi
    public TreasureApi getTreasureApi() {
        // 对接口请求的实现
        if (mTreasureApi == null) {
            mTreasureApi = mRetrofit.create(TreasureApi.class);
        }
        return mTreasureApi;
    }

}
