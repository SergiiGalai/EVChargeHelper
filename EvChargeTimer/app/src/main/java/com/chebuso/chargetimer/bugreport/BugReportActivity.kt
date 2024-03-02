package com.chebuso.chargetimer.bugreport

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.controls.BaseActivity


class BugReportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bugreport)
        findViewById<TextView>(R.id.error).apply {
            movementMethod = ScrollingMovementMethod()
            text = intent.getStringExtra("error")
        }
    }
}

