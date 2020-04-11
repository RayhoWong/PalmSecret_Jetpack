package com.palmapp.master.baselib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
public abstract class BaseMultipleMVPActivity extends BaseActivity implements IView {
    private ArrayList<IMultipleBasePresenter> mPresenters = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPresenters();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onAttach(this, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onRestart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onDetach();
        }
    }

    @Override
    public void finish() {
        for (IMultipleBasePresenter presenter : mPresenters) {
            if(presenter.onFinishEvent()){
                return;
            }
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        for (IMultipleBasePresenter presenter : mPresenters) {
            if(presenter.onBackPressedEvent()){
                return;
            }
        }
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (IMultipleBasePresenter presenter : mPresenters) {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected abstract void addPresenters();

    protected void addToPresenter(IMultipleBasePresenter presenter) {
        mPresenters.add(presenter);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
