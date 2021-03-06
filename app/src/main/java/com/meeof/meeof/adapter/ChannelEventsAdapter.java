package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meeof.meeof.R;
import com.meeof.meeof.model.ChannelEventArrayModel;
import com.meeof.meeof.util.Constant;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/18/2017.
 */

public class ChannelEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<ChannelEventArrayModel> intrestList;
    private int MyId;

    public ChannelEventsAdapter(Context context, ArrayList<ChannelEventArrayModel> intrestList) {
        super();
        mContext = context;
        this.intrestList=intrestList;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, Context.MODE_PRIVATE);
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.affliation_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ViewHolder holder1= (ViewHolder) holder;
        ChannelEventArrayModel item=intrestList.get(position);
        String imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/eImages/" + item.getProfilephoto();
        Glide.with(mContext).load(imgeUrl1).error(R.drawable.spalsh_screen_logo).into(holder1.ImgAffliationImage);
        holder1.TxtHeading.setText(intrestList.get(position).getTitle());
        holder1.TxtDescription.setText(intrestList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return intrestList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ImgAffliationImage;
        TextView TxtHeading,TxtDescription;
        public ViewHolder(View myView) {
            super(myView);
            ImgAffliationImage=(ImageView) myView.findViewById(R.id.ImgAffiliation);
            TxtHeading=(TextView)myView.findViewById(R.id.AffiliationTitle);
            TxtDescription=(TextView)myView.findViewById(R.id.AffiliationDescription);
        }
    }




}

