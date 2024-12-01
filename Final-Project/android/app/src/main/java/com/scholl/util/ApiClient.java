package com.scholl.util;

import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class ApiClient {

    private static final String BASE_URL = "http://192.168.1.123:8000/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(final android.content.Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);  // 打印请求和响应体
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
                            String token = sharedPreferences.getString("access_token", "");

                            if (!token.isEmpty()) {
                                Request newRequest = chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + token)
                                        .build();
                                return chain.proceed(newRequest);
                            }

                            return chain.proceed(chain.request());
                        }
                    })
                    .build();

            // 创建 Retrofit 实例
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
