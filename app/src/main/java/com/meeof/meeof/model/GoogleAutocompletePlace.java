package com.meeof.meeof.model;

import com.google.api.client.util.Types;

import java.util.List;

/**
 * Created by ransikadesilva on 12/13/17.
 */

public class GoogleAutocompletePlace {

    private String description;
    private String id;
    private String place_id;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
