package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransika on 7/11/2017.
 */
public class FriendsSearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FriendsSearchRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<com.meeof.meeof.model.search_query_dto.Data> listItem, filterList;
    private OnItemClicked onClick;


    public interface OnItemClicked {
        void onItemClick(int position, com.meeof.meeof.model.search_query_dto.Data item, MyViewHolderFriend myViewHolderParcel);
    }

    public FriendsSearchRecyclerAdapter(Context context, List<com.meeof.meeof.model.search_query_dto.Data> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<com.meeof.meeof.model.search_query_dto.Data>();
//        this.filterList.addAll(this.listItem); //to show all items at begining
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final ImageView friend_imageView;
        private final TextView friend_name_textView;
        private final TextView friend_status_textView;
        private final ImageView friend_status_imageView;



        public MyViewHolderFriend(View view) {
            super(view);
            friend_imageView = (ImageView) view.findViewById(R.id.friendAvatarIv);
            friend_name_textView = (TextView) view.findViewById(R.id.friendNameTv);
            friend_status_textView = (TextView) view.findViewById(R.id.friend_status_textView);
            friend_status_imageView = (ImageView) view.findViewById(R.id.friend_status_imageView);


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final com.meeof.meeof.model.search_query_dto.Data item = listItem.get(position);

        final MyViewHolderFriend myViewHolderParcel = (MyViewHolderFriend) holder;
        myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " " + (item.getLast_name() != null ? item.getLast_name() : ""));
        myViewHolderParcel.friend_status_textView.setText(item.getStatus());
//        myViewHolderParcel.friend_imageView.setText(item.getStatus());

        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();


        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderParcel.friend_imageView);

        Drawable statusDrawable;
        if (item.getStatus().equals("active")) {
            if (item.getFriend_status().equals("friend")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);
                myViewHolderParcel.friend_status_textView.setText("Already friends on Meeof");
            } else if (item.getFriend_status().equals("pending")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
                myViewHolderParcel.friend_status_textView.setText("Pending Request");
            } else if (item.getFriend_status().equals("none")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
                myViewHolderParcel.friend_status_textView.setText("User on Meeof");
            } else {
                Log.w(TAG, "Inside else green");
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
                myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
            }
        } else if (item.getStatus().equals("dummy")) {
            Log.w(TAG, "Inside elif dummy green");
            statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
        } else {
            Log.w(TAG, "Inside else dummy green");
            statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
        }
        myViewHolderParcel.friend_status_imageView.setImageDrawable(statusDrawable);

//                    myViewHolderParcel.friend_imageView.setText(item.getArea());
//                    myViewHolderParcel.friend_status_imageView.setText(item.getLocalGovernment());
        myViewHolderParcel.friend_status_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                onClick.onItemClick(position, listItem.get(position), myViewHolderParcel);

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
                    for (com.meeof.meeof.model.search_query_dto.Data item : listItem) {
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

    public List<com.meeof.meeof.model.search_query_dto.Data> getFilteredList() {
        return filterList;
    }

}

