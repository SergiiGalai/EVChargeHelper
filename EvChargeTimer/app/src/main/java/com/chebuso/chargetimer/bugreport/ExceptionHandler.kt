package com.chebuso.chargetimer.bugreport

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Process
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess


class ExceptionHandler internal constructor(private val context: Activity) :
    Thread.UncaughtExceptionHandler {
    @Suppress("unused")
    private val rootHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        val stackTrace = StringWriter()
        e.printStackTrace(PrintWriter(stackTrace))
        val intent = Intent(context, BugReportActivity::class.java)
        val errorReport = """
            ************ CAUSE OF ERROR ************
            $stackTrace
            
            ************ DEVICE INFORMATION ***********
            Brand: ${Build.BRAND}
            Device: ${Build.DEVICE}
            Model: ${Build.MODEL}
            Id: ${Build.ID}
            Product: ${Build.PRODUCT}
            
            ************ FIRMWARE ************
            SDK: ${Build.VERSION.SDK_INT}
            Release: ${Build.VERSION.RELEASE}
            Incremental: ${Build.VERSION.INCREMENTAL}
            
            """.trimIndent()

        intent.putExtra("error", errorReport)
        context.startActivity(intent)

        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    companion object {
        @Suppress("unused")
        val EXTRA_MY_EXCEPTION_HANDLER = "EXTRA_MY_EXCEPTION_HANDLER"
    }
}

