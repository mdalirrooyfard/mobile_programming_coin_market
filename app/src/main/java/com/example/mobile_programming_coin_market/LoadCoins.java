package com.example.mobile_programming_coin_market;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadCoins extends BaseTask {

    private WeakReference<MainActivity> mainActivityRef;
    private OkHttpClient client;
    private Request request;
    ArrayList<CoinModel> coins;
    private Context context;

    public LoadCoins(MainActivity mainActivity, int start, Context context) {
        this.mainActivityRef = new WeakReference<MainActivity>(mainActivity);
        client = new OkHttpClient();
        this.context = context;
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=10";
        request = new Request.Builder().cacheControl(new CacheControl.Builder()
                .maxStale(365, TimeUnit.DAYS)
                .build()).url(uri).addHeader("X-CMC_PRO_API_KEY", "1b622bf4-0389-4c29-8fd4-4bb3238a7e2e").addHeader("Accept" ,"application/json").build();
        coins = new ArrayList<CoinModel>();

    }

    @Override
    public Object call() throws Exception {

        Object result = null;
        //some network request for example
//        client.newCall(request).enqueue(new Callback() {
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("start", "fail to load");
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                JSONObject object = null;
                JSONArray array = null;
                try {
                    object = new JSONObject(response.body().string());
                    Log.i("body"," body:" + object.toString());
                    array = object.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 10; i++) {
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
        }

    }
}
