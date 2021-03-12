package com.example.mobile_programming_coin_market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private Boolean isFirstPageLoading = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkConnection()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        executor = MySingleTone.getThreadPool();
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

    public void startSecondActivity(String Symbol){
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        intent.putExtra("Symbol", Symbol);
        startActivity(intent);
    }

    public void startLoadingFirstPage(){
        this.isFirstPageLoading = true;
    }

    public void endLoadingFirstPage(){
        this.isFirstPageLoading = false;
    }

    public Boolean getIsFirstPageLoading(){
        return this.isFirstPageLoading;
    }
    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}