package com.meeof.meeof.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.ChatAdapter;
import com.meeof.meeof.model.AddNewConversationModel;
import com.meeof.meeof.model.ChatModel;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetConvertatioWebJob;
import com.meeof.meeof.webjob.ReplyToConvertationWebJob;
import com.meeof.meeof.webjob.SetReadPropertyWebJob;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ChatActivity extends BaseActivity {

    private static final String TAG = CommentsBaseActivity.class.getSimpleName();
    Thread subscribeThread;
    Thread unsubscribeThread;
    Thread publishThread;
    private String accessToken;
    private Connection connection;
    private Channel channel;
    protected BlockingDeque<String> queue = new LinkedBlockingDeque<>();
    ChatAdapter chatAdapter;
    ArrayList<ChatModel> arrayList = new ArrayList<>();
    String ReceiverName, ReceiverImage;
    int ReceiverId;
    String SenderName, SenderImage;
    ImageView ImgSend;
    int SenderId;
    EditText EdtSendMessage;
    RecyclerView recyclerView;
    ImageView ImgBack;
    int GroupId = 0;
    TextView HeadingText;
    ProgressBar progressBar;
    String LastPreviousDate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sharedPreferences = getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        ReceiverName = getIntent().getStringExtra("Name");
        ReceiverId = getIntent().getIntExtra("ReceiverId", 0);
        ReceiverImage = getIntent().getStringExtra("ReceiverImage");
        GroupId = getIntent().getIntExtra("GroupId", 0);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        SenderName = sharedPreferences.getString(Constant.MYNAME, "");
        SenderId = sharedPreferences.getInt(Constant.MYUSERID, 0);
        SenderImage = sharedPreferences.getString(Constant.MYIMAGE, "");
        ImgSend = (ImageView) findViewById(R.id.ImgSend);
        EdtSendMessage = (EditText) findViewById(R.id.EdtSendMessage);
        recyclerView = (RecyclerView) findViewById(R.id.ChatRecycler);
        HeadingText = (TextView) findViewById(R.id.HeadingText);
        if (ReceiverName!=null)
        HeadingText.setText(ReceiverName);
        progressBar = (ProgressBar) findViewById(R.id.ChatProgress);
        ImgBack = (ImageView) findViewById(R.id.ImgBack);
        ImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new GetConvertatioWebJob(accessToken, GroupId));
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
        ImgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EdtSendMessage.getText().toString().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "please enter message to send", Toast.LENGTH_SHORT).show();
                } else {
                    ChatModel chatModel = new ChatModel(SenderId,SenderName, EdtSendMessage.getText().toString(), "", SenderImage, GroupId,"",false,false);
                    String chat = chatModel.toJson();
                    publishMessage(chat);
                }
            }
        });
        setupConnectionFactory();
        publishToAMQP();


        subscribeToComments(incomingMessageHandler);
        chatAdapter = new ChatAdapter(ChatActivity.this, arrayList);
        recyclerView.setAdapter(chatAdapter);
    }

    ConnectionFactory factory = new ConnectionFactory();

    protected void setupConnectionFactory() {
        String uri = Constant.RABBIT_MQ_CLOUD_URI;
        factory.setUsername("bytqideb");
        factory.setPassword("xhJNp1oRDa21Z-yh_8DvXqH6IMBmU9HU");
        factory.setVirtualHost("bytqideb");
        factory.setHost("elephant.rmq.cloudamqp.com");
        factory.setPort(5672);
        try {

            factory.setUri(uri);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();
                        long deliveryTag = 0;
                        while (true) {
                            String message = queue.takeFirst();
                            try {

//                                ch.basicPublish("amq.fanout", "bytqideb", null, message.getBytes());
                                ch.basicPublish("amq.fanout", "bytqideb", null, message.getBytes());
                                Log.d(TAG, "[s] " + message);
                                ch.waitForConfirmsOrDie();
                                ch.basicAck(deliveryTag, true);
                            } catch (Exception e) {
                                Log.d(TAG, "[f] " + message);
                                queue.putFirst(message);
                                ch.basicReject(deliveryTag, true);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d(TAG, "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    @Override
    protected void onDestroy() {

        unsubscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    channel.close();
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishThread.interrupt();
                subscribeThread.interrupt();
            }
        });

        super.onDestroy();
    }

    void subscribeToComments(final Handler handler) {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = factory.newConnection();
                    channel = connection.createChannel();
                    channel.exchangeDeclare("amq.fanout", "fanout", true);
                    channel.basicQos(50);
                    AMQP.Queue.DeclareOk q = channel.queueDeclare();
//                    channel.queueBind(q.getQueue(), "amq.fanout", "bytqideb");
                    channel.queueBind(q.getQueue(), "amq.fanout", "bytqideb");
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    Log.d(TAG, "Queue: " + q.getQueue());
                    channel.basicConsume(/*"Meeof_Queue"*/q.getQueue(), false, consumer);

                    while (true) {
                        try {
                            // Process deliveries
                            while (true) {
                                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                                String message = new String(delivery.getBody());
                                Log.d(TAG, "QueueingConsumer msg " + message);

                                Message msg = handler.obtainMessage();
                                Bundle bundle = new Bundle();

                                JSONObject messageObj = new JSONObject(message);

                                if (messageObj.has("sender_id")) {
                                    bundle.putInt("sender_id", messageObj.getInt("sender_id"));
                                }
                                if (messageObj.has("Receiver_id")) {
                                    bundle.putString("Receiver_id", messageObj.getString("Receiver_id"));
                                }

                                if (messageObj.has("Sender_name")) {
                                    bundle.putString("Sender_name", messageObj.getString("Sender_name"));
                                }

                                if (messageObj.has("Receiver_Name")) {
                                    bundle.putString("Receiver_Name", messageObj.getString("Receiver_Name"));
                                }

                                if (messageObj.has("Message")) {
                                    bundle.putString("Message", messageObj.getString("Message"));
                                }

                                if (messageObj.has("Sender_image")) {
                                    bundle.putString("Sender_image", messageObj.getString("Sender_image"));
                                }

                                if (messageObj.has("timestamp")) {
                                    bundle.putString("timestamp", messageObj.getString("timestamp"));
                                }
                                if (messageObj.has("Receiver_image")) {
                                    bundle.putString("Receiver_image", messageObj.getString("Receiver_image"));
                                }

                                if (messageObj.has("group_id"))
                                {
                                    bundle.putInt("group_id",messageObj.getInt("group_id"));
                                }

                                msg.setData(bundle);
                                handler.sendMessage(msg);
                                channel.basicAck(0, true);
                            }
                        } catch (InterruptedException e) {

                            break;
                        } catch (Exception e1) {
                            Log.d(TAG, "Connection broken: " + e1.getClass().getName());
                            try {
                                Thread.sleep(4000); //sleep and then try again
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                        Log.d(TAG, "Loop End");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        subscribeThread.start();
    }

    void publishMessage(String message) {
        //Adds a message to internal blocking queue
        try {
            Log.d(TAG, "[q] " + message);
            queue.putLast(message);
            jobManager.addJobInBackground(new ReplyToConvertationWebJob(accessToken, GroupId, EdtSendMessage.getText().toString()));
            EdtSendMessage.setText("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetConvertatioCompleted(String result) {
        progressBar.setVisibility(View.GONE);
        stopProgressBar();
        if (result != null) {
            if (!result.equalsIgnoreCase("Something went wrong")) {
                int count = 0;
                if (arrayList != null && arrayList.size() > 0)
                    arrayList.clear();
                Log.e("Result", "" + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    int GroupId = data.getInt("group_id");
                    String CountMessages = data.getString("count_messages");
                    Object json=data.get("messages");
                    if (json instanceof JSONObject)
                    {
                        JSONObject MessagesObject = data.getJSONObject("messages");
                        for (int i = 0; i < MessagesObject.length(); i++) {
                            JSONObject jsonObject1 = MessagesObject.getJSONObject(String.valueOf(count));
                            String FirstName = jsonObject1.getString("first_name");
                            String user_id = jsonObject1.getString("user_id");
                            String profilephoto = jsonObject1.getString("profilephoto");
                            String channel_id = jsonObject1.getString("channel_id");
                            int sender_id = jsonObject1.getInt("sender_id");
                            String message = jsonObject1.getString("message");
                            String created_at = jsonObject1.getString("created_at");
                            profilephoto= "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/"+profilephoto;
                            String Date[] =created_at.split(" ");
                            String myDate=Date[0];
                            arrayList.add(new ChatModel(sender_id, FirstName, message, created_at, profilephoto,GroupId,myDate,false,false));
                            count++;
                        }
                    }
                    else if (json instanceof  JSONArray)
                    {
                        JSONArray MessagesObject = data.getJSONArray("messages");


                        for (int i = 0; i < MessagesObject.length(); i++) {
                            JSONObject jsonObject1 = (JSONObject) MessagesObject.get(i);
                            String FirstName = jsonObject1.getString("first_name");
//                            String user_id = jsonObject1.getString("user_id");
                            String profilephoto = jsonObject1.getString("profilephoto");
//                            String channel_id = jsonObject1.getString("channel_id");
                            int sender_id = jsonObject1.getInt("sender_id");
                            String message = jsonObject1.getString("message");
                            String created_at = jsonObject1.getString("created_at");
                            profilephoto= "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/"+profilephoto;
                            String Date[] =created_at.split(" ");
                            String myDate=Date[0];
                            arrayList.add(new ChatModel(sender_id,  FirstName,  message, created_at, profilephoto, GroupId,myDate,false,false));

                        }
                    }

                    if (arrayList != null && arrayList.size() > 0) {
                        Collections.reverse(arrayList);
                        boolean IsSame;
                        String PreviousDate="";
                        for (int i=0;i<arrayList.size();i++)
                        {
                            String created_at=arrayList.get(i).getTimestamp();
                            String Date[] =created_at.split(" ");
                            String myDate=Date[0];
                            if (PreviousDate.isEmpty())
                            {
                                IsSame=false;
                            }
                            else if (PreviousDate.equalsIgnoreCase(myDate))
                            {
                                IsSame=true;
                            }
                            else
                            {
                                IsSame=false;
                            }
                            PreviousDate=myDate;
                            LastPreviousDate=PreviousDate;
                            arrayList.get(i).setSame(IsSame);
                        }
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                    } else {
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                    }
                    if (arrayList != null && arrayList.size() > 0) {
                        recyclerView.scrollToPosition(arrayList.size() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.invalidate();
                    if (arrayList != null && arrayList.size() > 0) {
                        Collections.reverse(arrayList);
                        boolean IsSame;
                        String PreviousDate="";
                        for (int i=0;i<arrayList.size();i++)
                        {
                            String created_at=arrayList.get(i).getTimestamp();
                            String Date[] =created_at.split(" ");
                            String myDate=Date[0];
                            if (PreviousDate.isEmpty())
                            {
                                IsSame=false;
                            }
                            else if (PreviousDate.equalsIgnoreCase(myDate))
                            {
                                IsSame=true;
                            }
                            else
                            {
                                IsSame=false;
                            }
                            PreviousDate=myDate;
                            LastPreviousDate=PreviousDate;
                            arrayList.get(i).setSame(IsSame);
                        }
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                        recyclerView.scrollToPosition(arrayList.size() - 1);
                    } else {
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
            } else {
                Toast.makeText(ChatActivity.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ChatActivity.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
        }
        jobManager.addJobInBackground(new SetReadPropertyWebJob(accessToken,GroupId));
    }

    String TimeStampToDate(long Timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date resultdate = new Date(Timestamp);
        return sdf.format(resultdate);
    }

    @SuppressLint("HandlerLeak")
    private Handler incomingMessageHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            String SenderName = msg.getData().getString("Sender_name");
            int SenderId = msg.getData().getInt("sender_id");
            String Message = msg.getData().getString("Message");
            String SenderImage = msg.getData().getString("Sender_image");
            int GroupId=msg.getData().getInt("group_id");
            Log.e("Message", "" + msg.toString());
            if (SenderId != 0)
            {

                boolean IsSame;
                if (ChatActivity.this.GroupId==GroupId) {
                    long timestamp = System.currentTimeMillis();
                    String time = TimeStampToDate(timestamp);
                    String Date[] =time.split(" ");
                    String myDate=Date[0];
                    if (LastPreviousDate.isEmpty())
                    {
                        IsSame=false;
                    }
                    else if (LastPreviousDate.equalsIgnoreCase(myDate))
                    {
                        IsSame=true;
                    }
                    else
                    {
                        IsSame=false;
                    }
                    LastPreviousDate=myDate;
                    LastPreviousDate=LastPreviousDate + " " + Date[1];
                    arrayList.add(new ChatModel(SenderId, SenderName, Message, time, SenderImage, GroupId,LastPreviousDate,IsSame,true));
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.invalidate();
                    jobManager.addJobInBackground(new SetReadPropertyWebJob(accessToken,GroupId));
                }
            }
            recyclerView.scrollToPosition(arrayList.size() - 1);
        }
    };
}
