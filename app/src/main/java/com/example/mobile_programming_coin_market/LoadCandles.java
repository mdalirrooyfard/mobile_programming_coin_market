package com.example.mobile_programming_coin_market;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
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
    ProgressBar p;
    CandleStickChart candleStickChart;

    public LoadCandles(SecondActivity secondActivity, String symbol, Range range){
        this.range = range;
        this.symbol = symbol;
        this.secondActivityRef = new WeakReference<SecondActivity>(secondActivity);
        p = (ProgressBar)secondActivityRef.get().findViewById(R.id.progress_bar);
        candleStickChart = (CandleStickChart)secondActivityRef.get().findViewById(R.id.candle_stick);
    }

    public Object call() throws Exception{
        File httpCacheDirectory = new File(secondActivityRef.get().getCacheDir(), "responses");
        int cacheSize = 10*1024*1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        if (secondActivityRef.get().checkConnection()) {
                            int maxAge = 360; // read from cache for 1 minute
                            return originalResponse.newBuilder()
                                    .header("Cache-Control", "public, max-age=" + maxAge)
                                    .build();
                        } else {
                            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                            return originalResponse.newBuilder()
                                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                    .build();
                        }
                    }
                }).cache(cache).build();

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

        final Request request = new Request.Builder().cacheControl(new CacheControl.Builder()
                .maxStale(365, TimeUnit.DAYS)
                .build()).url(url)
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
        return str.split(" ")[0] ;
    }


    @Override
    public void setUiForLoading() {
        candleStickChart.setVisibility(View.INVISIBLE);
        p.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void setDataAfterLoading(Object candles) {
        Log.i("inPostOfCandles","entered set Data");
        if (secondActivityRef.get() != null){
            ArrayList<CandleModel> candleModels1 = (ArrayList<CandleModel>)candles;
            if (candleModels1.size() > 0) {
                candleStickChart = (CandleStickChart) secondActivityRef.get().findViewById(R.id.candle_stick);
                candleStickChart.setNoDataText("");
                candleStickChart.setNoDataTextColor(R.color.white);
                candleStickChart.setHighlightPerDragEnabled(true);
                candleStickChart.setDrawBorders(true);
                YAxis yAxis = candleStickChart.getAxisLeft();
                YAxis rightAxis = candleStickChart.getAxisRight();
                yAxis.setDrawGridLines(false);
                rightAxis.setDrawGridLines(false);
                candleStickChart.requestDisallowInterceptTouchEvent(true);
                XAxis xAxis = candleStickChart.getXAxis();
                xAxis.setDrawGridLines(false);
                xAxis.setDrawLabels(true);
                rightAxis.setTextColor(Color.WHITE);
                yAxis.setDrawLabels(true);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setAvoidFirstLastClipping(true);
                candleStickChart.setPinchZoom(true);
                Legend l = candleStickChart.getLegend();
                l.setEnabled(true);
                ArrayList<CandleEntry> yValsCandleStick = new ArrayList<CandleEntry>();
                for (int i = 0; i < candleModels1.size(); i++) {
                    yValsCandleStick.add(new CandleEntry(i, candleModels1.get(i).price_high.floatValue(), candleModels1.get(i).price_low.floatValue()
                            , candleModels1.get(i).price_open.floatValue(), candleModels1.get(i).price_close.floatValue()));
                }
                CandleDataSet set1 = new CandleDataSet(yValsCandleStick, "DataSet 1");
                set1.setColor(Color.rgb(80, 80, 80));
                set1.setShadowColor(secondActivityRef.get().getResources().getColor(R.color.colorLightGrayMore));
                set1.setShadowWidth(0.8f);
                set1.setDecreasingColor(secondActivityRef.get().getResources().getColor(R.color.colorRed));
                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                set1.setIncreasingColor(secondActivityRef.get().getResources().getColor(R.color.teal_700));
                set1.setIncreasingPaintStyle(Paint.Style.FILL);
                set1.setNeutralColor(Color.LTGRAY);
                set1.setDrawValues(false);
                CandleData data = new CandleData(set1);
                candleStickChart.setData(data);
                candleStickChart.invalidate();
                secondActivityRef.get().endChartLoading();
                candleStickChart.setVisibility(View.VISIBLE);
            }
            else{
                CharSequence message = "Sorry, No candles for this coin.";
                Toast toast = Toast.makeText(secondActivityRef.get(), message, Toast.LENGTH_LONG);
                toast.show();
            }
            p.setVisibility(View.INVISIBLE);
        }


    }
}
