package com.example.mobile_programming_coin_market;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;


public class LoadCoins implements Runnable {
    private OkHttpClient client;
    private Request request;
    private Context context;
    public LoadCoins(int start, Context context) {
        this.context = context;
        client = new OkHttpClient();
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=10";
        request = new Request.Builder().url(uri).addHeader("X-CMC_PRO_API_KEY", "3d34d69c-aefa-4c11-aa70-a8a9f49fa577").addHeader("Accept" ,"application/json").build();
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
                ArrayList<CoinModel> coins = new ArrayList<CoinModel>();
                JSONObject object = null;
                JSONArray array = null;
                try {
                    object = new JSONObject(response.body().string());
                    Log.i("body"," body:"+object.toString());
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
                    Log.i("start", "HERE"+ coin.toString());
                }
            }
        });
    }
}
