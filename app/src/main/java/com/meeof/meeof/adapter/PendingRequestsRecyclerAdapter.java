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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransika on 7/11/2017.
 */
public class PendingRequestsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PendingRequestsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Pending_requests> listItem, filterList;
    private OnItemClicked onClick;


    public interface OnItemClicked {
        void onItemClick(int position, boolean isAccept, Pending_requests item);
    }

    public PendingRequestsRecyclerAdapter(Context context, List<Pending_requests> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<Pending_requests>();
//        this.filterList.addAll(this.listItem); //to show all items at begining
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        private final ImageView pendingFriendIv;
        private final TextView pendingFriendTv;
        private final LinearLayout acceptRequestLlBtn;
        private final LinearLayout declineRequestLlBtn;


        public MyViewHolderFriend(View view) {
            super(view);

            pendingFriendIv = (ImageView) view.findViewById(R.id.pendingFriendIv);
            pendingFriendTv = (TextView) view.findViewById(R.id.pendingFriendTv);
            acceptRequestLlBtn = (LinearLayout) view.findViewById(R.id.acceptRequestLlBtn);
            declineRequestLlBtn = (LinearLayout) view.findViewById(R.id.declineRequestLlBtn);

        }
    }

    public void removeFriendById(int userId){
        for (int i=0;i<listItem.size();i++) {
            if(listItem.get(i).getUser_id()==userId){
                listItem.remove(i);
                this.notifyDataSetChanged();
            }
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends_request, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Pending_requests item = listItem.get(position);

        final MyViewHolderFriend myViewHolderPendingFriend = (MyViewHolderFriend) holder;
        myViewHolderPendingFriend.pendingFriendTv.setText(item.getFirst_name());

        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderPendingFriend.pendingFriendIv);


        myViewHolderPendingFriend.acceptRequestLlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClick.onItemClick(position, true, listItem.get(position));
            }
        });

        myViewHolderPendingFriend.declineRequestLlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClick.onItemClick(position, false, listItem.get(position));
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
                    for (Pending_requests item : listItem) {
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

    public List<Pending_requests> getFilteredList() {
        return filterList;
    }

}

