package com.untillness.bangkitsubmissionandroidintermediate1.widgets

import android.view.View
import com.google.android.material.snackbar.Snackbar

class AppSnackbar {
    companion object {
        fun show(view: View, message: String) {
            Snackbar.make(
                view, message, Snackbar.LENGTH_LONG
            ).show()
        }

    }

}