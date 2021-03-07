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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        Handler handler = new Handler(Looper.getMainLooper());
        Button start = (Button) findViewById(R.id.start);
        TextView textView = (TextView) findViewById((R.id.firstcoin));
        Context context = getApplicationContext();

        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoadCoins l = new LoadCoins(MainActivity.this, 0, context);
                try {
                    l.setUiForLoading();
                    executor.execute(new RunnableTask<R>(handler, l));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}