package com.afterapps.horus.beans;

/*
 * Created by mahmoudalyudeen on 4/22/17.
 */

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Stock extends RealmObject {

    @PrimaryKey
    private String symbol;
    private boolean handled;
    private float price;
    private float changeAbs;
    private float changePer;

    public boolean isHandled() {
        return handled;
    }

    public void setHandled() {
        this.handled = true;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getChangeAbs() {
        return changeAbs;
    }

    public void setChangeAbs(float changeAbs) {
        this.changeAbs = changeAbs;
    }

    public float getChangePer() {
        return changePer;
    }

    public void setChangePer(float changePer) {
        this.changePer = changePer;
    }
}
