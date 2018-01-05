package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meeof.meeof.R;
import com.meeof.meeof.model.ChatModel;
import com.meeof.meeof.util.Constant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Dharmesh on 11/23/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<ChatModel> chatList;
    private int MyId;

    public ChatAdapter(Context context, ArrayList<ChatModel> chatList) {
        super();
        mContext = context;
        this.chatList = chatList;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, Context.MODE_PRIVATE);
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chat_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String MyTime[] = chatList.get(position).getTimestamp().split(" ");
        if (chatList.get(position).getSenderId() == MyId) {
            ((ViewHolder) holder).you_layout.setVisibility(View.GONE);
            ((ViewHolder) holder).me_layout.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).TxtSenderText.setText(chatList.get(position).getMessage());
//            Picasso.with(mContext).load(chatList.get(position).getSenderImage()).placeholder(R.drawable.user_profile_icon).error(R.drawable.img_avatar_00).resize(50,50).into(((ViewHolder) holder).ImgSenderImage);
            if (MyTime.length > 1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                try {
                    Date date = sdf.parse(chatList.get(position).getTimestamp());

                    Date GmtTolocalDate = gmttoLocalDate(date);
                    String DecodedDate = "";
                    if (chatList.get(position).isFromIncoming()) {
                        DecodedDate = sdf.format(date);
                    } else {
                        DecodedDate = sdf.format(GmtTolocalDate);
                    }

                    String SplitDate[] = DecodedDate.split(" ");
                    String SplitTime[] = SplitDate[1].split(":");
                    int time = Integer.parseInt(SplitTime[0]);

                    if (time == 12) {
                        String hours = SplitTime[0];
                        String minutes = SplitTime[1];
                        String FullTime = hours + ":" + minutes + "pm";
                        ((ViewHolder) holder).SenderTime.setText(FullTime);
                    } else if (time > 12) {
                        time = time - 12;

                        String minutes = SplitTime[1];
                        String FullTime = time + ":" + minutes + "pm";
                        ((ViewHolder) holder).SenderTime.setText(FullTime);
                    } else {
                        String hours = SplitTime[0];
                        String minutes = SplitTime[1];
                        String FullTime = hours + ":" + minutes + "am";
                        ((ViewHolder) holder).SenderTime.setText(FullTime);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ((ViewHolder) holder).you_layout.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).me_layout.setVisibility(View.GONE);
            ((ViewHolder) holder).TxtReceiverText.setText(chatList.get(position).getMessage());
//            Picasso.with(mContext).load(chatList.get(position).getSenderImage()).placeholder(R.drawable.user_profile_icon).error(R.drawable.img_avatar_00).resize(50,50).into(((ViewHolder) holder).ImgReceiverImage);

            Glide.with(mContext).load(chatList.get(position).getSenderImage()).placeholder(R.drawable.user_profile_icon).error(R.drawable.user_profile_icon).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    e.printStackTrace();
                    return true;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(((ViewHolder) holder).ImgSenderImage);
            if (MyTime.length > 1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                try {
                    Date date = sdf.parse(chatList.get(position).getTimestamp());
                    Date GmtTolocalDate = gmttoLocalDate(date);
                    String DecodedDate = sdf.format(GmtTolocalDate);
                    String SplitDate[] = DecodedDate.split(" ");
                    String SplitTime[] = SplitDate[1].split(":");
                    int time = Integer.parseInt(SplitTime[0]);

                    if (time == 12) {

                        String hours = SplitTime[0];
                        String minutes = SplitTime[1];
                        String FullTime = hours + ":" + minutes + "pm";
                        ((ViewHolder) holder).ReceiverTime.setText(FullTime);
                    } else if (time > 12) {
                        time = time - 12;
                        String hours = SplitTime[0];
                        String minutes = SplitTime[1];
                        String FullTime = time + ":" + minutes + "pm";
                        ((ViewHolder) holder).ReceiverTime.setText(FullTime);
                    } else {
                        String hours = SplitTime[0];
                        String minutes = SplitTime[1];
                        String FullTime = hours + ":" + minutes + "am";
                        ((ViewHolder) holder).ReceiverTime.setText(FullTime);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }


//        if (!PreviousDate.equalsIgnoreCase("")) {
//            if (!Date.trim().equalsIgnoreCase(PreviousDate))
//            {
//                PreviousDate = Date;
//                ((ViewHolder) holder).TimeStamp.setVisibility(View.VISIBLE);
//                if (Date.equalsIgnoreCase(getDate(System.currentTimeMillis())))
//                {
//                    ((ViewHolder) holder).TimeStamp.setText("today");
//                }
//                else if (Date.equalsIgnoreCase(getDateYesterday(System.currentTimeMillis())))
//                {
//                    ((ViewHolder) holder).TimeStamp.setText("yesterday");
//                }
//                else
//                {
//                    ((ViewHolder) holder).TimeStamp.setText(Date);
//                }
//            } else {
//                ((ViewHolder) holder).TimeStamp.setVisibility(View.GONE);
//            }
//        }
//        else
//        {
//            PreviousDate = Date;
//            ((ViewHolder) holder).TimeStamp.setVisibility(View.VISIBLE);
//            if (Date.equalsIgnoreCase(getDate(System.currentTimeMillis())))
//            {
//                ((ViewHolder) holder).TimeStamp.setText("today");
//            }
//            else if (Date.equalsIgnoreCase(getDateYesterday(System.currentTimeMillis())))
//            {
//                ((ViewHolder) holder).TimeStamp.setText("yesterday");
//            }
//            else
//            {
//                ((ViewHolder) holder).TimeStamp.setText(Date);
//            }
//
//        }
        if (chatList.get(position).isSame()) {
            ((ViewHolder) holder).TimeStamp.setVisibility(View.GONE);
        } else {
            ((ViewHolder) holder).TimeStamp.setVisibility(View.VISIBLE);
            if (chatList.get(position).getPreviousDate().equalsIgnoreCase(getDate(System.currentTimeMillis()))) {
                ((ViewHolder) holder).TimeStamp.setText("Today");
            } else if (chatList.get(position).getPreviousDate().equalsIgnoreCase(getDateYesterday(System.currentTimeMillis()))) {
                ((ViewHolder) holder).TimeStamp.setText("Yesterday");
            } else {
                ((ViewHolder) holder).TimeStamp.setText(chatList.get(position).getPreviousDate());
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ImgSenderImage;
        ImageView ImgReceiverImage;
        TextView TxtSenderText, TxtReceiverText;
        TextView SenderTime, ReceiverTime;
        RelativeLayout you_layout, me_layout;
        TextView TimeStamp;

        public ViewHolder(View myView) {
            super(myView);
            ImgSenderImage = myView.findViewById(R.id.you_img_view);
            ImgReceiverImage = myView.findViewById(R.id.me_img_view);
            TxtSenderText = myView.findViewById(R.id.me_msg);
            TxtReceiverText = myView.findViewById(R.id.you_msg);
            SenderTime = myView.findViewById(R.id.TxtTimeSender);
            ReceiverTime = myView.findViewById(R.id.TxtTimeReceiver);
            you_layout = myView.findViewById(R.id.you_layout);
            me_layout = myView.findViewById(R.id.me_layout);
            TimeStamp = myView.findViewById(R.id.TimeStamp);
        }
    }

    private String getDate(long timeStamp) {

        DateFormat objFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar objCalendar =
                Calendar.getInstance();
        objCalendar.setTimeInMillis(timeStamp);
        String result = objFormatter.format(objCalendar.getTime());
        objCalendar.clear();
        return result;
    }

    private String getDateYesterday(long timeStamp) {

        DateFormat objFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar objCalendar =
                Calendar.getInstance();
        objCalendar.setTimeInMillis(timeStamp);//edit
        objCalendar.add(Calendar.DATE, -1);
        String result = objFormatter.format(objCalendar.getTime());
        objCalendar.clear();
        return result;
    }

    private static Date gmttoLocalDate(Date date) {

        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }
}