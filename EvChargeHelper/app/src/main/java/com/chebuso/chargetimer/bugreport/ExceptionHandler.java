package com.chebuso.chargetimer.bugreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler
{
    @SuppressWarnings("unused")
    public static final String EXTRA_MY_EXCEPTION_HANDLER = "EXTRA_MY_EXCEPTION_HANDLER";

    private final Activity context;
    @SuppressWarnings("unused")
    private final Thread.UncaughtExceptionHandler rootHandler;
    private final String LINE_SEPARATOR = "\n";

    ExceptionHandler(Activity context) {
        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        Intent intent = new Intent(context, BugReportActivity.class);
        String errorReport = "************ CAUSE OF ERROR ************\n\n" +
                stackTrace.toString() +
                "\n************ DEVICE INFORMATION ***********\n" +
                "Brand: " + Build.BRAND + LINE_SEPARATOR +
                "Device: " + Build.DEVICE + LINE_SEPARATOR +
                "Model: " + Build.MODEL + LINE_SEPARATOR +
                "Id: " + Build.ID + LINE_SEPARATOR +
                "Product: " + Build.PRODUCT + LINE_SEPARATOR +
                "\n************ FIRMWARE ************\n" +
                "SDK: " + Build.VERSION.SDK_INT + LINE_SEPARATOR +
                "Release: " + Build.VERSION.RELEASE + LINE_SEPARATOR +
                "Incremental: " + Build.VERSION.INCREMENTAL + LINE_SEPARATOR;
        intent.putExtra("error", errorReport);
        context.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}
