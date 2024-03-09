package com.chebuso.chargetimer

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

object UserMessage {
    fun showSnackbar(activity: Activity, text: String) {
        getSnackbar(activity, text, Snackbar.LENGTH_LONG).show()
    }

    fun getSnackbar(activity: Activity, @StringRes messageId: Int): Snackbar {
        Log.d(TAG, "getSnackbar")
        val view: View = activity.findViewById(android.R.id.content)
        return Snackbar.make(view, messageId, Snackbar.LENGTH_INDEFINITE)
    }

    fun getSnackbar(activity: Activity, text: String, snackbarTimeLength: Int): Snackbar {
        Log.d(TAG, "getSnackbar")
        val view: View = activity.findViewById(android.R.id.content)
        return Snackbar.make(view, text, snackbarTimeLength)
    }

    fun toMultilineSnackbar(snackbar: Snackbar, lineNumber: Int): Snackbar {
        val snackTextView: TextView = snackbar.view
            .findViewById(com.google.android.material.R.id.snackbar_text)
        snackTextView.setMaxLines(lineNumber)
        snackbar.setAction(R.string.ok) { snackbar.dismiss() }
        return snackbar
    }

    fun showToast(context: Context, text: String) {
        showToast(context, text, Toast.LENGTH_LONG)
    }

    fun showToast(context: Context, text: String, toastTimeLength: Int) {
        Log.d(TAG, "showToast")
        Toast.makeText(context, text, toastTimeLength).show()
    }

    fun showToast(context: Context, @StringRes messageId: Int, toastTimeLength: Int) {
        Log.d(TAG, "showToast")
        Toast.makeText(context, messageId, toastTimeLength).show()
    }

    private val TAG = UserMessage::class.java.simpleName
}

