package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.interests.InterestsBaseItem;
import com.meeof.meeof.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.meeof.meeof.activity.InterestsActivity.selectedCategories;
import static com.meeof.meeof.activity.InterestsActivity.subCatCheckMap;

public class ExpandListAdapter extends BaseExpandableListAdapter {
    private final String TAG = "ExpandListAdapter";
    private ArrayList<List<InterestsBaseItem>> mGroupList = new ArrayList<>();
    // private LinkedHashMap<String, LinkedHashMap<String, List<String>>> selectedCategories;

    String[] testgroupData;
    Context mContext;
    ArrayList<ArrayList<Boolean>> selectedChildCheckBoxStates = new ArrayList<>();
    ArrayList<Boolean> selectedParentCheckBoxesState = new ArrayList<>();
    TotalListener mListener;
    String currentGridCat;
    ArrayList<Integer> userinterests=new ArrayList<>();

    private String currentlySelectedId;
    private JSONArray activitiesArr;
    private JSONArray childActivitiesArr;
    private InterestMainCatGridAdapter interestMainCatGridAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public void setmListener(TotalListener mListener) {
        this.mListener = mListener;
    }

    public void setmGroupList(ArrayList<List<InterestsBaseItem>> mGroupList) {
        this.mGroupList = mGroupList;
    }

    public void setCurrentlySelectedId(String parentId){
        this.currentlySelectedId = parentId;
    }

    public class ViewHolder {
        public ImageView checkMarkDeselectedIv;
        public ImageView checkMarkSelectedIv;
        public ImageView drop_down_icon;
        public TextView dummyTextView; // View to expand or shrink the list
        public LinearLayout item_background_ll; // View to expand or shrink the list
    }

    public class ChildViewHolder {
        public ImageView checkMarkDeselectedIv;
        public ImageView checkMarkSelectedIv;
        public TextView childTextView;
    }

    public ExpandListAdapter(Context context, JSONArray activitiesArr,InterestMainCatGridAdapter interestMainCatGridAdapter,ArrayList<Integer> userList) throws JSONException {
        mContext = context;
        this.activitiesArr = activitiesArr;
        this.childActivitiesArr = getChildItemArr();
        this.interestMainCatGridAdapter = interestMainCatGridAdapter;
        this.userinterests=userList;
        //this.selectedCategories = new LinkedHashMap<>();

        sharedPreferences = mContext.getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);

        Log.d(TAG, "activitiesArr count :" + activitiesArr.length());
        Log.d(TAG, "childActivitiesArr count for activities  :" + childActivitiesArr.length());
//        rememberedCheckStatesNew();
    }

    /**
     * Called to initialize the default check states of items
     *
     */


    public void checkAll(boolean isCheckAll) {
        for (int i = 0; i < mGroupList.size(); i++) {
            selectedParentCheckBoxesState.add(i, isCheckAll);
            ArrayList<Boolean> childStates = new ArrayList<>();
            for (int j = 0; j < mGroupList.get(i).size(); j++) {
                childStates.add(isCheckAll);
            }

            selectedChildCheckBoxStates.add(i, childStates);
        }
        subCatCheckMap.put(currentGridCat, selectedChildCheckBoxStates);
    }

    public boolean isAllTrue(ArrayList<Boolean> array) {
//        for (boolean b : array) if (!b) return false;
        return true;
    }



    public JSONArray getChildItemArr() throws JSONException{

        JSONArray childArr = new JSONArray();

        for (int i = 0 ; i < activitiesArr.length(); i++){

            JSONObject activityObj = (JSONObject)  activitiesArr.get(i);
            childArr.put(getActivityArray(activityObj));
        }

        return childArr;
    }



    @Override
    public JSONObject getChild(int groupPosition, int childPosition){
        try {
            JSONArray tempArr = (JSONArray)  childActivitiesArr.get(groupPosition);
            return tempArr.getJSONObject(childPosition);

        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            JSONArray tempArr = (JSONArray)  childActivitiesArr.get(groupPosition);
            return tempArr.length();

        } catch (JSONException e) {
            e.printStackTrace();
            return  0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        try {
            return activitiesArr.get(groupPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public int getGroupCount() {
        return activitiesArr.length();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        boolean tickedOnce=false;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_header_expandable, null);
            holder = new ViewHolder();
            holder.checkMarkDeselectedIv = (ImageView) convertView.findViewById(R.id.checkMarkDeselectedIv);
            holder.checkMarkSelectedIv = (ImageView) convertView.findViewById(R.id.checkMarkSelectedIv);
            holder.drop_down_icon = (ImageView) convertView.findViewById(R.id.drop_down_icon);
            holder.dummyTextView = (TextView) convertView.findViewById(R.id.dummy_txt_view);
            holder.item_background_ll = (LinearLayout) convertView.findViewById(R.id.item_background_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            Log.d(TAG, "getGroupView item: " + activitiesArr.get(groupPosition));

            JSONObject activityObj =  (JSONObject)  activitiesArr.get(groupPosition);

            final String parentId =  activityObj.getString("parentId");
            String activityName =  activityObj.getString("name");
            final String catid =  activityObj.getString("catid");
            Boolean hasSub =  activityObj.getBoolean("has_sub");

            if(currentlySelectedId == null){
                currentlySelectedId = catid;
            }

            Log.d(TAG, "currentlySelectedId : " + currentlySelectedId);

            Boolean isAllCatSeleted = true;

            final JSONArray subActivitiesArr = getActivityArray(activityObj);
            Log.d(TAG, "subActivitiesArr count: " + subActivitiesArr.length());

            LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();
            List<String> subActCatIdArr = new ArrayList<String>();

            if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                selectedActivitiesMap = selectedCategories.get(parentId);

                if(selectedActivitiesMap != null && selectedActivitiesMap.size() > 0 && selectedActivitiesMap.get(catid) != null){
                    subActCatIdArr  =  selectedActivitiesMap.get(catid);
                }
            }

            Log.d(TAG, "group view selectedActivitiesMap " + selectedActivitiesMap);
            Log.d(TAG, "group view selectedCategories " + selectedCategories);
            Log.d(TAG, "group view activityObj " + activityObj);
            Log.d(TAG, "group view catid 123 " + catid);
            Log.d(TAG, "group view subActCatIdArr 123 " + subActCatIdArr);



            if(hasSub){
                int originalArrCount = getOriginalSuCatArrCount(activityObj);

                if(originalArrCount != subActCatIdArr.size()){
                    isAllCatSeleted = false;
                }

                holder.drop_down_icon.setVisibility(View.VISIBLE);

            }else{
                holder.drop_down_icon.setVisibility(View.INVISIBLE);
                int originalArrCount = getOriginalSuCatArrCount(activityObj);
                Log.d(TAG, "group view with no sub subActCatIdArr " + subActCatIdArr);
                Log.d(TAG, "group view with no sub subActCatIdArr catid " + catid);


                if(subActCatIdArr.contains(catid)){
                    isAllCatSeleted = true;
                }else{
                    isAllCatSeleted = false;

                }
            }

//            for(int id:userinterests){
//                for (int i = 0; i < subActivitiesArr.length(); i++) {
//                    JSONObject row = subActivitiesArr.getJSONObject(i);
//
//
//                    if(id==row.getInt("catid")){
//                        if(!tickedOnce){
//                            isAllCatSeleted=true;
//                            tickedOnce=true;
//                            Log.d(TAG,"SHOULD BE TICKED");
//                        }
//                        break;
//                    }
//
//                }
//            }



            //int totalMainActivityCount = activitiesArr.length();


            //calculate selected activities and the categories
//            int selectedActivityCountForCurrentCategory = getSelectedActivityCount(selectedActivitiesMap);
//            Log.d(TAG, "totalActivityCountForCurrentCategory " + selectedActivityCountForCurrentCategory);
//
//            int totalCategoryActvityCount = getCategoryActivityCount(activitiesArr);
//
//            if(totalCategoryActvityCount == selectedActivityCountForCurrentCategory){
//                updateGridLayout();
//            }

            //updateGridLayout();

//            Log.d(TAG, "totalCategoryActvityCount " + totalCategoryActvityCount);
            Log.d(TAG, "isAllCatSeleted for activity " + isAllCatSeleted);

            setCheckMarkDrawable(parentId, holder);

            if(isAllCatSeleted){
                Log.d(TAG, "isAllCatSeleted. checkmark is on");
                holder.checkMarkDeselectedIv.setVisibility(View.GONE);
                holder.checkMarkSelectedIv.setVisibility(View.VISIBLE);
                setSelectedActivityBackground(parentId, holder, false);

                if(isExpanded){
                    setDarkCollapseArrow(holder);
                }else{
                    setDarkDropArrow(holder);
                }

            }else {
                holder.checkMarkDeselectedIv.setVisibility(View.VISIBLE);
                holder.checkMarkSelectedIv.setVisibility(View.GONE);

                if(currentlySelectedId.equalsIgnoreCase(catid)){
                    Log.d(TAG, "group view 123 sub is selected catid" + catid);
                    Log.d(TAG, "group view 123 sub is selected currentlySelectedId" + currentlySelectedId);

                    if(subActCatIdArr.size() > 0){
                        setSelectedActivityBackground(parentId, holder, true);

                        if(isExpanded){
                            setWhiteCollapseArrow(holder);
                        }else{
                            setWhiteDropArrow(holder);
                        }

                    }else{
                        setSelectedActivityBackground(parentId, holder, false);

                        if(isExpanded){
                            setDarkCollapseArrow(holder);
                        }else{
                            setDarkDropArrow(holder);
                        }
                    }
                }else{
                    if(subActCatIdArr.size() > 0){
                        setSelectedActivityBackground(parentId, holder, true);

                        if(isExpanded){
                            setWhiteCollapseArrow(holder);

                        }else{
                            setWhiteDropArrow(holder);
                        }
                    }else{
                        setSelectedActivityBackground(parentId, holder, false);

                        if(isExpanded){
                            setDarkCollapseArrow(holder);
                        }else{
                            setDarkDropArrow(holder);
                        }
                    }
                }

            }


//            if(hasSub){
//                for(int id:userinterests){
//                    for (int i = 0; i < subActivitiesArr.length(); i++) {
//                        JSONObject row = subActivitiesArr.getJSONObject(i);
//
//                        if(id==row.getInt("catid")){
//                            if(!tickedOnce){
////                            holder.checkMarkDeselectedIv.setVisibility(View.GONE);
////                            holder.checkMarkSelectedIv.setVisibility(View.VISIBLE);
//                                setSelectedActivityBackground(parentId, holder, true);
//                                subActCatIdArr.add(id+"");
//                                selectedActivitiesMap.put(catid, subActCatIdArr);
//                                selectedCategories.put(parentId, selectedActivitiesMap);
//                                updateGridLayout();
//                                tickedOnce=true;
//                            }
//                            break;
//                        }
//
//                    }
//                }
//            }else{
//                for(int id:userinterests){
//                    if(catid.equals(id+"")){
//                        if(!tickedOnce) {
//                            holder.checkMarkDeselectedIv.setVisibility(View.GONE);
//                            holder.checkMarkSelectedIv.setVisibility(View.VISIBLE);
//
//                            subActCatIdArr.add(catid);
//                            selectedActivitiesMap.put(catid, subActCatIdArr);
//                            selectedCategories.put(parentId, selectedActivitiesMap);
//                            updateGridLayout();
//                            tickedOnce=true;
//                        }
//                        break;
//                    }
//                }
//            }




            holder.dummyTextView.setText(activityName);
            holder.checkMarkSelectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //checkbox on the parent activity is cliked. select all the activities under it

//                        if (!isExpanded)
//                            mListener.expandGroupEvent(groupPosition, isExpanded);


                    if(!sharedPreferences.getBoolean(Constant.CHANGES_AVIALABLE, false)){
                        Log.wtf(TAG, "CHANGES_AVIALABLE inside  : ");
                        editor = sharedPreferences.edit();
                        editor.putBoolean(Constant.CHANGES_AVIALABLE, true);
                        editor.commit();
                    }

                    //deselected all
                    Log.d(TAG, "all are deselceted");

                    LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();

                    if (selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null) {
                        selectedActivitiesMap = selectedCategories.get(parentId);
                    }

                    if (selectedActivitiesMap.get(catid) != null) {
                        selectedActivitiesMap.remove(catid);

                    }
                    selectedCategories.put(parentId, selectedActivitiesMap);

                    Log.d(TAG, "final selectedActivitiesMap " + selectedActivitiesMap);
                    Log.d(TAG, "final selectedCategories " + selectedCategories);

                    notifyDataSetChanged();
                    updateGridLayout();
                }
            });

            holder.checkMarkDeselectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //checkbox on the parent activity is cliked. select all the activities under it
                    try {
                        //selected all

//                        if (!isExpanded)
//                            mListener.expandGroupEvent(groupPosition, isExpanded);

                        if(!sharedPreferences.getBoolean(Constant.CHANGES_AVIALABLE, false)){
                            Log.wtf(TAG, "CHANGES_AVIALABLE inside");
                            editor = sharedPreferences.edit();
                            editor.putBoolean(Constant.CHANGES_AVIALABLE, true);
                            editor.commit();
                        }

                        LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();

                        if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                            selectedActivitiesMap = selectedCategories.get(parentId);
                        }

                        Log.d(TAG, "selectedActivitiesMap for clicked = " + selectedActivitiesMap);
                        List<String> subActCatIdArr = new ArrayList<String>();

                        if(subActivitiesArr.length() >0 ){
                            Log.d(TAG, "print all subActivities");
                            for (int i = 0 ; i < subActivitiesArr.length() ; i++) {
                                JSONObject activityObj = subActivitiesArr.getJSONObject(i);
                                String subActivityCatId = activityObj.getString("catid");
                                Log.d(TAG, "subActivityCatId = " + subActivityCatId);
                                subActCatIdArr.add(subActivityCatId);
                            }
                        }else{
                            subActCatIdArr.add(catid);
                        }

                        Log.d(TAG, "subActCatIdArr size " + subActCatIdArr.size());

                        selectedActivitiesMap.put(catid, subActCatIdArr);
                        selectedCategories.put(parentId, selectedActivitiesMap);

                        Log.d(TAG, "final selectedActivitiesMap " + selectedActivitiesMap);
                        Log.d(TAG, "final selectedCategories " + selectedCategories);

                        notifyDataSetChanged();
                        updateGridLayout();

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            //callback to expand or shrink list view from dummy text click
            holder.dummyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                //Callback to expansion of group item
                    currentlySelectedId = catid;
                    mListener.expandGroupEvent(groupPosition, isExpanded);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        boolean tickedOnce=false;
        final ChildViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_expandable, null);
            holder = new ChildViewHolder();
            holder.childTextView = (TextView) convertView.findViewById(R.id.list_element_textView);
            holder.checkMarkDeselectedIv = (ImageView) convertView.findViewById(R.id.checkMarkDeselectedIv);
            holder.checkMarkSelectedIv = (ImageView) convertView.findViewById(R.id.checkMarkSelectedIv);

            convertView.setTag(holder);

        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        try {

            JSONObject activityObj =  (JSONObject)  activitiesArr.get(groupPosition);
            final String catid =  activityObj.getString("catid");
            Boolean isSelected = false;

            JSONArray tempArr = (JSONArray)  childActivitiesArr.get(groupPosition);
            JSONObject item = tempArr.getJSONObject(childPosition);

            final String subActivityName =  item.getString("name").replaceAll("\r\n|\r", "");
            final String subActivityCatId =  item.getString("catid");
            final String parentId =  item.getString("parentId");

            holder.childTextView.setText(subActivityName);

            LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();
            List<String> subActCatIdArr = new ArrayList<String>();

            if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                selectedActivitiesMap = selectedCategories.get(parentId);

                if(selectedActivitiesMap != null && selectedActivitiesMap.size() > 0 && selectedActivitiesMap.get(catid) != null){
                    subActCatIdArr  =  selectedActivitiesMap.get(catid);
                }
            }

            Log.d(TAG, "original selectedActivitiesMap : " + selectedActivitiesMap);
            Log.d(TAG, "subActCatIdArr : " + subActCatIdArr.size());
            Log.d(TAG, "subActivityCatId original : " + subActivityCatId);

            for(String catId : subActCatIdArr){
                Log.d(TAG, "subActCatIdArr  catId: " + catId);
                Log.d(TAG, "subActivityCatId match : " + subActivityCatId);

                if(catId.equalsIgnoreCase(subActivityCatId)){
                    isSelected = true;
                    Log.d(TAG, "subActCatIdArr  catId: matched" + catId);
                    break;
                }
            }

            setCheckMarkDrawable(parentId, holder);

            Log.d(TAG, "isSelected : " + isSelected);


            ///////

//            if(userinterests!=null){
//                for(Integer interestId:userinterests){
//                    if(subActivityCatId.equals(interestId.toString())){
//                        if(!tickedOnce){
//                            userinterests.remove(interestId);
//                            tickedOnce=true;
//                            isSelected=true;
//                        }
//
//                        break;
//                    }
//                }
//            }

            if(isSelected){
                Log.d(TAG, "isSelected name : "  + subActivityName);
                holder.checkMarkSelectedIv.setVisibility(View.VISIBLE);
                holder.checkMarkDeselectedIv.setVisibility(View.GONE);

                if(!subActCatIdArr.contains(subActivityCatId)){
                    subActCatIdArr.add(subActivityCatId);
                    selectedActivitiesMap.put(catid, subActCatIdArr);
                    selectedCategories.put(parentId, selectedActivitiesMap);
                }

            }else{
                Log.d(TAG, "is not Selected : "  + subActivityName);
                holder.checkMarkSelectedIv.setVisibility(View.GONE);
                holder.checkMarkDeselectedIv.setVisibility(View.VISIBLE);
            }

            holder.childTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.checkMarkSelectedIv.getVisibility()==View.VISIBLE){
                        holder.checkMarkSelectedIv.performClick();
                    }else if(holder.checkMarkDeselectedIv.getVisibility()==View.VISIBLE){
                        holder.checkMarkDeselectedIv.performClick();
                    }
                }
            });
            ///////



            holder.checkMarkSelectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentlySelectedId = catid;
                    LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();
                    List<String> subActCatIdArr = new ArrayList<String>();

                    if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                        selectedActivitiesMap = selectedCategories.get(parentId);

                        if(selectedActivitiesMap != null && selectedActivitiesMap.size() > 0 && selectedActivitiesMap.get(catid) != null){
                            subActCatIdArr  =  selectedActivitiesMap.get(catid);
                        }
                    }

                    Log.d(TAG, "selectedActivitiesMap clicked 1 : " + selectedActivitiesMap);
                    Log.d(TAG, "subActivityCatId clicked 1 : " + subActivityCatId);

                    if(subActCatIdArr.contains(subActivityCatId)){
                        subActCatIdArr.remove(subActivityCatId);
                    }

                    selectedActivitiesMap.put(catid, subActCatIdArr);
                    selectedCategories.put(parentId, selectedActivitiesMap);

                    Log.d(TAG, "selectedActivitiesMap clicked 2  : " + selectedActivitiesMap);
                    Log.d(TAG, "selectedCategories clicked 2 : " + selectedCategories);
                    notifyDataSetChanged();
                    updateGridLayout();



                    if(!sharedPreferences.getBoolean(Constant.CHANGES_AVIALABLE, false)){
                        Log.wtf(TAG, "CHANGES_AVIALABLE inside");
                        editor = sharedPreferences.edit();
                        editor.putBoolean(Constant.CHANGES_AVIALABLE, true);
                        editor.commit();
                    }
                }
            });


            holder.checkMarkDeselectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentlySelectedId = catid;
                    LinkedHashMap<String, List<String>> selectedActivitiesMap = new LinkedHashMap();
                    List<String> subActCatIdArr = new ArrayList<String>();

                    if(selectedCategories != null && selectedCategories.size() > 0 && selectedCategories.get(parentId) != null){
                        selectedActivitiesMap = selectedCategories.get(parentId);

                        if(selectedActivitiesMap != null && selectedActivitiesMap.size() > 0 && selectedActivitiesMap.get(catid) != null){
                            subActCatIdArr  =  selectedActivitiesMap.get(catid);
                        }
                    }

                    Log.d(TAG, "selectedActivitiesMap clicked 1 : " + selectedActivitiesMap);
                    Log.d(TAG, "subActivityCatId clicked 1 : " + subActivityCatId);

                    if(!subActCatIdArr.contains(subActivityCatId)){
                        subActCatIdArr.add(subActivityCatId);
                    }

                    selectedActivitiesMap.put(catid, subActCatIdArr);
                    selectedCategories.put(parentId, selectedActivitiesMap);

                    Log.d(TAG, "selectedActivitiesMap clicked 2  : " + selectedActivitiesMap);
                    Log.d(TAG, "selectedCategories clicked 2 : " + selectedCategories);
                    notifyDataSetChanged();
                    updateGridLayout();



                    if(!sharedPreferences.getBoolean(Constant.CHANGES_AVIALABLE, false)){
                        Log.wtf(TAG, "CHANGES_AVIALABLE inside");
                        editor = sharedPreferences.edit();
                        editor.putBoolean(Constant.CHANGES_AVIALABLE, true);
                        editor.commit();
                    }


                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }



        return convertView;
    }

    /**
     * Called to reflect the sum of checked prices
     *
     */

    private void updateGridLayout() {
        Log.d(TAG, "updateGridLayout");
        mListener.onTotalChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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

    private int getOriginalSuCatArrCount(JSONObject activityObj) throws JSONException {
        JSONArray tempArr = getActivityArray(activityObj);
        return  tempArr.length();
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
                totalCount = activitiesArr.length();
            }
        }
        return  totalCount;
    }

    private void setCheckMarkDrawable(String parentId, ViewHolder holder){

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

    private void setCheckMarkDrawable(String parentId, ChildViewHolder holder){

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

    private void setSelectedActivityBackground(String parentId, ViewHolder holder, Boolean isSelected){
        Log.d(TAG,"PARENT ID: "+parentId);
        switch (parentId){
            case "1":

                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.sports_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sports_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);

                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }

                break;

            case "2":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.board_games_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.board_games_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);
                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }
                break;

            case "3":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.creative_corner_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.creative_corner_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);
                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }

                break;

            case "4":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.gaming_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gaming_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);
                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }

                break;

            case "5":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.going_out_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.going_out_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);
                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }
                break;

            case "6":
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.parks_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.parks_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);
                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }
                break;

            default:
                if(isSelected){
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.sports_expandable_item_selected));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sports_expandable_item_selected));
                    }
                    holder.dummyTextView.setTextColor(Color.WHITE);
                }else {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_background_ll.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    } else {
                        holder.item_background_ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.expandable_item));
                    }
                    holder.dummyTextView.setTextColor(Color.GRAY);
                }
                break;
        }
    }

    private void setWhiteDropArrow(ViewHolder holder){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.drop_down_icon.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.drop_down_arrow_white));
        } else {
            holder.drop_down_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.drop_down_arrow_white));
        }
    }

    private void setDarkDropArrow(ViewHolder holder){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.drop_down_icon.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.drop_down_arrow));
        } else {
            holder.drop_down_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.drop_down_arrow));
        }
    }

    private void setWhiteCollapseArrow(ViewHolder holder){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.drop_down_icon.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.collapse_arrow_white));
        } else {
            holder.drop_down_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.collapse_arrow_white));
        }
    }

    private void setDarkCollapseArrow(ViewHolder holder){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.drop_down_icon.setBackgroundDrawable( ContextCompat.getDrawable(mContext, R.drawable.collapse_icon));
        } else {
            holder.drop_down_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.collapse_icon));
        }
    }

}
