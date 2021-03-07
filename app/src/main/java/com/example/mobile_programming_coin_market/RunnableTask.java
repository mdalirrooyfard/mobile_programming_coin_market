package com.example.mobile_programming_coin_market;


import android.os.Handler;

import java.util.ArrayList;

public class RunnableTask<R> implements Runnable{
    private final Handler handler;
    private final CustomCallable<ArrayList> callable;

    public RunnableTask(Handler handler, CustomCallable<ArrayList> callable) {
        this.handler = handler;
        this.callable = callable;
    }

    @Override
    public void run() {
        try {
            final ArrayList result = callable.call();
            handler.post(new RunnableTaskForHandler(callable, result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

