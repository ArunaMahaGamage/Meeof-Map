package com.meeof.meeof.model.tag;

import com.meeof.meeof.model.friends_all_dto.Friends;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by ransikadesilva on 10/20/17.
 */

public class CustomTagFriend {

    Friends friend;
    int tagId;
    int friendId;

    public CustomTagFriend(Friends friend, int tagId) {
        this.friend = friend;
        this.tagId = tagId;
        this.friendId = friend.getId();
    }

    public Friends getFriend() {
        return friend;
    }

    public void setFriend(Friends friend) {
        this.friend = friend;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CustomTagFriend)) {
            return false;
        }

        CustomTagFriend customTagFriend = (CustomTagFriend) o;

        return new EqualsBuilder()
                .append(friendId, customTagFriend.getFriend().getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(friendId)
                .toHashCode();
    }
}
