package com.imax.app.data.api.interceptors;

import android.content.Context;
import android.text.TextUtils;
import com.imax.app.App;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private Context context;

    public HeaderInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        if (((App) context.getApplicationContext()).isPref_sessionOpen() &&
                TextUtils.isEmpty(original.header("Authorization"))) {
            Request request = original.newBuilder()
                    .addHeader("Authorization",
                            ((App) context.getApplicationContext()).getAuthorization()).build();

            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}
