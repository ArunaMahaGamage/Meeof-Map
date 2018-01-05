package com.meeof.meeof.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransika on 7/11/2017.
 */
public class SearchCountryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<String> dataList;
    private List<String> filterList;
    private static final String TAG = SearchCountryAdapter.class.getSimpleName();

    private OnItemClicked onClick;
//    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClicked {
        void onItemClick(int position, String item);
    }


    public SearchCountryAdapter(Context context, List<String> countryList) {
        this.mContext = context;
        this.dataList = countryList;
        this.filterList = new ArrayList<String>();
        filterList.addAll(this.dataList);
    }

    public void clearList() {
        this.filterList.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView activityName;
        private final TextView categoryName;
        private final ImageView checkMarkIv;

        public MyViewHolder(View view) {
            super(view);
            activityName = (TextView) view.findViewById(R.id.activityName);
            categoryName = (TextView) view.findViewById(R.id.categoryName);
            checkMarkIv = (ImageView) view.findViewById(R.id.checkMarkIv);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country_search, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {

            String item = filterList.get(position);

            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.activityName.setText(item);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClick(position, filterList.get(position));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {

        return (null != filterList ? filterList.size() : dataList.size());
    }

    public List<String> dataList() {
        return filterList;
    }





    public void filter(final String text) {
        Log.d(TAG, "SearchText: " + text + "\ninside filter");
        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "filterList size: " + filterList.size() + "\ninside filter");
                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    Log.d(TAG, "TextUtils.isEmpty(text) filterList size: " + filterList.size() + "\ninside filter");
                    filterList.addAll(dataList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (String item : dataList) {
                        if (item.toLowerCase().startsWith(text.toLowerCase())) {

                            Log.d(TAG, "for (FriendListItem item : listItem) filter : " + item.toString() + "\ninside filter");
                            filterList.add(item);

                        }

                    }
                }

                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}

