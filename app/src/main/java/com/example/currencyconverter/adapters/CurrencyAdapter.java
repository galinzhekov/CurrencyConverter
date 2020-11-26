package com.example.currencyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.holders.CurrencyViewHolder;
import com.example.currencyconverter.listeners.OnItemListener;
import com.example.currencyconverter.models.Currencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Currencies> mCurrencies;
    List<Currencies> currenciesAll;

    Filter filter = new Filter() {
        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Currencies> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(currenciesAll);
            } else {
                for (Currencies oCurrency : currenciesAll) {
                    if (oCurrency.getName().toLowerCase().contains(constraint.toString().toLowerCase()) || oCurrency.getCode().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(oCurrency);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //run on Ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCurrencies.clear();
            mCurrencies.addAll((Collection<? extends Currencies>) results.values);
            notifyDataSetChanged();
        }
    };

    private OnItemListener mOnItemListener;

    public CurrencyAdapter(ArrayList<Currencies> currencies, OnItemListener mOnItemListener) {
        this.mCurrencies = currencies;
        this.mOnItemListener = mOnItemListener;
        currenciesAll = new ArrayList<>(mCurrencies);
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.currency_layout, parent, false);
        return new CurrencyViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        Currencies oCurrencies = mCurrencies.get(position);

        holder.setTvCode(oCurrencies.getCode());
        holder.setTvName(oCurrencies.getName());
        holder.setTvRate(String.valueOf(oCurrencies.getRate()));
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
