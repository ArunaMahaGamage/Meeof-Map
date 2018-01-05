package com.meeof.meeof.model.tag;

import com.meeof.meeof.model.friends_all_dto.Friends;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Anuja Ranwalage on 10/22/2017.
 */

public class CustomTagEmail {
    String email;
    int tagId;

    public CustomTagEmail(String email, int tagId) {
        this.email = email;
        this.tagId = tagId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (!(o instanceof CustomTagEmail)) {
            return false;
        }

        CustomTagEmail customTagEmail = (CustomTagEmail) o;

        return new EqualsBuilder()
                .append(email, customTagEmail.getEmail())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .toHashCode();
    }
}
