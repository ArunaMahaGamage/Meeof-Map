package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.ProfileActivity;
import com.meeof.meeof.model.PeopleNearMeInsideModel;
import com.meeof.meeof.model.events_dto.Event;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dharmesh on 12/4/2017.
 */

public class PeopleNearMeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    public List<PeopleNearMeInsideModel> listItems;
    protected SharedPreferences sharedPreferences;
    public PeopleNearMeAdapter(Context context, List<PeopleNearMeInsideModel> listItems) {
        this.listItems = listItems;
        this.mContext = context;
    }

    public interface OnItemClicked {
        void onItemClick(int position, Event item, View button);
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        TextView TxtName,TxtDescription;
        CircleImageView ImgChannelImage;
        RelativeLayout RelativeNearMeChannel;
        public MyViewHolderFriend(View view) {
            super(view);
            TxtName=view.findViewById(R.id.TxtChannelNameItem);
            TxtDescription=view.findViewById(R.id.TxtDescription);
            ImgChannelImage=view.findViewById(R.id.ImgChannelItem);
            RelativeNearMeChannel=view.findViewById(R.id.RelativeNearMeChannel);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_near_me_item, parent, false);
        return new MyViewHolderFriend(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final PeopleNearMeInsideModel item = listItems.get(position);
        MyViewHolderFriend myViewHolderFriend= (MyViewHolderFriend) holder;
        myViewHolderFriend.TxtName.setText(item.getFirst_name());
        myViewHolderFriend.TxtDescription.setText(Math.round(item.getDistance()) + "m away");
        String imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();
        Glide.with(mContext).load(imgeUrl1).placeholder(R.drawable.siloet_img)
                .error(ContextCompat.getDrawable(mContext, R.drawable.siloet_img)).into(myViewHolderFriend.ImgChannelImage);

        myViewHolderFriend.RelativeNearMeChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getUser_id()!=0)
                {
                    Intent intent=new Intent(mContext,ProfileActivity.class);
                    intent.putExtra("UserId",item.getUser_id());
                    mContext.startActivity(intent);
                }
            }
        });
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

    public List<PeopleNearMeInsideModel> getFilteredList() {
        return listItems;
    }

    public List<PeopleNearMeInsideModel> getListItems() {
        return listItems;
    }

    public void setListItems(List<PeopleNearMeInsideModel> listItems)
    {
        this.listItems = listItems;
    }
}