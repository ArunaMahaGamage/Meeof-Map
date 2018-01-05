package com.meeof.meeof.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.edit_event_dto.Photos;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransikadesilva on 12/17/17.
 */

public class PhotoUpdateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PhotoUpdateAdapter.class.getSimpleName();
    private final Context mContext;
    private List<String> listItem;
    private PhotoUpdateAdapter.OnItemClicked onClick;
    private ProgressDialog progressDialog;

    boolean isImageFitToScreen;


    public interface OnItemClicked {
        void onItemClick(int position, boolean isAccept, String item);
    }

    public PhotoUpdateAdapter(Context context, List<String> listItems) {
        this.listItem = listItems;
        this.mContext = context;
//        this.filterList.addAll(this.listItem); //to show all items at begining
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {


        private final ImageView userImagesIv;
        private final ProgressBar progressBar;

        public MyViewHolderFriend(View view) {
            super(view);

            userImagesIv = (ImageView) view.findViewById(R.id.userImagesIv);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update_image, parent, false);

        return new PhotoUpdateAdapter.MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String item = listItem.get(position);

        final PhotoUpdateAdapter.MyViewHolderFriend myViewHolderPendingFriend = (PhotoUpdateAdapter.MyViewHolderFriend) holder;


        myViewHolderPendingFriend.progressBar.setVisibility(View.VISIBLE);



        String imageUrlEventImage = item == null || item.length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                Constant.UPDATE_IMAGES_BASE_URL + item;


        Picasso.with(mContext.getApplicationContext())
                .load(imageUrlEventImage)
                .centerCrop()
                .fit()
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderPendingFriend.userImagesIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        myViewHolderPendingFriend.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        myViewHolderPendingFriend.progressBar.setVisibility(View.GONE);
                    }
                });


//        myViewHolderPendingFriend.userImagesIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                onClick.onItemClick(position, true, item);
//            }
//        });





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

//        return (null != filterList ? filterList.size() : listItem.size());
        return listItem.size();
    }


    public void setOnClick(PhotoUpdateAdapter.OnItemClicked onClick) {
        this.onClick = onClick;
    }



}

