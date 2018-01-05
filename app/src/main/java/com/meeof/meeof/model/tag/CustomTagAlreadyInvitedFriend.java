package com.meeof.meeof.model.tag;

import com.meeof.meeof.model.edit_event_dto.AttendeeList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Anuja Ranwalage on 10/24/2017.
 */

public class CustomTagAlreadyInvitedFriend {
    AttendeeList friend;
    int tagId;
    int friendId;

    public CustomTagAlreadyInvitedFriend(AttendeeList friend, int tagId) {
        this.friend = friend;
        this.tagId = tagId;
    }

    public AttendeeList getFriend() {
        return friend;
    }

    public void setFriend(AttendeeList friend) {
        this.friend = friend;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CustomTagAlreadyInvitedFriend)) {
            return false;
        }

        CustomTagAlreadyInvitedFriend customTagFriend = (CustomTagAlreadyInvitedFriend) o;

        return new EqualsBuilder()
                .append(friendId, customTagFriend.getFriend().getUser_id())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(friendId)
                .toHashCode();
    }
}
