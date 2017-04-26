package com.afterapps.horus;

import android.os.Bundle;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import icepick.Icepick;
import icepick.State;

@SuppressWarnings("unused")
public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> implements MvpView {

    @State
    protected boolean isLoading;
    @State
    protected boolean userInteracted;

    protected boolean landscape;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public void showProgress() {
        isLoading = true;
        displayLoadingState();
    }

    public void hideProgress() {
        isLoading = false;
        displayLoadingState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        retainInstance = true;
        landscape = getResources().getBoolean(R.bool.landscape);
        if (savedInstanceState == null) {
            isLoading = false;
            userInteracted = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayLoadingState();
    }

    protected abstract void displayLoadingState();

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showConnectionError() {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    public void showLoadingError() {
        Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
    }

}
