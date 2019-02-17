package com.laurikosonen.nappipeli;

import com.google.gson.annotations.SerializedName;

public class Winner {

    @SerializedName("id")
    int id;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("prizetier")
    int prizeTier;

    public Winner(int id, String nickname, int prizeTier) {
        this.id = id;
        this.nickname = nickname;
        this.prizeTier = prizeTier;
    }

    public Winner(String nickname, int prizeTier) {
        this.nickname = nickname;
        this.prizeTier = prizeTier;
    }
}
