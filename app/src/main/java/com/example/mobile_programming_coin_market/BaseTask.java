package com.example.mobile_programming_coin_market;

import java.util.ArrayList;

public abstract class BaseTask<ArrayList> implements CustomCallable<ArrayList> {
    @Override
    public void setUiForLoading() {

    }

    @Override
    public void setDataAfterLoading(ArrayList result) {

    }

    @Override
    public ArrayList call() throws Exception {
        return null;
    }
}