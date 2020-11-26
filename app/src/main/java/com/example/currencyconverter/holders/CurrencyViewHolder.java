package com.example.currencyconverter.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.listeners.OnItemListener;

public class CurrencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView tvCode;
    private TextView tvName;
    private TextView tvRate;
    private TextView tvDots;

    OnItemListener onItemListener;

    public CurrencyViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);
        this.tvCode = itemView.findViewById(R.id.tvCode);
        this.tvName = itemView.findViewById(R.id.tvName);
        this.tvRate = itemView.findViewById(R.id.tvRate);
        this.tvDots = itemView.findViewById(R.id.tvDots);
        this.onItemListener = onItemListener;
        tvDots.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void setTvCode(String tvCode) {
        this.tvCode.setText(tvCode);;
    }

    public void setTvName(String tvName) {
        this.tvName.setText(tvName);
    }

    public void setTvRate(String tvRate) {
        this.tvRate.setText(tvRate);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(v, getAdapterPosition());
    }
}
