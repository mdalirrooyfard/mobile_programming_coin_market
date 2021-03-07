package com.example.mobile_programming_coin_market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        Button start = (Button) findViewById(R.id.start);
        Context context = getApplicationContext();
        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i("HEY", "HEY");
                LoadCoins l = new LoadCoins(0, context);
                executor.execute(l);
            }
        });

        Button picture = (Button) findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i("HEY", "in the second button");
                ImageView imageView = (ImageView)findViewById(R.id.coin_picture);
                Glide.with(getApplicationContext()).load("https://s2.coinmarketcap.com/static/img/coins/64x64/1.png").into(imageView);
                Log.i("HEY", "Done the picture");
            }
        });

    }
}