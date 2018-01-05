package com.meeof.meeof.model.rsvp_response;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/31/17.
 */

public class RSVPResponse implements Serializable {

    private String mStatus;
    private String mRequest;

    public RSVPResponse() {
    }

    public RSVPResponse(String mStatus, String mRequest) {
        this.mStatus = mStatus;
        this.mRequest = mRequest;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getRequest() {
        return mRequest;
    }

    public void setRequest(String mRequest) {
        this.mRequest = mRequest;
    }
}
