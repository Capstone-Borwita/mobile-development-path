package com.untillness.borwita.widgets

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.untillness.borwita.R

class AppDialog(context: Context) : Dialog(context) {

    companion object {
        interface AppDialogCallback {
            fun onDismiss()
            fun onConfirm()
        }

        fun error(context: Context, message: String? = null) {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle(context.getString(R.string.gagal))
                .setIcon(R.drawable.outline_cancel_24)
                .setMessage(
                    message
                        ?: context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
                .setPositiveButton(context.getString(R.string.tutup)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        fun success(
            context: Context, message: String? = null, callback: AppDialogCallback? = null
        ) {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered,
            ).setTitle(context.getString(R.string.berhasil))
                .setIcon(R.drawable.baseline_check_circle_outline_24)
                .setMessage(message ?: context.getString(R.string.proses_telah_selesai))
                .setOnDismissListener {
                    callback?.onDismiss()
                }.setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        fun confirm(
            context: Context,
            message: String? = null,
            callback: AppDialogCallback? = null,
            title: String? = null,
        ) {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered,
            ).setTitle(title ?: context.getString(R.string.berhasil))
                .setMessage(message ?: context.getString(R.string.proses_telah_selesai))
                .setNegativeButton(context.getString(R.string.batalkan)) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(context.getString(R.string.ya)) { dialog, _ ->
                    dialog.dismiss()
                    callback?.onConfirm()
                }.show()
        }
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun showLoadingDialog() {
        show()
    }

    fun hideLoadingDialog() {
        dismiss()
    }
}