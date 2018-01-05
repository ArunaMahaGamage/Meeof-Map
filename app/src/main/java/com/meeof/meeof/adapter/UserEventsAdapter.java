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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.model.GetEventsByUserInsideModel;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Dharmesh on 11/30/2017.
 */

public class UserEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = UserEventsAdapter.class.getSimpleName();
    private final Context mContext;
    public List<GetEventsByUserInsideModel> listItems;
    private OnItemClicked onClick;
    private OnLlItemClicked onClickItem;
    protected SharedPreferences sharedPreferences;
    private ProfileResponse profileResponse;
    Boolean isKM = true;
    private OnLikeLlItemClicked onLiketClick;
    private ArrayList<Integer> likedEventIds = new ArrayList<>();
    private OnChannelViewClick channelViewClick;
    public UserEventsAdapter(Context context, List<GetEventsByUserInsideModel> listItems, OnItemClicked onClick, OnChannelViewClick channelViewClick)
    {
        this.listItems = listItems;
        this.mContext = context;
        loadCurrentUser();
        this.onClick=onClick;
        this.channelViewClick=channelViewClick;
    }

    public interface OnItemClicked {
        void onItemClick(int position, GetEventsByUserInsideModel item, View button);
    }
    public interface OnChannelViewClick {
        void onItemClick(int position, GetEventsByUserInsideModel item);
        void onItemClick(int position, Event item);
    }
    public interface OnLlItemClicked {
        void onItemLlClick(int position, GetEventsByUserInsideModel item, View button);
    }

    public interface OnLikeLlItemClicked {
        void onItemLlClick(int position, Event item, View button);
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final TextView eventNameHeaderTv;
        private final TextView eventDistanceTv;
        private final ImageView moreIv;
        private final TextView profileViewTv;
        private final TextView eventTypeTv;
        private final TextView eventDateTv;
        private final TextView eventMonthTv;
        private final TextView eventLocationTv;
        private final TextView eventFullDateTv;
        private final TextView eventCreaterTv;
        private final ImageView eventPictureIv;
        private final ImageView going_people1_imageView;
        private final ImageView going_people2_imageView;
        private final ImageView going_people3_imageView;
        private final TextView goingNumberTv;
        private final ImageView cameraIv;
        private final TextView eventStatusTv;
        private final TextView commentNoTv;
        private final TextView likesNoTv;
        private final ImageView likeIv;
        private final TextView plusForGoingTv;
        private final TextView areGoingTv;
        private final LinearLayout cameraBadge;
        private final TextView cameraBadgeTxt;
        private final LinearLayout moreLlBtn;
        private final LinearLayout goingHostLl;
        private final TextView goingStatusTxt;
        private final TextView slashTxt;
        private final TextView hostTxt;
        private final TextView rsvpTxt;
        private final TextView commentLableTv;
        private final TextView friend_status_textView;
        private final ImageView friend_status_imageView;
        private final LinearLayout eventDetailsLlBtn;


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
            commentNoTv = (TextView) view.findViewById(R.id.commentNoTvEvent);
            likesNoTv = (TextView) view.findViewById(R.id.likesNoTvEvent);
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

//            friend_imageView = (ImageView) view.findViewById(R.id.friend_imageView);
//            friend_name_textView = (TextView) view.findViewById(R.id.friend_name_textView);
            friend_status_textView = (TextView) view.findViewById(R.id.friend_status_textView);
            friend_status_imageView = (ImageView) view.findViewById(R.id.friend_status_imageView);


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_by_users2, parent, false);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final GetEventsByUserInsideModel item = listItems.get(position);
        Log.i(TAG, "DRAWING CELL " + item.toString());
        final MyViewHolderFriend myViewHolder = (MyViewHolderFriend) holder;
        myViewHolder.eventNameHeaderTv.setText(item.getTitle().toString() != null ? item.getTitle().toString() : "");
        myViewHolder.eventCreaterTv.setText((item.getFirst_name() == null || item.getFirst_name().trim().length() <= 0 ? " " : item.getFirst_name().toString()));
        myViewHolder.eventLocationTv.setText((item.getPlaceName() == null || item.getPlaceName().trim().length() <= 0 ? " " : item.getPlaceName().toString()));
        myViewHolder.eventTypeTv.setText(item.getInterestName() == null || item.getInterestName().trim().length() <= 0 ? " " : item.getInterestName().toString());
        myViewHolder.commentNoTv.setText("" + item.getCountComments());
        myViewHolder.likesNoTv.setText("" + item.getCountLikes());
        if (item.getLive() == 0) {
            Log.d(TAG, "Evetnt over ");
            myViewHolder.eventStatusTv.setText("Event is Over");  //rsvpTxt goingStatusTxt slashTxt hostTxt eventStatusTv
            myViewHolder.eventStatusTv.setVisibility(View.VISIBLE);
            myViewHolder.rsvpTxt.setVisibility(View.GONE);
        } else if (item.getLive() == 1) {
            myViewHolder.eventStatusTv.setVisibility(View.GONE);
            myViewHolder.goingStatusTxt.setVisibility(View.VISIBLE);
            myViewHolder.hostTxt.setVisibility(View.VISIBLE);
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


        //myViewHolder.friend_status_textView.setText(item.getStatus());
//        myViewHolder.friend_imageView.setText(item.getStatus());

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
            default:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.going_out_selected);
                break;

        }

        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(drawable)
                .resize(150,0)
                .error(drawable)
                .into(myViewHolder.eventPictureIv);

//        Double d = item.getDistance();
//        int distance = d.intValue();
//        Log.d(TAG, "distace " + distance);
//        if (isKM) {
//            myViewHolder.eventDistanceTv.setText("( " + distance + " KM away" + " )");
//        } else {
//            myViewHolder.eventDistanceTv.setText(distance + " miles away");
//        }
        int atendfriendCount = 0;
        if (item.getAttendeeList().size() != 0) {

            if (item.getAttendeeList().size() == 1) {
                myViewHolder.plusForGoingTv.setVisibility(View.GONE);
                myViewHolder.goingNumberTv.setVisibility(View.GONE);
                myViewHolder.areGoingTv.setText("is going");
            } else if (item.getAttendeeList().size() == 2 || item.getAttendeeList().size() == 3) {
                myViewHolder.plusForGoingTv.setVisibility(View.GONE);
                myViewHolder.goingNumberTv.setVisibility(View.GONE);
                myViewHolder.areGoingTv.setText("are going");
            } else if (item.getAttendeeList().size() > 3) {
                Log.d(TAG, "max attendees " + item.getMax_attendees());
                Log.d(TAG, "getAttendeeList size " + item.getAttendeeList().size());
                Log.d(TAG, "getCountPhotos " + item.getCountPhotos());

                atendfriendCount = item.getAttendeeList().size() - 3;
                myViewHolder.plusForGoingTv.setVisibility(View.VISIBLE);
                Log.d(TAG, "attendence " + item.getMax_attendees() + " REMAINNG " + atendfriendCount + "");
                myViewHolder.goingNumberTv.setText(atendfriendCount + "");
                myViewHolder.areGoingTv.setText("are going");
                myViewHolder.areGoingTv.setVisibility(View.VISIBLE);
                myViewHolder.goingNumberTv.setVisibility(View.VISIBLE);
            }

        } else {
            Log.i(TAG, "no one is attending to event");
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
//                if (!item.isHost()) {
//                    myViewHolder.moreLlBtn.setVisibility(View.INVISIBLE);
//                }
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
        } else if (profileCount == 1) {
            myViewHolder.going_people1_imageView.setVisibility(View.VISIBLE);
            myViewHolder.going_people2_imageView.setVisibility(View.GONE);
            myViewHolder.going_people3_imageView.setVisibility(View.GONE);
            imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(0).getProfilephoto();
            Picasso.with(mContext.getApplicationContext())
                    .load(imgeUrl1)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .resize(40,40)
                    .into(myViewHolder.going_people1_imageView);
        } else if (profileCount == 2) {
            myViewHolder.going_people1_imageView.setVisibility(View.VISIBLE);
            myViewHolder.going_people2_imageView.setVisibility(View.VISIBLE);
            myViewHolder.going_people3_imageView.setVisibility(View.GONE);
            imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(0).getProfilephoto();
            Picasso.with(mContext.getApplicationContext())
                    .load(imgeUrl1)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .resize(40,40)
                    .into(myViewHolder.going_people1_imageView);
            imgeUrl2 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(1).getProfilephoto();
            Picasso.with(mContext.getApplicationContext())
                    .load(imgeUrl2)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .resize(40,40)
                    .into(myViewHolder.going_people2_imageView);

        } else if (profileCount >= 3) {
            myViewHolder.going_people1_imageView.setVisibility(View.VISIBLE);
            myViewHolder.going_people2_imageView.setVisibility(View.VISIBLE);
            myViewHolder.going_people3_imageView.setVisibility(View.VISIBLE);
            imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(0).getProfilephoto();
            Picasso.with(mContext.getApplicationContext())
                    .load(imgeUrl1)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .resize(40,40)
                    .into(myViewHolder.going_people1_imageView);
            imgeUrl2 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(1).getProfilephoto();
            Picasso.with(mContext.getApplicationContext())
                    .load(imgeUrl2)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .resize(40,40)
                    .into(myViewHolder.going_people2_imageView);
            imgeUrl3 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getAttendeeList().get(2).getProfilephoto();
            Picasso.with(mContext.getApplicationContext())
                    .load(imgeUrl3)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                    .resize(40,40)
                    .into(myViewHolder.going_people3_imageView);

        }

//        myViewHolder.going_people1_imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                if (item.getAttendeeList().get(0).getChannel_id()!=null)
//            }
//        });
        myViewHolder.eventDetailsLlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickItem.onItemLlClick(position, item, v);
            }
        });
        myViewHolder.commentLableTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onCommentClick.onCommentItemClick(position, item);
            }
        });
        myViewHolder.likeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onLiketClick.onItemLlClick(position, item, v);
            }
        });

        myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_active));
//        Boolean isLike = item.isLike();
//        if (isLike) {
//            myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_active));
//        } else {
//            myViewHolder.likeIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ico_event_like_normal));
//        }
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
        myViewHolder.going_people1_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                int channelId=item.getAttendeeList().get(0).getChannel_id();
//                Log.e("ChannelId",""+channelId);
                channelViewClick.onItemClick(0 ,item);
            }
        });
        myViewHolder.going_people2_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelViewClick.onItemClick(1 ,item);
            }
        });
        myViewHolder.going_people3_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelViewClick.onItemClick(2 ,item);
            }
        });
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
    private onCommentClick onCommentClick;

    public interface onCommentClick {
        void onCommentItemClick(int position, Event item);
    }

    public void setOnCommentClick(onCommentClick onCommentClick) {
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
//            if (am_pm == 0) {
//                myViewHolder.eventFullDateTv.setText(day + " " + monthName + " " + "at " + hour + "." + minute + " AM");
//            } else if (am_pm == 1) {
//                myViewHolder.eventFullDateTv.setText(day + " " + monthName + " " + "at " + hour + "." + minute + " PM");
//            }
//            myViewHolder.eventDateTv.setText(day + "");
//            myViewHolder.eventMonthTv.setText(monthNameShort);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "error in date format " + e.getMessage().toString());
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



    public List<GetEventsByUserInsideModel> getFilteredList() {
        return listItems;
    }

    private void loadCurrentUser() {
        if (sharedPreferences != null) {
            String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
            Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
            if (!profileObjectJsonStr.equals("")) {
                Gson gson = new Gson();
                profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);

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

    public List<GetEventsByUserInsideModel> getListItems() {
        return listItems;
    }

    public void setListItems(List<GetEventsByUserInsideModel> listItems)
    {
        this.listItems = listItems;
    }
}



