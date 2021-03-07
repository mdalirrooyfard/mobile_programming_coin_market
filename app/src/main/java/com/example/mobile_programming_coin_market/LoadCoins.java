package com.example.mobile_programming_coin_market;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

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
        request = new Request.Builder().url(uri).addHeader("X-CMC_PRO_API_KEY", "3d34d69c-aefa-4c11-aa70-a8a9f49fa577").addHeader("Accept" ,"application/json").build();
        coins = new ArrayList<CoinModel>();

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
//                        coin.setIcon(GlideApp.with(context).load(url));
                        coins.add(coin);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("start", "HERE"+ coin.toString());
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
//        mainActivity.showProgressBar();
    }

    @Override
    public void setDataAfterLoading(Object coins) {
        Log.i("inPost","entered set Data");
        if (mainActivityRef.get() != null){
            TextView textView = (TextView) mainActivityRef.get().findViewById((R.id.firstcoin));
            textView.setText(((ArrayList<CoinModel>)coins).get(0).name);
//            //adapt contents
        }

    }
}
