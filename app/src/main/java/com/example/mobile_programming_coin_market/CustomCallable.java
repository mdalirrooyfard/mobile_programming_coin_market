package com.example.mobile_programming_coin_market;

import java.util.concurrent.Callable;

public interface CustomCallable<ArrayList> extends Callable<ArrayList> {
    void setDataAfterLoading(ArrayList result);
    void setUiForLoading();
}