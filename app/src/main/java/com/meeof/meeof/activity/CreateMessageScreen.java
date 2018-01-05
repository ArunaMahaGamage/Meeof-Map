package com.meeof.meeof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.SelectFriendsAdapter;
import com.meeof.meeof.interfaces.SetOnItemClick;
import com.meeof.meeof.model.AddNewConversationModel;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.AddNewConversationWebJob;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CreateMessageScreen extends BaseActivity {

    RecyclerView allFriendListRv;
    private String accessToken;
    TextView TxtNoFriendsFound;
    LinearLayout LinearWriteMessage;

    SelectFriendsAdapter friendsRecyclerAdapter;
    ImageView ImgSendFriendsScreen;
    EditText EdtSendMessage;
    public TextView SelectedUsersName;
    TextView TxtTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);

        ImageView backAcIvBtn = (ImageView) findViewById(R.id.backAcIvBtn);
        TxtNoFriendsFound = (TextView) findViewById(R.id.TxtNoFriendsFound);
        EdtSendMessage = (EditText) findViewById(R.id.EdtSendMessage);
        SelectedUsersName=(TextView)findViewById(R.id.SelectedUsersName);
        ImgSendFriendsScreen = (ImageView) findViewById(R.id.ImgSendFriendsScreen);
        ImgSendFriendsScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendsRecyclerAdapter != null)
                {
                    ArrayList<Integer> arrayList=new ArrayList<>();
                    if (friendsRecyclerAdapter.SelectedUsers != null) {
                        if (friendsRecyclerAdapter.SelectedUsers.size() > 0) {
                            if (EdtSendMessage.getText().toString().isEmpty()) {
                                Toast.makeText(CreateMessageScreen.this, "please enter message", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ImgSendFriendsScreen.setEnabled(false);
                            for (int i=0;i<friendsRecyclerAdapter.SelectedUsers.size();i++)
                            {
                                arrayList.add(friendsRecyclerAdapter.SelectedUsers.get(i).getId());
                            }

                            jobManager.addJobInBackground(new AddNewConversationWebJob(accessToken, arrayList, EdtSendMessage.getText().toString()));
                        } else {
                            Toast.makeText(CreateMessageScreen.this, "please select a user to chat", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        backAcIvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        allFriendListRv = (RecyclerView) findViewById(R.id.allFriendListRv);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        jobManager.addJobInBackground(new GetMyAllFriendsWebJob(accessToken));
        startProgressBar();
        LinearWriteMessage = (LinearLayout) findViewById(R.id.LinearWriteMessage);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyAllFriendsCompleted(FriendsAllResponse friendsAllResponse) {
        stopProgressBar();
        if (friendsAllResponse != null) {
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> friends = friendsAllResponse.getFriends();
                List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();
                if (friends.size() > 0) {
                    setDataToListFriends(friends);
                    TxtNoFriendsFound.setVisibility(View.GONE);
                    LinearWriteMessage.setVisibility(View.VISIBLE);
                } else {
                    TxtNoFriendsFound.setVisibility(View.VISIBLE);
                    LinearWriteMessage.setVisibility(View.GONE);
                }
//                setDataToListPendingRequests(pendingRequests);
            } else {
//                showSnackbar(back_button, getString(R.string.unable_to_retrive_friends), Constant.ERROR);
                Toast.makeText(CreateMessageScreen.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateMessageScreen.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
        }
    }


    void setDataToListFriends(List<Friends> arrayList) {
        friendsRecyclerAdapter = new SelectFriendsAdapter(CreateMessageScreen.this, arrayList, new SetOnItemClick() {
            @Override
            public void OnItemClick(Friends friends) {
                Intent intent = new Intent(CreateMessageScreen.this, ChatActivity.class);
                int ID = friends.getId();
                intent.putExtra("Name", friends.getFirst_name());
                intent.putExtra("ReceiverId", ID);
                intent.putExtra("ReceiverImage", friends.getProfilephoto());
                startActivity(intent);

                Log.e("FirstName", "" + friends.getFirst_name());
                Log.e("ChannelDetails", "" + friends.getFirst_name());
                Log.e("ChannelDetails", "" + friends.getChannel_d_type());
                Log.e("ChannelDetails", "" + friends.getChannel_description());
                Log.e("ChannelDetails", "" + friends.getChannel_id());
                Log.e("ChannelDetails", "" + friends.getChannel_master());
                Log.e("ChannelDetails", "" + friends.getChannel_type());
                Log.e("ChannelDetails", "" + friends.getChannel_published());

            }
        });
        allFriendListRv.setLayoutManager(new LinearLayoutManager(CreateMessageScreen.this));
        allFriendListRv.setAdapter(friendsRecyclerAdapter);
        allFriendListRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewConversationCompleted(AddNewConversationModel addNewConversationModel) {
        stopProgressBar();
        if (addNewConversationModel != null) {
            if (addNewConversationModel.getStatus() != null && addNewConversationModel.getStatus().equals(Constant.SUCCESS)) {
                int GroupId = addNewConversationModel.getGroupID();
//                Friends friends = friendsRecyclerAdapter.SelectedUsers.get(0);
                Intent intent = new Intent(CreateMessageScreen.this, ChatActivity.class);
//                int ID = friends.getId();
                intent.putExtra("Name", SelectedUsersName.getText().toString());
                intent.putExtra("ReceiverId", 50);
                intent.putExtra("ReceiverImage", "");
                intent.putExtra("GroupId", GroupId);

                startActivity(intent);
                CreateMessageScreen.this.finish();
//                addNewConversationModel.getGroupID()
//                setDataToListPendingRequests(pendingRequests);
            } else {
//                showSnackbar(back_button, getString(R.string.unable_to_retrive_friends), Constant.ERROR);
                Toast.makeText(CreateMessageScreen.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateMessageScreen.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
        }
    }
}
