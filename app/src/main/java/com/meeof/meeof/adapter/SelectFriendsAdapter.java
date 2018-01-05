package com.meeof.meeof.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.activity.CreateMessageScreen;
import com.meeof.meeof.interfaces.SetOnItemClick;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dharmesh on 11/22/2017.
 */

public class SelectFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Friends> arrayList;
    public List<Friends> SelectedUsers;
    SetOnItemClick setOnItemClick;
    public SelectFriendsAdapter(Context mContext, List<Friends> arrayList, SetOnItemClick setOnItemClick) {
        SelectedUsers=new ArrayList<>();
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.setOnItemClick=setOnItemClick;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Friends item=arrayList.get(position);
        ((MyViewHolder) holder).TxtUserName.setText(arrayList.get(position).getFirst_name());
        ((MyViewHolder)holder).TxtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setOnItemClick.OnItemClick(arrayList.get(holder.getAdapterPosition()));
            }
        });
        String imageUrl = item.getProfilephoto() == null || item.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + item.getProfilephoto();
        Picasso.with(mContext).load(imageUrl).resize(150,150).placeholder(R.drawable.img_avatar_00).into(((MyViewHolder)holder).ImgUserImage);
        ((MyViewHolder) holder).ChkUserCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed())
                {
                    if (b) {
//                        if (SelectedUsers.size()==1)
//                        {
//                            Toast.makeText(mContext,"group chat yet to go, please select only one",Toast.LENGTH_SHORT).show();
//                            ((MyViewHolder)holder).ChkUserCheckBox.setChecked(false);
//                            return;
//                        }
                        SelectedUsers.add(arrayList.get(holder.getAdapterPosition()));
                        String SelectedUsersName="";
                        if (mContext instanceof CreateMessageScreen)
                        {
                            for (int i=0;i<SelectedUsers.size();i++)
                            {
                                if (SelectedUsersName.isEmpty())
                                {
                                    SelectedUsersName=SelectedUsers.get(i).getFirst_name();
                                }
                                else
                                {
                                    SelectedUsersName = SelectedUsersName + "," + SelectedUsers.get(i).getFirst_name();
                                }
                            }
                            ((CreateMessageScreen)mContext).SelectedUsersName.setText(SelectedUsersName);
                        }

                    } else {
                        String SelectedUsersName="";
                        SelectedUsers.remove(arrayList.get(holder.getAdapterPosition()));
                        if (mContext instanceof CreateMessageScreen)
                        {
                            for (int i=0;i<SelectedUsers.size();i++)
                            {
                                if (SelectedUsersName.isEmpty())
                                {
                                    SelectedUsersName=arrayList.get(holder.getAdapterPosition()).getFirst_name();
                                }
                                else
                                {
                                    SelectedUsersName = SelectedUsersName + "," + arrayList.get(holder.getAdapterPosition()).getFirst_name();
                                }
                            }
                            ((CreateMessageScreen)mContext).SelectedUsersName.setText(SelectedUsersName);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        //        TextView TxtName;

        ImageView ImgUserImage;
        TextView TxtUserName;
        CheckBox ChkUserCheckBox;

        MyViewHolder(View ItemView) {
            super(ItemView);
            ImgUserImage = ItemView.findViewById(R.id.FriendsItemImage);
            TxtUserName = ItemView.findViewById(R.id.TxtUsersName);
            ChkUserCheckBox = ItemView.findViewById(R.id.ChkFriends);
        }
    }
}