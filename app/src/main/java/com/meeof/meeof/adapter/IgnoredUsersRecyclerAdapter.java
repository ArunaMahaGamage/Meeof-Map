package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.meeof.meeof.R;
import com.meeof.meeof.activity.ManageFriendsActivity;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.ignored_members_dto.Ignored;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

/**
 * Created by ransika on 7/11/2017.
 */
public class IgnoredUsersRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = UserFriendsRecyclerAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Ignored> listItem, filterList;
    private OnItemClicked onClick;
    private OnDeleteClicked onDeleteClick;
    private boolean isShowDeleteButtons;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClicked {
        void onItemClick(int position, Ignored item);
    }

    public interface OnDeleteClicked {
        void onDeleteItemClick(int position, Ignored item);
    }

    public IgnoredUsersRecyclerAdapter(Context context, List<Ignored> listItems) {
        this.listItem = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<Ignored>();
//        this.filterList.addAll(this.listItem); //to show all items at begining
        viewBinderHelper.setOpenOnlyOne(true);//Only open delete at one item
    }

    public void showdeleteButtons(){
        isShowDeleteButtons=true;
        notifyDataSetChanged();
    }

    public void hidedeleteButtons(){
        isShowDeleteButtons=false;
        notifyDataSetChanged();
    }

    public void updateList(List<Ignored> listItems){
        this.listItem = listItems;
        notifyDataSetChanged();
    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final ImageView friend_imageView;
        private final TextView friend_name_textView;
        private final TextView friend_status_textView;
        private final ImageView friend_status_imageView;
        private final SwipeRevealLayout swipeLayoutSR;
        private final FrameLayout deleteLayoutFl;
        private final LinearLayout swipeRevealLl;
        private final LinearLayout deleteMultiIvLl;



        public MyViewHolderFriend(View view) {
            super(view);
            friend_imageView = (ImageView) view.findViewById(R.id.friendAvatarIv);
            friend_name_textView = (TextView) view.findViewById(R.id.friendNameTv);
            friend_status_textView = (TextView) view.findViewById(R.id.friend_status_textView);
            friend_status_imageView = (ImageView) view.findViewById(R.id.friend_status_imageView);
            swipeLayoutSR = (SwipeRevealLayout) view.findViewById(R.id.swipeLayoutSR);
            deleteLayoutFl = (FrameLayout)view.findViewById(R.id.deleteLayoutFl);
            swipeRevealLl = (LinearLayout)view.findViewById(R.id.swipeRevealLl);
            deleteMultiIvLl = (LinearLayout)view.findViewById(R.id.deleteMultiIvLl);


            //1->left
            //2->right
//            swipeLayoutSR.setDragEdge(2);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Helper.delay(3000, new Helper.DelayCallBack() {
//                        @Override
//                        public void postDelay() {
//                            swipeLayoutSR.open(true);
//                            swipeLayoutSR.setLockDrag(true);
//                        }
//                    });
//                }
//            });

        }


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_friend, parent, false);

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ignored_deletable, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Ignored item = listItem.get(position);

        final MyViewHolderFriend myViewHolderParcel = (MyViewHolderFriend) holder;

        myViewHolderParcel.friend_name_textView.setText(item.getFirst_name() + " " + (item.getLast_name() != null ? item.getLast_name() : ""));
        myViewHolderParcel.friend_status_textView.setText(item.getStatus());
//        myViewHolderParcel.friend_imageView.setText(item.getStatus());

        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        Picasso.with(mContext.getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolderParcel.friend_imageView);

        Drawable statusDrawable;
        if (item.getStatus().equals("active")) {
            if (item.getFriend_status().equals("friend")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);
                //should hide in manage friends and show in find friends
                myViewHolderParcel.friend_status_textView.setText("Already friends on Meeof");
                if(mContext instanceof ManageFriendsActivity){
                    myViewHolderParcel.friend_status_textView.setVisibility(View.INVISIBLE);
                }

            } else if (item.getFriend_status().equals("pending")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
                myViewHolderParcel.friend_status_textView.setText("Pending Request");
            } else if (item.getFriend_status().equals("none")) {
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_addfriend);
                myViewHolderParcel.friend_status_textView.setText("User on Meeof");
            } else {
                Log.w(TAG, "Inside else green");
                statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
                myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
            }
        } else if (item.getStatus().equals("dummy")) {
            Log.w(TAG, "Inside elif dummy green");
            statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
        } else {
            Log.w(TAG, "Inside else dummy green");
            statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_invite);
            myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
        }
        myViewHolderParcel.friend_status_imageView.setImageDrawable(statusDrawable);

//                    myViewHolderParcel.friend_imageView.setText(item.getArea());
//                    myViewHolderParcel.friend_status_imageView.setText(item.getLocalGovernment());
        myViewHolderParcel.friend_status_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

//                Drawable statusDrawable;
//                if (item.getStatus().equals("active")) {
//                    if (item.getFriend_status().equals("friend")) {
//                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_friend);
//                        myViewHolderParcel.friend_status_textView.setText("Already friends on Meeof");
//                    } else if (item.getFriend_status().equals("pending")) {
//                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
//                        myViewHolderParcel.friend_status_textView.setText("Pending Request");
//                    } else if (item.getFriend_status().equals("none")) {
//                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
//                        myViewHolderParcel.friend_status_textView.setText("User on Meeof");
//                    } else {
//                        statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
//                        myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
//                    }
//                } else if (item.getStatus().equals("dummy")) {
//                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
//                    myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
//                } else {
//                    statusDrawable = ContextCompat.getDrawable(mContext, R.drawable.ico_status_pending);
//                    myViewHolderParcel.friend_status_textView.setText("Invite Friend to Join Meeof");
//                }
//                myViewHolderParcel.friend_status_imageView.setImageDrawable(statusDrawable);

                onClick.onItemClick(position, listItem.get(position));

            }
        });

        viewBinderHelper.bind(myViewHolderParcel.swipeLayoutSR, item.getId()+"");


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

    public void filter(final String text) {
        Log.d(TAG, "SearchText: " + text + "\ninside filter");
        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "filterList size: " + filterList.size() + "\ninside filter");
                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    Log.d(TAG, "TextUtils.isEmpty(text) filterList size: " + filterList.size() + "\ninside filter");
                    filterList.addAll(listItem);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Ignored item : listItem) {
                        if (item.getFirst_name().toLowerCase().startsWith(text.toLowerCase())) {

                            Log.d(TAG, "for (FriendListItem item : listItem) filter : " + item.toString() + "\ninside filter");
                            filterList.add(item);

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

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public void setOnDeleteClick(OnDeleteClicked onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public List<Ignored> getFilteredList() {
        return filterList;
    }

}

