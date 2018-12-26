package com.sergiigalai.chargetimer.bugreport;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.sergiigalai.chargetimer.R;

public class BugReportActivity extends BaseActivity
{
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugreport);

        error = findViewById(R.id.error);
        error.setMovementMethod(new ScrollingMovementMethod());
        error.setText(getIntent().getStringExtra("error"));
    }
}
