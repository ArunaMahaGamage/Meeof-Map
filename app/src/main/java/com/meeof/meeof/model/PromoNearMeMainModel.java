package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/21/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class PromoNearMeMainModel
{
    private boolean success;
    private int count;
    private ArrayList<PromoNearMeInsideModel> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<PromoNearMeInsideModel> getData() {
        return data;
    }

    public void setData(ArrayList<PromoNearMeInsideModel> data) {
        this.data = data;
    }
}
