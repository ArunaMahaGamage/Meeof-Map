package com.meeof.meeof.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meeof.meeof.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.meeof.meeof.activity.InterestsActivity.selectedCategories;

/**
 * Created by Anuja Ranwalage on 10/8/2017.
 */

public class InterestMainCatGridAdapter extends BaseAdapter {

    String[] result;
    Context context;
    int[] imageId;
    private Boolean isIntialLoading;
    private Boolean isClicked;
    private JSONArray interestsCategoriesArr;
    List<String> titleList;
    private static LayoutInflater inflater = null;
    private static final String TAG = "GridAdapter";
    private String currentlySelectedId;

    private OnItemClicked onClick;
    private OnCheckBoxClicked onCheckBoxClicked;

    private ArrayList<Integer> userInterestList;

    private JSONArray selectedItemList;

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position) throws JSONException;
    }

    public interface OnCheckBoxClicked {
        void onCheckBoxClick(int position, boolean b);
    }

    public InterestMainCatGridAdapter(Context mainActivity, int[] prgmImages, JSONArray interestsCategoriesArr,ArrayList<Integer> userInterestList) {
        // TODO Auto-generated constructor stub
        this.userInterestList=userInterestList;
        this.interestsCategoriesArr = interestsCategoriesArr;
        context = mainActivity;
        imageId = prgmImages;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        isClicked=false;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return interestsCategoriesArr.length();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
        ImageView checkMarkDeselectedIv;
        ImageView checkMarkSelectedIv;
        LinearLayout itemClickLlBtn;
        RelativeLayout outerRl;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_grid_interest, null);



        holder.itemClickLlBtn = (LinearLayout)rowView.findViewById(R.id.itemClickLlBtn);
        holder.tv = (TextView) rowView.findViewById(R.id.gridCatLabelTv);
        holder.checkMarkDeselectedIv = (ImageView) rowView.findViewById(R.id.checkMarkDeselectedIv);
        holder.checkMarkSelectedIv = (ImageView) rowView.findViewById(R.id.checkMarkSelectedIv);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.outerRl = (RelativeLayout)rowView.findViewById(R.id.outerRl);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(120), dpToPx(120));
        holder.outerRl.setLayoutParams(params);

        try {
            Log.d(TAG,"isIntialLoading "+isIntialLoading);

            if(isIntialLoading){
                setIntialLoading(false);
                onClick.onItemClick(position);
            }



            JSONObject tempCategoryItem =  (JSONObject) interestsCategoriesArr.get(position);
            Log.d(TAG, "categoryItem: " + tempCategoryItem.toString());


            Boolean isAllCatSeleted = false;
            Boolean isAnyCatSeleted = false;


            Iterator<String> keysIterator = tempCategoryItem.keys();
            String gridKey = (String) keysIterator.next();
            final String parentId = gridKey.split(",")[0];
            JSONObject categoryItem =  tempCategoryItem.getJSONObject(gridKey);
            String categoryName = categoryItem.getString("name");
            String catid = categoryItem.getString("catid");

            Log.d(TAG, "grid view getView tempCategoryItem: " + tempCategoryItem);
            Log.d(TAG, "grid view getView categoryItem: " + categoryItem);
            Log.d(TAG, "parentId:123 " + parentId);
            Log.d(TAG, "currentlySelectedId:123 " + currentlySelectedId);


            if(currentlySelectedId == null){
                currentlySelectedId = parentId;
            }

            holder.tv.setText(categoryName);
            //holder.img.setImageResource(imageId[position]);

            LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();

            if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                selectedActivitiesMap = selectedCategories.get(parentId);
            }

            if(selectedActivitiesMap != null && selectedActivitiesMap.size() > 0 && selectedActivitiesMap.get(catid) != null){
                List<String> tempArr  = selectedActivitiesMap.get(catid);

                if(tempArr.size()>0){
                    isAnyCatSeleted = true;
                }
            }
            Log.d(TAG, "currentlySelectedId:catid " + catid);
            Log.d(TAG, "grid layout isAnyCatSeleted: " + isAnyCatSeleted);
            Log.d(TAG, "grid layout selectedActivitiesMap: " + selectedActivitiesMap);

            int selectedActivityCountForCurrentCategory = getSelectedActivityCount(selectedActivitiesMap);
            Log.d(TAG, "selectedActivityCountForCurrentCategory " + selectedActivityCountForCurrentCategory);

            final JSONArray activitiesArr = getActivtieArray(categoryItem);






            int totalCategoryActvityCount = getCategoryActivityCount(activitiesArr);
            Log.d(TAG, "totalCategoryActvityCount " + totalCategoryActvityCount);

            if(totalCategoryActvityCount == selectedActivityCountForCurrentCategory){
                Log.d(TAG, "grid layout all selected ");
                isAllCatSeleted = true;
            }else  if(selectedActivityCountForCurrentCategory > 0){
                setSelectedCategoryDrawable(parentId, holder, true);
            }else {
                setSelectedCategoryDrawable(parentId, holder, false);
            }

            setCheckMarkDrawable(parentId, holder);

            if(currentlySelectedId.equalsIgnoreCase(parentId)){
                Log.d(TAG, "currentlySelectedId equalsIgnoreCase " + parentId);
                setSelectedBackgroundDrawable(parentId, holder);

                setSelectedCategoryDrawable(parentId, holder, true);

                if(isAllCatSeleted){
                    holder.checkMarkSelectedIv.setVisibility(View.VISIBLE);
                    holder.checkMarkDeselectedIv.setVisibility(View.GONE);

                }else{
                    holder.checkMarkSelectedIv.setVisibility(View.GONE);
                    holder.checkMarkDeselectedIv.setVisibility(View.VISIBLE);
                }
            }else{
                if(isAllCatSeleted){
                    setSelectedCategoryDrawable(parentId, holder, true);
                }

                holder.checkMarkSelectedIv.setVisibility(View.GONE);
                holder.checkMarkDeselectedIv.setVisibility(View.GONE);

                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable(null);
                } else {
                    holder.itemClickLlBtn.setBackground(null);
                }
            }


            if(!isClicked){
                if(userInterestList!=null){
                    setSelectedActvities(activitiesArr,userInterestList);
                }

            }

            holder.checkMarkSelectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isClicked=true;
                    deselectAllActivities(activitiesArr);
                    onCheckBoxClicked.onCheckBoxClick(position, false);
                }
            });

            holder.checkMarkDeselectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isClicked=true;
                    selectAllActivities(activitiesArr);
                    onCheckBoxClicked.onCheckBoxClick(position, true);
                }
            });



            holder.itemClickLlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        isClicked=true;
                        currentlySelectedId =  parentId;
                        Log.d(TAG, "currentlySelectedId:1111after " + currentlySelectedId);
//                        if(userInterestList!=null){
//                            setSelectedActvities(activitiesArr,userInterestList);
//                        }
                        notifyDataSetChanged();
                        onClick.onItemClick(position);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        isClicked=true;

        super.notifyDataSetChanged();
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }

    public void setOnCheckBoxClicked(OnCheckBoxClicked onClick)
    {
        this.onCheckBoxClicked=onClick;
    }

    private JSONArray getActivtieArray(JSONObject tempCategoryItem) throws JSONException {

        JSONArray activitiesArr = new JSONArray();

        Object subCat = tempCategoryItem.get("sub_cat");

        if (subCat instanceof JSONObject) {

            JSONObject subCategoryJSONObj = tempCategoryItem.getJSONObject("sub_cat");
            Iterator<String> keysIterator = subCategoryJSONObj.keys();

            while (keysIterator.hasNext()) {
                String gridKey = (String) keysIterator.next(); //NEED
                activitiesArr.put(subCategoryJSONObj.getJSONObject(gridKey));
            }

        } else if (subCat instanceof JSONArray) {
            activitiesArr = (JSONArray) subCat;
        }
        return  activitiesArr;
    }

    private int getSelectedActivityCount(LinkedHashMap<String,List<String>> selectedActivitiesMap) throws JSONException {

        int totalCount = 0;

        for (Map.Entry<String, List<String>> entry : selectedActivitiesMap.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            totalCount += value.size();
        }

        return  totalCount;
    }

    private int getCategoryActivityCount(JSONArray activitiesArr) throws JSONException {

        int totalCount = 0;

        for (int i = 0 ; i < activitiesArr.length() ; i++) {
            JSONObject item = (JSONObject) activitiesArr.get(i);
            Boolean hasSub = item.getBoolean("has_sub");

            if(hasSub){
                Log.d(TAG, "getCategoryActivityCount item " + item);
                JSONArray tempArr = getActivityArray(item);
                totalCount += tempArr.length();

                Log.d(TAG,"totalCount count  " + totalCount);
            }else{
                ++totalCount;
            }
        }
        return  totalCount;
    }

    private JSONArray getActivityArray(JSONObject tempCategoryItem) throws JSONException {

        JSONArray activitiesArr = new JSONArray();
        Log.d(TAG, "getActivityArray tempCategoryItem: " + tempCategoryItem);

        if(tempCategoryItem.has("has_sub") && tempCategoryItem.getBoolean("has_sub") != false){

            Object subCat = tempCategoryItem.get("sub_cat");
            if (subCat instanceof JSONObject) {

                JSONObject subCategoryJSONObj = tempCategoryItem.getJSONObject("sub_cat");
                Iterator<String> keysIterator = subCategoryJSONObj.keys();

                while (keysIterator.hasNext()) {
                    String gridKey = (String) keysIterator.next(); //NEED
                    activitiesArr.put(subCategoryJSONObj.getJSONObject(gridKey));
                }

            } else if (subCat instanceof JSONArray) {
                activitiesArr = (JSONArray) subCat;
            }
        }

        return  activitiesArr;
    }

    private void setCheckMarkDrawable(String parentId, Holder holder){

        switch (parentId){
            case "1":
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.blue_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_blue_color_category_selected);
                break;

            case "2":
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.green_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_green_color_category_selected);
                break;

            case "3":
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.red_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_red_color_category_selected);

                break;

            case "4":
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.purple_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_purple_color_category_selected);
                break;

            case "5":
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.pink_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_pink_color_category_selected);
                break;

            case "6":
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.orange_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_orange_color_category_selected);
                break;

            default:
                holder.checkMarkDeselectedIv.setImageResource(R.drawable.blue_color_main_category_unselected);
                holder.checkMarkSelectedIv.setImageResource(R.drawable.new_blue_color_category_selected);
                break;
        }
    }

    private void setSelectedCategoryDrawable(String parentId, Holder holder, Boolean isSelected){

        switch (parentId){
            case "1":

                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.sports_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstBlue_sports_activities));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.sports_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstBlue_sports_activities));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.sports_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.sports_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }

                break;

            case "2":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.boardgames_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstGreen_board_games));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.boardgames_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstGreen_board_games));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.boardgames_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.boardgames_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }
                break;

            case "3":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.creative_corner_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstRed_creative_corner));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.creative_corner_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstRed_creative_corner));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.creative_corner_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.creative_corner_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }

                break;

            case "4":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.gaming_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstPurple_gaming));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.gaming_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstPurple_gaming));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.gaming_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.gaming_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }

                break;

            case "5":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.going_out_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstRose_going_out));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.going_out_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstRose_going_out));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.going_out_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.going_out_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }
                break;

            case "6":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.parks_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstOrange_parks));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.parks_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstOrange_parks));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.parks_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.parks_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }
                break;

            default:
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.sports_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstBlue_sports_activities));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.sports_selected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.interstBlue_sports_activities));
                    }

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.img.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.sports_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    } else {
                        holder.img.setBackground(ContextCompat.getDrawable(context, R.drawable.sports_unselected));
                        holder.tv.setTextColor(ContextCompat.getColor(context,R.color.usualTextColor));
                    }
                }
                break;
        }
    }

    private void setSelectedBackgroundDrawable(String parentId, Holder holder){

        switch (parentId){
            case "1":
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_sports_activities));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_sports_activities));
                }
                break;

            case "2":
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_board_games));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_board_games));
                }
                break;

            case "3":
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_creative_corner));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_creative_corner));
                }

                break;

            case "4":
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_gaming));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_gaming));
                }

                break;

            case "5":
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_going_out));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_going_out));
                }
                break;

            case "6":
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_parks_amusement));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_parks_amusement));
                }
                break;

            default:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemClickLlBtn.setBackgroundDrawable( ContextCompat.getDrawable(context, R.drawable.rounded_corners_sports_activities));
                } else {
                    holder.itemClickLlBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_sports_activities));
                }
                break;
        }
    }




    private void selectAllActivities(JSONArray activitiesArr){
        Log.d(TAG, "selectAllActivities : " + activitiesArr.length());
        try{

            LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();

            for(int i = 0 ; i < activitiesArr.length() ; i ++){
                JSONObject activityObj =  (JSONObject)  activitiesArr.get(i);
                final String parentId =  activityObj.getString("parentId");
                String activityName =  activityObj.getString("name");
                final String catid =  activityObj.getString("catid");
                Boolean hasSub =  activityObj.getBoolean("has_sub");


//                if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
//                    selectedActivitiesMap = selectedCategories.get(parentId);
//                }

                final JSONArray subActivitiesArr;

                if(hasSub){

                    subActivitiesArr = getActivityArray(activityObj);
                    Log.d(TAG, "subActivitiesArr for clicked = " + subActivitiesArr);

                    List<String> subActCatIdArr = new ArrayList<String>();

                    for (int j = 0 ; j < subActivitiesArr.length() ; j++) {
                        //eg : going out act > eating new > breakfast
                        JSONObject subActivityObj = subActivitiesArr.getJSONObject(j);
                        String subActivityCatId = subActivityObj.getString("catid");
                        Log.d(TAG, "add to subActivitiesArr " + subActivityCatId);
                        Log.d(TAG, "add to subActivitiesArr from " + subActivityObj);
                        subActCatIdArr.add(subActivityCatId);

                    }
                    selectedActivitiesMap.put(catid, subActCatIdArr);

                }else{
                    Log.d(TAG, "subActivitiesArr for clicked no sub cat 1234 " );
                    List<String> subActCatIdArr = new ArrayList<String>();
                    subActCatIdArr.add(catid);
                    selectedActivitiesMap.put(catid, subActCatIdArr);
                }

                selectedCategories.put(parentId, selectedActivitiesMap);

                Log.d(TAG, "final selectedActivitiesMap " + selectedActivitiesMap);
                Log.d(TAG, "final selectedCategories " + selectedCategories);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void setSelectedActvities(JSONArray activitiesArr,ArrayList<Integer> userList){
        Log.d(TAG, "selectAllActivities : " + activitiesArr.length()+" userList: "+userList.toString());
        try{

            LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();

            for(int i = 0 ; i < activitiesArr.length() ; i ++){
                JSONObject activityObj =  (JSONObject)  activitiesArr.get(i);
                final String parentId =  activityObj.getString("parentId");
                String activityName =  activityObj.getString("name");
                final String catid =  activityObj.getString("catid");
                Boolean hasSub =  activityObj.getBoolean("has_sub");


//                if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
//                    selectedActivitiesMap = selectedCategories.get(parentId);
//                }

                final JSONArray subActivitiesArr;

                if(hasSub){

                    subActivitiesArr = getActivityArray(activityObj);
                    Log.d(TAG, "subActivitiesArr for clicked = " + subActivitiesArr);

                    List<String> subActCatIdArr = new ArrayList<String>();

                    for (int j = 0 ; j < subActivitiesArr.length() ; j++) {
                        //eg : going out act > eating new > breakfast
                        JSONObject subActivityObj = subActivitiesArr.getJSONObject(j);
                        String subActivityCatId = subActivityObj.getString("catid");
                        Log.d(TAG, "add to subActivitiesArr " + subActivityCatId);
                        Log.d(TAG, "add to subActivitiesArr from " + subActivityObj);
                        for(Integer userListId:userList){
                            if(subActivityCatId.equals(userListId.toString())){
                                subActCatIdArr.add(subActivityCatId);
                                selectedActivitiesMap.put(catid, subActCatIdArr);
                                break;
                            }
                        }


                    }


                }else{
                    Log.d(TAG, "subActivitiesArr for clicked no sub cat 1234 " );
                    List<String> subActCatIdArr = new ArrayList<String>();
                    for(Integer userListId:userList){
                        if(catid.equals(userListId.toString())){
                            subActCatIdArr.add(catid);
                            selectedActivitiesMap.put(catid, subActCatIdArr);
                            break;
                        }
                    }

                }

                selectedCategories.put(parentId, selectedActivitiesMap);

                Log.d(TAG, "final selectedActivitiesMap " + selectedActivitiesMap);
                Log.d(TAG, "final selectedCategories " + selectedCategories);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void deselectAllActivities(JSONArray activitiesArr){

        Log.d(TAG, "selectAllActivities : " + activitiesArr.length());
        try{

            for(int i = 0 ; i < activitiesArr.length() ; i ++){

                JSONObject activityObj =  (JSONObject)  activitiesArr.get(i);
                final String parentId =  activityObj.getString("parentId");
                final String catid =  activityObj.getString("catid");

                if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                    LinkedHashMap<String, List<String>>  tempSelectedCategoryIdsDict  = selectedCategories.get(parentId);

                    if(tempSelectedCategoryIdsDict != null && tempSelectedCategoryIdsDict.size() > 0 && tempSelectedCategoryIdsDict.get(catid) != null){
                        tempSelectedCategoryIdsDict.remove(catid);
                        selectedCategories.put(parentId, tempSelectedCategoryIdsDict);
                    }
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public Boolean getIntialLoading() {
        return isIntialLoading;
    }

    public void setIntialLoading(Boolean intialLoading) {
        isIntialLoading = intialLoading;
    }


    private int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}

