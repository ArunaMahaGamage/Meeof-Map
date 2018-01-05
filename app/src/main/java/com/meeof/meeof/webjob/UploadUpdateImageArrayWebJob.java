package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.event_image_upload_dto.EventImageUploadResponse;
import com.meeof.meeof.model.update_image_upload_dto.UpdateImageUploadResponse;
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
 * Created by ransikadesilva on 12/20/17.
 */

public class UploadUpdateImageArrayWebJob extends Job {
    /**
     * The imgur client ID for OkHttp recipes. If you're using imgur for anything other than running
     * these examples, please request your own client ID! https://api.imgur.com/oauth2
     */
    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = UploadUpdateImageArrayWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final String endPoint = "/api/updates/photo/upload";

    //    private final
    private final String accessToken;
    private String[] path;
    private File imageFile;

    public UploadUpdateImageArrayWebJob(String accessToken, String[] path) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.path = path;
        this.accessToken = accessToken;

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

        File[] files = new File[path.length];
        for (int i = 0; i < path.length; i++) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path[i]);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(path[i]));
            } catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t.toString());
                t.printStackTrace();
            }

            files[i] = new File(path[i]);
        }
        Log.d(TAG,"File "+files);

        UpdateImageUploadResponse imageResponse = null;
        String url = Constant.BASE_URL + endPoint;
        String response = uploadImages(url, files, accessToken);
        Log.d(TAG, "Response : "+response.toString());
        logLargeString("response"+response.toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            imageResponse = mapper.readValue(response, UpdateImageUploadResponse.class);

            if (imageResponse != null) {
                if (imageResponse.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(imageResponse);///
                } else {
                    EventBus.getDefault().post(new UpdateImageUploadResponse(Constant.ERROR,null,null));
                }
            } else {
                EventBus.getDefault().post(new UpdateImageUploadResponse(null,null,null));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
            EventBus.getDefault().post(new UpdateImageUploadResponse(null,null,null));
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    public void logLargeString(String str) {
        if(str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str); // continuation
        }
    }

    public String uploadImages(String url, File[] media, String token) {

        String URL_UPLOAD_IMAGE = url;
        try {

            MediaType MEDIA_TYPE_PNG;

            MultipartBody.Builder buildernew = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("images", String.valueOf(media.length));

            int i = 0;
            for (File image : media) {
                Log.d(TAG,"Image :"+image.getName());
                MEDIA_TYPE_PNG = image.getName().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
                RequestBody imageBody = RequestBody.create(MEDIA_TYPE_PNG, image);
                buildernew.addFormDataPart("files["+i+"]", image.getName(), imageBody);
                i++;
            }
            MultipartBody requestBody = buildernew.build();
            final Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(URL_UPLOAD_IMAGE)
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