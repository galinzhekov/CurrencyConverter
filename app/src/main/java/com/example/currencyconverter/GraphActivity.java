package com.example.currencyconverter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.AutoCompleteCurrenciesAdapter;
import com.example.currencyconverter.adapters.GraphCurrencyAdapter;
import com.example.currencyconverter.models.Currencies;
import com.example.currencyconverter.util.VerticalSpacingItemDecorator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GraphActivity extends AppCompatActivity {
    private LineChart mLineChart;
    private RecyclerView mRecyclerView;
    private GraphCurrencyAdapter mCurrencyAdapter;
    private static final String TAG = "off";

    private int colorClassArray[] = new int[]{Color.BLUE, Color.RED, Color.MAGENTA};
    private ArrayList<Currencies> mCurrencyList;
    private ArrayList<Currencies> mCurrentCurrencyList;
    private ArrayList<Currencies> mDefaultCurrentCurrencyList;
    private AutoCompleteCurrenciesAdapter mAdapter;
    private List<Double> mCurrenciesRate;
    private List<Double> mCurrentCurrenciesRate;
    private List<LineDataSet> lineDataSets;
    private List<ILineDataSet> dataSets;
    private Legend legend;
    private List<LegendEntry> legendEntries;
    private Currencies mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mCurrentCurrencyList = new ArrayList<>();
        mCurrentCurrenciesRate = new ArrayList<>();

        if (getIntent().hasExtra("single_currency")) {
            mCurrency = getIntent().getParcelableExtra("single_currency");
        }

        if (getIntent().hasExtra("currencies_list")) {
            mCurrencyList = getIntent().getParcelableArrayListExtra("currencies_list");
        }

        if (getIntent().hasExtra("default_currencies_list")) {
            mDefaultCurrentCurrencyList = getIntent().getParcelableArrayListExtra("default_currencies_list");
        }

        if (getIntent().hasExtra("currencies_rate")) {
            mCurrenciesRate = (ArrayList<Double>) getIntent().getSerializableExtra("currencies_rate");
            Log.d(TAG, String.valueOf(mCurrenciesRate));
        }

        AutoCompleteTextView textView = findViewById(R.id.actvLineChart);
        mAdapter = new AutoCompleteCurrenciesAdapter(this, mCurrencyList);

        textView.setOnItemClickListener((parent, view, position, id) -> {
            addCurrencytoView(String.valueOf(textView.getText()));
            textView.setText("");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View v = getCurrentFocus();
            assert imm != null;
            assert v != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
        textView.setAdapter(mAdapter);

        mRecyclerView = findViewById(R.id.recyclerViewGraph);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        mRecyclerView.addItemDecoration(itemDecorator);
        mCurrencyAdapter = new GraphCurrencyAdapter(mCurrentCurrencyList);
        mRecyclerView.setAdapter(mCurrencyAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        initializeLineChart();
        setBottomNavigationMenu();
    }

    private void addCurrencytoView(String code) {
        boolean bIsCurrencyThere = false;
        if (mCurrentCurrencyList.size() < 3) {
            for (Currencies oCurrency : mCurrentCurrencyList) {
                if (oCurrency.getCode().equals(code)) {
                    bIsCurrencyThere = true;
                    break;
                }
            }
            if (!bIsCurrencyThere) {
                double rate = 0;
                for (int i = 0; i < mDefaultCurrentCurrencyList.size(); i++) {
                    Currencies oCurrency = mDefaultCurrentCurrencyList.get(i);
                    if (oCurrency.getCode().equals(code)) {
                        rate = oCurrency.getRate();
                        break;
                    }
                }
                for (int i = 0; i < mCurrencyList.size(); i++) {
                    Currencies oCurrency = mCurrencyList.get(i);
                    if (oCurrency.getCode().equals(code)) {
                        mCurrentCurrencyList.add(oCurrency);
                        mCurrentCurrenciesRate.add(rate);
                        Log.d(TAG, String.valueOf(rate));
                        addLineDataSet(rate, code);
                        mCurrencyAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    private void addLineDataSet(double rate, String code) {
        int position = 0;
        boolean bExist = false;
        LineDataSet lineDataSet = new LineDataSet(dataValues(rate), "Data Set");
        for(int i=0;i<colorClassArray.length;i++) {
            for (int j = 0;j<lineDataSets.size();j++){
                if(lineDataSets.get(j).getColor() == colorClassArray[i]) {
                    bExist = true;
                }
            }
            if(!bExist) {
                position = i;
                break;
            }
            bExist= false;
        }
        lineDataSets.add(lineDataSet);
        dataSets.add(lineDataSet);

        lineDataSet.setLineWidth(4);
        lineDataSet.setColor(colorClassArray[position]);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColor(Color.GRAY);
        lineDataSet.setCircleHoleColor(Color.GREEN);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setValueTextColor(Color.BLUE);
        lineDataSet.setValueFormatter(new YAxisText());

        LegendEntry entry = new LegendEntry();
        entry.formColor = colorClassArray[position];
        entry.label = code;
        legendEntries.add(entry);

        legend.setCustom(legendEntries);

        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    private ArrayList<Entry> dataValues(double rate) {
        double min = rate - rate / 10;
        double max = rate + rate / 10;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        ArrayList<Entry> dataValues = new ArrayList<>();

        for (int i = 0; i < 100; i += 10) {
            float random = (float) ThreadLocalRandom.current().nextDouble(min, max);
            float twoDigitsRandom = Float.parseFloat(decimalFormat.format(random));
            dataValues.add(new Entry(i, twoDigitsRandom));
        }

        return dataValues;
    }

    private class YAxisText extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            float twoDigitsRandom = Float.parseFloat(decimalFormat.format(value));
            return twoDigitsRandom + "$";
        }
    }

    private class XAxisText extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return (int) value + " days";
        }
    }

    private void initializeLineChart() {
        mLineChart = findViewById(R.id.lineCurrencyGraph);
        mLineChart.setNoDataText("No Data");
        mLineChart.setNoDataTextColor(Color.BLUE);
        mLineChart.setDrawGridBackground(true);
        mLineChart.setDrawBorders(true);
        mLineChart.setBorderColor(Color.BLUE);

        Description description = new Description();
        description.setText("Currencies");
        description.setTextColor(Color.BLUE);
        description.setTextSize(20);
        mLineChart.setDescription(description);

        XAxis xAxis = mLineChart.getXAxis();
        YAxis yAxisLeft = mLineChart.getAxisLeft();
        YAxis yAxisRight = mLineChart.getAxisRight();

        xAxis.setValueFormatter(new XAxisText());
        yAxisLeft.setValueFormatter(new YAxisText());
        yAxisRight.setValueFormatter(new YAxisText());

        legend = mLineChart.getLegend();

        legend.setEnabled(true);
        legend.setTextColor(Color.CYAN);
        legend.setTextSize(15);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setFormSize(20);
        legend.setXEntrySpace(30);
        legend.setFormToTextSpace(10);

        lineDataSets = new ArrayList<>();
        legendEntries = new ArrayList<>();
        dataSets = new ArrayList<>();
    }

    private void setBottomNavigationMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_chart);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (!(bottomNavigationView.getSelectedItemId() == item.getItemId())) {
                switch (item.getItemId()) {
                    case R.id.page_rate:
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("single_currency", mCurrency);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.page_chart:
                        startActivity(new Intent(getApplicationContext(), GraphActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
            }
            return false;
        });
    }

    private void deleteNote(Currencies oCurrency, int position){
        dataSets.remove(lineDataSets.get(position));
        lineDataSets.get(position).removeEntry(0);
        lineDataSets.remove(position);
        legendEntries.remove(position);
        legend.setCustom(legendEntries);
        if(dataSets.size() == 0) {
            mLineChart.clear();
        }
        mLineChart.invalidate();
        mCurrentCurrencyList.remove(oCurrency);
        mCurrencyAdapter.notifyDataSetChanged();
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteNote(mCurrentCurrencyList.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
        }
    };
}