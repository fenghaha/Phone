package com.fhh.phone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class CallRecordsFragment extends Fragment {
    private List<Call> myCallList;
    private MyClickListener clickListener;

    public void setMyCallList(List<Call> mCallList) {
        this.myCallList = mCallList;
    }
    public void setMyClickListener(MyClickListener myClickListener) {
        this.clickListener = myClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_records, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_call_records);
        CallRecyclerAdapter recyclerAdapter = new CallRecyclerAdapter(myCallList);
        recyclerAdapter.setMyClickListener(clickListener);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(manager);

        return view;
    }

}
