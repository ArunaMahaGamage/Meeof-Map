package com.meeof.meeof.model.interests;

import java.io.Serializable;

/**
 * Created by Anuja Ranwalage on 10/8/2017.
 */

public class InterestsBaseItem implements Serializable{


    private int parentId;
    private String name;
    private int catid;
    private boolean has_sub;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public boolean getHas_sub() {
        return has_sub;
    }

    public void setHas_sub(boolean has_sub) {
        this.has_sub = has_sub;
    }

    @Override
    public String toString() {
        return "InterestsBaseItem{" +
                "parentId=" + parentId +
                ", name='" + name + '\'' +
                ", catid=" + catid +
                ", has_sub=" + has_sub +
                '}';
    }
}
