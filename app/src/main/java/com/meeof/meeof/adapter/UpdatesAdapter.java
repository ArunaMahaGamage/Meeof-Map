package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.array_updatesModel;
import com.meeof.meeof.model.events_dto.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dharmesh on 12/4/2017.
 */

public class UpdatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    public List<array_updatesModel> listItems;
    protected SharedPreferences sharedPreferences;

    public UpdatesAdapter(Context context, List<array_updatesModel> listItems) {
        this.listItems = listItems;
        this.mContext = context;
    }

    public interface OnItemClicked {
        void onItemClick(int position, Event item, View button);
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        TextView userNameTv, eventTimeTv, userLocationTv, TxtStatusEvent, TxtNoOfImages, commentNoTv, likesNoTv;
        ImageView imageIv, ImgFirstImage, ImgSecondImage, ImgThirdImage;
        LinearLayout MainLinearPhotos;
        public MyViewHolderFriend(View view) {
            super(view);
            imageIv = view.findViewById(R.id.imageIv);
            userNameTv = view.findViewById(R.id.userNameTv);
            eventTimeTv = view.findViewById(R.id.eventTimeTv);
            userLocationTv = view.findViewById(R.id.userLocationTv);
            TxtStatusEvent = view.findViewById(R.id.TxtStatusEvent);
            TxtNoOfImages = view.findViewById(R.id.ImageCount);
            ImgFirstImage = view.findViewById(R.id.ImgFirstImage);
            ImgSecondImage = view.findViewById(R.id.ImgSecondImage);
            ImgThirdImage = view.findViewById(R.id.ImgThirdImage);
            likesNoTv = view.findViewById(R.id.likesNoTv);
            commentNoTv = view.findViewById(R.id.commentNoTv);
            MainLinearPhotos=view.findViewById(R.id.MainLinearPhotos);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update2, parent, false);
        return new MyViewHolderFriend(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final array_updatesModel item = listItems.get(position);
        MyViewHolderFriend myViewHolderFriend = (MyViewHolderFriend) holder;
        myViewHolderFriend.commentNoTv.setText("" + item.getCountComments());
        myViewHolderFriend.likesNoTv.setText("" + item.getCountLikes());
        myViewHolderFriend.userNameTv.setText(item.getFirst_name());
        myViewHolderFriend.userLocationTv.setText(item.getLocation());
        myViewHolderFriend.eventTimeTv.setText(Math.round(item.getDistance()) + "m away");
        if (item.getPhotocount() > 3) {
            myViewHolderFriend.MainLinearPhotos.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgFirstImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgSecondImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgThirdImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.TxtNoOfImages.setVisibility(View.VISIBLE);
        } else if (item.getPhotocount() == 3) {
            myViewHolderFriend.MainLinearPhotos.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgFirstImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgSecondImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgThirdImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.TxtNoOfImages.setVisibility(View.GONE);
        } else if (item.getPhotocount() == 2) {
            myViewHolderFriend.MainLinearPhotos.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgFirstImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgSecondImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgThirdImage.setVisibility(View.GONE);
            myViewHolderFriend.TxtNoOfImages.setVisibility(View.GONE);
        } else if (item.getPhotocount() == 1) {
            myViewHolderFriend.MainLinearPhotos.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgFirstImage.setVisibility(View.VISIBLE);
            myViewHolderFriend.ImgSecondImage.setVisibility(View.GONE);
            myViewHolderFriend.ImgThirdImage.setVisibility(View.GONE);
            myViewHolderFriend.TxtNoOfImages.setVisibility(View.GONE);
        }
        else
        {
            myViewHolderFriend.MainLinearPhotos.setVisibility(View.GONE);
            myViewHolderFriend.ImgFirstImage.setVisibility(View.GONE);
            myViewHolderFriend.ImgSecondImage.setVisibility(View.GONE);
            myViewHolderFriend.ImgThirdImage.setVisibility(View.GONE);
            myViewHolderFriend.TxtNoOfImages.setVisibility(View.GONE);
        }
        myViewHolderFriend.TxtStatusEvent.setText(item.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.MONTH,1);
            int CurrentHour=currentCalendar.get(Calendar.HOUR);
            int CurrentMinute=currentCalendar.get(Calendar.MINUTE);
            long currentMilliseconds = System.currentTimeMillis();
            long CurrentSeconds = currentMilliseconds / 1000;
            long Next24HoursSeconds = CurrentSeconds + 86400;
            long Previous24HoursSeconds = CurrentSeconds - 86400;
            long NextHour=CurrentSeconds+3600;
            Date date = simpleDateFormat.parse(item.getCreated_at());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int EventHours=calendar.get(Calendar.HOUR);
            int EventMinutes=calendar.get(Calendar.MINUTE);
            long CalendarTimeInMilliseconds = calendar.getTimeInMillis();
            long CalendarTimeInSeconds = CalendarTimeInMilliseconds / 1000;

            if (CalendarTimeInSeconds > Next24HoursSeconds) {
                myViewHolderFriend.eventTimeTv.setText(item.getCreated_at() + " -" + Math.round(item.getDistance()) + " KM away");
            } else if (CalendarTimeInSeconds < Next24HoursSeconds && CalendarTimeInSeconds > CurrentSeconds)
            {
                if (CalendarTimeInSeconds<NextHour)
                {
                    int MinuteDifference=CurrentMinute-EventMinutes;
                    myViewHolderFriend.eventTimeTv.setText(MinuteDifference + " Minutes to go" + "-" + Math.round(item.getDistance()) + " Km away");

                }
                else
                {
                    int hoursDifference=CurrentHour-EventHours;
                    myViewHolderFriend.eventTimeTv.setText(hoursDifference + " hours to go" + "-" + Math.round(item.getDistance()) + " Km away");
                }
            }
            else if (CalendarTimeInSeconds < CurrentSeconds && CalendarTimeInSeconds > Previous24HoursSeconds) {
                myViewHolderFriend.eventTimeTv.setText("Yesterday " +"-"+Math.round(item.getDistance()) + "KM away");
            }

        } catch (ParseException e) {
            e.printStackTrace();
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

    public List<array_updatesModel> getFilteredList() {
        return listItems;
    }

    public List<array_updatesModel> getListItems() {
        return listItems;
    }

    public void setListItems(List<array_updatesModel> listItems) {
        this.listItems = listItems;
    }
}
