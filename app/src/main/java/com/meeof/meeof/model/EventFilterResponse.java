package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Anuja Ranwalage on 10/18/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventFilterResponse {

    private String status;
    private String message;
    private String error;

    public EventFilterResponse(String status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    @Override
    public String toString() {
        return "EventFilterResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    public EventFilterResponse() {
        super();
    }
}
