package com.example.mobile_programming_coin_market;

import android.util.Log;
import android.widget.TextView;


import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.text.DateFormat.getDateTimeInstance;


public class LoadCandles extends BaseTask {
    private WeakReference<SecondActivity> secondActivityRef;
    private String symbol;
    private Range range;
    CandleModel[] candleModels = null;
    private final Object o = new Object();

    public LoadCandles(SecondActivity secondActivity, String symbol, Range range){
        this.range = range;
        this.symbol = symbol;
        this.secondActivityRef = new WeakReference<SecondActivity>(secondActivity);
    }

    public Object call() throws Exception{
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
                    Log.i("Candle", result);
                    Log.i("Candle", "Ok received");
                    Gson gson = new Gson();
                    candleModels = gson.fromJson(result, CandleModel[].class);
                    Log.i("candles", "first "+ candleModels[0].time_close);
                    //extractCandlesFromResponse(response.body().string(), description);
                }
                synchronized (o){
                    o.notify();
                }
            }
        });
        synchronized (o){
            o.wait();
        }
        return new ArrayList<CandleModel>(Arrays.asList(candleModels));
    }

    private String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return str.split(" ")[0] + "T"+str.split(" ")[1];
    }


    @Override
    public void setUiForLoading() {

    }

    @Override
    public void setDataAfterLoading(Object candles) {
        Log.i("inPostOfCandles","entered set Data");
        if (secondActivityRef.get() != null){
            ArrayList<CandleModel> candleModels1 = (ArrayList<CandleModel>)candles;
            TextView temp = (TextView) secondActivityRef.get().findViewById(R.id.Temp);
            temp.setText(candleModels1.get(0).time_close);
        }

    }
}
