package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.meeof.meeof.R;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransika on 7/11/2017.
 */
public class EventInviteFriendsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EventInviteFriendsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<Friends> selectedFriends;
    private List<Friends> listItem, filterList;
    private OnItemClicked onClick;
    private OnDeleteClicked onDeleteClick;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClicked {
        void onItemClick(int position, Friends item, boolean isChecked);
    }

    public interface OnDeleteClicked {
        void onDeleteItemClick(int position, Friends item);
    }

    public EventInviteFriendsRecyclerAdapter(Context context, List<Friends> listItems, List<Friends> selectedFriends) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<Friends>();
        this.filterList.addAll(this.listItem); //to show all items at begining
        this.selectedFriends = selectedFriends;
        viewBinderHelper.setOpenOnlyOne(true);//Only open delete at one item
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final LinearLayout mainItemLayoutLl;
        private final ImageView friendAvatarIv;
        private final TextView friendNameTv;
        private final CheckBox friendCb;

        public MyViewHolderFriend(View view) {
            super(view);
            mainItemLayoutLl = (LinearLayout) view.findViewById(R.id.mainItemLayoutLl);
            friendAvatarIv = (ImageView) view.findViewById(R.id.friendAvatarIv);
            friendNameTv = (TextView) view.findViewById(R.id.friendNameTv);
            friendCb = (CheckBox) view.findViewById(R.id.publicCb);


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invite_friends_to_event, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Friends item = filterList.get(position);

        final MyViewHolderFriend myViewHolderParcel = (MyViewHolderFriend) holder;

        myViewHolderParcel.friendNameTv.setText(item.getFirst_name() + " " + (item.getLast_name() != null ? item.getLast_name() : ""));
//        myViewHolderParcel.friend_imageView.setText(item.getStatus());

        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderParcel.friendAvatarIv);

        for (Friends selectedFriend : selectedFriends) {
            if (item.getId() == selectedFriend.getId()) {
                Log.d(TAG, selectedFriend.getFirst_name() + "==" + item.getId() + "\n" + selectedFriend.getId() + "==" + item.getId());
                myViewHolderParcel.friendCb.performClick();
                myViewHolderParcel.friendCb.setChecked(true);
            }
        }


        myViewHolderParcel.friendCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = myViewHolderParcel.friendCb.isChecked();
                if (isChecked) {
                    myViewHolderParcel.friendCb.setChecked(true);
                }
                onClick.onItemClick(position, filterList.get(position), isChecked);
            }
        });



    }


    @Override
    public int getItemViewType(int position) {

        return 1;
    }

//    public void checkSelected() {
//        for (int i = 0; i < filterList.size(); i++) {
//            for (int j = 0; j < selectedFriends.size(); j++) {
//                if (filterList.get(i).getId() == selectedFriends.get(j).getId()) {
//                    Log.d(TAG, selectedFriends.get(i).getFirst_name() + " | " + selectedFriends.get(i).getId());
//                    myViewHolderParcel.friendCb.performClick();
//                    myViewHolderParcel.friendCb.setChecked(true);
//                }
//            }
//        }
//    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {

        return (null != filterList ? filterList.size() : listItem.size());
//        return listItem.size();
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
                    for (Friends item : listItem) {
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

    public void setOnDeleteClick(OnDeleteClicked onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public List<Friends> getFilteredList() {
        return filterList;
    }

}

