package com.example.anshul.jsonparsing;

import android.widget.ImageView;

/**
 * Created by Anshul on 05/12/15.
 */
public class RestaurantGetterSetter {
    String brandName;
    String[] name;
    String url;
    double distance;
    int offers;
    String Neighbouhood;

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrandName() {
        return brandName;
    }

    public String[] getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getOffers() {
        return offers;
    }

    public void setOffers(int offers) {
        this.offers = offers;
    }

    public String getNeighbouhood() {
        return Neighbouhood;
    }

    public void setNeighbouhood(String neighbouhood) {
        Neighbouhood = neighbouhood;
    }
}
