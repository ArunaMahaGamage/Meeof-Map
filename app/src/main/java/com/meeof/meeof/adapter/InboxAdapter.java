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

import com.daimajia.swipe.SwipeLayout;
import com.meeof.meeof.R;
import com.meeof.meeof.interfaces.SetOnInboxItemClicked;
import com.meeof.meeof.model.InboxDataModel;
import com.meeof.meeof.util.Constant;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Dharmesh on 11/24/2017.
 */

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private List<InboxDataModel> arrayList;
    SetOnInboxItemClicked setOnInboxItemClicked;
    private int MyId;
    public boolean SetDustbinEnable;
    public InboxAdapter(Context context, List<InboxDataModel> arrayList, SetOnInboxItemClicked setOnInboxItemClicked) {
        super();
        mContext = context;
        this.arrayList = arrayList;
        this.setOnInboxItemClicked = setOnInboxItemClicked;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, Context.MODE_PRIVATE);
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.inbox_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final InboxDataModel inboxDataModel = arrayList.get(position);
        ((ViewHolder) holder).LastMessage.setText(arrayList.get(position).getMessage());
        ((ViewHolder) holder).RelativeInboxItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnInboxItemClicked.OnInboxClicked(inboxDataModel);

            }
        });


        String[] StTime = inboxDataModel.getCreated_at().split(" ");
        if (StTime.length > 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            try {
                Date date=sdf.parse(inboxDataModel.getCreated_at());
                Date GmtTolocalDate=gmttoLocalDate(date);
                String DecodedDate=sdf.format(GmtTolocalDate);
                String SplitDate[]=DecodedDate.split(" ");
                String SplitTime[]=SplitDate[1].split(":");
                int time=Integer.parseInt(SplitTime[0]);

                if (time==12)
                {

                    String hours=SplitTime[0];
                    String minutes=SplitTime[1];
                    String FullTime=hours + ":" + minutes + "pm";
                    ((ViewHolder) holder).TxtTime.setText(FullTime);
                }
                else if (time>12)
                {
                    time=time-12;
                    String hours=SplitTime[0];
                    String minutes=SplitTime[1];
                    String FullTime=time + ":" + minutes + "pm";
                    ((ViewHolder) holder).TxtTime.setText(FullTime);
                }
                else
                {
                    String hours=SplitTime[0];
                    String minutes=SplitTime[1];
                    String FullTime=hours + ":" + minutes + "am";
                    ((ViewHolder) holder).TxtTime.setText(FullTime);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

//            ((ViewHolder) holder).TxtTime.setText(StTime[1]);
        }

        String Name = "";
        for (int i = 0; i < inboxDataModel.getMembers().size(); i++) {
            if (Name == null)
            {
                Name = "";
            }
            if (Name.isEmpty())
            {
                if (inboxDataModel.getMembers().get(i).getUser_id()!=MyId) {
                    Name = inboxDataModel.getMembers().get(i).getFirst_name();
                }
            } else {
                if (inboxDataModel.getMembers().get(i).getUser_id()!=MyId) {
                    Name = Name + "," + inboxDataModel.getMembers().get(i).getFirst_name();
                }
            }
        }
        if (Name == null) {
            Name = "Users";
        }
        String BaseUrl = "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/";
        if (inboxDataModel.getMembers() != null) {
            if (inboxDataModel.getMembers().size() > 4) {
                ((ViewHolder) holder).ImageInboxSingleImage.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser1.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser2.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser3.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser4.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(0).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser1);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(1).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser2);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(2).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser3);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(3).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser4);
            } else if (inboxDataModel.getMembers().size() > 3) {
                ((ViewHolder) holder).ImageInboxSingleImage.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser1.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser2.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser3.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser4.setVisibility(View.GONE);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(0).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser1);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(1).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser2);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(2).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser3);
            } else if (inboxDataModel.getMembers().size() > 2) {
                ((ViewHolder) holder).ImageInboxSingleImage.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser1.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser2.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser3.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser4.setVisibility(View.GONE);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(0).getProfilephoto())
                        .error(R.drawable.img_avatar_00)
                        .placeholder(R.drawable.img_avatar_00)
                        .resize(100, 100)
                        .into(((ViewHolder) holder).ImgUser1);
                Picasso.with(mContext)
                        .load(BaseUrl + inboxDataModel.getMembers().get(1).getProfilephoto())
                        .resize(100, 100)
                        .placeholder(R.drawable.img_avatar_00)
                        .error(R.drawable.img_avatar_00)
                        .into(((ViewHolder) holder).ImgUser2);
            } else {
                ((ViewHolder) holder).ImageInboxSingleImage.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ImgUser1.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser2.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser3.setVisibility(View.GONE);
                ((ViewHolder) holder).ImgUser4.setVisibility(View.GONE);
                if (inboxDataModel.getMembers().size() >= 1) {
                    Picasso.with(mContext)
                            .load(BaseUrl + inboxDataModel.getMembers().get(0).getProfilephoto())
                            .resize(100, 100)
                            .placeholder(R.drawable.img_avatar_00)
                            .error(R.drawable.img_avatar_00)
                            .into(((ViewHolder) holder).ImageInboxSingleImage);
                }
            }
        }
        if (arrayList.get(position).getNew()!=0)
        {
            ((ViewHolder)holder).TxtUnreadMessages.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).TxtUnreadMessages.setText(""+arrayList.get(position).getNew());
        }
        else
        {
            ((ViewHolder)holder).TxtUnreadMessages.setVisibility(View.GONE);
        }
        ((ViewHolder) holder).TxtUsersNameInbox.setText("" + Name);

        if (SetDustbinEnable)
        {
            ((ViewHolder)holder).ImgDeleteDustbin.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).swipeLayout.setRightSwipeEnabled(true);
        }
        else
        {
            ((ViewHolder)holder).ImgDeleteDustbin.setVisibility(View.GONE);
            ((ViewHolder)holder).swipeLayout.setRightSwipeEnabled(false);
        }

        ((ViewHolder)holder).RelativeInboxDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnInboxItemClicked.OnInboxDeleteClicked(inboxDataModel.getGroup_id());


            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ImageInboxSingleImage;
        TextView TxtUsersNameInbox, LastMessage, TxtTime,TxtUnreadMessages;
        RelativeLayout RelativeInboxItem;
        ImageView ImgUser1, ImgUser2, ImgUser3, ImgUser4;
        SwipeLayout swipeLayout;
        RelativeLayout RelativeInboxDelete;
        ImageView ImgDeleteDustbin;
        public ViewHolder(View myView) {
            super(myView);
            ImageInboxSingleImage = myView.findViewById(R.id.ImageInboxSingleImage);
            ImgUser1 = myView.findViewById(R.id.UserImage1);
            ImgUser2 = myView.findViewById(R.id.UserImage2);
            ImgUser3 = myView.findViewById(R.id.UserImage3);
            ImgUser4 = myView.findViewById(R.id.UserImage4);
            swipeLayout=myView.findViewById(R.id.SwipeLayoutInbox);
            ImgDeleteDustbin=myView.findViewById(R.id.ImgDeleteDustbin);
            RelativeInboxDelete=myView.findViewById(R.id.RelativeInboxDelete);
            TxtUsersNameInbox = myView.findViewById(R.id.TxtUsersNameInbox);
            TxtUnreadMessages=myView.findViewById(R.id.TxtUnreadMessage);
            LastMessage = myView.findViewById(R.id.LastMessage);
            RelativeInboxItem = myView.findViewById(R.id.RelativeInboxItem);
            TxtTime = myView.findViewById(R.id.TxtTime);

        }
    }

    public void OnDeletePressed(String text)
    {
        if (text.equalsIgnoreCase("edit"))
        {
            SetDustbinEnable=false;
            notifyDataSetChanged();
        }
        else
        {
            SetDustbinEnable=true;
            notifyDataSetChanged();
        }
    }

    private static Date gmttoLocalDate(Date date) {

        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }
}