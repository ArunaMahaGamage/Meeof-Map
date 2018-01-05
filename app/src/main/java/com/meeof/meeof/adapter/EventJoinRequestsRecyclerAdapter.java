package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.model.Event_join_request2_dto.Data;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.PostLikeEventAsFriendWebJob;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ransika on 7/11/2017.
 */
public class EventJoinRequestsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EventJoinRequestsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Data> listItem;
    private OnItemClicked onClick;
    private JobManager jobManager;
    private String accessToken;
    private SharedPreferences sharedPreferences;



    public interface OnItemClicked {
        void onItemClick(int position, boolean isAccept, Data item);
    }

    public EventJoinRequestsRecyclerAdapter(Context context, List<Data> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        jobManager = MeeofApplication.getInstance().getJobManager();
        sharedPreferences = this.mContext.getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
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
                .inflate(R.layout.item_event_join_request, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Data item = listItem.get(position);

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
                Log.d(TAG,"myViewHolderPendingFriend true");
            }
        });

        myViewHolderPendingFriend.declineRequestLlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClick.onItemClick(position, false, listItem.get(position));
                Log.d(TAG,"myViewHolderPendingFriend false");
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
            return listItem.size();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}

