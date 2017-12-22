package com.fhh.phone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

/**
 * Created by FengHaHa on 2017/12/22.
 */

public class CallRecyclerAdapter extends RecyclerView.Adapter<CallRecyclerAdapter.ViewHolder> {

    private List<Call> callList;
    private MyClickListener myClickListener;

    public void setMyClickListener(MyClickListener logClickListener) {
        this.myClickListener = logClickListener;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        View callView;
        TextView nameOrPhone;
        TextView use_number;
        TextView use_name;
        ImageView photo;
        ImageView callType;
        TextView date;
        TextView time;
        TextView duration;
        LinearLayout dateLine;
        LinearLayout click ;
        View callLine;

        public ViewHolder(View view) {
            super(view);
            callView = view;
            nameOrPhone = callView.findViewById(R.id.tv_call_name_or_phone);
            callType = callView.findViewById(R.id.image_type);
            photo = callView.findViewById(R.id.image_photo);
            date = callView.findViewById(R.id.tv_call_date);
            time = callView.findViewById(R.id.tv_call_time);
            duration = callView.findViewById(R.id.tv_call_duration);
            use_number = callView.findViewById(R.id.use_phone);
            use_name = callView.findViewById(R.id.use_name);
            dateLine = callView.findViewById(R.id.date_line);
            callLine = callView.findViewById(R.id.call_line);
            click = callView.findViewById(R.id.use_to_click);
        }
    }

    public CallRecyclerAdapter(List<Call> callList) {
        this.callList = callList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Call call = callList.get(position);
                myClickListener.onClick(call.getName(),call.getPhoneNumber());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Call call = callList.get(position);

        if (call.getType() == Call.CALL_IN) {
            holder.callType.setImageResource(R.drawable.call_in);
        } else if (call.getType() == Call.CALL_OUT) {
            holder.callType.setImageResource(R.drawable.call_out);
        } else {
            holder.callType.setVisibility(View.GONE);
        }
        holder.duration.setText(call.getDuration());

        holder.use_number.setText(call.getPhoneNumber());

        holder.use_name.setText(call.getName());

        if (call.getName().equals("无")) {
            holder.nameOrPhone.setText(call.getPhoneNumber());
        } else {
            holder.nameOrPhone.setText(call.getName());
        }

        if (!(position > 0 && callList.get(position - 1).getDate().equals(call.getDate()))) {
            holder.dateLine.setVisibility(View.VISIBLE);
            holder.date.setText(call.getDate());
        }
        if (call.getPhoto() != null) {
            holder.photo.setImageURI(call.getPhoto());
        } else {
            holder.photo.setImageResource(R.drawable.photo);
        }
        if (call.getDate().equals(callList.get(position + 1).getDate())) {
            holder.callLine.setVisibility(View.VISIBLE);
        }
        holder.time.setText(call.getTime());


    }

    @Override
    public int getItemCount() {
        return callList.size();
    }
}

