package com.example.mobile_programming_coin_market;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import java.io.IOException;


public class LoadCoins implements Runnable {
    private OkHttpClient client;
    private Request request;
    public LoadCoins(int start) {
        client = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=0&limit=10";
        request = new Request.Builder().url(uri).addHeader("X-CoinAPI-Key", "1b622bf4-0389-4c29-8fd4-4bb3238a7e2e").build();
    }

    @Override
    public void run() {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("start", "fail to load");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                android.os.Process.killProcess(android.os.Process.myPid());
                String body = response.body().toString();
                Log.i("start", "done"+body);
            }
        });
    }
}
