package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.EventDetailsActivity;
import com.meeof.meeof.activity.ProfileActivity;
import com.meeof.meeof.model.PeopleNearMeInsideModel;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ransikadesilva on 1/2/18.
 */

public class PrivateInvitationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ShowEventsRecyclerAdapter.class.getSimpleName();

    private final Context mContext;
    public List<Event> listItems;
    protected SharedPreferences sharedPreferences;
    public PrivateInvitationAdapter(Context context, List<Event> listItems) {
        this.listItems = listItems;
        this.mContext = context;
    }

    public interface OnItemClicked {
        void onItemClick(int position, Event item, View button);
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        public final TextView friend_status_textView;
        public final ImageView friend_status_imageView;


        public final LinearLayout publicEventLl;
        public final LinearLayout privateInvitationLl;
        public final ImageView privateInvitationIv;
        public final TextView privateHostNameTv;
        public final TextView privateLocationlocationTv;
        public final TextView PrivateDateTv;
        public final TextView privateMonthTv;
        public final TextView privateEventTitleTv;
        public MyViewHolderFriend(View view) {
            super(view);
            friend_status_textView = (TextView) view.findViewById(R.id.friend_status_textView);
            friend_status_imageView = (ImageView) view.findViewById(R.id.friend_status_imageView);

            publicEventLl=(LinearLayout) view.findViewById(R.id.publicEventLl);
            privateInvitationLl=(LinearLayout) view.findViewById(R.id.privateInvitationLl);
            privateInvitationIv=(ImageView) view.findViewById(R.id.privateInvitationIv);
            privateHostNameTv=(TextView)view.findViewById(R.id.privateHostNameTv);
            PrivateDateTv=(TextView)view.findViewById(R.id.PrivateDateTv);
            privateMonthTv=(TextView)view.findViewById(R.id.privateMonthTv);
            privateLocationlocationTv=(TextView)view.findViewById(R.id.privateLocationlocationTv);
            privateEventTitleTv=(TextView)view.findViewById(R.id.privateEventTitleTv);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_private_invitaion, parent, false);
        return new PrivateInvitationAdapter.MyViewHolderFriend(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final Event item = listItems.get(position);
        PrivateInvitationAdapter.MyViewHolderFriend myViewHolder= (PrivateInvitationAdapter.MyViewHolderFriend) holder;
//        myViewHolder.publicEventLl.setVisibility(View.GONE);
//        myViewHolder.privateInvitationLl.setVisibility(View.VISIBLE);

        Log.d(TAG,"PrivateInvitation :"+item.toString());
        Log.d(TAG,"PrivateInvitation :"+item.getTitle().toString());

        myViewHolder.privateEventTitleTv.setText(item.getTitle());
        myViewHolder.privateHostNameTv.setText(item.getFirst_name());
        myViewHolder.privateLocationlocationTv.setText(item.getLocation());

        String imgeUrlPrivate = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();
        Picasso.with(mContext.getApplicationContext())
                .load(imgeUrlPrivate)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolder.privateInvitationIv);

        if(item.getStart_date()!=null){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(item.getStart_date().toString());
                Calendar c = Calendar.getInstance();
                c.setTime(date);

                int day = c.get(Calendar.DATE);
                SimpleDateFormat monthDate = new SimpleDateFormat("MMMM");
                String monthName = monthDate.format(c.getTime());
                SimpleDateFormat monthDateShort = new SimpleDateFormat("MMM");
                String monthNameShort = monthDateShort.format(c.getTime());
                // monthNameShort = monthNameShort.toUpperCase();
                int year = c.get(Calendar.YEAR);
                int hour = c.get(Calendar.HOUR);
                int minute = c.get(Calendar.MINUTE);
                int am_pm = c.get(Calendar.AM_PM);  //0 am 1 pm
                // String monthName = c.get(Calendar.getD)
                Log.d(TAG, "DATE " + hour + " " + minute + " " + am_pm + " " + day + " " + monthName + " " + monthNameShort + " " + year + " ");
                myViewHolder.PrivateDateTv.setText(day + "");
                myViewHolder.privateMonthTv.setText(monthNameShort);


            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "error in date format " + e.getMessage().toString());
            }
        }

        myViewHolder.privateInvitationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                Bundle args = new Bundle();
                args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) item);
                intent.putExtra("BUNDLE_EVENT", args);
                mContext.startActivity(intent);
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

    public List<Event> getFilteredList() {
        return listItems;
    }

    public List<Event> getListItems() {
        return listItems;
    }

    public void setListItems(List<Event> listItems)
    {
        this.listItems = listItems;
    }
}
