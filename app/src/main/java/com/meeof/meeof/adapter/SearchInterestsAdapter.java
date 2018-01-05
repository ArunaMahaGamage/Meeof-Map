package com.meeof.meeof.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ransika on 7/11/2017.
 */
public class SearchInterestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private boolean isFromCreateEvent;
    private JSONArray dataList;
    private JSONArray interestsCategoriesArr;
    private List<JSONObject> selectedActivitiesArr;
    private static final String TAG = "SearchInterestsAdapter";
    private JSONObject selectedItemCreateEvent;

    public SearchInterestsAdapter(Context context,
                                  JSONArray listItems,
                                  JSONArray interestsCategoriesArr,
                                  List<JSONObject> selectedActivitiesArr,
                                  boolean isFromCreateEvent,
                                  JSONObject item) {
        this.mContext = context;
        this.dataList = listItems;
        this.interestsCategoriesArr = interestsCategoriesArr;
        this.selectedActivitiesArr = selectedActivitiesArr;


//        Log.d(TAG, "Inside selectItemForCreateEvent object: " + item.toString());
        if (isFromCreateEvent) {
            if (item != null) {
                Log.d(TAG, "Inside selectItemForCreateEvent object: " + item.toString());
                this.isFromCreateEvent = isFromCreateEvent;
                this.selectedItemCreateEvent = item;
            }
        }


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
                .inflate(R.layout.item_interest_search_result, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {

            JSONObject item = dataList.getJSONObject(position);
            Log.d(TAG, "onBindViewHolder: " + item);
            String activityName = item.getString("name").replaceAll("\r\n|\r", "");

            String tier1 = item.getString("tier1");
            String tier2 = item.getString("tier2");
            String catId = item.getString("id");

            Log.d(TAG, "onBindViewHolder:tier1 " + tier1);
            Log.d(TAG, "onBindViewHolder:tier2 " + tier2);
            Log.d(TAG, "onBindViewHolder:catId " + catId);
            Log.d(TAG, "onBindViewHolder:activityName " + activityName);

            String categoryName = getCategoryName(tier1);
            Log.d(TAG, "onBindViewHolder:final categoryName " + categoryName);

            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.activityName.setText(activityName);
            viewHolder.categoryName.setText(categoryName);

            if (!isFromCreateEvent) {

                if (selectedActivitiesArr.contains(item)) {
                    Log.d(TAG, "selectedActivitiesArr:contains " + item);
                    viewHolder.activityName.setTextColor(ContextCompat.getColor(mContext, R.color.brightBlue));
                    viewHolder.checkMarkIv.setVisibility(View.VISIBLE);
                } else if(containsItemWithId(selectedActivitiesArr, item)){
                    Log.d(TAG, "selectedActivitiesArr:contains2 " + item);
                    viewHolder.activityName.setTextColor(ContextCompat.getColor(mContext, R.color.brightBlue));
                    viewHolder.checkMarkIv.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "selectedActivitiesArr: not contains");
                    viewHolder.activityName.setTextColor(ContextCompat.getColor(mContext, R.color.usualTextColor));
                    viewHolder.checkMarkIv.setVisibility(View.INVISIBLE);
                }

                Log.d(TAG, "Inside selectItemForCreateEvent object: " + item.toString() == null ? "Null" : item.toString());
                Log.d(TAG, "isFromCreateEvent: bool: " + isFromCreateEvent);

            } else if (isFromCreateEvent) {
                if (item != null) {
                    Log.d(TAG, "Inside selectItemForCreateEvent object: " + item.toString());
                    Log.d(TAG, "Inside selectItemForCreateEvent object: " + item.toString() + " : " + selectedItemCreateEvent);

                    if (item.getString("id").equals(selectedItemCreateEvent.getString("id"))) {
                        viewHolder.activityName.setTextColor(ContextCompat.getColor(mContext, R.color.brightBlue));
                        viewHolder.checkMarkIv.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.activityName.setTextColor(ContextCompat.getColor(mContext, R.color.usualTextColor));
                        viewHolder.checkMarkIv.setVisibility(View.INVISIBLE);
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "onBindViewHolder exception:" + e.toString());
        }
    }

    private boolean containsItemWithId(List<JSONObject> selectedActivitiesArr, JSONObject item) throws JSONException {
        boolean value = false;

        Log.wtf(TAG, "item obj: " + item.toString());

        for(int i = 0 ; i < selectedActivitiesArr.size() ; i++){

            JSONObject obj = (JSONObject) selectedActivitiesArr.get(i);

            Log.wtf(TAG, "containsItemWithId obj: " + obj);

            if(obj.getString("id").equalsIgnoreCase(item.getString("id"))){
                value = true;
                Log.wtf(TAG, "containsItemWithId index: " + i);
                break;
            }
        }

        return  value;
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
        return dataList.length();
    }

    public JSONArray dataList() {
        return dataList;
    }


//    private String getCategoryName(String parentId){
//        Log.d(TAG, "getCategoryName:parentId " +parentId);
//        String categoryName = "";
//
//        Set<String> keysSet  = interestsCategoriesArr.keySet();
//        Log.d(TAG, "getCategoryName:keysSet " + keysSet.toString());
//
//        for (String key : keysSet) {
//            String keyId = key.split(",")[0];
//
//            if(keyId.equalsIgnoreCase(parentId)){
//                Log.d(TAG, "getCategoryName:keyId matched" +keyId);
//                categoryName = key.split(",")[1];
//                break;
//            }
//        }
//
//        return categoryName;
//    }


    private String getCategoryName(String parentId) {
        try {
            Log.d(TAG, "getCategoryName:parentId " + parentId);
            String name = "";

            for (int i = 0; i < interestsCategoriesArr.length(); i++) {
                JSONObject tempCategoryItem = (JSONObject) interestsCategoriesArr.get(i);
                Iterator<String> keysIterator = tempCategoryItem.keys();
                String gridKey = (String) keysIterator.next();
                String tempId = gridKey.split(",")[0];
                Log.d(TAG, "getCategoryName:tempId " + tempId);

                if (tempId.equalsIgnoreCase(parentId)) {
                    Log.d(TAG, "getCategoryName matched");
                    name = gridKey.split(",")[1];
                    break;
                }
            }
            return name;

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public List<JSONObject> getSelectedActivitiesArr() {
        return selectedActivitiesArr;
    }

    public void setSelectedActivitiesArr(List<JSONObject> selectedActivitiesArr) {
        this.selectedActivitiesArr = selectedActivitiesArr;
    }
}

