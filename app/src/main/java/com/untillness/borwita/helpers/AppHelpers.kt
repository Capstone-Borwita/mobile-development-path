package com.untillness.borwita.helpers

import android.util.Log
import android.view.View
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppHelpers {
    companion object {
        fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            return emailRegex.matches(email)

        }

        fun log(value: String) {
            Log.i("SAID", value)
        }

        fun formatDate(date: String): String {
            val parsedDate = LocalDateTime.parse(
                date, DateTimeFormatter.ISO_DATE_TIME
            )
            val utcPlus7DateTime = parsedDate.plusHours(7)

            val formattedDate =
                utcPlus7DateTime.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy - HH:mm"))

            return formattedDate
        }

        fun hideView(views: List<View>) {
            views.forEach {
                it.visibility = View.GONE
            }
        }

        fun showView(views: List<View>) {
            views.forEach {
                it.visibility = View.VISIBLE
            }
        }

        fun getFileSizeInMB(file: File): String {
            val fileSizeInBytes = file.length()
            val fileSizeInMB = fileSizeInBytes / (1024.0 * 1024)
            return "Size $fileSizeInMB MB"
        }
    }
}