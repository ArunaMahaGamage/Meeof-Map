package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.event_image_upload_dto.EventImageUploadResponse;
import com.meeof.meeof.model.image_upload_dto.ImageUploadResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ransikadesilva on 10/6/17.
 */

public class UploadEventImageWebJob extends Job {
    /**
     * The imgur client ID for OkHttp recipes. If you're using imgur for anything other than running
     * these examples, please request your own client ID! https://api.imgur.com/oauth2
     */
    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = UploadEventImageWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final String endPoint = "/api/events/";

    //    private final
    private final String accessToken;
    private final int eventId;
    private String path;
    private File imageFile;

    public UploadEventImageWebJob(String accessToken, String path, int eventId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.path = path;
        this.accessToken = accessToken;
        this.eventId = eventId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();

        if (id != jobCounter.get()) {

            Log.d(TAG, "onRun id != jobCounter.get()");
            // looks like other fetch jobs has been added after me. no reason to
            // keep fetching
            // many times, cancel me, let the other one fetch tweets.
            return;
        }

        EventImageUploadResponse imageResponse = null;
        String url = Constant.BASE_URL + endPoint;
        String response = uploadImage(url, path, accessToken, eventId);
        Log.d(TAG, response.toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            imageResponse = mapper.readValue(response, EventImageUploadResponse.class);

            if (imageResponse != null) {
                if (imageResponse.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(imageResponse);///
                } else {
                    EventBus.getDefault().post(imageResponse);
                }
            } else {
                EventBus.getDefault().post(imageResponse);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
            EventBus.getDefault().post(imageResponse);
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }


    public String uploadImage(String url, String sourceImageFile, String token, int eventId) {

        String URL_UPLOAD_IMAGE = url;
        try {

            File sourceFile = new File(sourceImageFile);

            try {
                Bitmap bitmap = BitmapFactory.decodeFile(sourceFile.getPath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(sourceFile));
            } catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t.toString());
                t.printStackTrace();
            }

            Log.d(TAG, "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE = sourceImageFile.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");


            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("files[0]", sourceFile.getName(), RequestBody.create(MEDIA_TYPE, sourceFile))
                    .addFormDataPart("images", String.valueOf(1))
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(URL_UPLOAD_IMAGE + eventId + "/photos")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return new String(response.body().string());

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }

}