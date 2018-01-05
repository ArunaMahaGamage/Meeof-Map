package com.meeof.meeof.model;

import java.util.List;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class GoogleAutocompleteResponse {

    private String status;
    private List<GoogleAutocompletePlace> predictions;

    public GoogleAutocompleteResponse(String status, List<GoogleAutocompletePlace> predictions) {
        this.status = status;
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GoogleAutocompletePlace> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<GoogleAutocompletePlace> predictions) {
        this.predictions = predictions;
    }
}
