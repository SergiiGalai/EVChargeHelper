package com.sergiigalai.chargetimer.notifications;

import android.app.Activity;
import android.net.Uri;

import com.sergiigalai.chargetimer.R;

public class ResourceProvider implements IResourceProvider {
    private Activity activity;

    public ResourceProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Uri getApplicationNotificationSoundUri() {
        return Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.carhorn4);
    }
}
