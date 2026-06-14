package com.agrovet.pos.network;

import com.agrovet.pos.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofitUsuarios = null;
    private static Retrofit retrofitInventario = null;
    private static Retrofit retrofitVentas = null;
    private static Retrofit retrofitReportes = null;

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    okhttp3.Request request = chain.request();
                    // Log request details to our AppLogger
                    com.agrovet.pos.utils.AppLogger.d(">> HTTP " + request.method() + " " + request.url());
                    if (request.body() != null) {
                        okio.Buffer buffer = new okio.Buffer();
                        request.body().writeTo(buffer);
                        com.agrovet.pos.utils.AppLogger.d(">> Payload: " + buffer.readUtf8());
                    }
                    
                    okhttp3.Response response = chain.proceed(request);
                    com.agrovet.pos.utils.AppLogger.d("<< Response [" + response.code() + "] de " + request.url());
                    return response;
                })
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public static Retrofit getUsuariosClient() {
        if (retrofitUsuarios == null) {
            retrofitUsuarios = buildRetrofit(Constants.URL_USUARIOS);
        }
        return retrofitUsuarios;
    }

    public static Retrofit getInventarioClient() {
        if (retrofitInventario == null) {
            retrofitInventario = buildRetrofit(Constants.URL_INVENTARIO);
        }
        return retrofitInventario;
    }

    public static Retrofit getVentasClient() {
        if (retrofitVentas == null) {
            retrofitVentas = buildRetrofit(Constants.URL_VENTAS);
        }
        return retrofitVentas;
    }

    public static Retrofit getReportesClient() {
        if (retrofitReportes == null) {
            retrofitReportes = buildRetrofit(Constants.URL_REPORTES);
        }
        return retrofitReportes;
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
