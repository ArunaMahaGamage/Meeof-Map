package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 11/25/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteInboxModel
{
    private String status;
    private String message;

    public DeleteInboxModel()
    {}
    public DeleteInboxModel(String status, String message)
    {
        this.status=status;
        this.message=message;
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
}
