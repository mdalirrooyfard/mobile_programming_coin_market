package com.example.mobile_programming_coin_market;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoadCoins extends BaseTask {

    private WeakReference<MainActivity> mainActivityRef;
    private OkHttpClient client;
    private Request request;
    ArrayList<CoinModel> coins;
    Boolean[] isLoading;
    Integer[] start;
    Integer[] limit;
    private Context context;

    public LoadCoins(MainActivity mainActivity, Integer[] start, Integer[] limit, ArrayList<CoinModel> coins, Boolean[] isLoading, Context context) {
        this.mainActivityRef = new WeakReference<MainActivity>(mainActivity);
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10*1024*1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        if (mainActivity.checkConnection()) {
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
        this.context = context;
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start="+start[0].toString()+"&limit=20"+limit[0].toString();
        request = new Request.Builder().cacheControl(new CacheControl.Builder()
                .maxStale(365, TimeUnit.DAYS)
                .build()).url(uri).addHeader("X-CMC_PRO_API_KEY", "3d34d69c-aefa-4c11-aa70-a8a9f49fa577").addHeader("Accept" ,"application/json").build();

        this.coins = coins;
        this.start = start;
        this.limit = limit;
        this.isLoading = isLoading;

    }

    @Override
    public Object call() throws Exception {

        Object result = null;
        //some network request for example
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("start", "fail to load");
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                isLoading[0] = true;
                mainActivityRef.get().startLoadingFirstPage();
                JSONObject object = null;
                JSONArray array = null;
                try {
                    object = new JSONObject(response.body().string());
                    Log.i("body"," body:" + object.toString());
                    array = object.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < limit[0]; i++) {
                    JSONObject body = null;
                    JSONObject prices = null;
                    CoinModel coin = null;
                    try {
                        body = (JSONObject)array.get(i);
                        prices = body.getJSONObject("quote").getJSONObject("USD");
                        coin = new CoinModel(body.getInt("id"),body.getString("name"), body.getString("symbol"),prices.getDouble("price"), prices.getDouble("percent_change_1h"), prices.getDouble("percent_change_24h"), prices.getDouble("percent_change_7d"));
                        String url = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+body.getInt("id")+".png";
                        coin.setIcon(GlideApp.with(context).load(url));
                        coins.add(coin);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("start", "HERE "+ coin.toString());
                }
                synchronized (coins){
                    coins.notify();
                }
            }
        });
        synchronized (coins){
            coins.wait();
        }
        result = coins;
        isLoading[0] = false;
        mainActivityRef.get().endLoadingFirstPage();
        start[0] += limit[0];
        limit[0] = 10;
        return result;
    }

    @Override
    public void setUiForLoading() {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mainActivityRef.get().findViewById(R.id.rootlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mainActivityRef.get().getIsFirstPageLoading()) {
                    Intent refresh = new Intent(mainActivityRef.get(), mainActivityRef.get().getClass());
                    refresh.putExtra("Start", start);
                    mainActivityRef.get().startActivity(refresh);
                    mainActivityRef.get().finish();
                }
                else{
                    CharSequence message = "Please wait for full loading. Then refresh again.";
                    Toast toast = Toast.makeText(mainActivityRef.get(), message, Toast.LENGTH_LONG);
                    swipeRefreshLayout.setRefreshing(false);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void setDataAfterLoading(Object coins) {
        Log.i("inPost","entered set Data");
        if (mainActivityRef.get() != null){
            RecyclerView recyclerView = (RecyclerView) mainActivityRef.get().findViewById(R.id.coinlist);
            recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityRef.get()));
            CoinAdapter adapter = new CoinAdapter(recyclerView, mainActivityRef.get(), (ArrayList<CoinModel>) coins);
            recyclerView.setAdapter(adapter);
            mainActivityRef.get().endLoadingFirstPage();
            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)mainActivityRef.get().findViewById(R.id.rootlayout);
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}
