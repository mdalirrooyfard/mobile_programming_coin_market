package com.example.mobile_programming_coin_market;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ThreadPoolExecutor;

public class SecondActivity extends AppCompatActivity {
    ThreadPoolExecutor executor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_page_layout);
        executor = MySingleTone.getThreadPool();
        Handler handler1 = new Handler(Looper.getMainLooper());
        String Symbol = (String) getIntent().getSerializableExtra("Symbol");
//        TextView t = (TextView)findViewById(R.id.symbolOfCoin);
//        t.setText(Symbol);
        Button view_7_days_candle = (Button)findViewById(R.id.seven_days);
        view_7_days_candle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadCandles l2 = new LoadCandles(SecondActivity.this, Symbol, Range.weekly);
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
                LoadCandles l2 = new LoadCandles(SecondActivity.this, Symbol, Range.oneMonth);
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
