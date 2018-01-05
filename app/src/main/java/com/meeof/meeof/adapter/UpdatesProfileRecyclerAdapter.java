package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.AddUpdateActivity;
import com.meeof.meeof.activity.UpdateSocialActivity;
import com.meeof.meeof.model.UpdatesMainModel;
import com.meeof.meeof.model.array_updatesModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.updates_all_dto.Array_updates;
import com.meeof.meeof.model.updates_all_dto.Data;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.PostDeleteUpdateWebJob;
import com.meeof.meeof.webjob.PostLikeUpdateWebJob;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ransikadesilva on 1/2/18.
 */

public class UpdatesProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = UpdatesRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    public UpdatesMainModel updateData;
    public List<array_updatesModel> updatesList;
    public JobManager jobManager;
    private String accessToken;
    protected SharedPreferences sharedPreferences;
    //private int myId;


//    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public UpdatesProfileRecyclerAdapter(Context context, UpdatesMainModel data) {
        updateData = data;
        this.updatesList = updateData.getData().getArray_updates();
        this.mContext = context;
        //this.myId=data.getMyID();
        Log.d(TAG, "UpdatesAdapter " + updatesList.size());


    }

    public void setData(Context context, UpdatesMainModel data) {
        updateData = data;
        this.updatesList = updateData.getData().getArray_updates();
        Log.d(TAG, "UpdatesAdapter setting " + updatesList.size());
    }

    public void updateLikeCount(Context context, int id, int count, boolean isLike) {
        Log.d(TAG, "UpdatesAdapter setting " + updatesList.size());
        for (array_updatesModel updateItem : updatesList) {
            if (updateItem.getUpdateid() == id) {
                updateItem.setCountLikes(count);
                if (isLike) {
                    updateData.getData().getWhatDoILike().add(id);
                } else {
                    updateData.getData().getWhatDoILike().remove(id);
                }
                break;
            }
        }
        notifyDataSetChanged();
    }

    public class MyViewHolderUpdates extends RecyclerView.ViewHolder {
        private final TextView userNameTv;
        private final TextView locationTv;
        private final TextView tagsTv;
        private final TextView commentTv;
        public final TextView likeTv;
        private final TextView photoCountTv;
        private final ImageView imageIv;
        private final TextView eventTimeTv;
        private final TextView distanceTv;
        private final TextView updateDescTv;
        private final LinearLayout imageLayout1;
        private final LinearLayout imageLayout2;
        private final LinearLayout imageLayout3;
        private final ImageView image11Iv;
        private final ImageView image21Iv;
        private final ImageView image22Iv;
        private final ImageView image31Iv;
        private final ImageView image32Iv;
        private final ImageView image33Iv;
        private final LinearLayout commentsBtn;
        private final LinearLayout likeBtn;
        public final ImageView likedBtn;
        public final ImageView unLiked;
        private final LinearLayout popUpMenuLl;


        public MyViewHolderUpdates(View view) {
            super(view);
            userNameTv = (TextView) view.findViewById(R.id.userNameTv);
            locationTv = (TextView) view.findViewById(R.id.userLocationTv);
            tagsTv = (TextView) view.findViewById(R.id.tagsTv);
            commentTv = (TextView) view.findViewById(R.id.commentTv);
            likeTv = (TextView) view.findViewById(R.id.likeCountTv);
            imageIv = (ImageView) view.findViewById(R.id.imageIv);
            photoCountTv = (TextView) view.findViewById(R.id.photoCountTv);
            eventTimeTv = (TextView) view.findViewById(R.id.eventTimeTv);
            distanceTv = (TextView) view.findViewById(R.id.distanceTv);
            updateDescTv = (TextView) view.findViewById(R.id.updateDescTv);

            imageLayout1 = (LinearLayout) view.findViewById(R.id.imagesLayout1);
            imageLayout2 = (LinearLayout) view.findViewById(R.id.imagesLayout2);
            imageLayout3 = (LinearLayout) view.findViewById(R.id.imagesLayout3);

            image11Iv = (ImageView) view.findViewById(R.id.image11Iv);
            image21Iv = (ImageView) view.findViewById(R.id.image21Iv);
            image22Iv = (ImageView) view.findViewById(R.id.image22Iv);
            image31Iv = (ImageView) view.findViewById(R.id.image31Iv);
            image32Iv = (ImageView) view.findViewById(R.id.image32Iv);
            image33Iv = (ImageView) view.findViewById(R.id.image33Iv);


            likedBtn = (ImageView) view.findViewById(R.id.likedIv);
            unLiked = (ImageView) view.findViewById(R.id.unlikedIv);


            commentsBtn = (LinearLayout) view.findViewById(R.id.commentsTv);

            likeBtn = (LinearLayout) view.findViewById(R.id.likeBtnLL);

            popUpMenuLl = (LinearLayout) view.findViewById(R.id.popUpMenuLl);


//            swipeLayoutSR = (SwipeRevealLayout) view.findViewById(R.id.swipeLayoutSR);

            jobManager = MeeofApplication.getInstance().getJobManager();
            sharedPreferences = view.getContext().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
            accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update, parent, false);

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_friend_deletable, parent, false);

        return new UpdatesProfileRecyclerAdapter.MyViewHolderUpdates(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final array_updatesModel updateItem = updatesList.get(position);
        final UpdatesProfileRecyclerAdapter.MyViewHolderUpdates myViewHolder = (UpdatesProfileRecyclerAdapter.MyViewHolderUpdates) holder;

        Log.d(TAG, "update id: " + updateItem.getChannel_id());

        myViewHolder.userNameTv.setText(updateItem.getFirst_name());
        myViewHolder.locationTv.setText(updateItem.getLocation());
        myViewHolder.commentTv.setText(updateItem.getCountComments() + "");
        myViewHolder.likeTv.setText(updateItem.getCountLikes() + "");

        Log.d(TAG, "TAGS: " + updateItem.getTags());

        Log.d(TAG, "Update details: " + updateItem.toString());

        String desc = updateItem.getTitle() + " at " + updateItem.getPlaceName();
        String friends = "";

        try {

            JSONArray jsonArray = new JSONArray(updateItem.getFriends());
            if (jsonArray.length() == 1) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                friends = jsonObject.getString("first_name");
            } else if (jsonArray.length() > 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String friend = jsonObject.getString("first_name");
                    if (friends.equals("")) {
                        friends = friend;
                    } else {
                        if (i == (jsonArray.length() - 1)) {
                            friends += " & " + friend;
                        } else {
                            friends += "," + friend;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "TAGS 1: ");
        }

        myViewHolder.updateDescTv.setText(desc + (friends.equals("") ? "" : " with " + friends));


        try {
            String tags = "";
            JSONArray jsonArray = new JSONArray(updateItem.getTags());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String tag = jsonObject.getString("hashtag");
                if (tags.equals("")) {
                    tags = "#" + tag;
                } else {
                    tags += " #" + tag;
                }
                Log.d(TAG, "TAGS 2: " + tag);
            }
            myViewHolder.tagsTv.setText(tags);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "TAGS 1: ");
            myViewHolder.tagsTv.setText("");
        }

        myViewHolder.eventTimeTv.setText(updateItem.getCreated_at());

        try {
            DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            parseFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            if (updateItem.getCreated_at() != null) {
                Date date = null;

                date = parseFormat.parse(updateItem.getCreated_at());

                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(date);
                myViewHolder.eventTimeTv.setText(ago);
                Log.d(TAG, "prettyTime: " + ago);
            } else {
                myViewHolder.eventTimeTv.setText("Not Available");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);
        String matrix = profileResponse.getData().getMatrix();
        Log.d(TAG, "MATRIX: " + matrix);
        boolean isKm = false;
        if (matrix.equals("0")) {
            isKm = true;
        }
        Double d = updateItem.getDistance();
        int distance = d.intValue();
        Log.d(TAG, "distace " + distance);
        if (isKm) {
            myViewHolder.distanceTv.setText(distance + " KM away");
        } else {
            myViewHolder.distanceTv.setText(distance + " miles away");
        }


        Log.d(TAG, "Likes count: " + updateItem.getCountLikes());


        String imgeUrl1 = updateItem.getProfilephoto() == null || updateItem.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + updateItem.getProfilephoto();


        Picasso.with(mContext.getApplicationContext())
                .load(imgeUrl1)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(mContext, R.drawable.ico_profile_edit_avatar))
                .into(myViewHolder.imageIv);


//        if(myId==updateItem.getUser_id()){
//            myViewHolder.likedBtn.setVisibility(View.VISIBLE);
//            myViewHolder.unLiked.setVisibility(View.GONE);
//        }else{
//            myViewHolder.likedBtn.setVisibility(View.GONE);
//            myViewHolder.unLiked.setVisibility(View.VISIBLE);
//        }

        for (int i = 0; i < updateData.getData().getWhatDoILike().size(); i++) {
            if (updateData.getData().getWhatDoILike().contains(updateItem.getUpdateid())) {
                myViewHolder.likedBtn.setVisibility(View.VISIBLE);
                myViewHolder.unLiked.setVisibility(View.GONE);
                break;
            } else {
                myViewHolder.likedBtn.setVisibility(View.GONE);
                myViewHolder.unLiked.setVisibility(View.VISIBLE);
            }
        }

        String imgeUrl31, imgeUrl32, imgeUrl33, imgeUrl21, imgeUrl22, imgeUrl11;
        imgeUrl31 = imgeUrl32 = imgeUrl33 = imgeUrl21 = imgeUrl22 = imgeUrl11 = "https://www.allcloud.io/wp-content/uploads/2017/01/default-thumbnail.jpg";


        myViewHolder.image11Iv.setImageDrawable(null);
        myViewHolder.image22Iv.setImageDrawable(null);
        myViewHolder.image21Iv.setImageDrawable(null);
        myViewHolder.image31Iv.setImageDrawable(null);
        myViewHolder.image32Iv.setImageDrawable(null);
        myViewHolder.image33Iv.setImageDrawable(null);
        myViewHolder.imageLayout3.setVisibility(View.GONE);
        myViewHolder.imageLayout2.setVisibility(View.GONE);
        myViewHolder.imageLayout1.setVisibility(View.GONE);


        if (updateItem.getPhotocount() != 0) {
            try {
                Log.d(TAG, "updates items: " + updateItem);
                Log.d(TAG, "updates items images: " + updateItem.getPhoto_arrays().toString());
                if (updateItem.getPhotocount() == 1) {
                    imgeUrl11 = updateItem.getPhoto_arrays().get(0) == null || updateItem.getPhoto_arrays().get(0).trim().length() <= 0 ? "" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/small/" + updateItem.getPhoto_arrays().get(0);
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl11)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.default_thumbnail)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.default_thumbnail))
                            .into(myViewHolder.image11Iv);
                    myViewHolder.imageLayout3.setVisibility(View.GONE);
                    myViewHolder.imageLayout2.setVisibility(View.GONE);
                    myViewHolder.imageLayout1.setVisibility(View.VISIBLE);
                } else if (updateItem.getPhotocount() == 2) {
                    imgeUrl21 = updateItem.getPhoto_arrays().get(0) == null || updateItem.getPhoto_arrays().get(0).trim().length() <= 0 ? "" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/small/" + updateItem.getPhoto_arrays().get(0);
                    imgeUrl22 = updateItem.getPhoto_arrays().get(1) == null || updateItem.getPhoto_arrays().get(1).trim().length() <= 0 ? "" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/small/" + updateItem.getPhoto_arrays().get(1);
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl21)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.default_thumbnail)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.default_thumbnail))
                            .into(myViewHolder.image21Iv);
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl22)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.default_thumbnail)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.default_thumbnail))
                            .into(myViewHolder.image22Iv);
                    myViewHolder.imageLayout3.setVisibility(View.GONE);
                    myViewHolder.imageLayout2.setVisibility(View.VISIBLE);
                    myViewHolder.imageLayout1.setVisibility(View.GONE);
                } else if (updateItem.getPhotocount() > 2) {
                    imgeUrl31 = updateItem.getPhoto_arrays().get(0) == null || updateItem.getPhoto_arrays().get(0).trim().length() <= 0 ? "" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/small/" + updateItem.getPhoto_arrays().get(0);
                    imgeUrl32 = updateItem.getPhoto_arrays().get(1) == null || updateItem.getPhoto_arrays().get(1).trim().length() <= 0 ? "" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/small/" + updateItem.getPhoto_arrays().get(1);
                    imgeUrl33 = updateItem.getPhoto_arrays().get(2) == null || updateItem.getPhoto_arrays().get(2).trim().length() <= 0 ? "" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/small/" + updateItem.getPhoto_arrays().get(2);
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl31)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.default_thumbnail)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.default_thumbnail))
                            .into(myViewHolder.image31Iv);
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl32)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.default_thumbnail)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.default_thumbnail))
                            .into(myViewHolder.image32Iv);
                    Picasso.with(mContext.getApplicationContext())
                            .load(imgeUrl33)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.default_thumbnail)
                            .error(ContextCompat.getDrawable(mContext, R.drawable.default_thumbnail))
                            .into(myViewHolder.image33Iv);
                    myViewHolder.imageLayout3.setVisibility(View.VISIBLE);
                    myViewHolder.imageLayout2.setVisibility(View.GONE);
                    myViewHolder.imageLayout1.setVisibility(View.GONE);

                    if (updateItem.getPhoto_arrays().size() > 3) {
                        myViewHolder.photoCountTv.setText("+" + (updateItem.getPhoto_arrays().size() - 3));
                        myViewHolder.photoCountTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    myViewHolder.imageLayout3.setVisibility(View.GONE);
                    myViewHolder.imageLayout2.setVisibility(View.GONE);
                    myViewHolder.imageLayout1.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception: " + e.getClass().getCanonicalName());
            }
        }

        myViewHolder.popUpMenuLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateItem.getUser_id() == updateData.getData().getMyID()) {
                    showPopUpMenu(true, v, updateItem);
                } else {
                    showPopUpMenu(false, v, updateItem);
                }
            }
        });

        myViewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobManager.addJobInBackground(new PostLikeUpdateWebJob(accessToken, updateItem.getUpdateid()));
                Log.d(TAG, "Like button onClikc item: " + updateItem.getUpdateid());
            }
        });

        myViewHolder.commentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Comments button on updates pressed! Position: " + position);

                //REDO
                Intent intent = new Intent(v.getContext(), UpdateSocialActivity.class);
                Bundle args = new Bundle();
                args.putSerializable(Constant.SELECTED_UPDATE_ITEM, (Serializable) updateItem);
                args.putInt(Constant.USER_ID, updateData.getData().getMyID());
                args.putBoolean(Constant.IS_LIKE, updateData.getData().getWhatDoILike().contains(updateItem.getUpdateid()));
                intent.putExtra("BUNDLE_UPDATE", args);

                Log.d(TAG, "IS LIKE: " + updateData.getData().getWhatDoILike().toString());
                Log.d(TAG, "IS LIKE: " + updateData.getData().getWhatDoILike().contains(updateItem.getUpdateid()));
                v.getContext().startActivity(intent);

            }
        });
    }


    @Override
    public int getItemViewType(int position) {

        return 1;
    }


    public void addItem(array_updatesModel updateItemData) {

        updatesList.add(updateItemData);
        notifyDataSetChanged();

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {

        return (updatesList.size());
//        return listItem.size();
    }


    private void showPopUpMenu(final boolean isHost, View anchor, final array_updatesModel updateItem) {
        final PopupMenu popup = new PopupMenu(mContext, anchor);
        //Inflating the Popup using xml file
        if (isHost) {
            popup.getMenuInflater().inflate(R.menu.popup_menu_updates_host, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.popup_menu_updates_non_host, popup.getMenu());
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (isHost) {
                    switch (item.getItemId()) {
                        case R.id.editItem:
                            //add here
                            openEditUpdate(updateItem);
                            Log.i(TAG, "updateItem : " + updateItem);
                            break;
                        case R.id.deleteItem:
                            openDeleteUpdate(updateItem);
                            break;
                    }
                } else {
                    switch (item.getItemId()) {
                        case R.id.ignorePersonItem:
                            break;
                        case R.id.reportPersonItem:
                            break;
                    }
                }
                return true;
            }


        });
        popup.show();//showing popup menu
    }

    private void openDeleteUpdate(array_updatesModel updateItem) {
        jobManager.addJobInBackground(new PostDeleteUpdateWebJob(accessToken, updateItem.getUpdateid()));
    }

    private void openEditUpdate(array_updatesModel updateItem) {
//        Intent intent = new Intent(mContext, AddUpdateActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable("UPDATE_ITEMS", (Serializable) updateItem);
//        intent.putExtra("FROM_EDIT", args);
//        mContext.startActivity(intent);
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
