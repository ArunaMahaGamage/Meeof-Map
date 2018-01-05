package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.EventDetailsActivity;
import com.meeof.meeof.model.events_dto.AttendeeList;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.private_invitation_dto_model.Array_events;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by ransika on 7/11/2017.
 */
public class ShowEventsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ShowEventsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private final ArrayList<Event> filterList;
    public List<Event> listItems;
    private OnItemClicked onClick;
    private OnLlItemClicked onClickItem;

    protected SharedPreferences sharedPreferences;
    private ProfileResponse profileResponse;
    Boolean isKM = true;
    private OnLikeLlItemClicked onLiketClick;
    private ArrayList<Integer> likedEventIds = new ArrayList<>();




    public ShowEventsRecyclerAdapter(Context context, List<Event> listItems) {
        this.listItems = listItems;
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        loadCurrentUser();
        this.filterList = new ArrayList<Event>();
        this.filterList.addAll(this.listItems); //to show all items at begining
    }

    public interface OnItemClicked {
        void onItemClick(int position, Event item, View button);
    }

    public interface OnLlItemClicked {
        void onItemLlClick(int position, Event item, View button);
    }

    public interface OnLikeLlItemClicked {
        void onItemLlClick(int position, Event item, View button);
    }

    public void addPrivateInvitaions(List<Array_events> privateInvitations){

        for(Array_events privateInvitation:privateInvitations){
            Event event=new Event();
            event.setCountLikes(privateInvitation.getCountLikes());
            event.setCategory_id(privateInvitation.getCategory_id());
            event.setChannel_id(privateInvitation.getChannel_id());
            event.setCountComments(privateInvitation.getCountComments());
            event.setCountPhotos(privateInvitation.getCountPhotos());
            event.setCreated_at(privateInvitation.getCreated_at());
            event.setDescription(privateInvitation.getDescription());
            event.setDetailedaddress(privateInvitation.getDetailedaddress());
            event.setDistance(privateInvitation.getDistance());
            event.setCountLikes(privateInvitation.getCountLikes());
            event.setEnd_date(privateInvitation.getEnd_date());
            event.setFirst_name(privateInvitation.getFirst_name());
            event.setEvent_poster(privateInvitation.getEvent_poster());
            event.setUser_id(privateInvitation.getUser_id());
            event.setType(privateInvitation.getType());
            event.setTitle(privateInvitation.getTitle());
            event.setStart_date(privateInvitation.getStart_date());
            event.setLocation(privateInvitation.getLocation());
            event.setProfilephoto(privateInvitation.getProfilephoto());
            event.setLive(privateInvitation.getLive());
            event.setChannel_id(privateInvitation.getChannel_id());
            event.setEventid(privateInvitation.getEventid());
            event.setPlaceName(privateInvitation.getPlaceName());
            event.setPlaceID(privateInvitation.getPlaceID());
            event.setLongitude(privateInvitation.getLongitude());
            event.setLatitude(privateInvitation.getLatitude());
            event.setCreated_at(privateInvitation.getCreated_at());
            event.setOrganizer_id(privateInvitation.getOrganizer_id());
            event.setCategory_id(privateInvitation.getCategory_id());
            event.setIs_hide_location(privateInvitation.getIs_hide_location());
            event.setMax_attendees(privateInvitation.getMax_attendees());
            event.setTier1(privateInvitation.getTier1());
            event.setInterestName(privateInvitation.getInterestName());


            Log.d(TAG,"addPrivateInvitaions :"+event.toString());

            listItems.add(0,event);
        }
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        public final TextView eventNameHeaderTv;
        public final TextView eventDistanceTv;
        public final ImageView moreIv;
        public final TextView profileViewTv;
        public final TextView eventTypeTv;
        public final TextView eventDateTv;
        public final TextView eventMonthTv;
        public final TextView eventLocationTv;
        public final TextView eventFullDateTv;
        public final TextView eventCreaterTv;
        public final ImageView eventPictureIv;
        public final ImageView going_people1_imageView;
        public final ImageView going_people2_imageView;
        public final ImageView going_people3_imageView;
        public final TextView goingNumberTv;
        public final ImageView cameraIv;
        public final TextView eventStatusTv;
        public final TextView commentNoTv;
        public final TextView likesNoTv;
        public final ImageView likeIv;
        public final TextView plusForGoingTv;
        public final TextView areGoingTv;
        public final LinearLayout cameraBadge;
        public final TextView cameraBadgeTxt;
        public final LinearLayout moreLlBtn;
        public final LinearLayout goingHostLl;
        public final TextView goingStatusTxt;
        public final TextView slashTxt;
        public final TextView hostTxt;
        public final TextView rsvpTxt;
        public final TextView commentLableTv;
        public final TextView likesTv;


        //        private final ImageView friend_imageView;
//        private final TextView friend_name_textView;
        public final TextView friend_status_textView;
        public final ImageView friend_status_imageView;
        public final LinearLayout eventDetailsLlBtn;

        public final LinearLayout publicEventLl;
        //public final LinearLayout privateInvitationLl;
        public final ImageView privateInvitationIv;
        public final TextView privateHostNameTv;
        public final TextView privateLocationlocationTv;
        public final TextView PrivateDateTv;
        public final TextView privateMonthTv;
        public final TextView privateEventTitleTv;


        public MyViewHolderFriend(View view) {
            super(view);
            eventNameHeaderTv = (TextView) view.findViewById(R.id.eventNameHeaderTv);
            eventDistanceTv = (TextView) view.findViewById(R.id.eventDistanceTv);
            moreIv = (ImageView) view.findViewById(R.id.moreIv);
            profileViewTv = (TextView) view.findViewById(R.id.profileViewTv);
            eventTypeTv = (TextView) view.findViewById(R.id.eventTypeTv);
            eventDateTv = (TextView) view.findViewById(R.id.eventDateTv);
            eventMonthTv = (TextView) view.findViewById(R.id.eventMonthTv);
            eventLocationTv = (TextView) view.findViewById(R.id.eventLocationTv);
            eventFullDateTv = (TextView) view.findViewById(R.id.eventFullDateTv);
            eventCreaterTv = (TextView) view.findViewById(R.id.eventCreaterTv);
            goingNumberTv = (TextView) view.findViewById(R.id.goingNumberTv);
            eventStatusTv = (TextView) view.findViewById(R.id.eventStatusTv);
            commentNoTv = (TextView) view.findViewById(R.id.commentNoTv);
            likesNoTv = (TextView) view.findViewById(R.id.likesNoTv);
            eventPictureIv = (ImageView) view.findViewById(R.id.eventPictureIv);
            going_people1_imageView = (ImageView) view.findViewById(R.id.going_people1_imageView);
            going_people2_imageView = (ImageView) view.findViewById(R.id.going_people2_imageView);
            going_people3_imageView = (ImageView) view.findViewById(R.id.going_people3_imageView);
            cameraIv = (ImageView) view.findViewById(R.id.cameraIv);
            likeIv = (ImageView) view.findViewById(R.id.likeIv);
            plusForGoingTv = (TextView) view.findViewById(R.id.plusForGoingTv);
            areGoingTv = (TextView) view.findViewById(R.id.areGoingTv);
            cameraBadge = (LinearLayout) view.findViewById(R.id.cameraBadge);
            cameraBadgeTxt = (TextView) view.findViewById(R.id.cameraBadgeTxt);
            moreLlBtn = (LinearLayout) view.findViewById(R.id.moreLlBtn);
            goingHostLl = (LinearLayout) view.findViewById(R.id.goingHostLl);
            slashTxt = (TextView) view.findViewById(R.id.slashTxt);
            hostTxt = (TextView) view.findViewById(R.id.hostTxt);
            goingStatusTxt = (TextView) view.findViewById(R.id.goingStatusTxt);
            rsvpTxt = (TextView) view.findViewById(R.id.rsvpTxt);
            eventDetailsLlBtn = (LinearLayout) view.findViewById(R.id.eventDetailsLlBtn);
            commentLableTv = (TextView) view.findViewById(R.id.commentLableTv);
            likesTv=(TextView)view.findViewById(R.id.event_like_textView);

//            friend_imageView = (ImageView) view.findViewById(R.id.friend_imageView);
//            friend_name_textView = (TextView) view.findViewById(R.id.friend_name_textView);
            friend_status_textView = (TextView) view.findViewById(R.id.friend_status_textView);
            friend_status_imageView = (ImageView) view.findViewById(R.id.friend_status_imageView);

            publicEventLl=(LinearLayout) view.findViewById(R.id.publicEventLl);
            //privateInvitationLl=(LinearLayout) view.findViewById(R.id.privateInvitationLl);
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
                .inflate(R.layout.item_event_by_users, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    public void setLikedEventIds(int id, boolean isLike) {
        if (isLike) {

            likedEventIds.add(id);
            Log.d(TAG, "event is like " + isLike + " id " + id + " likedEventIds size " + likedEventIds.size());
        } else {
            for (int evenId : likedEventIds) {
                if (id == evenId) {
                    likedEventIds.remove(new Integer(id));
                    Log.d(TAG, "event is unlike " + isLike + " id " + id + " likedEventIds size " + likedEventIds.size());
                }
            }
        }

        notifyItemRangeChanged(0, listItems.size());
    }

    private void onBindEvents(MyViewHolderFriend myViewHolder,int position,Event event){

    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Event item = listItems.get(position);
        Log.i(TAG, "DRAWING CELL " + item.toString());
        final MyViewHolderFriend myViewHolder = (MyViewHolderFriend) holder;
        Log.i(TAG, "item isLike " + item.isLike());
        Log.d(TAG,"adapter RSVP 2: "+item.getRsvp());

        if(item.getAttendeeList()!=null){
//            myViewHolder.publicEventLl.setVisibility(View.VISIBLE);
//            myViewHolder.privateInvitationLl.setVisibility(View.GONE);


            if (item.isLike()) {
                myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_active));
            } else {
                myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_normal));
            }

            myViewHolder.eventNameHeaderTv.setText(item.getTitle().toString() != null ? item.getTitle().toString() : "");
            myViewHolder.eventCreaterTv.setText((item.getFirst_name() == null || item.getFirst_name().trim().length() <= 0 ? " " : item.getFirst_name().toString()));
            //myViewHolder.eventLocationTv.setText((item.getPlaceName() == null || item.getPlaceName().trim().length() <= 0 ? " " : item.getPlaceName().toString()));
            myViewHolder.eventTypeTv.setText(item.getInterestName() == null || item.getInterestName().trim().length() <= 0 ? " " : item.getInterestName().toString());
            myViewHolder.commentNoTv.setText(item.getCountComments()+"");
            myViewHolder.likesNoTv.setText(item.getCountLikes()+"");

            if(item.getIs_hide_location()==0){
                myViewHolder.eventLocationTv.setText((item.getPlaceName() == null || item.getPlaceName().trim().length() <= 0 ? " " : item.getPlaceName().toString()));
            }else{
                myViewHolder.eventLocationTv.setText("Only visible to attendees");
            }

            Log.d(TAG,"adapter RSVP 1: "+item.getRsvp());
            Log.d(TAG,"adapter firstName: "+item.getFirst_name());

            int rsvp=0;
            try{
                for(AttendeeList attendeeList:item.getAttendeeList()){
                    if(profileResponse.getData().getUser_id()==attendeeList.getUserId()){
                        rsvp=attendeeList.getRsvp();
                        break;
                    }
                }
            }catch(Exception e){
                Log.d(TAG,"profileResponse.getData().getUser_id()==attendeeList.getUserId() Exception: "+e.getClass().getCanonicalName());
            }

            Log.d(TAG, "item get live: " + item.getLive());

            myViewHolder.hostTxt.setVisibility(View.GONE);
            if (item.getLive() == 0) {
                myViewHolder.eventStatusTv.setText("Event is Over");  //rsvpTxt goingStatusTxt slashTxt hostTxt eventStatusTv
                myViewHolder.eventStatusTv.setVisibility(View.VISIBLE);
                myViewHolder.rsvpTxt.setVisibility(View.GONE);
                myViewHolder.goingStatusTxt.setVisibility(View.GONE);

            } else if (item.getLive() == 1) {
                myViewHolder.eventStatusTv.setVisibility(View.GONE);

                if (item.isHost()) {
                    myViewHolder.rsvpTxt.setVisibility(View.GONE);
                    myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);
                    myViewHolder.hostTxt.setVisibility(View.VISIBLE);

                } else if (rsvp==3) {

                    Log.d(TAG + " inside going-- ", item.getRsvp().toString());

                    //myViewHolder.rsvpTxt.setVisibility(View.VISIBLE);
                    myViewHolder.goingStatusTxt.setText("Going");
                    myViewHolder.goingStatusTxt.setTextColor(Color.parseColor("#4C8C5F"));
                    myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);


                } else if (rsvp==5) {

                    Log.d(TAG + " inside maybe-- ", item.getRsvp().toString());

                    //myViewHolder.rsvpTxt.setVisibility(View.VISIBLE);
                    myViewHolder.goingStatusTxt.setText("MayBe");
                    myViewHolder.goingStatusTxt.setTextColor(Color.parseColor("#B79428"));
                    myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);

                } else if (rsvp==4) {

                    Log.d(TAG + " inside notgoing --", item.getRsvp().toString());

                    //myViewHolder.rsvpTxt.setVisibility(View.VISIBLE);
                    myViewHolder.goingStatusTxt.setText("Not Going");
                    myViewHolder.goingStatusTxt.setTextColor(Color.parseColor("#ff0000"));
                    myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);

                } else if (rsvp==2) {

                    Log.d(TAG + " inside interested --", item.getRsvp().toString());

                    //myViewHolder.rsvpTxt.setVisibility(View.VISIBLE);
                    myViewHolder.goingStatusTxt.setText("Interested");
                    myViewHolder.goingStatusTxt.setTextColor(Color.parseColor("#727272"));
                    myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);

                } else if (rsvp==1) {

                    Log.d(TAG + " inside invited --", item.getRsvp().toString());

                    //myViewHolder.rsvpTxt.setVisibility(View.VISIBLE);
                    myViewHolder.goingStatusTxt.setText("Invited");
                    myViewHolder.goingStatusTxt.setTextColor(Color.parseColor("#727272"));
                    myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);
                }
            }else if(item.getLive() == 2){
                Log.d(TAG, "Event canceled ");
                myViewHolder.eventStatusTv.setText("Cancelled");
                myViewHolder.eventStatusTv.setVisibility(View.VISIBLE);
                myViewHolder.rsvpTxt.setVisibility(View.GONE);
                myViewHolder.goingStatusTxt.setVisibility(View.GONE);
            }else {

            }
            if(item.getCountLikes()<1){
                myViewHolder.likesNoTv.setVisibility(View.GONE);
                //myViewHolder.likesTv.setVisibility(View.GONE);
            }
            if(item.getCountComments()<1){
                //myViewHolder.commentLableTv.setVisibility(View.GONE);
                myViewHolder.commentNoTv.setVisibility(View.GONE);
            }
            switch (item.getTier1()) {
                case (1):
                    myViewHolder.profileViewTv.setText("Sports & Activities");
                    break;
                case (2):
                    myViewHolder.profileViewTv.setText("Board Games");
                    break;
                case (3):
                    myViewHolder.profileViewTv.setText("Creative Corner");
                    break;
                case (4):
                    myViewHolder.profileViewTv.setText("Gaming (digital)");
                    break;
                case (5):
                    myViewHolder.profileViewTv.setText("Going Out");
                    break;
                case (6):
                    myViewHolder.profileViewTv.setText("Parks / Amusements");
                    break;
            }
            if (item.getStart_date() == null || item.getStart_date().trim().length() <= 0) {
                Log.i(TAG, "event date is null");
            } else {
                configureEventDate(myViewHolder, item.getStart_date());
            }

            Log.d(TAG,"is hidden: "+item.getIs_hide_location()+" eventName: "+item.getFirst_name());

            if(item.isHost()||item.getIs_hide_location()==0){
                myViewHolder.eventLocationTv.setText((item.getPlaceName() == null || item.getPlaceName().trim().length() <= 0 ? " " : item.getPlaceName().toString()));
            }else{
                for(AttendeeList attendeeList:item.getAttendeeList()){
                    if(attendeeList.getUserId()==profileResponse.getData().getUser_id()){
                        myViewHolder.eventLocationTv.setText((item.getPlaceName() == null || item.getPlaceName().trim().length() <= 0 ? " " : item.getPlaceName().toString()));
                        break;
                    }
                }
            }

            String imageUrl = item.getEvent_poster() == null || item.getEvent_poster().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/dev/public/eAvatar/" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/eAvatar/" + item.getEvent_poster();


            Drawable drawable = null;
            switch (item.getTier1()) {

                case (1):
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.sports_selected);
                    break;
                case (2):
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.boardgames_selected);
                    break;
                case (3):
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.creative_corner_selected);
                    break;
                case (4):
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.gaming_selected);
                    break;
                case (5):
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.going_out_selected);
                    break;
                case (6):
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.parks_selected);
                    break;
            }

            Picasso.with(mContext.getApplicationContext())
                    .load(imageUrl)
                    .placeholder(drawable)
                    .error(drawable)
                    .centerCrop()
                    .fit()
                    .into(myViewHolder.eventPictureIv);

            Double d = item.getDistance();
            int distance = d.intValue();
            Log.d(TAG, "distace " + distance);
            if (isKM) {
                myViewHolder.eventDistanceTv.setText("( " + distance + " KM away" + " )");
            } else {
                myViewHolder.eventDistanceTv.setText(distance + " miles away");
            }

            int atendfriendCount = 0;

            if (item.getAttendeeList().size() > 0) {

                for (int i = 0 ; i < item.getAttendeeList().size() ; i ++){

                    AttendeeList attendee = item.getAttendeeList().get(i);
                    Log.wtf(TAG, "attendee rsvp " + attendee.getRsvp());

                    if(attendee.getRsvp() == 3){
                        ++atendfriendCount;
                    }
                }

                if(atendfriendCount == 1){
                    myViewHolder.plusForGoingTv.setVisibility(View.GONE);
                    myViewHolder.goingNumberTv.setVisibility(View.GONE);
                    myViewHolder.areGoingTv.setText("is going");
                }else if(atendfriendCount > 1 && atendfriendCount <= 3){
                    myViewHolder.plusForGoingTv.setVisibility(View.GONE);
                    myViewHolder.goingNumberTv.setVisibility(View.GONE);
                    myViewHolder.areGoingTv.setText("are going");
                }else if(atendfriendCount > 3) {
                    atendfriendCount = atendfriendCount - 3;
                    myViewHolder.plusForGoingTv.setVisibility(View.VISIBLE);
                    myViewHolder.goingNumberTv.setText(atendfriendCount + "");
                    myViewHolder.areGoingTv.setText("are going");
                    myViewHolder.areGoingTv.setVisibility(View.VISIBLE);
                    myViewHolder.goingNumberTv.setVisibility(View.VISIBLE);

                }else {
                    myViewHolder.plusForGoingTv.setVisibility(View.GONE);
                    myViewHolder.goingNumberTv.setVisibility(View.GONE);
                    myViewHolder.areGoingTv.setVisibility(View.GONE);
                }

            } else {
                Log.d(TAG, "no one is attending to event");
                myViewHolder.plusForGoingTv.setVisibility(View.GONE);
                myViewHolder.goingNumberTv.setVisibility(View.GONE);
                myViewHolder.areGoingTv.setVisibility(View.GONE);
            }


            if (item.getCountPhotos() != 0) {
                myViewHolder.cameraBadge.setVisibility(View.VISIBLE);
                myViewHolder.cameraBadgeTxt.setText(item.getCountPhotos() + "");
                myViewHolder.cameraBadgeTxt.setVisibility(View.VISIBLE);
                myViewHolder.cameraBadge.setVisibility(View.VISIBLE);

            } else {
                Log.i(TAG, "No photos for event");
            }

            myViewHolder.moreLlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.isHost()) {
                        myViewHolder.moreLlBtn.setVisibility(View.INVISIBLE);
                    }
                    onClick.onItemClick(position, item, v);
                }
            });

            int profileCount = item.getAttendeeList().size();    //TODO JANITHA

            String imgeUrl1 = "";
            String imgeUrl2 = "";
            String imgeUrl3 = "";


            if (profileCount == 0) {
                myViewHolder.going_people1_imageView.setVisibility(View.GONE);
                myViewHolder.going_people2_imageView.setVisibility(View.GONE);
                myViewHolder.going_people3_imageView.setVisibility(View.GONE);

            } else{

                AttendeeList attendee = item.getAttendeeList().get(0);

                myViewHolder.going_people1_imageView.setVisibility(View.GONE);
                myViewHolder.going_people2_imageView.setVisibility(View.GONE);
                myViewHolder.going_people3_imageView.setVisibility(View.GONE);

                if(attendee.getRsvp() == 3){

                    myViewHolder.going_people1_imageView.setVisibility(View.VISIBLE);
                    imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(0).getProfilephoto();
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl1)
                            .placeholder(R.drawable.ico_profile_edit_avatar)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                            .into(myViewHolder.going_people1_imageView);
                }

                if(item.getAttendeeList().size() > 1){

                    AttendeeList attendee2 = item.getAttendeeList().get(1);

                    if(attendee2.getRsvp() == 3){
                        myViewHolder.going_people2_imageView.setVisibility(View.VISIBLE);

                        imgeUrl1 = attendee2.getProfilephoto() == null || attendee2.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + attendee2.getProfilephoto();
                        Picasso.with(mContext.getApplicationContext())
                                .load(imgeUrl1)
                                .placeholder(R.drawable.ico_profile_edit_avatar)
                                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                                .into(myViewHolder.going_people2_imageView);
                    }

                    if(item.getAttendeeList().size() > 2){

                        AttendeeList attendee3 = item.getAttendeeList().get(2);

                        if(attendee3.getRsvp() == 3){

                            myViewHolder.going_people3_imageView.setVisibility(View.VISIBLE);

                            imgeUrl1 = attendee3.getProfilephoto() == null || attendee3.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + attendee3.getProfilephoto();
                            Picasso.with(mContext.getApplicationContext())
                                    .load(imgeUrl1)
                                    .placeholder(R.drawable.ico_profile_edit_avatar)
                                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                                    .into(myViewHolder.going_people3_imageView);
                        }
                    }
                }
            }
            myViewHolder.moreLlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.isHost()) {

                    }
                    onClick.onItemClick(position, item, v);
                }
            });

            myViewHolder.eventDetailsLlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem.onItemLlClick(position, item, v);
                }
            });
            myViewHolder.commentLableTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentClick.onCommentItemClick(position, item);
                }
            });
            myViewHolder.likeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLiketClick.onItemLlClick(position, item, v);
                }
            });
            Boolean isLike = item.isLike();
            if (isLike) {
                myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_active));
            } else {
                myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_normal));
            }
            for (int likedEvent : likedEventIds) {
                Log.d(TAG, "likedEvent ID: " + likedEvent);
                Log.d(TAG, "likedEvent getEventid: " + item.getEventid());

                if (item.getEventid() == likedEvent) {
                    Log.d(TAG, "item ID " + item.getEventid() + " likedEvent ID " + likedEvent);
                    myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_active));
                    break;
                } else {
                    Log.d(TAG, "item not liked!!!!!!! ");
                    myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_normal));
                    break;
                }
            }
        }


    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public void setItemOnClick(OnLlItemClicked onClick) {
        this.onClickItem = onClick;
    }


    //    private void sendToEventSocialAvtivity(Event item) {
//        Intent intent = new Intent(getActivity(), EventSocialActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) item);
//        intent.putExtra("BUNDLE_EVENT", args);
//        startActivity(intent);
//
//    }
    private ShowEventsRecyclerAdapter.onCommentClick onCommentClick;

    public interface onCommentClick {
        void onCommentItemClick(int position, Event item);
    }

    public void setOnCommentClick(ShowEventsRecyclerAdapter.onCommentClick onCommentClick) {
        this.onCommentClick = onCommentClick;
    }

    public void setOnLiketClick(OnLikeLlItemClicked onLiketClick) {
        this.onLiketClick = onLiketClick;
    }

    void configureEventDate(MyViewHolderFriend myViewHolder, String dateString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dateString);
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
            if (am_pm == 0) {
                myViewHolder.eventFullDateTv.setText(day + " " + monthName + " " + "at " + hour + "." + minute + " AM");
            } else if (am_pm == 1) {
                myViewHolder.eventFullDateTv.setText(day + " " + monthName + " " + "at " + hour + "." + minute + " PM");
            }
            myViewHolder.eventDateTv.setText(day + "");
            myViewHolder.eventMonthTv.setText(monthNameShort);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "error in date format " + e.getMessage().toString());
        }

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
        //  Log.d(TAG,"ITEM COUNT "+listItems.size());
//        return (null != filterList ? filterList.size() : listItem.size());
        return listItems.size();
    }

    public void filter(final String text) {
        Log.d(TAG, "SearchText: " + text + "\ninside filter");
        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "filterList size: " + listItems.size() + "\ninside filter");
                // Clear the filter list
                listItems.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    Log.d(TAG, "TextUtils.isEmpty(text) filterList size: " + listItems.size() + "\ninside filter");
                    listItems.addAll(filterList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Event item : filterList) {

                        if (item.getPlaceName() != null) {
                            Log.d(TAG, "Search Item : " + item.getPlaceName().toLowerCase().startsWith(text.toLowerCase()));
                            if (item.getPlaceName().toLowerCase().startsWith(text.toLowerCase())) {

                                Log.d(TAG, "for (FriendListItem item : listItem) filter : " + item.toString() + "\ninside filter");
                                listItems.add(item);

                            }
                        }else {
                            continue;
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


    public List<Event> getFilteredList() {
        return listItems;
    }

    private void loadCurrentUser() {
        Log.d(TAG,"loadCurrentUser 1");
        if (sharedPreferences != null) {
            Log.d(TAG,"loadCurrentUser 2");
            String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
            Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
            if (!profileObjectJsonStr.equals("")) {
                Gson gson = new Gson();
                profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);

                Log.d(TAG,"Profile user id: "+profileResponse.getData().getUser_id());

                if (profileResponse != null) {
                    if (profileResponse.getData().getMatrix().equals("0")) {
                        isKM = true;
                        Log.d(TAG, "km selected");

                        if (profileResponse.getData().getMatrix().equals("1")) {
                            isKM = false;
                            Log.d(TAG, "mile selected");

                        }


                    }
                }
            }
        }
    }

    public List<Event> getListItems() {
        return listItems;
    }

    public void setListItems(List<Event> listItems) {
        this.listItems = listItems;
    }
}


