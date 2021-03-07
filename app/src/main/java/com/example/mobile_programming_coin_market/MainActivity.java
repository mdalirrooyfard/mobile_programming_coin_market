package com.example.mobile_programming_coin_market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class MainActivity extends AppCompatActivity {
    ThreadPoolExecutor executor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        Handler handler = new Handler(Looper.getMainLooper());
        Context context = getApplicationContext();
        LoadCoins l = new LoadCoins(MainActivity.this, 0, context);
        try {
            l.setUiForLoading();
            executor.execute(new RunnableTask<R>(handler, l));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setButtons(){
        Handler handler1 = new Handler(Looper.getMainLooper());
        Button view_7_days_candle = (Button)findViewById(R.id.seven_days);
        view_7_days_candle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Symbol = ((TextView)findViewById(R.id.symbolOfCoin)).getText().toString();
                LoadCandles l2 = new LoadCandles(MainActivity.this, Symbol, Range.weekly);
                try {
                    l2.setUiForLoading();
                    executor.execute(new RunnableTask<R>(handler1, l2));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        Handler handler2 = new Handler(Looper.getMainLooper());
        Button view_30days_candles = (Button)findViewById(R.id.one_month);
        view_30days_candles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Symbol = ((TextView)findViewById(R.id.symbolOfCoin)).getText().toString();
                LoadCandles l2 = new LoadCandles(MainActivity.this, Symbol, Range.oneMonth);
                try {
                    l2.setUiForLoading();
                    executor.execute(new RunnableTask<R>(handler2, l2));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}