package com.meeof.meeof.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.events_dto.AttendeeList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Anuja Ranwalage on 7/11/2017.
 */
public class InvitedFriendsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = InvitedFriendsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<AttendeeList> invitedList;
    private OnItemClicked onClick;


    public interface OnItemClicked {
        void onItemClick(int position, AttendeeList item, MyViewHolderFriend myViewHolderParcel);
    }

    public InvitedFriendsRecyclerAdapter(Context context, List<AttendeeList> invitedList) {
        this.invitedList = invitedList;
        this.mContext = context;
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final ImageView friend_imageView;
        private final TextView friend_name_textView;
        private final ImageView friend_status_imageView;

        public MyViewHolderFriend(View view) {
            super(view);
            friend_imageView = (ImageView) view.findViewById(R.id.friendAvatarIv);
            friend_name_textView = (TextView) view.findViewById(R.id.friendNameTv);
            friend_status_imageView = (ImageView) view.findViewById(R.id.friend_status_imageView);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rsvp_friend, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AttendeeList item = invitedList.get(position);

        final MyViewHolderFriend myViewHolderParcel = (MyViewHolderFriend) holder;
        myViewHolderParcel.friend_name_textView.setText(item.getFirstName());


        Picasso.with(mContext.getApplicationContext())
                .load(item.getProfilephoto())
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderParcel.friend_imageView);

        Drawable statusDrawable;
        if (item.getStatus()!= null && item.getStatus().equals("active")) {
            if (item.getFriendstatus()!=null && item.getFriendstatus().equals("friend")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);
            } else if (item.getFriendstatus()!=null && item.getFriendstatus().equals("pending")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
            } else if (item.getFriendstatus()!=null && item.getFriendstatus().equals("notfriend")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
            } else {
                Log.w(TAG, "Inside else green");
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
                myViewHolderParcel.friend_status_imageView.setVisibility(View.GONE);
            }
        } else if (item.getStatus()!= null && item.getStatus().equals("dummy")) {
            Log.w(TAG, "Inside elif dummy green");
            statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            myViewHolderParcel.friend_status_imageView.setVisibility(View.GONE);
        } else {
            Log.w(TAG, "Inside else dummy green");
            statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            myViewHolderParcel.friend_status_imageView.setVisibility(View.GONE);
        }
        myViewHolderParcel.friend_status_imageView.setImageDrawable(statusDrawable);

        myViewHolderParcel.friend_status_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Drawable statusDrawable;
                if (item.getStatus().equals("active")) {
                    if (item.getFriendstatus()!=null && item.getFriendstatus().equals("friend")) {
                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);

                    } else if (item.getFriendstatus()!=null && item.getFriendstatus().equals("pending")) {
                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                    } else if (item.getFriendstatus()!=null && item.getFriendstatus().equals("notfriend")) {
                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                    } else {
                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                    }
                } else if (item.getStatus().equals("dummy")) {
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                } else {
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                }
                myViewHolderParcel.friend_status_imageView.setImageDrawable(statusDrawable);

                onClick.onItemClick(position, invitedList.get(position), myViewHolderParcel);

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
        return (null != invitedList ? invitedList.size() : invitedList.size());
    }


    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public List<AttendeeList> getAtteneesList() {
        return invitedList;
    }

}

