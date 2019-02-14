package com.laurikosonen.nappipeli;

import com.google.gson.annotations.SerializedName;

public class Counter {

    @SerializedName("value")
    int value;

    public Counter(int value) {
        this.value = value;
    }
}
