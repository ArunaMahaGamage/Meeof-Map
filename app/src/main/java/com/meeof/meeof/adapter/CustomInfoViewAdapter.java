package com.meeof.meeof.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.meeof.meeof.R;
import com.meeof.meeof.model.ClusterModel;

/**
 * Created by ransikadesilva on 1/5/18.
 */

public class CustomInfoViewAdapter implements GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemClickListener<ClusterModel> {
    private static final String TAG = "CustomInfoViewAdapter";
    private final LayoutInflater mInflater;

    public CustomInfoViewAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override public View getInfoWindow(Marker marker) {
        Log.d(TAG,"CustomInfoViewAdapter getInfoWindow");
        final View popup = mInflater.inflate(R.layout.map_bubble, null);
        popup.setBackgroundColor(Color.WHITE);
        //((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

        return popup;
        //return null;
    }



    @Override public View getInfoContents(Marker marker) {
        Log.d(TAG,"CustomInfoViewAdapter getInfoContents");
        final View popup = mInflater.inflate(R.layout.map_bubble, null);
        popup.setBackgroundColor(Color.WHITE);

        //((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

        return popup;
    }


    @Override
    public boolean onClusterItemClick(ClusterModel clusterModel) {
        Log.d(TAG,"CustomInfoViewAdapter onClusterItemClick");
        return false;
    }
}