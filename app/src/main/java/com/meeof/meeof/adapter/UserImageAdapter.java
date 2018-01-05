package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meeof.meeof.R;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/1/2017.
 */

public class UserImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<String> imageList;
    private int MyId;

    public UserImageAdapter(Context context, ArrayList<String> imageList) {
        super();
        mContext = context;
        this.imageList = imageList;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, Context.MODE_PRIVATE);
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String imageUrl = imageList.get(position) == null || imageList.get(position).trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/eImages/" + imageList.get(position);
//        Glide.with(mContext).load(imageUrl).placeholder(R.drawable.siloet_img).error(R.drawable.siloet_img).into(((ViewHolder) holder).ImgUserImages);
        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.siloet_img)
                .resize(100,100)
                .error(R.drawable.siloet_img)
                .into(((ViewHolder) holder).ImgUserImages);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ImgUserImages;

        public ViewHolder(View myView) {
            super(myView);
            ImgUserImages = myView.findViewById(R.id.ImgUserImages);
        }
    }


}