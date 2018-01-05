package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.FriendStatus;
import com.meeof.meeof.model.events_dto.AttendeeList;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Anuja Ranwalage on 7/11/2017.
 */
public class GoingFriendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GoingFriendRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<AttendeeList> goingList;
    private OnItemClicked onClick;
    private ProfileResponse profileResponse;
    private List<FriendStatus> friendStatusList;


    public interface OnItemClicked {
        void onItemClick(int position, AttendeeList item, MyViewHolderFriend myViewHolderParcel);
    }

    public GoingFriendRecyclerAdapter(Context context, List<AttendeeList> goingList, List<FriendStatus> friendStatusList) {
        this.goingList = goingList;
        this.mContext = context;
        profileResponse = retriveSavedProfileObject();
        this.friendStatusList = friendStatusList;
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
        final AttendeeList item = goingList.get(position);

        final MyViewHolderFriend myViewHolderParcel = (MyViewHolderFriend) holder;
        myViewHolderParcel.friend_name_textView.setText(item.getFirstName());


        Picasso.with(mContext.getApplicationContext())
                .load(Helper.getProfilePhotoUser(item.getProfilephoto()))
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderParcel.friend_imageView);

        Drawable statusDrawable;
        Log.d(TAG, "status: " + item.getStatus());
        Log.d(TAG, "friend status: " + item.getFriendstatus());


        if(item.getUserId() != profileResponse.getData().getUser_id()){

            if (item.getStatus() != null && item.getStatus().equals("active")) {

                String frindStatus = "notfriend";

                for(FriendStatus friendStatus : friendStatusList){
                    if(item.getUserId() == friendStatus.getUserId()){
                        frindStatus = friendStatus.getFriendStatus();
                        break;
                    }
                }

                if(frindStatus.equalsIgnoreCase("friend")){
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);
                }else if(frindStatus.equalsIgnoreCase("pending")){
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
                }else {
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
                }

//                if (item.getFriendstatus() != null && item.getFriendstatus().equals("friend")) {
//                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);
//                } else if (item.getFriendstatus() != null && item.getFriendstatus().equals("pending")) {
//                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
//                } else if (item.getFriendstatus() != null && item.getFriendstatus().equals("notfriend")) {
//                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
//                } else {
//                    Log.w(TAG, "Inside else green");
//                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
//                    myViewHolderParcel.friend_status_imageView.setVisibility(View.GONE);
//                }


            } else if (item.getStatus() != null && item.getStatus().equals("dummy")) {
                Log.w(TAG, "Inside elif dummy green");
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
                myViewHolderParcel.friend_status_imageView.setVisibility(View.GONE);

            } else {
                Log.w(TAG, "Inside else dummy green");
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);

            }

            myViewHolderParcel.friend_status_imageView.setImageDrawable(statusDrawable);

        }else {
            myViewHolderParcel.friend_status_imageView.setVisibility(View.GONE);
        }

        myViewHolderParcel.friend_status_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Drawable statusDrawable;
                if (item.getFriendstatus() != null && item.getStatus().equals("active")) {
                    if (item.getFriendstatus() != null && item.getFriendstatus().equals("friend")) {
                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);

                    } else if (item.getFriendstatus() != null && item.getFriendstatus().equals("pending")) {
                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                    } else if (item.getFriendstatus() != null && item.getFriendstatus().equals("notfriend")) {
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

                onClick.onItemClick(position, goingList.get(position), myViewHolderParcel);

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
        return (null != goingList ? goingList.size() : goingList.size());
    }


    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public List<AttendeeList> getAtteneesList() {
        return goingList;
    }

    private ProfileResponse retriveSavedProfileObject() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            return profileResponse;
        }
        return null;
    }

}

