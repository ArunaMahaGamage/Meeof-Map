package com.meeof.meeof.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.activity.ChatActivity;
import com.meeof.meeof.activity.CreateMessageScreen;
import com.meeof.meeof.adapter.InboxAdapter;
import com.meeof.meeof.interfaces.SetOnInboxItemClicked;
import com.meeof.meeof.model.DeleteInboxModel;
import com.meeof.meeof.model.GetInboxModel;
import com.meeof.meeof.model.InboxDataModel;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteGroupWebJob;
import com.meeof.meeof.webjob.InBoxWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dharmesh on 11/22/2017.
 */

public class MessageListScreen extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener
{
    ImageView ImgCreateMsg;
    private String accessToken;
    View view;
    List<InboxDataModel> arrayList;
    RecyclerView RecyclerMessageList;
    InboxAdapter inboxAdapter;
    ProgressBar ProgressMessageInbox;
    SwipeRefreshLayout SwipeToRefreshForInbox;
    TextView EdtMsg;
    private int MyId;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_messagelist, container, false);
        RecyclerMessageList=view.findViewById(R.id.RecyclerMessageList);
        EdtMsg=view.findViewById(R.id.EdtMsg);
        RecyclerMessageList.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
        arrayList=new ArrayList<>();
        ImgCreateMsg=view.findViewById(R.id.EditMessage);
        ProgressMessageInbox=view.findViewById(R.id.ProgressMessageInbox);
        SwipeToRefreshForInbox=view.findViewById(R.id.SwipeToRefreshForInbox);
        SwipeToRefreshForInbox.setOnRefreshListener(this);
        ProgressMessageInbox.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new InBoxWebJob(accessToken));
        ImgCreateMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CreateMessageScreen.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });
        EdtMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EdtMsg.getText().toString().equalsIgnoreCase("Edit"))
                {
                    EdtMsg.setText("Done");
                    if (inboxAdapter!=null)
                    {
                        inboxAdapter.OnDeletePressed("Done");
                    }
                }
                else
                {
                    EdtMsg.setText("Edit");
                    if (inboxAdapter!=null)
                    {
                        inboxAdapter.OnDeletePressed("Edit");
                    }
                }
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void InBoxWebJobWebJobCompleted(GetInboxModel inboxModel) {
        if (SwipeToRefreshForInbox.isRefreshing())
            SwipeToRefreshForInbox.setRefreshing(false);
        if (arrayList!=null && arrayList.size()>0)
            arrayList.clear();
        ProgressMessageInbox.setVisibility(View.GONE);

        if (inboxModel != null) {
            {
                if (inboxModel.getStatus().equals(Constant.SUCCESS))
                {
                    arrayList=inboxModel.getData();

                    if (arrayList!=null && arrayList.size()>0)
                    {

                        inboxAdapter=new InboxAdapter(getActivity().getApplicationContext(), arrayList, new SetOnInboxItemClicked() {
                            @Override
                            public void OnInboxClicked(InboxDataModel inboxDataModel) {
                                String name="";
                                for (int i=0;i<inboxDataModel.getMembers().size();i++)
                                {


                                    if (name==null)
                                    {
                                        name="";
                                    }
                                    if (name.isEmpty()) {
                                        if (inboxDataModel.getMembers().get(i).getUser_id()!=MyId)
                                        {
                                            name = inboxDataModel.getMembers().get(i).getFirst_name();
                                        }

                                    }
                                    else
                                    {
                                        if (inboxDataModel.getMembers().get(i).getUser_id()!=MyId)
                                        {
                                            name=name + "," +inboxDataModel.getMembers().get(i).getFirst_name();
                                        }
                                    }
                                }
                                Intent intent=new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("Name",name);
                                intent.putExtra("GroupId",inboxDataModel.getGroup_id());
                                startActivity(intent);
                            }

                            @Override
                            public void OnInboxDeleteClicked(final int GroupId) {
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(MessageListScreen.this.getActivity());
                                builder.setTitle("Alert !")
                                        .setMessage("Are you sure you want to delete this Group?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                ProgressMessageInbox.setVisibility(View.VISIBLE);
                                                jobManager.addJobInBackground(new DeleteGroupWebJob(accessToken,GroupId));
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                            }
                        });
                        inboxAdapter.OnDeletePressed(EdtMsg.getText().toString());
                        RecyclerMessageList.setAdapter(inboxAdapter);
                    }

                } else {
                    showSnackbar(view, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                    stopProgressBar();
                }
            }
        } else {
            showSnackbar(view, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            stopProgressBar();
        }
        stopProgressBar();
    }

    @Override
    public void onRefresh() {
        jobManager.addJobInBackground(new InBoxWebJob(accessToken));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DeleteGroupCompleted(DeleteInboxModel inboxModel) {

        ProgressMessageInbox.setVisibility(View.GONE);
        Log.d("MessageListScreen", "onPostLikeEventAsFriendWebJobCompleted ");
        if (inboxModel != null) {
            {
                if (inboxModel.getStatus().equals(Constant.SUCCESS))
                {
                        Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                        ProgressMessageInbox.setVisibility(View.VISIBLE);
                        jobManager.addJobInBackground(new InBoxWebJob(accessToken));

                } else {
                    showSnackbar(view, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                    stopProgressBar();
                }
            }
        } else {
            showSnackbar(view, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            stopProgressBar();
        }
        stopProgressBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        ProgressMessageInbox.setVisibility(View.VISIBLE);
        onRefresh();
    }
}
