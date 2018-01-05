package com.meeof.meeof.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.notification_dto.NotificationsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper ";
    public static String DB_NAME = "MeeofDb";
    final static String TABLE_EVENTS = "TABLE_EVENTS";
    final static String KEY_ID = "KEY_ID";
    final static String KEY_SERVER_PK = "KEY_SERVER_PK";
    final static String KEY_DATA = "KEY_DATA";
    final static String KEY_CREATED_DATE = "KEY_CREATED_DATE";
    final static String KEY_IS_REQUEST = "KEY_IS_REQUEST";
    final static String KEY_RSVP = "KEY_RSVP";

    final static String TABLE_IMPORTNOTIFICATIONS ="TABLE_IMPORTNOTIFICATIONS";
    final static String TABLE_OTHERNOTIFICATIONS ="TABLE_OTHERNOTIFICATIONS";

    SQLiteDatabase db;

    private static DBHelper instance;


    private static final String CREATE_EVENT_TABLE = " CREATE TABLE IF NOT EXISTS "
            + TABLE_EVENTS + "("
            + KEY_ID + " AUTO INCREMENT INTEGER PRIMARY KEY ,"
            + KEY_SERVER_PK + " INT,"
            + KEY_DATA + " TEXT,"
            + KEY_CREATED_DATE + " TEXT,"
            + KEY_RSVP + " TEXT,"
            + KEY_IS_REQUEST + " TEXT"
            + ")";

    private static final String CREATE_IMPORTNOTIFICATIONS_TABLE ="CREATE TABLE IF NOT EXISTS "
            + TABLE_IMPORTNOTIFICATIONS +"("
            + KEY_ID + " AUTO INCREMENT INTEGER PRIMARY KEY ,"
            + KEY_SERVER_PK + " INT,"
            + KEY_DATA + " TEXT,"
            + KEY_CREATED_DATE + " TEXT"
            + ")";
    private static final String CREATE_OTHERNOTIFICATIONS_TABLE="CREATE TABLE IF NOT EXISTS "
            + TABLE_OTHERNOTIFICATIONS +"("
            + KEY_ID + " AUTO INCREMENT INTEGER PRIMARY KEY ,"
            + KEY_SERVER_PK + " INT,"
            + KEY_DATA + " TEXT,"
            + KEY_CREATED_DATE + " TEXT"
            + ")";


    public static synchronized DBHelper getInstance(Context c) {

        if (instance != null) {

        } else {
            instance = new DBHelper(c);

        }

        return instance;
    }

    public DBHelper(Context c) {
        super(c, DB_NAME, null, 1);

//        if(android.os.Build.VERSION.SDK_INT >= 4.2){
//            DB_PATH = c.getApplicationInfo().dataDir + "/databases/";
//        } else {
//            DB_PATH = "/data/data/" + c.getPackageName() + "/databases/";
//        }
        //this.mContext = c;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_IMPORTNOTIFICATIONS_TABLE);
        db.execSQL(CREATE_OTHERNOTIFICATIONS_TABLE);
        System.out.println(TAG + "CREATE_RECIPE ");


        System.out.println(TAG + "Tables Created");
        Log.i(TAG,"Tables Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Important Notification  Insert
    public void insertOrUpdateNotification(JSONArray data) {
        db = getWritableDatabase();
        db.beginTransaction();

        try{
            Log.i(TAG,"Delete All Notifications");
            db.execSQL("delete from "+ TABLE_IMPORTNOTIFICATIONS);
        }catch (Exception e){
            Log.i(TAG,"Delete ALL ROWS EXCEPTION "+e.getMessage());
        }

        long insert = 0;
        try {
            Log.i(TAG, "insertOrUpdateNotifications " + data.length());

            for (int i = 0; i < data.length(); i++) {
                JSONObject row = data.getJSONObject(i);
                Cursor cursor = checkForExsistRow(row.get("zone_id").toString());
                if (cursor != null) {
                    Log.d(TAG, "cursor if ");
                    if (cursor.moveToFirst()) {
                        Log.d(TAG, "cursor while " + cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK)));
                        String serverPk = cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(KEY_DATA, row.toString());
                        String where = KEY_SERVER_PK + " = " + serverPk;
                        // String[] whereArgs = new String[]{serverPk};
                        try {
                            db.update(TABLE_IMPORTNOTIFICATIONS, contentValues, where, null);
                            Log.d(TAG, "UPDATED NOTIFICATION ID " + serverPk);
                        } catch (Exception e) {
                            Log.i(TAG, "error updating " + e.getMessage().toString());
                        }
                    }

                } else {

                    Log.i(TAG, "insert new row");
                    ContentValues contentValues = new ContentValues();
                    int eventId = Integer.parseInt(row.get("zone_id").toString());
                    Log.i(TAG, "KEY_SERVER_PK " + eventId);
                    contentValues.put(KEY_SERVER_PK, eventId);
                    Log.i(TAG, "KEY_DATA " + row.toString());
                    contentValues.put(KEY_DATA, row.toString());
                    Log.i(TAG, "KEY_CREATED_DATE " + row.getString("created_at"));
                    contentValues.put(KEY_CREATED_DATE, row.getString("created_at"));


                    insert = db.insert(TABLE_IMPORTNOTIFICATIONS, null, contentValues);
                    Log.i(TAG, "notifications inserted : " + insert);

                }
            }

//            Log.d(TAG, "table size " + getTableCount() + "");
            //updateMystatusOnEvents(myArrayAttendance);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            Log.i(TAG, "insertOrUpdateEvents error " + e.getMessage().toString());

        } finally {
            db.close();
        }
    }

    //Other Notification  Insert
    public void insertOrUpdateOtherNotification(JSONArray data) {
        db = getWritableDatabase();
        db.beginTransaction();

        try{
            Log.i(TAG,"Delete All Notifications");
            db.execSQL("delete from "+ TABLE_OTHERNOTIFICATIONS);
        }catch (Exception e){
            Log.i(TAG,"Delete ALL ROWS EXCEPTION "+e.getMessage());
        }

        long insert = 0;
        try {
            Log.i(TAG, "insertOrUpdateOtherNotifications " + data.length());

            for (int i = 0; i < data.length(); i++) {
                JSONObject row = data.getJSONObject(i);
                Cursor cursor = checkForExsistRow(row.get("zone_id").toString());
                if (cursor != null) {
                    Log.d(TAG, "cursor if ");
                    if (cursor.moveToFirst()) {
                        Log.d(TAG, "cursor while " + cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK)));
                        String serverPk = cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(KEY_DATA, row.toString());
                        String where = KEY_SERVER_PK + " = " + serverPk;
                        // String[] whereArgs = new String[]{serverPk};
                        try {
                            db.update(TABLE_OTHERNOTIFICATIONS, contentValues, where, null);
                            Log.d(TAG, "UPDATED NOTIFICATION ID " + serverPk);
                        } catch (Exception e) {
                            Log.i(TAG, "error updating " + e.getMessage().toString());
                        }
                    }

                } else {

                    Log.i(TAG, "insert new row");
                    ContentValues contentValues = new ContentValues();
                    int eventId = Integer.parseInt(row.get("zone_id").toString());
                    Log.i(TAG, "KEY_SERVER_PK " + eventId);
                    contentValues.put(KEY_SERVER_PK, eventId);
                    Log.i(TAG, "KEY_DATA " + row.toString());
                    contentValues.put(KEY_DATA, row.toString());
                    Log.i(TAG, "KEY_CREATED_DATE " + row.getString("created_at"));
                    contentValues.put(KEY_CREATED_DATE, row.getString("created_at"));


                    insert = db.insert(TABLE_OTHERNOTIFICATIONS, null, contentValues);
                    Log.i(TAG, "notifications inserted : " + insert);

                }
            }

//            Log.d(TAG, "table size " + getTableCount() + "");
            //updateMystatusOnEvents(myArrayAttendance);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            Log.i(TAG, "insertOrUpdateEvents error " + e.getMessage().toString());

        } finally {
            db.close();
        }
    }

    //Event Data Insert
    public void insertOrUpdateEvents(JSONArray data, JSONArray myArrayAttendance) {

        db = getWritableDatabase();
        db.beginTransaction();

        try{
            Log.i(TAG,"Delete All Events");
            db.execSQL("delete from "+ TABLE_EVENTS);
        }catch (Exception e){
            Log.i(TAG,"Delete ALL ROWS EXCEPTION "+e.getMessage());
        }

        long insert = 0;
        try {
            Log.i(TAG, "insertOrUpdateEvents " + data.length());

            for (int i = 0; i < data.length(); i++) {
                JSONObject row = data.getJSONObject(i);
                Cursor cursor = checkForExsistRow(row.get("eventid").toString());
                if (cursor != null) {
                    Log.d(TAG, "cursor if ");
                    if (cursor.moveToFirst()) {
                        Log.d(TAG, "cursor while " + cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK)));
                        String serverPk = cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(KEY_DATA, row.toString());
                        String where = KEY_SERVER_PK + " = " + serverPk;
                        // String[] whereArgs = new String[]{serverPk};
                        try {
                            db.update(TABLE_EVENTS, contentValues, where, null);
                            Log.d(TAG, "UPDATED EVENT ID " + serverPk);
                        } catch (Exception e) {
                            Log.i(TAG, "error updating " + e.getMessage().toString());
                        }
                    }

                } else {

                    Log.i(TAG, "insert new row");
                    ContentValues contentValues = new ContentValues();
                    int eventId = Integer.parseInt(row.get("eventid").toString());
                    Log.i(TAG, "KEY_SERVER_PK " + eventId);
                    contentValues.put(KEY_SERVER_PK, eventId);
                    Log.i(TAG, "KEY_DATA " + row.toString());
                    contentValues.put(KEY_DATA, row.toString());
                    Log.i(TAG, "KEY_CREATED_DATE " + row.getString("created_at"));
                    contentValues.put(KEY_CREATED_DATE, row.getString("created_at"));

                    if(row.has("myRSVP")){
                        contentValues.put(KEY_RSVP, row.getString("myRSVP"));
                    }else {
                        contentValues.putNull(KEY_RSVP);
                    }

                    contentValues.putNull(KEY_IS_REQUEST);

                    insert = db.insert(TABLE_EVENTS, null, contentValues);
                    Log.i(TAG, "event inserted : " + insert);

                }
            }

//            Log.d(TAG, "table size " + getTableCount() + "");
            updateMystatusOnEvents(myArrayAttendance);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            Log.i(TAG, "insertOrUpdateEvents error " + e.getMessage().toString());

        } finally {
            db.close();
        }
    }

    //Delete Important Notifications
    public void deleteAllNotifications(){


        db = getWritableDatabase();
        db.beginTransaction();

        try{
            Log.i(TAG,"Delete All Notifications");
            db.execSQL("delete from "+ TABLE_IMPORTNOTIFICATIONS);

            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            Log.i(TAG,"Delete ALL ROWS EXCEPTION "+e.getMessage());
        } finally {
            db.close();
        }

    }

    //Delete Other Notifications
    public void deleteAllOtherNotifications(){


        db = getWritableDatabase();
        db.beginTransaction();

        try{
            Log.i(TAG,"Delete All Notifications");
            db.execSQL("delete from "+ TABLE_OTHERNOTIFICATIONS);

            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            Log.i(TAG,"Delete ALL ROWS EXCEPTION "+e.getMessage());
        } finally {
            db.close();
        }

    }

    //Delete Event
    public void deleteAllEvents(){


        db = getWritableDatabase();
        db.beginTransaction();

        try{
            Log.i(TAG,"Delete All Events");
            db.execSQL("delete from "+ TABLE_EVENTS);

            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            Log.i(TAG,"Delete ALL ROWS EXCEPTION "+e.getMessage());
        } finally {
            db.close();
        }

    }


    public void insertOrUpdateEvent(Event data) {

        db = getWritableDatabase();
        db.beginTransaction();

        long insert = 0;
        try {

            Cursor cursor = checkForExsistRow(String.valueOf(data.getEventid()));
            if (cursor != null) {
                Log.d(TAG, "cursor if ");
                if (cursor.moveToFirst()) {
                    Log.d(TAG, "cursor while " + cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK)));
                    String serverPk = cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK));
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(KEY_DATA, data.toString());
                    String where = KEY_SERVER_PK + " = " + serverPk;

                    try {
                        db.update(TABLE_EVENTS, contentValues, where, null);
                        Log.d(TAG, "UPDATED EVENT ID " + serverPk);
                    } catch (Exception e) {
                        Log.i(TAG, "error updating " + e.getMessage().toString());
                    }
                }

            } else {

                Log.i(TAG, "insert new row");
                ContentValues contentValues = new ContentValues();
                int eventId = data.getEventid();
                contentValues.put(KEY_SERVER_PK, eventId);
                contentValues.put(KEY_DATA, data.toString());
                contentValues.put(KEY_CREATED_DATE, data.getCreated_at());
                contentValues.put(KEY_RSVP, data.getRsvp());
                //contentValues.putNull(KEY_RSVP);
                contentValues.putNull(KEY_IS_REQUEST);

                insert = db.insert(TABLE_EVENTS, null, contentValues);
                Log.i(TAG, "event inserted : " + insert);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {

            Log.i(TAG, "insertOrUpdateEvents error " + e.getMessage().toString());

        } finally {
            db.close();
        }


    }



    //Get All Important Notifications
    public List<NotificationsData> getAllNotifications() {

        db = getReadableDatabase();
        db.beginTransaction();

        Cursor cursor = null;

        List<NotificationsData> notifications = new ArrayList<>();
        try {

            Log.i(TAG, "searching events");
            cursor = db.query(TABLE_IMPORTNOTIFICATIONS, null, null, null, null, null, null);

            if (cursor != null) {
                NotificationsData notification = new NotificationsData();
                while (cursor.moveToNext()) {
                    String notificationsKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                    try {

                        JSONObject tempObj = new JSONObject(notificationsKeyData);
                        Log.i(TAG, "Object from database " + tempObj.toString());

                        //String jsonArrayText = tempObj.getString("attendeeList");
                        //Log.i(TAG, "friendstatus :" + jsonArrayText.toString());

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        notification = mapper.readValue(notificationsKeyData, NotificationsData.class);

                        notifications.add(notification);
                        Log.d(TAG, "notificationsAdded to list " + notifications.toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d(TAG, "error in mapper" + ex.getMessage().toString());
                    }
                }

                Log.d(TAG, "notifications size on DBhelper " + notifications.size());
                db.setTransactionSuccessful();
                db.endTransaction();
            }

        } catch (Exception e) {
            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                db.close();
            }
        }

        return notifications;
    }

    //Get All Other Notifications
    public List<NotificationsData> getAllOtherNotifications() {

        db = getReadableDatabase();
        db.beginTransaction();

        Cursor cursor = null;

        List<NotificationsData> notifications = new ArrayList<>();
        try {

            Log.i(TAG, "searching events");
            cursor = db.query(TABLE_OTHERNOTIFICATIONS, null, null, null, null, null, null);

            if (cursor != null) {
                NotificationsData notification = new NotificationsData();
                while (cursor.moveToNext()) {
                    String notificationsKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                    try {

                        JSONObject tempObj = new JSONObject(notificationsKeyData);
                        Log.i(TAG, "Other Object from database " + tempObj.toString());

                        //String jsonArrayText = tempObj.getString("attendeeList");
                        //Log.i(TAG, "friendstatus :" + jsonArrayText.toString());

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        notification = mapper.readValue(notificationsKeyData, NotificationsData.class);

                        notifications.add(notification);
                        Log.d(TAG, "othernotificationsAdded to list " + notifications.toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d(TAG, "error in mapper" + ex.getMessage().toString());
                    }
                }

                Log.d(TAG, "notifications size on DBhelper " + notifications.size());
                db.setTransactionSuccessful();
                db.endTransaction();
            }

        } catch (Exception e) {
            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                db.close();
            }
        }

        return notifications;
    }

    //Get Events
    public List<Event> getAllEvents() {

        db = getReadableDatabase();
        db.beginTransaction();

        Cursor cursor = null;

        List<Event> events = new ArrayList<>();
        try {

            Log.i(TAG, "searching events");
            cursor = db.query(TABLE_EVENTS, null, null, null, null, null, null);

            if (cursor != null) {
                Event event = new Event();
                while (cursor.moveToNext()) {
                    String eventKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                    try {

                        JSONObject tempObj = new JSONObject(eventKeyData);
                        Log.i(TAG, "Object from database " + tempObj.toString());

                        String jsonArrayText = tempObj.getString("attendeeList");
                        Log.i(TAG, "friendstatus :" + jsonArrayText.toString());

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        event = mapper.readValue(eventKeyData, Event.class);

                        events.add(event);
                        Log.d(TAG, "eventAdded to list " + event.toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d(TAG, "error in mapper" + ex.getMessage().toString());
                    }
                }

                Log.d(TAG, "event size on DBhelper " + events.size());
                db.setTransactionSuccessful();
                db.endTransaction();
            }

        } catch (Exception e) {
            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                db.close();
            }
        }

        return events;
    }


    //Get Notifications By Notifications ID
    public NotificationsData getNotificationByNotificationId(String id) {

        db = getReadableDatabase();

        Cursor cursor = null;
        NotificationsData notificationsData = new NotificationsData();

        try {

            String arg = KEY_SERVER_PK + " = " + "\"" + id + "\"";
            cursor = db.query(TABLE_IMPORTNOTIFICATIONS, null, arg, null, null, null, null);

            if (cursor.moveToNext()) {

                String notificationsDataKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                JSONObject tempObj = new JSONObject(notificationsDataKeyData);
                Log.i(TAG, "Object from database " + tempObj.toString());

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                notificationsData = mapper.readValue(notificationsDataKeyData, NotificationsData.class);
                Log.d(TAG, "eventAdded to list " + notificationsData.toString());
            }

        } catch (Exception e) {
            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                cursor.close();
                db.close();
            }
        }

        return  notificationsData;
    }

    //Get Event By Event ID
    public Event getEventByEventId(String id) {

        db = getReadableDatabase();

        Cursor cursor = null;
        Event event = new Event();

        try {

            String arg = KEY_SERVER_PK + " = " + "\"" + id + "\"";
            cursor = db.query(TABLE_EVENTS, null, arg, null, null, null, null);

            if (cursor.moveToNext()) {

                String eventKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                JSONObject tempObj = new JSONObject(eventKeyData);
                Log.i(TAG, "Object from database " + tempObj.toString());

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                event = mapper.readValue(eventKeyData, Event.class);
                Log.d(TAG, "eventAdded to list " + event.toString());
            }

        } catch (Exception e) {
            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                cursor.close();
                db.close();
            }
        }

        return  event;
    }

    //Search My Notifications
    public List<NotificationsData> getMyNotifications() {

        db = getReadableDatabase();

        Cursor cursor = null;

        List<NotificationsData> notificationsData = new ArrayList<>();
        try {

            Log.i(TAG, "searching notifications");
            String whereMyEvents = KEY_IS_REQUEST + " =\"0\"";
            String value = "0";
            cursor = db.query(TABLE_IMPORTNOTIFICATIONS, null, KEY_IS_REQUEST + "=\'" + value + "\'", null/*KEY_IS_REQUEST +"=?", new String[]{"0"}*/, null, null, null);

            if (cursor != null) {
                NotificationsData notifications = new NotificationsData();
                while (cursor.moveToNext()) {
                    String notificationsKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                    try {

                        JSONObject tempObj = new JSONObject(notificationsKeyData);
                        Log.i(TAG, "Object from database " + tempObj.toString());

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        notifications = mapper.readValue(notificationsKeyData, NotificationsData.class);

                        int countComments = tempObj.getInt("CountComments");
                        // event.setCountComments(countComments);
                        // Log.d(TAG, "ecountComments event  " + event.getCountComments());
                        notificationsData.add(notifications);
                        Log.d(TAG, "eventAdded to list " + notifications.toString());
//                        Log.i(TAG,"place "+ event.getPlaceName().toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d(TAG, "error in mapper" + ex.getMessage().toString());
                    }


                }

                Log.d(TAG, "event size on DBhelper " + notificationsData.size());

            }

        } catch (Exception e) {

            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                db.close();
            }
        }

        return notificationsData;
    }

    //Search My Events
    public List<Event> searchMyEvents() {

        db = getReadableDatabase();

        Cursor cursor = null;

        List<Event> events = new ArrayList<>();
        try {

            Log.i(TAG, "searching events");
            String whereMyEvents = KEY_IS_REQUEST + " =\"0\"";
            String value = "0";
            cursor = db.query(TABLE_EVENTS, null, KEY_IS_REQUEST + "=\'" + value + "\'", null/*KEY_IS_REQUEST +"=?", new String[]{"0"}*/, null, null, null);

            if (cursor != null) {
                Event event = new Event();
                while (cursor.moveToNext()) {
                    String eventKeyData = cursor.getString(cursor.getColumnIndex(KEY_DATA));

                    try {

                        JSONObject tempObj = new JSONObject(eventKeyData);
                        Log.i(TAG, "Object from database " + tempObj.toString());

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        event = mapper.readValue(eventKeyData, Event.class);

                        int countComments = tempObj.getInt("CountComments");
                        // event.setCountComments(countComments);
                        // Log.d(TAG, "ecountComments event  " + event.getCountComments());
                        events.add(event);
                        Log.d(TAG, "eventAdded to list " + event.toString());
//                        Log.i(TAG,"place "+ event.getPlaceName().toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d(TAG, "error in mapper" + ex.getMessage().toString());
                    }


                }

                Log.d(TAG, "event size on DBhelper " + events.size());

            }

        } catch (Exception e) {

            System.out.println("error in db fetching " + e.getMessage().toString());

        } finally {
            if (db != null) {
                db.close();
            }
        }

        return events;
    }

    //Check Exits Row by Notifications
    private Cursor checkForExsistRowNotifications(String id) {
        Log.i(TAG, "checkForExsisRow");
        //  SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        String arg = KEY_SERVER_PK + " = " + "\"" + id + "\"";
        cursor = db.query(TABLE_IMPORTNOTIFICATIONS, null, arg, null, null, null, null);
        Log.i(TAG, "cursor size " + cursor.getCount());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.i(TAG, "duplicate row found " + cursor.getString(cursor.getColumnIndex(KEY_DATA)));
            }
        }
        if (cursor.getCount() != 0) {
            Log.i(TAG, "cursor not null");
            return cursor;
        } else {
            Log.i(TAG, "cursor null");
            return null;
        }
    }

    //Check Exits Row by Event
    private Cursor checkForExsistRow(String id) {
        Log.i(TAG, "checkForExsisRow");
        //  SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        String arg = KEY_SERVER_PK + " = " + "\"" + id + "\"";
        cursor = db.query(TABLE_EVENTS, null, arg, null, null, null, null);
        Log.i(TAG, "cursor size " + cursor.getCount());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.i(TAG, "duplicate row found " + cursor.getString(cursor.getColumnIndex(KEY_DATA)));
            }
        }
        if (cursor.getCount() != 0) {
            Log.i(TAG, "cursor not null");
            return cursor;
        } else {
            Log.i(TAG, "cursor null");
            return null;
        }
    }

    //Update My status On Notifications
    private void updateMystatusOnNotifications(JSONArray myArrayAttendance) {

        JSONArray myAttendance = myArrayAttendance;
        Log.d(TAG, "updateMystatusOnNotifications " + myAttendance.length());
        for (int i = 0; i < myAttendance.length(); i++) {

            try {
                Log.d(TAG, "updateMystatusOnNotifications try");
                JSONObject data = myAttendance.getJSONObject(i);
                String serverKeyPk = data.getString("event_id");
                Cursor cursor = null;
                String arg = KEY_SERVER_PK + " = " + "\"" + serverKeyPk + "\"";
                cursor = db.query(TABLE_IMPORTNOTIFICATIONS, null, arg, null, null, null, null);
                Log.d(TAG, "updateMystatusOnNotifications try " + serverKeyPk + "  " + cursor.getCount());
                if (cursor.getCount() != 0) {
                    Log.i(TAG, "updating my status cursor not null");
                    if (cursor.moveToFirst()) {
                        String serverPk = cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK));
                        ContentValues contentValues = new ContentValues();
                        String where = KEY_SERVER_PK + " = " + serverPk;
                        // String[] whereArgs = new String[]{serverPk};
                        try {
                            db.update(TABLE_IMPORTNOTIFICATIONS, contentValues, where, null);
                            //  Log
                            Log.d(TAG, "UPDATED MY STATUS ON NOTIFICATIONS ID " + serverPk);
                        } catch (Exception e) {
                            Log.i(TAG, "UPDATED MY STATUS ON NOTIFICATIONS ID " + e.getMessage().toString());
                        }
                    }
                }


            } catch (JSONException e) {
                Log.i(TAG, "ERROR MAPPING JSON OBJECT " + e.getMessage().toString());
            }
        }


    }

    //Update My status On Events
    private void updateMystatusOnEvents(JSONArray myArrayAttendance) {

        JSONArray myAttendance = myArrayAttendance;
        Log.d(TAG, "updateMystatusOnEvents " + myAttendance.length());
        for (int i = 0; i < myAttendance.length(); i++) {

            try {
                Log.d(TAG, "updateMystatusOnEvents try");
                JSONObject data = myAttendance.getJSONObject(i);
                String serverKeyPk = data.getString("event_id");
                Cursor cursor = null;
                String arg = KEY_SERVER_PK + " = " + "\"" + serverKeyPk + "\"";
                cursor = db.query(TABLE_EVENTS, null, arg, null, null, null, null);
                Log.d(TAG, "updateMystatusOnEvents try " + serverKeyPk + "  " + cursor.getCount());
                if (cursor.getCount() != 0) {
                    Log.i(TAG, "updating my status cursor not null");
                    if (cursor.moveToFirst()) {
                        String serverPk = cursor.getString(cursor.getColumnIndex(KEY_SERVER_PK));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(KEY_RSVP, data.getString("rsvp"));
                        contentValues.put(KEY_IS_REQUEST, data.getString("is_request"));
                        String where = KEY_SERVER_PK + " = " + serverPk;
                        // String[] whereArgs = new String[]{serverPk};
                        try {
                            db.update(TABLE_EVENTS, contentValues, where, null);
                            //  Log
                            Log.d(TAG, "UPDATED MY STATUS ON EVENT ID " + serverPk);
                        } catch (Exception e) {
                            Log.i(TAG, "UPDATED MY STATUS ON EVENT ID " + e.getMessage().toString());
                        }
                    }
                }


            } catch (JSONException e) {
                Log.i(TAG, "ERROR MAPPING JSON OBJECT " + e.getMessage().toString());
            }
        }


    }

    private int getTableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENTS;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        //  db.close();
        return cnt;
    }
}

