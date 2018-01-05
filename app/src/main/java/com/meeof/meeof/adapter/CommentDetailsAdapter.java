package com.meeof.meeof.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.event_comment_dto.CommentData;
import com.meeof.meeof.model.friends_dto.FriendListItem;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class CommentDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CommentDetailsAdapter.class.getSimpleName();
    private final Context mContext;
    private List<CommentData> commentData;

//    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClicked {
        void onItemClick(int position, FriendListItem item, FriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel);
    }

    public CommentDetailsAdapter(Context context, List<CommentData> commentData) {
        this.commentData = commentData;
        this.mContext = context;
        Log.d(TAG, "CommentAdapter " + commentData.size());

    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final ImageView commentAvatarIv;
        private final TextView commentTxt;
        private final TextView commentUserTxt;
        private final TextView commentTimeTxt;


        public MyViewHolderFriend(View view) {
            super(view);
            commentAvatarIv = (ImageView) view.findViewById(R.id.commentAvatarIv);
            commentTxt = (TextView) view.findViewById(R.id.commentTxt);
            commentUserTxt = (TextView) view.findViewById(R.id.commentUserTxt);
            commentTimeTxt = (TextView) view.findViewById(R.id.commentTimeTxt);


//            swipeLayoutSR = (SwipeRevealLayout) view.findViewById(R.id.swipeLayoutSR);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment_details, parent, false);

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_friend_deletable, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CommentData item = commentData.get(position);
        final MyViewHolderFriend myViewHolder = (MyViewHolderFriend) holder;

        myViewHolder.commentTxt.setText(item.getContent());
        myViewHolder.commentUserTxt.setText(item.getFirstName());

        Log.d(TAG, "getCreatedAt " + item.getCreatedAt());
        Log.d(TAG, "getContent " + item.getContent());
        Log.d(TAG, "getCreatedAt " + item.getCreatedAt());
        Log.d(TAG, "getProfilephoto " + item.getProfilephoto());

        DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parseFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {

            if(item.getCreatedAt() != null){
                Date date = parseFormat.parse(item.getCreatedAt());
                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(date);
                myViewHolder.commentTimeTxt.setText(ago);
                Log.d(TAG, "prettyTime: " + ago);
            }else {
                myViewHolder.commentTimeTxt.setText("Not Available");
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        String imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() == 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        Picasso.with(mContext.getApplicationContext())
                .load(imgeUrl1)
                .placeholder(R.drawable.siloet_img)
                .error(ContextCompat.getDrawable(mContext, R.drawable.siloet_img))
                .into(myViewHolder.commentAvatarIv);


    }

    public void addItem(CommentData commentdata) {
        commentData.add(0,commentdata);
        notifyDataSetChanged();

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

        return (commentData.size());
//        return listItem.size();
    }


}
