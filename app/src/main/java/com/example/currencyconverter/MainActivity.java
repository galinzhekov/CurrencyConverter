package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.currencyconverter.adapters.CurrencyAdapter;
import com.example.currencyconverter.listeners.OnItemListener;
import com.example.currencyconverter.models.Currencies;
import com.example.currencyconverter.util.VerticalSpacingItemDecorator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnItemListener {
    private RecyclerView mRecyclerView;
    private CurrencyAdapter mCurrencyAdapter;
    private ArrayList<Currencies> mCurrencyList;
    private RequestQueue mRequestQueue;
    private static final String TAG = "off";
    private Toolbar mToolbar;
    private TextView tvDefaultCode, tvDefaultName, etSum;
    private String mCountryCode;
    private ArrayList<Double> currencies;
    private ArrayList<Currencies> mDefaultCurrenciesList;
    private Currencies mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerViewCurrency);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        mRecyclerView.addItemDecoration(itemDecorator);
        mCurrencyList = new ArrayList<>();
        currencies = new ArrayList<>();
        mDefaultCurrenciesList = new ArrayList<>();

        tvDefaultCode = findViewById(R.id.tvDefaultCode);
        tvDefaultName = findViewById(R.id.tvDefaultName);
        etSum = findViewById(R.id.editTextNumberSigned);

        if (getIntent().hasExtra("single_currency")) {
            mCurrency = getIntent().getParcelableExtra("single_currency");
            tvDefaultCode.setText(mCurrency.getCode());
            tvDefaultName.setText(mCurrency.getName());
            mCountryCode = mCurrency.getCode();
        }

        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON(mCountryCode);

        etSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCurrencySum(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_rate);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (!(bottomNavigationView.getSelectedItemId() == item.getItemId())) {
                switch (item.getItemId()) {
                    case R.id.page_rate:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.page_chart:
                        Currencies oCurrency = new Currencies(String.valueOf(tvDefaultCode.getText()), String.valueOf(tvDefaultName.getText()), 1.00);
                        Intent intent = new Intent(this, GraphActivity.class);
                        intent.putExtra("single_currency", oCurrency);
                        intent.putExtra("currencies_rate", currencies);
                        intent.putExtra("currencies_list", mCurrencyList);
                        intent.putExtra("default_currencies_list", mDefaultCurrenciesList);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
            }
            return false;
        });
    }

    void updateCurrencySum(CharSequence s) {
        if (!mCurrencyList.isEmpty()) {
            double input = 0;
            if(! TextUtils.isEmpty(s)) {
                input = Double.parseDouble(s.toString());
            }
            for ( int i = 0; i < mCurrencyList.size(); i++) {
                Currencies oCurrency = mCurrencyList.get(i);
                double rate = currencies.get(i);
                double sum = rate * input;
                oCurrency.setRate(sum);
                mCurrencyAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mCurrencyList.isEmpty()) {
                    return false;
                }
                mCurrencyAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void parseJSON(String countryCode) {
        String url;
        if (TextUtils.isEmpty(countryCode)) {
            url = "https://floatrates.com/daily/usd.json";
        } else {
            url = "https://floatrates.com/daily/" + countryCode.toLowerCase() + ".json";
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response));
                        Iterator<?> keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (jsonObject.get(key) instanceof JSONObject) {
                                JSONObject jsonObjectCurrency = new JSONObject(jsonObject.get(key).toString());
                                String code = jsonObjectCurrency.getString("code");
                                String name = jsonObjectCurrency.getString("name");
                                double rate = jsonObjectCurrency.getDouble("rate");
                                currencies.add(rate);
                                mDefaultCurrenciesList.add(new Currencies(code, name, rate));
                                mCurrencyList.add(new Currencies(code, name, rate));
                            }
                        }
                        mCurrencyAdapter = new CurrencyAdapter(mCurrencyList, this);
                        mRecyclerView.setAdapter(mCurrencyAdapter);
                        updateCurrencySum(etSum.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueue.add(request);

    }

    @Override
    public void onItemClick(View v, int iPosition) {
        if (v.getId() == R.id.tvDots) {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(this, v);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.baseCurrency:
                        Currencies oCurrency = mCurrencyList.get(iPosition);
                        mCountryCode = oCurrency.getCode();
                        mCurrencyList.clear();
                        currencies.clear();
                        mRecyclerView.setAdapter(null);
                        parseJSON(mCountryCode);
                        tvDefaultCode.setText(oCurrency.getCode());
                        tvDefaultName.setText(oCurrency.getName());
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();
        }
        v.setEnabled(false);
        Handler handlerTimer = new Handler();
        handlerTimer.postDelayed(new Runnable() {
            public void run() {
                v.setEnabled(true);
            }
        }, 1000);
    }
}