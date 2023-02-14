package com.skyfolk.quantoflife.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.skyfolk.quantoflife.R

fun Context.showConfirmDialog(
    title: String,
    message: String,
    positiveButtonTitle: String,
    negativeButtonTitle: String,
    onPositiveClick: () -> Unit
) {
    val builder = AlertDialog.Builder(this, R.style.AlertDialog)
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonTitle) { _, _ ->
            onPositiveClick()
        }
        .setNegativeButton(negativeButtonTitle) { _, _ -> }
    builder.show()
}
