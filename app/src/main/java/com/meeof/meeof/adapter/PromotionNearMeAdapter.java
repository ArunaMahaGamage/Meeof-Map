package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.PromoNearMeInsideModel;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/21/2017.
 */

public class PromotionNearMeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<PromoNearMeInsideModel> promotionsInsideDataModels;
    private int MyId;

    public PromotionNearMeAdapter(Context context, ArrayList<PromoNearMeInsideModel> promotionsInsideDataModels) {
        super();
        mContext = context;
        this.promotionsInsideDataModels=promotionsInsideDataModels;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, Context.MODE_PRIVATE);
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.promotion_near_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ViewHolder holder1= (ViewHolder) holder;
        PromoNearMeInsideModel item=promotionsInsideDataModels.get(position);
        String imgeUrl1 = item.getPromo_poster() == null || item.getPromo_poster().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/eImages/" + item.getPromo_poster();
//        Glide.with(mContext).load(imgeUrl1).error(R.drawable.spalsh_screen_logo).into(holder1.ImgPromotionImage);

        Picasso.with(mContext.getApplicationContext())
                .load(imgeUrl1)
                .placeholder(R.drawable.spalsh_screen_logo)
                .resize(70,70)
                .error(R.drawable.spalsh_screen_logo)
                .into(holder1.ImgPromotionImage);
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

        ImageView ImgPromotionImage;
        TextView TxtHeading,TxtDescription;
        public ViewHolder(View myView) {
            super(myView);
            ImgPromotionImage=(ImageView) myView.findViewById(R.id.ImgPromotionImage);
            TxtHeading=(TextView)myView.findViewById(R.id.TxtPromotionHeading);
            TxtDescription=(TextView)myView.findViewById(R.id.TxtPromotionDescription);
        }
    }




}



