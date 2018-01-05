package com.meeof.meeof.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.model.notification_dto.NotificationResponse;
import com.meeof.meeof.model.notification_dto.NotificationsData;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteOtherNotificationsWebJob;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ransikadesilva on 12/13/17.
 */

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = NotificationRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    public List<NotificationsData> listItem, filterList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private OnItemClicked onClick;
    private OnDeleteClicked onDeleteClick;
    private boolean isShowDeleteButtons;
    public JobManager jobManager;
    private String accessToken;
    private SharedPreferences sharedPreferences;
    public interface OnItemClicked {
        void onItemClick(int position, NotificationResponse item, NotificationRecyclerAdapter.MyViewHolderFriend myViewHolderParcel);
    }
    public interface OnDeleteClicked {
        void onDeleteItemClick(int position, NotificationsData item);
    }
    public NotificationRecyclerAdapter(Context context, List<NotificationsData> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<NotificationsData>();
        this.filterList.addAll(this.listItem); //to show all items at begining
    }
    public void showdeleteButtons(){
        isShowDeleteButtons=true;
        notifyDataSetChanged();
    }

    public void hidedeleteButtons(){
        isShowDeleteButtons=false;
        notifyDataSetChanged();
    }

    public void updateList(List<NotificationsData> listItems){
        this.listItem = listItems;
        notifyDataSetChanged();
    }
    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final ImageView friend_imageView;
        private final TextView friend_name_textView;
        private final TextView friend_status_textView;
        private final SwipeRevealLayout swipeLayoutSR;
        private final FrameLayout deleteLayoutFl;
        private final LinearLayout swipeRevealLl;
        private final LinearLayout deleteMultiIvLl;

        public MyViewHolderFriend(View view) {
            super(view);
            friend_imageView = (ImageView) view.findViewById(R.id.friendAvatarIv);
            friend_name_textView = (TextView) view.findViewById(R.id.friendNameTv);
            friend_status_textView = (TextView) view.findViewById(R.id.friend_status_textView);
            swipeLayoutSR = (SwipeRevealLayout) view.findViewById(R.id.swipeLayoutSR);
            deleteLayoutFl = (FrameLayout)view.findViewById(R.id.deleteLayoutFl);
            swipeRevealLl = (LinearLayout)view.findViewById(R.id.swipeRevealLl);
            deleteMultiIvLl = (LinearLayout)view.findViewById(R.id.deleteMultiIvLl);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, parent, false);

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_friend_deletable, parent, false);

        return new NotificationRecyclerAdapter.MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NotificationsData item = listItem.get(position);

        final NotificationRecyclerAdapter.MyViewHolderFriend myViewHolderParcel = (NotificationRecyclerAdapter.MyViewHolderFriend) holder;
        jobManager = MeeofApplication.getInstance().getJobManager();
        sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        //myViewHolderParcel.friend_name_textView.setText(item.getFirst_name());

        if (item.getContent().equals("comment_in_meeof")) {
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " commented on " + item.getTitle() + " event at "+item.getPlaceName()+" with "+item.getAttendeeCount()+" others.");
        } else if (item.getContent().equals("likes_in_meeof")) {
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name()+" with "+item.getAttendeeCount()+" others." + " liked " + item.getTitle() + " event at "+item.getPlaceName());
        } else if (item.getContent().equals("new_event")) {
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name()+" created "+item.getTitle()+" event at "+item.getPlaceName());
        }else if(item.getContent().equals("user_going")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " going " + item.getTitle() + " event at "+item.getPlaceName()+" with "+item.getAttendeeCount()+" others.");
        }else if(item.getContent().equals("meeof_changed")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " changed" + item.getTitle() + " event at "+item.getPlaceName()+" with "+item.getAttendeeCount()+" others.");
        }else if(item.getContent().equals("meeof_cancelled")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " cancelled " + item.getTitle() + " event.");
        }else if(item.getContent().equals("friend_sent_request")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " wants to be friends with you on Meeof.");
        }else if(item.getContent().equals("friend_has_been_accepted")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " accepted your friend request.");
        }else if(item.getContent().equals("meeof_sent_request")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " invited to join "+"' "+item.getTitle()+" '");
        }else if(item.getContent().equals("comment_in_stream")){
            myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " comment on "+"' "+item.getTitle()+" '");
        }
        String time=getTimeDifferences(item.getCreated_at());
        Log.i(TAG,"TimeA "+time);
        myViewHolderParcel.friend_status_textView.setText(time);

        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderParcel.friend_imageView);

        viewBinderHelper.bind(myViewHolderParcel.swipeLayoutSR, item.getZone_id() + "");



        myViewHolderParcel.deleteLayoutFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"isShowDeleteButtons :"+ item.getNotifyid());
                showConfirmationAlert(mContext,item.getNotifyid());
            }
        });

        if(!isShowDeleteButtons){
            //Log.d(TAG,"isShowDeleteButtons :"+ isShowDeleteButtons);
            viewBinderHelper.setOpenOnlyOne(true);
            myViewHolderParcel.swipeRevealLl.setVisibility(View.VISIBLE);
            myViewHolderParcel.deleteMultiIvLl.setVisibility(View.GONE);
            myViewHolderParcel.swipeLayoutSR.setDragEdge(2);
            myViewHolderParcel.swipeLayoutSR.close(true);
            myViewHolderParcel.swipeLayoutSR.setLockDrag(false);
        }else{
           // Log.d(TAG,"isShowDeleteButtons :"+ isShowDeleteButtons);
            viewBinderHelper.setOpenOnlyOne(false);
            myViewHolderParcel.swipeRevealLl.setVisibility(View.GONE);
            myViewHolderParcel.deleteMultiIvLl.setVisibility(View.VISIBLE);
            myViewHolderParcel.swipeLayoutSR.setDragEdge(1);
            myViewHolderParcel.swipeLayoutSR.open(true);
            myViewHolderParcel.swipeLayoutSR.setLockDrag(true);
        }
    }
    public void showConfirmationAlert(Context c, final int id) {
        try {
            final Dialog dialog = new Dialog(c);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_discard_delete_notification_confirmation);
            dialog.setCancelable(false);


            Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            Button confirmBtn = (Button) dialog.findViewById(R.id.confirmBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   dialog.cancel();
                    //Delete
                    deleteEvent(id);
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
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

        return (null != filterList ? filterList.size() : listItem.size());
//        return listItem.size();
    }
    public void setOnClick(OnItemClicked onClick) {
        this.onClick = (OnItemClicked) onClick;
    }

    public void setOnDeleteClick(OnDeleteClicked onDeleteClick) {
        this.onDeleteClick = (OnDeleteClicked) onDeleteClick;
    }
    public List<NotificationsData> getFilteredList() {
        return filterList;
    }


    //Get time defference
    public String getTimeDifferences(String crateAt) {
        String differences = "", dateStop;
        Calendar c = Calendar.getInstance();
        long diffSeconds = 0, diffMinutes = 0, diffHours = 0, diffDays = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateStop = format.format(c.getTime());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(crateAt);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            if (diffDays != 0) {
                differences = diffDays + " days ago";
            } else if (diffHours != 0) {
                differences = diffHours + " hours ago";
            } else if (diffMinutes != 0) {
                differences = diffMinutes + " minutes ago";
            } else if (diffSeconds != 0) {
                differences = diffSeconds + " seconds ago";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return differences;
    }


    private void deleteEvent(int eventid) {
        if (eventid != 0) {
            if (isNetworkAvailable()) {
                Log.i(TAG,"AccessTokenDelete "+accessToken+ "  ID "+eventid);
                jobManager.addJobInBackground(new DeleteOtherNotificationsWebJob(accessToken, eventid));
                //startProgressBar();
            } else {
                //BaseActivityPopup.showSnackbar(isShowDeleteButtons, mContext.getString(R.string.no_internet), Constant.ERROR);
            }
        } else {
            //BaseActivityPopup.showSnackbar(isShowDeleteButtons, mContext.getString(R.string.unable_to_delete_event), Constant.ERROR);
        }
    }
    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}