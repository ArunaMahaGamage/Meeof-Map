package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.activity.PlacesActivity;
import com.meeof.meeof.model.GoogleAutocompletePlace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransika on 7/11/2017.
 */
public class GoogleAutocompleteSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<GoogleAutocompletePlace> dataList;
    private static final String TAG = GoogleAutocompleteSearchAdapter.class.getSimpleName();
    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(int position, GoogleAutocompletePlace item);
    }

    public GoogleAutocompleteSearchAdapter(Context context, List<GoogleAutocompletePlace> data, OnItemClicked onClick) {
        this.mContext = context;
        this.dataList = data;
        this.onClick = onClick;
    }

    public void clearList() {
        if(dataList != null){
            this.dataList.clear();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView address;

        public MyViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_placesearch, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {

            GoogleAutocompletePlace item = dataList.get(position);

            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.address.setText(item.getDescription());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClick(position, dataList.get(position));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {

        return (null != dataList ? dataList.size() : dataList.size());
    }

    public List<GoogleAutocompletePlace> dataList() {
        return dataList;
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public List<GoogleAutocompletePlace> getDataList() {
        return dataList;
    }

    public void setDataList(List<GoogleAutocompletePlace> dataList) {
        this.dataList = dataList;
    }
}

