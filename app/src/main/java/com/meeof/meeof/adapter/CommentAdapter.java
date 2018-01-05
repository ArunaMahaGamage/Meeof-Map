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
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.friends_dto.FriendListItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CommentAdapter.class.getSimpleName();
    private final Context mContext;
    private List<CommentData> commentData;

//    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClicked {
        void onItemClick(int position, FriendListItem item, FriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel);
    }

    public CommentAdapter(Context context, List<CommentData> commentData) {
        this.commentData = commentData;
        this.mContext = context;
        Log.d(TAG, "CommentAdapter " + commentData.size());

    }

    public class MyViewHolderFriend extends RecyclerView.ViewHolder {
        private final ImageView commentAvatarIv;
        private final TextView commentTxt;


        public MyViewHolderFriend(View view) {
            super(view);
            commentAvatarIv = (ImageView) view.findViewById(R.id.commentAvatarIv);
            commentTxt = (TextView) view.findViewById(R.id.commentTxt);

//            swipeLayoutSR = (SwipeRevealLayout) view.findViewById(R.id.swipeLayoutSR);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_friend_deletable, parent, false);

        return new MyViewHolderFriend(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CommentData item = commentData.get(position);
        final MyViewHolderFriend myViewHolder = (MyViewHolderFriend) holder;

        Log.d(TAG, "user getProfilephoto: " + item.getProfilephoto());

        myViewHolder.commentTxt.setText(item.getContent());
        String imgeUrl1 = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();

        Picasso.with(mContext.getApplicationContext())
                .load(imgeUrl1)
                .placeholder(R.drawable.siloet_img)
                .error(ContextCompat.getDrawable(mContext, R.drawable.siloet_img))
                .into(myViewHolder.commentAvatarIv);

    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    public void addItem(CommentData commentdata) {
        Log.d(TAG, "addItem: " + commentdata.getContent());
        commentData.add(0,commentdata);
        notifyDataSetChanged();

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
