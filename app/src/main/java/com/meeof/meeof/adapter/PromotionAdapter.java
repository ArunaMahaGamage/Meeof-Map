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
import com.meeof.meeof.model.PromotionsInsideDataModel;
import com.meeof.meeof.util.Constant;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/18/2017.
 */

public class PromotionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<PromotionsInsideDataModel> promotionsInsideDataModels;
    private int MyId;

    public PromotionAdapter(Context context, ArrayList<PromotionsInsideDataModel> promotionsInsideDataModels) {
        super();
        mContext = context;
        this.promotionsInsideDataModels=promotionsInsideDataModels;
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
        Glide.with(mContext).load(promotionsInsideDataModels.get(position).getPromo_poster()).error(R.drawable.spalsh_screen_logo).into(holder1.ImgAffliationImage);
        holder1.TxtHeading.setText(promotionsInsideDataModels.get(position).getPromo_name());
        holder1.TxtDescription.setText(promotionsInsideDataModels.get(position).getPromo_desc());

    }

    @Override
    public int getItemCount() {
        return promotionsInsideDataModels.size();
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


