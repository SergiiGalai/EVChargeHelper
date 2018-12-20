package com.example.dell.chargetimer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public final class UserMessage {
    public static void showSnackbar(@NonNull Activity activity, @NonNull String description) {
        Snackbar.make(activity.findViewById(android.R.id.content), description, Snackbar.LENGTH_LONG)
                .show();
    }

    static void showSnackbar(@NonNull Activity activity, @StringRes int messageId, int snackbarTimeLength) {
        Snackbar.make(activity.findViewById(android.R.id.content), messageId, snackbarTimeLength)
                .show();
    }

    static void showMultilineSnackbar(@NonNull Activity activity, @StringRes int messageId, int lineNumber) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                messageId,
                Snackbar.LENGTH_INDEFINITE);

        final TextView snackTextView = snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setMaxLines(lineNumber);

        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static void showToast(@NonNull Context context, @NonNull String description) {
        showToast(context, description, Toast.LENGTH_SHORT);
    }

    public static void showToast(@NonNull Context context, @NonNull String description, int toastTimeLength) {
        Toast.makeText(context, description, toastTimeLength).show();
    }
}
