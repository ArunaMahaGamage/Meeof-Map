package com.meeof.meeof.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.event_comment_dto.CommentData;
import com.meeof.meeof.util.Constant;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by ransikadesilva on 11/7/17.
 */

public class CommentsBaseActivity extends BaseActivity {

    private static final String TAG = CommentsBaseActivity.class.getSimpleName();
    private Connection connection;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupConnectionFactory();
        publishToAMQP();
        setupPubButton();

    }


    void setupPubButton() {
//        Button button = (Button) findViewById(R.id.publish);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                EditText et = (EditText) findViewById(R.id.text);
//                publishMessage(et.getText().toString());
//                et.setText("");
//            }
//        });
    }

    Thread subscribeThread;
    Thread unsubscribeThread;
    Thread publishThread;

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

    public void stopRabbitQueue(){
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
    }

    protected BlockingDeque<String> queue = new LinkedBlockingDeque<String>();

    void publishMessage(String message) {
        //Adds a message to internal blocking queue
        try {
            Log.d(TAG, "[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public void sendMessage(){

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
                                Log.d(TAG, "messageObj " + messageObj.toString());

                                if(messageObj.has("event_comment")){
                                    bundle.putString("event_comment", message);
                                }else if (messageObj.has("event_like")){
                                    bundle.putString("event_like", message);
                                }else {
                                    bundle.putString("other_message", message);
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


    public void publishToAMQP(final CommentData commentData) {
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
                            String message = commentData.getContent();
                            try {
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


}
