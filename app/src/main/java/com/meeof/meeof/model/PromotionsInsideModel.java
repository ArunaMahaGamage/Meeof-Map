package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/18/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class PromotionsInsideModel
{
    private String matrix;
    private int myID;
    private ArrayList<PromotionsInsideDataModel> promotions;

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public ArrayList<PromotionsInsideDataModel> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<PromotionsInsideDataModel> promotions) {
        this.promotions = promotions;
    }
}
