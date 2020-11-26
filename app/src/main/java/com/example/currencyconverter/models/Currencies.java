package com.example.currencyconverter.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Currencies implements Parcelable {
    private String code;
    private String name;
    private double rate;

    public Currencies(String code, String name, double rate) {
        this.code = code;
        this.name = name;
        this.rate = rate;
    }

    protected Currencies(Parcel in) {
        code = in.readString();
        name = in.readString();
        rate = in.readDouble();
    }

    public static final Creator<Currencies> CREATOR = new Creator<Currencies>() {
        @Override
        public Currencies createFromParcel(Parcel in) {
            return new Currencies(in);
        }

        @Override
        public Currencies[] newArray(int size) {
            return new Currencies[size];
        }
    };

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeDouble(rate);
    }
}
