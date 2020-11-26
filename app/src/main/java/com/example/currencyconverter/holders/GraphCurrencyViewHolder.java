package com.example.currencyconverter.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;

public class GraphCurrencyViewHolder extends RecyclerView.ViewHolder{
    private TextView tvCode;
    private TextView tvName;

    public GraphCurrencyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.tvCode = itemView.findViewById(R.id.tvGraphCode);
        this.tvName = itemView.findViewById(R.id.tvGraphName);
    }

    public void setTvCode(String tvCode) {
        this.tvCode.setText(tvCode);;
    }

    public void setTvName(String tvName) {
        this.tvName.setText(tvName);
    }

}
