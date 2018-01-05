package com.meeof.meeof.interfaces;

import com.meeof.meeof.model.InboxDataModel;

/**
 * Created by Dharmesh on 11/24/2017.
 */

public interface SetOnInboxItemClicked
{
    public void OnInboxClicked(InboxDataModel inboxDataModel);

    public void OnInboxDeleteClicked(int GroupId);
}
