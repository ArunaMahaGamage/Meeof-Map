package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/18/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelEventInsideModel
{
    private ArrayList<ChannelEventArrayModel> array_events;


    public ArrayList<ChannelEventArrayModel> getArray_events() {
        return array_events;
    }

    public void setArray_events(ArrayList<ChannelEventArrayModel> array_events) {
        this.array_events = array_events;
    }
}
