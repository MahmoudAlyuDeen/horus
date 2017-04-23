package com.afterapps.horus.beans;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/*
 * Created by mahmoudalyudeen on 4/22/17.
 */

public class HistoryEntry extends RealmObject {

    @PrimaryKey
    private long timestamp;
    @Index
    private String symbol;
    float close;

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
