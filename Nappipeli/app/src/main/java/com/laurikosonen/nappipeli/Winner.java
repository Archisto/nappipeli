package com.laurikosonen.nappipeli;

import com.google.gson.annotations.SerializedName;

public class Winner {

    @SerializedName("id")
    int id;

    @SerializedName("username")
    String username;

    @SerializedName("prize")
    int prize;

    public Winner(int id, String username, int prize) {
        this.id = id;
        this.username = username;
    }

    public Winner(String username, int prize) {
        this.username = username;
        this.prize = prize;
    }
}
