package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.UpcomingEventInsideModel;
import com.meeof.meeof.model.events_dto.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Dharmesh on 12/2/2017.
 */

public class UpdateEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    public List<UpcomingEventInsideModel> listItems;
    protected SharedPreferences sharedPreferences;
    public UpdateEventsAdapter(Context context, List<UpcomingEventInsideModel> listItems) {
        this.listItems = listItems;
        this.mContext = context;
    }

    public interface OnItemClicked {
        void onItemClick(int position, Event item, View button);
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {

        TextView TxtDate,TxtMonth,TxtTitle,TxtAddress,TxtStatus;
        public MyViewHolderFriend(View view) {
            super(view);
            TxtDate=view.findViewById(R.id.TxtDate);
            TxtMonth=view.findViewById(R.id.TxtMonth);
            TxtTitle=view.findViewById(R.id.TxtTitle);
            TxtAddress=view.findViewById(R.id.TxtAddress);
            TxtStatus=view.findViewById(R.id.TxtStatus);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.update_event_adapter_item, parent, false);
        return new MyViewHolderFriend(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final UpcomingEventInsideModel item = listItems.get(position);
        MyViewHolderFriend myViewHolderFriend= (MyViewHolderFriend) holder;
        if (item.getRsvp()==1)
        {
            myViewHolderFriend.TxtStatus.setText("Invited");
        }
        else if (item.getRsvp()==2)
        {
            myViewHolderFriend.TxtStatus.setText("Interested");
        }
        else if (item.getRsvp()==3)
        {
            myViewHolderFriend.TxtStatus.setText("Going");
        }
        else if (item.getRsvp()==4)
        {
            myViewHolderFriend.TxtStatus.setText("Not Going");
        }
        else if (item.getRsvp()==5)
        {
            myViewHolderFriend.TxtStatus.setText("Maybe");
        }

        myViewHolderFriend.TxtTitle.setText(item.getTitle());
        myViewHolderFriend.TxtAddress.setText(item.getPlaceName());
        String StartDate=item.getStart_date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date=simpleDateFormat.parse(StartDate);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);

            int myDate=calendar.get(Calendar.DATE);
            int MyMonth=calendar.get(Calendar.MONTH);
            MyMonth=MyMonth+1;
            String MyMonthString="";
            switch (MyMonth)
            {
                case 1:
                    MyMonthString="Jan";
                    break;
                case 2:
                    MyMonthString="Feb";
                    break;
                case 3:
                    MyMonthString="Mar";
                    break;
                case 4:
                    MyMonthString="Apr";
                    break;
                case 5:
                    MyMonthString="May";
                    break;
                case 6:
                    MyMonthString="Jun";
                    break;
                case 7:
                    MyMonthString="Jul";
                    break;
                case 8:
                    MyMonthString="Aug";
                    break;
                case 9:
                    MyMonthString="Sep";
                    break;
                case 10:
                    MyMonthString="Oct";
                    break;
                case 11:
                    MyMonthString="Nov";
                    break;
                case 12:
                    MyMonthString="Dec";
                    break;

            }
            myViewHolderFriend.TxtDate.setText("" + myDate);
            myViewHolderFriend.TxtMonth.setText(MyMonthString);

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

    public List<UpcomingEventInsideModel> getFilteredList() {
        return listItems;
    }

    public List<UpcomingEventInsideModel> getListItems() {
        return listItems;
    }

    public void setListItems(List<UpcomingEventInsideModel> listItems)
    {
        this.listItems = listItems;
    }
}