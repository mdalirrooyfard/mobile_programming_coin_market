package com.example.mobile_programming_coin_market;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CoinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ImageView coin_icon;
    public TextView coin_name, coin_symbol, coin_price, percentageChangeOneHourText, percentageChangeOneDayText, percentageChangeOneWeekText;
    private ItemClickListener mClickListener;

    public CoinViewHolder(@NonNull View itemView) {
        super(itemView);
        coin_icon = (ImageView)itemView.findViewById(R.id.coin_icon);
        coin_symbol = (TextView)itemView.findViewById(R.id.coin_symbol);
        coin_price = (TextView)itemView.findViewById(R.id.priceUsdText);
        coin_name = (TextView)itemView.findViewById(R.id.coin_name);
        percentageChangeOneHourText = (TextView)itemView.findViewById(R.id.percentageChangeOneHourText);
        percentageChangeOneDayText = (TextView)itemView.findViewById(R.id.percentageChangeOneDayText);
        percentageChangeOneWeekText = (TextView)itemView.findViewById(R.id.percentageChangeOneWeekText);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
