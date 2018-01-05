package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.interested_ignored_event_reponse_dto.Events;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ransikadesilva on 12/5/17.
 */

public class IgnoredInterestedEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = UserFriendsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Events> listItem, filterList;
    private IgnoredUsersRecyclerAdapter.OnItemClicked onClick;
    private IgnoredInterestedEventsAdapter.OnDeleteClicked onDeleteClick;
    private boolean isShowDeleteButtons;
    private SharedPreferences sharedPreferences;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClicked {
        void onItemClick(int position, Events item);
    }

    public interface OnDeleteClicked {
        void onDeleteItemClick(int position, Events item);
    }

    public IgnoredInterestedEventsAdapter(Context context, List<Events> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<Events>();
//        this.filterList.addAll(this.listItem); //to show all items at begining
        viewBinderHelper.setOpenOnlyOne(true);//Only open delete at one item
        sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);

    }

    public void showdeleteButtons(){
        isShowDeleteButtons=true;
        notifyDataSetChanged();
    }

    public void hidedeleteButtons(){
        isShowDeleteButtons=false;
        notifyDataSetChanged();
    }

    public void updateList(List<Events> listItems){
        this.listItem = listItems;
        notifyDataSetChanged();
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        private final TextView eventNameHeaderTv;
        private final TextView profileViewTv;
        private final TextView eventTypeTv;
        private final TextView eventDateTv;
        private final TextView eventMonthTv;
        private final TextView eventLocationTv;
        private final TextView eventFullDateTv;
        private final TextView eventCreaterTv;
        private final ImageView eventPictureIv;
        private final TextView eventDistanceTv;


        private final SwipeRevealLayout swipeLayoutSR;
        private final FrameLayout deleteLayoutFl;
        private final LinearLayout swipeRevealLl;
        private final LinearLayout deleteMultiIvLl;



        public MyViewHolderFriend(View view) {
            super(view);
            eventNameHeaderTv = (TextView) view.findViewById(R.id.eventNameHeaderTv);
            profileViewTv = (TextView) view.findViewById(R.id.profileViewTv);
            eventTypeTv = (TextView) view.findViewById(R.id.eventTypeTv);
            eventDateTv = (TextView) view.findViewById(R.id.eventDateTv);
            eventMonthTv = (TextView) view.findViewById(R.id.eventMonthTv);
            eventLocationTv = (TextView) view.findViewById(R.id.eventLocationTv);
            eventFullDateTv = (TextView) view.findViewById(R.id.eventFullDateTv);
            eventCreaterTv = (TextView) view.findViewById(R.id.eventCreaterTv);
            eventPictureIv = (ImageView) view.findViewById(R.id.eventPictureIv);
            eventDistanceTv=(TextView)view.findViewById(R.id.eventDistanceTv);


            swipeLayoutSR = (SwipeRevealLayout) view.findViewById(R.id.swipeLayoutSR);
            deleteLayoutFl = (FrameLayout)view.findViewById(R.id.deleteLayoutFl);
            swipeRevealLl = (LinearLayout)view.findViewById(R.id.swipeRevealLl);
            deleteMultiIvLl = (LinearLayout)view.findViewById(R.id.deleteMultiIvLl);

        }


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ignored_interested_events, parent, false);

        return new IgnoredInterestedEventsAdapter.MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Events item = listItem.get(position);

        final IgnoredInterestedEventsAdapter.MyViewHolderFriend myViewHolderParcel = (IgnoredInterestedEventsAdapter.MyViewHolderFriend) holder;

        myViewHolderParcel.eventNameHeaderTv.setText(item.getTitle().toString() != null ? item.getTitle().toString() : "");
        myViewHolderParcel.eventTypeTv.setText(item.getInterestName() == null || item.getInterestName().trim().length() <= 0 ? " " : item.getInterestName().toString());
        myViewHolderParcel.eventLocationTv.setText((item.getPlaceName() == null || item.getPlaceName().trim().length() <= 0 ? " " : item.getPlaceName().toString()));
        myViewHolderParcel.eventCreaterTv.setText((item.getFirst_name() == null || item.getFirst_name().trim().length() <= 0 ? " " : item.getFirst_name().toString()));


        switch (item.getTier1()) {
            case (1):
                myViewHolderParcel.profileViewTv.setText("Sports & Activities");
                break;
            case (2):
                myViewHolderParcel.profileViewTv.setText("Board Games");
                break;
            case (3):
                myViewHolderParcel.profileViewTv.setText("Creative Corner");
                break;
            case (4):
                myViewHolderParcel.profileViewTv.setText("Gaming (digital)");
                break;
            case (5):
                myViewHolderParcel.profileViewTv.setText("Going Out");
                break;
            case (6):
                myViewHolderParcel.profileViewTv.setText("Parks / Amusements");
                break;
        }

        if (item.getStart_date() == null || item.getStart_date().trim().length() <= 0) {
            Log.i(TAG, "event date is null");
        } else {
            configureEventDate(myViewHolderParcel, item.getStart_date());
        }

        String photoUrlPoster = Constant.EVENT_POSTER_BASE_URL + item.getEvent_poster();

        Picasso.with(mContext.getApplicationContext())
                .load(photoUrlPoster)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .centerCrop()
                .fit()
                .into(myViewHolderParcel.eventPictureIv);




        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);

        double lat1=item.getLatitude();
        double lon1=item.getLongitude();

        double lat2 =profileResponse.getData().getLatitude();
        double lon2 =profileResponse.getData().getLongitude();

        Log.d(TAG,"DISTANCE User lat: "+lat2+" long: "+lon2);
        Log.d(TAG,"DISTANCE event lat: "+lat1+" long: "+lon1);

        if(lat2==0||lon2==2){
            myViewHolderParcel.eventDistanceTv.setVisibility(View.INVISIBLE);
        }else{
            Location startPoint=new Location("locationA");
            startPoint.setLatitude(lat1);
            startPoint.setLongitude(lon1);

            Location endPoint=new Location("locationA");
            endPoint.setLatitude(lat2);
            endPoint.setLongitude(lon2);

            double distance= (int) startPoint.distanceTo(endPoint);
            Log.d(TAG,"DISTANCE1: "+distance);

            int distanceInt =(int)distance/1000;
            myViewHolderParcel.eventDistanceTv.setText("( " + distanceInt + " KM away" + " )");
        }


        myViewHolderParcel.deleteLayoutFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick.onDeleteItemClick(position,listItem.get(position));
            }
        });

        if(isShowDeleteButtons){
            viewBinderHelper.setOpenOnlyOne(true);
            myViewHolderParcel.swipeRevealLl.setVisibility(View.VISIBLE);
            myViewHolderParcel.deleteMultiIvLl.setVisibility(View.GONE);
            myViewHolderParcel.swipeLayoutSR.setDragEdge(2);
            myViewHolderParcel.swipeLayoutSR.close(true);
            myViewHolderParcel.swipeLayoutSR.setLockDrag(false);
        }else{
            viewBinderHelper.setOpenOnlyOne(false);
            myViewHolderParcel.swipeRevealLl.setVisibility(View.GONE);
            myViewHolderParcel.deleteMultiIvLl.setVisibility(View.VISIBLE);
            myViewHolderParcel.swipeLayoutSR.setDragEdge(1);
            myViewHolderParcel.swipeLayoutSR.open(true);
            myViewHolderParcel.swipeLayoutSR.setLockDrag(true);
        }

    }

    void configureEventDate(IgnoredInterestedEventsAdapter.MyViewHolderFriend myViewHolder, String dateString) {
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

//        return (null != filterList ? filterList.size() : listItem.size());
        return listItem.size();
    }

    public void setOnClick(IgnoredUsersRecyclerAdapter.OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public void setOnDeleteClick(IgnoredInterestedEventsAdapter.OnDeleteClicked onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public List<Events> getFilteredList() {
        return filterList;
    }

    private ProfileResponse retriveSavedProfileObject(SharedPreferences sharedPreferences) {
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
