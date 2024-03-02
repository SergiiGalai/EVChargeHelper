package com.chebuso.chargetimer

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

object UserMessage {
    fun showSnackbar(activity: Activity, description: String) {
        getSnackbar(activity, description, Snackbar.LENGTH_LONG).show()
    }

    fun getSnackbar(activity: Activity, @StringRes messageId: Int): Snackbar {
        return Snackbar.make(
            activity.findViewById(android.R.id.content),
            messageId,
            Snackbar.LENGTH_INDEFINITE
        )
    }

    fun getSnackbar(activity: Activity, description: String, snackbarTimeLength: Int): Snackbar {
        return Snackbar.make(
            activity.findViewById(android.R.id.content),
            description,
            snackbarTimeLength
        )
    }

    fun toMultilineSnackbar(snackbar: Snackbar, lineNumber: Int): Snackbar {
        val snackTextView: TextView = snackbar.view
            .findViewById(com.google.android.material.R.id.snackbar_text)
        snackTextView.setMaxLines(lineNumber)
        snackbar.setAction(R.string.ok) { snackbar.dismiss() }
        return snackbar
    }

    fun showToast(context: Context, description: String) {
        showToast(context, description, Toast.LENGTH_SHORT)
    }

    fun showToast(context: Context, description: String, toastTimeLength: Int) {
        Toast.makeText(context, description, toastTimeLength).show()
    }

    fun showToast(context: Context, @StringRes messageId: Int, toastTimeLength: Int) {
        Toast.makeText(context, messageId, toastTimeLength).show()
    }
}

