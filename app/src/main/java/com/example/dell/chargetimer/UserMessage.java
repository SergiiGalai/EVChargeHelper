package com.example.dell.chargetimer;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public final class UserMessage {
    public static void showSnackbar(Activity activity, String description) {
        showSnackbar(activity, description, Snackbar.LENGTH_LONG);
    }

    public static void showSnackbar(Activity activity, String description, int snackbarTimeLength) {
        Snackbar.make(activity.findViewById(android.R.id.content), description, snackbarTimeLength)
                .show();
    }

    public static void showToast(Context context, String description) {
        showToast(context, description, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String description, int toastTimeLength) {
        Toast.makeText(context, description, toastTimeLength).show();
    }
}
