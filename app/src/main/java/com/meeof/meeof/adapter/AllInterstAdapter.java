package com.meeof.meeof.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.util.Constant;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 11/30/2017.
 */

public class AllInterstAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<String> intrestList;
    private int MyId;

    public AllInterstAdapter(Context context, ArrayList<String> intrestList) {
        super();
        mContext = context;
        this.intrestList=intrestList;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MEEOF_SHARED_PREF, Context.MODE_PRIVATE);
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.interest_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ((ViewHolder)holder).TxtInterestItem.setText(intrestList.get(position).replace("\n",""));
    }

    @Override
    public int getItemCount() {
        return intrestList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {



        TextView TxtInterestItem;
        public ViewHolder(View myView) {
            super(myView);
            TxtInterestItem=(TextView)myView.findViewById(R.id.TxtInterstText);
        }
    }




}
