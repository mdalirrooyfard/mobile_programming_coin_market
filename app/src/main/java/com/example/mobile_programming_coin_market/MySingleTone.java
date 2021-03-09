package com.example.mobile_programming_coin_market;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MySingleTone {

    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
    public static ThreadPoolExecutor getThreadPool(){
        return executor;
    }
}
