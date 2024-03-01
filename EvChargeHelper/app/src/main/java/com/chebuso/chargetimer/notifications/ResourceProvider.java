package com.chebuso.chargetimer.notifications;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.chebuso.chargetimer.R;

public class ResourceProvider implements IResourceProvider {
    private final Activity activity;

    public ResourceProvider(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Uri getApplicationNotificationSoundUri() {
        return Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.carhorn4);
    }
}
