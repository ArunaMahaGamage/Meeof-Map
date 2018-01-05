package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dharmesh on 11/23/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConvertationDataModel implements Serializable {
    private String group_id;
    private String count_messages;
    private String my_id;
    private String take;
    private List<MessagesModel> data;


    public ConvertationDataModel() {
    }

    public ConvertationDataModel(String group_id, List<MessagesModel> data) {
        this.group_id = group_id;
        this.data = data;

    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getCount_messages() {
        return count_messages;
    }

    public void setCount_messages(String count_messages) {
        this.count_messages = count_messages;
    }

    public String getMy_id() {
        return my_id;
    }

    public void setMy_id(String my_id) {
        this.my_id = my_id;
    }

    public String getTake() {
        return take;
    }

    public void setTake(String take) {
        this.take = take;
    }

    public List<MessagesModel> getData() {
        return data;
    }

    public void setData(List<MessagesModel> data) {
        this.data = data;
    }
}
