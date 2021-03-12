package com.example.mobile_programming_coin_market;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    boolean isLoading;
    MainActivity activity;
    List<CoinModel> items;
    int visibleThreshold = 10, lastVisibleItem, totalItemCount;

    public CoinAdapter(RecyclerView recyclerView, MainActivity activity, ArrayList<CoinModel> items) {
        this.activity = activity;
        this.items = items;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            }
        });
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.coin_layout, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CoinModel item = items.get(position);
        CoinViewHolder item_holder = (CoinViewHolder)holder;
        item_holder.setClickListener(new CoinViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                activity.startSecondActivity(item.getSymbol());
            }
        });
        item_holder.coin_name.setText(item.getName());
        item_holder.coin_symbol.setText(item.getSymbol());
        Formatter formatter = new Formatter();
        item_holder.coin_price.setText((formatter.format("%.2f", (Double)item.getPrice()).toString()));
        item_holder.percentageChangeOneHourText.setText((int) item.getPercent_change_1h()+"%");
        item_holder.percentageChangeOneDayText.setText((int) item.getPercent_change_24h()+"%");
        item_holder.percentageChangeOneWeekText.setText((int) item.getPercent_change_7d()+"%");

        item.getIcon().into(item_holder.coin_icon);

        item_holder.percentageChangeOneHourText.setTextColor(item.getPercent_change_1h()<0?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        item_holder.percentageChangeOneDayText.setTextColor(item.getPercent_change_24h()<0?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        item_holder.percentageChangeOneWeekText.setTextColor(item.getPercent_change_7d()<0?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoading(){
        isLoading = true;
    }

    public void updateDate(List<CoinModel> coinModels){
        this.items = coinModels;
        notifyDataSetChanged();
    }
}
