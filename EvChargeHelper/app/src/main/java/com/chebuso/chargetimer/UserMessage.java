package com.chebuso.chargetimer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class UserMessage {
    public static void showSnackbar(@NonNull Activity activity, @NonNull String description) {
        getSnackbar(activity, description, Snackbar.LENGTH_LONG).show();
    }

    static Snackbar getSnackbar(@NonNull Activity activity, @StringRes int messageId) {
        return Snackbar.make(activity.findViewById(android.R.id.content), messageId, Snackbar.LENGTH_INDEFINITE);
    }

    public static Snackbar getSnackbar(@NonNull Activity activity, @NonNull String description, int snackbarTimeLength) {
        return Snackbar.make(activity.findViewById(android.R.id.content), description, snackbarTimeLength);
    }

    public static Snackbar toMultilineSnackbar(@NonNull final Snackbar snackbar, int lineNumber) {
        final TextView snackTextView = snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setMaxLines(lineNumber);

        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        return snackbar;
    }

    static void showToast(@NonNull Context context, @NonNull String description) {
        showToast(context, description, Toast.LENGTH_SHORT);
    }

    public static void showToast(@NonNull Context context, @NonNull String description, int toastTimeLength) {
        Toast.makeText(context, description, toastTimeLength).show();
    }

    public static void showToast(@NonNull Context context, @StringRes int messageId, int toastTimeLength) {
        Toast.makeText(context, messageId, toastTimeLength).show();
    }
}
