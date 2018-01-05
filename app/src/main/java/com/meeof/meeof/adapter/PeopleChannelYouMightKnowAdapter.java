package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.PeopleYouMightKnowInsideModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Dharmesh on 12/20/2017.
 */

public class PeopleChannelYouMightKnowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    public List<PeopleYouMightKnowInsideModel> listItems;
    protected SharedPreferences sharedPreferences;
    private OnItemClicked onClick;
    public PeopleChannelYouMightKnowAdapter(Context context, List<PeopleYouMightKnowInsideModel> listItems) {
        this.listItems = listItems;
        this.mContext = context;
    }

    public interface OnItemClicked {
        void onItemClick(int position, PeopleYouMightKnowInsideModel item);
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        LinearLayout LinearChannelItem,LinearUserItem;
        ImageView ImgUserImagePeopleYouMightKnow,ChannelImage;
        TextView TxtUserName,TxtChannelName;
        Button BtnFollowChannel;
        ImageView friend_status_imageView;
        public MyViewHolderFriend(View view) {
            super(view);
            LinearChannelItem=view.findViewById(R.id.LinearChannelItem);
            LinearUserItem=view.findViewById(R.id.LinearUser);
            ImgUserImagePeopleYouMightKnow=view.findViewById(R.id.UserImagePeopleYouMightKnow);
            ChannelImage=view.findViewById(R.id.ItemChannelImage);
            TxtUserName=view.findViewById(R.id.UserNamePeopleUMightKnow);
            TxtChannelName=view.findViewById(R.id.ChannelNameTv);
            BtnFollowChannel=view.findViewById(R.id.BtnFollowUs);
            friend_status_imageView=view.findViewById(R.id.friend_status_imageView);


        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_channel_might_know_item, parent, false);
        return new MyViewHolderFriend(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {

        final PeopleYouMightKnowInsideModel item = listItems.get(position);
        final MyViewHolderFriend myViewHolderFriend= (MyViewHolderFriend) holder;
        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        if (item.getChannel_id()==null)
        {
            myViewHolderFriend.LinearUserItem.setVisibility(View.VISIBLE);
            myViewHolderFriend.LinearChannelItem.setVisibility(View.GONE);
            Picasso.with(mContext.getApplicationContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .resize(70,70)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .into(myViewHolderFriend.ImgUserImagePeopleYouMightKnow);

            myViewHolderFriend.TxtUserName.setText(item.getFirst_name());



            Drawable statusDrawable;
            if (item.getStatus().equals("active")) {
                if (item.getStatus().equals("friend")) {
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);

                } else if (item.getStatus().equals("pending")) {
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);

                } else if (item.getStatus().equals("none")) {
                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
                } else {

                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
                }
            } else if (item.getStatus().equals("dummy")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            } else {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            }
            myViewHolderFriend.friend_status_imageView.setImageDrawable(statusDrawable);
            myViewHolderFriend.friend_status_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onItemClick(position, listItems.get(position));
                }
            });
        }
        else
        {
            myViewHolderFriend.LinearChannelItem.setVisibility(View.VISIBLE);
            myViewHolderFriend.LinearUserItem.setVisibility(View.GONE);
            Picasso.with(mContext.getApplicationContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .into(myViewHolderFriend.ChannelImage);
            myViewHolderFriend.TxtChannelName.setText(item.getFirst_name());
            myViewHolderFriend.BtnFollowChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        //  Log.d(TAG,"ITEM COUNT "+listItems.size());
//        return (null != filterList ? filterList.size() : listItem.size());
        return listItems.size();
    }
    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}
