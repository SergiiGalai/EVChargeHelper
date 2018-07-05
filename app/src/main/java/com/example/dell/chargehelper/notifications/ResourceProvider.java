package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.net.Uri;

import com.example.dell.chargehelper.R;

public class ResourceProvider implements IResourceProvider {
    private Activity activity;

    ResourceProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Uri getApplicationNotificationSoundUri() {
        return Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.carhorn4);
    }
}
