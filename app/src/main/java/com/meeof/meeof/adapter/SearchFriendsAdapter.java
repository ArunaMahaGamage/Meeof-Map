package com.meeof.meeof.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.model.search_my_friend_dto.Data;

import java.util.List;

/**
 * Created by ransikadesilva on 12/20/17.
 */

public class SearchFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<Data> searchTagList;
    private List<Data> selectedList;
    private static final String TAG = SearchTagsAdapter.class.getSimpleName();

    private SearchFriendsAdapter.OnItemClicked onClick;


    public interface OnItemClicked {
        void onItemClick(int position, String item);
    }


    public SearchFriendsAdapter(Context context, List<Data> searchTagList, List<Data> selectedList) {
        this.mContext = context;
        this.searchTagList=searchTagList;
        this.selectedList=selectedList;
    }

    public void updateList(List<Data> searchTagList, List<Data> selectedList) {
        this.searchTagList=searchTagList;
        this.selectedList=selectedList;
        notifyDataSetChanged();
    }

    public List<Data> getData(){
        return searchTagList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tagNameTv;


        public MyViewHolder(View view) {
            super(view);
            tagNameTv = (TextView) view.findViewById(R.id.activityName);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country_search, parent, false);

        return new SearchFriendsAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {

            String tagName = searchTagList.get(position).getFirst_name();

            SearchFriendsAdapter.MyViewHolder viewHolder = (SearchFriendsAdapter.MyViewHolder) holder;
            viewHolder.tagNameTv.setText("#"+tagName);

//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onClick.onItemClick(position, searchTagList.get(position));
//                }
//            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClick(position,searchTagList.get(position).getFirst_name()+"");
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

        return (null != searchTagList ? searchTagList.size() : searchTagList.size());
    }

    public List<Data> dataList() {
        return searchTagList;
    }


    public void setOnClick(SearchFriendsAdapter.OnItemClicked onClick) {
        this.onClick = onClick;
    }
}
