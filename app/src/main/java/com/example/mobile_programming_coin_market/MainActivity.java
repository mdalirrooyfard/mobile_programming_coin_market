package com.example.mobile_programming_coin_market;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i("HEY", "HEY");
                LoadCoins l = new LoadCoins(0);
                executor.execute(l);
            }
        });

    }
}