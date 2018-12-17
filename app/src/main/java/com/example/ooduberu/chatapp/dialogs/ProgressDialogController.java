package com.example.ooduberu.chatapp.dialogs;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Class used to show and hide the progress spinner.
 */
public class ProgressDialogController {
    private FragmentManager mFragmentManager;
    private ProgressDialogFragment mProgressFragment;

    public ProgressDialogController(@NonNull FragmentManager fragmentManager, String msg) {
        mFragmentManager = fragmentManager;
        mProgressFragment = ProgressDialogFragment.newInstance(msg);
    }

    public void setMessageResource(String msg) {
        if (mProgressFragment.isVisible()) {
            mProgressFragment.dismiss();
            mProgressFragment = null;
        }
        mProgressFragment = ProgressDialogFragment.newInstance(msg);
    }

    public boolean isProgressVisible(){
        return mProgressFragment.isVisible();
    }

    public void startProgress() {
        mProgressFragment.show(mFragmentManager, "progress");
    }

    public void finishProgress() {
        mProgressFragment.dismiss();
    }

    private void clearStack(){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment prev = mFragmentManager.findFragmentByTag("progress");
        if(prev != null){
             ft.remove(prev);
        }
        ft.addToBackStack(null);
    }
}