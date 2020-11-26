package com.example.currencyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.holders.GraphCurrencyViewHolder;
import com.example.currencyconverter.models.Currencies;

import java.util.ArrayList;

public class GraphCurrencyAdapter extends RecyclerView.Adapter<GraphCurrencyViewHolder> {
    private Context context;
    private ArrayList<Currencies> mCurrencies;

    public GraphCurrencyAdapter(ArrayList<Currencies> currencies) {
        this.mCurrencies = currencies;
    }

    @NonNull
    @Override
    public GraphCurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.graph_currency_layout, parent, false);
        return new GraphCurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GraphCurrencyViewHolder holder, int position) {
        Currencies oCurrencies = mCurrencies.get(position);

        holder.setTvCode(oCurrencies.getCode());
        holder.setTvName(oCurrencies.getName());
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }
}
