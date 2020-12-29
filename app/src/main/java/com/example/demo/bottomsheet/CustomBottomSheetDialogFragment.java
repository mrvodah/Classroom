package com.example.demo.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = "CustomBottomSheetDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) != null || isAdded()) return;
        try {
            manager.executePendingTransactions();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();

        } catch (IllegalStateException e) {
            Log.d(TAG, "Exception", e);
        }
    }

}
