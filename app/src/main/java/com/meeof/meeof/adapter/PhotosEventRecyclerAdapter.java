package com.meeof.meeof.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by ransika on 7/11/2017.
 */
public class PhotosEventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PhotosEventRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Photos> listItem, filterList;
    private OnItemClicked onClick;
    private ProgressDialog progressDialog;


    public interface OnItemClicked {
        void onItemClick(int position, boolean isAccept, Photos item);
    }

    public PhotosEventRecyclerAdapter(Context context, List<Photos> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<Photos>();
//        this.filterList.addAll(this.listItem); //to show all items at begining
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {


        private final ImageView userImagesIv;
        private final ImageView userAvatarIv;
        private final TextView userNameTv;
        private final ProgressBar progressBar;

        public MyViewHolderFriend(View view) {
            super(view);

            userImagesIv = (ImageView) view.findViewById(R.id.userImagesIv);
            userAvatarIv = (ImageView) view.findViewById(R.id.userAvatarIv);
            userNameTv = (TextView) view.findViewById(R.id.userNameTv);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_image, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Photos item = listItem.get(position);

        final MyViewHolderFriend myViewHolderPendingFriend = (MyViewHolderFriend) holder;
        myViewHolderPendingFriend.userNameTv.setText(item.getFirst_name());

        myViewHolderPendingFriend.progressBar.setVisibility(View.VISIBLE);

        String imageUrlUserAvatar = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        String imageUrlEventImage = item.getFile_name() == null || item.getFile_name().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                Constant.EVENT_IMAGES_BASE_URL + item.getFile_name();

        Log.d(TAG, "PhotoList: " + imageUrlEventImage);
        Picasso.with(mContext.getApplicationContext())
                .load(imageUrlUserAvatar)
                .centerCrop()
                .fit()
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderPendingFriend.userAvatarIv);

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


        myViewHolderPendingFriend.userImagesIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClick.onItemClick(position, true, item);
            }
        });


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

    public void filter(final String text) {
        Log.d(TAG, "SearchText: " + text + "\ninside filter");
        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "filterList size: " + filterList.size() + "\ninside filter");
                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    Log.d(TAG, "TextUtils.isEmpty(text) filterList size: " + filterList.size() + "\ninside filter");
                    filterList.addAll(listItem);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Photos item : listItem) {
                        if (item.getFirst_name().toLowerCase().startsWith(text.toLowerCase())) {

                            Log.d(TAG, "for (FriendListItem item : listItem) filter : " + item.toString() + "\ninside filter");
                            filterList.add(item);

                        }

                    }
                }

                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public List<Photos> getFilteredList() {
        return filterList;
    }

}

