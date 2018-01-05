package com.meeof.meeof.model.ignored_members_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 12/1/17.
 */

public class IgnoredMembersResponse {

    private String status;
    private List<Ignored> ignored;
    private List<Pending_requests> pending_requests;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Ignored> getIgnored() {
        return ignored;
    }

    public void setIgnored(List<Ignored> ignored) {
        this.ignored = ignored;
    }

    public List<Pending_requests> getPending_requests() {
        return pending_requests;
    }

    public void setPending_requests(List<Pending_requests> pending_requests) {
        this.pending_requests = pending_requests;
    }
}
