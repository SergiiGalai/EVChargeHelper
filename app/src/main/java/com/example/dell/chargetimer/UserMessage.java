package com.example.dell.chargetimer;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public final class UserMessage {
    public static void showSnackbar(Activity activity, String description) {
        Snackbar.make(activity.findViewById(android.R.id.content), description, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void showToast(Context context, String description) {
        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
    }
}
