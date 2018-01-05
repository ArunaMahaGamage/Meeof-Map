package com.meeof.meeof.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by ransikadesilva on 1/5/18.
 */

public class ClusterModel implements ClusterItem {
    public int type;
    public GetNearMeEventInsideModel getNearMeEventInsideModel;
    public PromoNearMeInsideModel promoNearMeInsideModel;
    public NearMeUpdateInsideModel nearMeUpdateInsideModel;

    public ClusterModel(int type, GetNearMeEventInsideModel getNearMeEventInsideModel) {
        this.type = type;
        this.getNearMeEventInsideModel = getNearMeEventInsideModel;
    }

    public ClusterModel(int type, PromoNearMeInsideModel promoNearMeInsideModel) {
        this.type = type;
        this.promoNearMeInsideModel = promoNearMeInsideModel;
    }

    public ClusterModel(int type, NearMeUpdateInsideModel nearMeUpdateInsideModel) {
        this.type = type;
        this.nearMeUpdateInsideModel = nearMeUpdateInsideModel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GetNearMeEventInsideModel getGetNearMeEventInsideModel() {
        return getNearMeEventInsideModel;
    }

    public void setGetNearMeEventInsideModel(GetNearMeEventInsideModel getNearMeEventInsideModel) {
        this.getNearMeEventInsideModel = getNearMeEventInsideModel;
    }

    public PromoNearMeInsideModel getPromoNearMeInsideModel() {
        return promoNearMeInsideModel;
    }

    public void setPromoNearMeInsideModel(PromoNearMeInsideModel promoNearMeInsideModel) {
        this.promoNearMeInsideModel = promoNearMeInsideModel;
    }

    public NearMeUpdateInsideModel getNearMeUpdateInsideModel() {
        return nearMeUpdateInsideModel;
    }

    public void setNearMeUpdateInsideModel(NearMeUpdateInsideModel nearMeUpdateInsideModel) {
        this.nearMeUpdateInsideModel = nearMeUpdateInsideModel;
    }

    @Override
    public LatLng getPosition() {
        if(type==1){
            return new LatLng(getNearMeEventInsideModel.getLatitude(),getNearMeEventInsideModel.getLongitude());
        }else if(type==2){
            return new LatLng(promoNearMeInsideModel.getLatitude(),promoNearMeInsideModel.getLongitude());
        }else if(type==3){
            return new LatLng(nearMeUpdateInsideModel.getLatitude(),nearMeUpdateInsideModel.getLongitude());
        }else{
            return null;
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
