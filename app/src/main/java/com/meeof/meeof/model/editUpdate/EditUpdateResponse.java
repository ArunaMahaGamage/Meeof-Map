package com.meeof.meeof.model.editUpdate;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 12/21/17.
 */

public class EditUpdateResponse implements Serializable {
    private String status;
    private String message;

    public EditUpdateResponse(){}
    public EditUpdateResponse(String status, String message) {
        this.status = status;
        this.message = message;
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
