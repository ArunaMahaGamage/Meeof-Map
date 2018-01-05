package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.activity.AddUpdateBiginingActivity;
import com.meeof.meeof.model.update_image_upload_dto.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ransikadesilva on 12/19/17.
 */

public class GridViewAdapter extends ArrayAdapter<Photo> {
    private static final String TAG = GridViewAdapter.class.getSimpleName();
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Photo> mGridData = new ArrayList<Photo>();

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<Photo> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<Photo> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            //holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.userImagesIv);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Photo item = mGridData.get(position);
        Log.d(TAG, "getImage" + item.getFile_name());
        //holder.imageView.setImageURI(Uri.parse(item.getFile_name()));
        String filePath="https://meeofbucket.s3.amazonaws.com/dev/public/uImages/medium/"+item.getFile_name();
        Picasso.with(mContext).load(Uri.parse(filePath))
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .fit()
                .centerCrop()
                .into(holder.imageView);
        return row;
    }
    private void removeImage(){
        Log.i(TAG,"Delete");
    }
    static class ViewHolder {
        ImageView deleteMultiIv;
        ImageView imageView;
    }
}