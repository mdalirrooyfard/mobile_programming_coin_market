package com.example.mobile_programming_coin_market;

class RunnableTaskForHandler<ArrayList> implements Runnable{

    private CustomCallable<ArrayList> callable;
    private ArrayList result;

    public RunnableTaskForHandler(CustomCallable<ArrayList> callable, ArrayList result) {
        this.callable = callable;
        this.result = result;
    }

    @Override
    public void run() {
        callable.setDataAfterLoading(result);
    }
}
