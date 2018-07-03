package com.ictuniv.eventbus3_sample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ictuniv.eventbus3_sample.LoginActivity;
import com.ictuniv.eventbus3_sample.R;
import com.ictuniv.eventbus3_sample.event.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentC extends Fragment {
    private static final String TAG="FragmentC";
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.loginBtn)
    Button loginBtn;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);

        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @OnClick(R.id.loginBtn)
    public void onClick() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageLoginEvent(LoginEvent event){
        Log.e(TAG,event.toString());
        tvContent.setText(event.toString());
    }
}
