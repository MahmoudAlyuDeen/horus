package com.afterapps.horus.home;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afterapps.horus.BaseActivity;
import com.afterapps.horus.R;

import butterknife.ButterKnife;

public class MainActivity
        extends BaseActivity<HomeView, HomePresenter>
        implements HomeView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @NonNull
    @Override
    public HomePresenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    protected void displayLoadingState() {

    }

    @Override
    protected void discardData() {

    }
}
