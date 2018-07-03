package com.ictuniv.eventbus3_sample.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ictuniv.eventbus3_sample.R;
import com.ictuniv.eventbus3_sample.event.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentA extends Fragment {
    private static final String TAG="FragmentA";
    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);

        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageLoginEvent(LoginEvent event){
        Log.e(TAG,event.toString());
        tvContent.setText(event.toString());
    }
}
