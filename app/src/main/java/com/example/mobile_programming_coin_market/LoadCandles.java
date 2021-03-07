package com.example.mobile_programming_coin_market;

import android.icu.util.Calendar;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.text.DateFormat.getDateTimeInstance;


public class LoadCandles implements Runnable{
    private String symbol;
    private Range range;

    public LoadCandles(String symbol, Range range){
        this.range = range;
        this.symbol = symbol;
    }

    public void getCandles() {

        OkHttpClient okHttpClient = new OkHttpClient();

        String miniUrl;
        final String description;
        switch (range) {

            case weekly:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(getCurrentDate()).concat("&limit=7"));
                description = "Daily candles from now";
                break;

            case oneMonth:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(getCurrentDate()).concat("&limit=30"));
                description = "Daily candles from now";
                break;

            default:
                miniUrl = "";
                description = "";

        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(symbol).concat("/USD/history?".concat(miniUrl)))
                .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
                .addHeader("X-CoinAPI-Key", "D35CB680-6FEC-4CC9-AAD7-7BB34F7631B1")
                .addHeader("Accept" ,"application/json")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String result = response.body().string();
                    Log.i("Candle", "Ok received");
                    Gson gson = new Gson();
                    CandleModel[] candleModels = gson.fromJson(result, CandleModel[].class);
                    //extractCandlesFromResponse(response.body().string(), description);
                }
            }
        });

    }

    private String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return str.split(" ")[0] + "T"+str.split(" ")[1];
    }


    @Override
    public void run() {
        getCandles();
    }
}
