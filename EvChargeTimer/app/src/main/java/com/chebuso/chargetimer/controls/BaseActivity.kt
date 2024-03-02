package com.chebuso.chargetimer.controls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.chebuso.chargetimer.bugreport.ExceptionHandler


abstract class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExceptionHandler(this@BaseActivity)
    }
}