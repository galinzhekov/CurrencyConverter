package com.example.currencyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Currencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AutoCompleteCurrenciesAdapter extends ArrayAdapter<Currencies> {
    private List<Currencies> currenciesArrayListFull;

    public AutoCompleteCurrenciesAdapter(@NonNull Context context, @NonNull List<Currencies> currenciesArrayList) {
        super(context, 0, currenciesArrayList);
        currenciesArrayListFull = new ArrayList<>(currenciesArrayList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return currencyFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.graph_currency_layout, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvGraphCode);
        TextView textView1 = convertView.findViewById(R.id.tvGraphName);

        Currencies currencies = getItem(position);

        if(currencies!=null){
            textView.setText(currencies.getCode());
            textView1.setText(currencies.getName());
        }
        return convertView;
    }

    private Filter currencyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Currencies> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(currenciesArrayListFull);
            } else {
                for (Currencies oCurrency : currenciesArrayListFull) {
                    if (oCurrency.getName().toLowerCase().contains(constraint.toString().toLowerCase()) || oCurrency.getCode().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(oCurrency);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((Collection<? extends Currencies>) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Currencies) resultValue).getCode();
        }
    };
}
