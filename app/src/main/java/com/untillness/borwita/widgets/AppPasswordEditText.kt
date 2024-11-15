package com.untillness.borwita.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.untillness.borwita.R

class AppPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs), View.OnTouchListener {

    private var prefixIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_password_24) as Drawable

    init {
        startIconDrawable = prefixIcon

        setOnTouchListener(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.hint = context.getString(R.string.masukkan_password)
        this.editText?.maxLines = 1
        this.endIconMode = END_ICON_PASSWORD_TOGGLE

        this.editText?.doOnTextChanged { inputText, _, _, _ ->
            if (inputText.toString().isEmpty()) {
                error = context.getString(R.string.password_wajib_diisi)
                return@doOnTextChanged
            }

            error = if (inputText.toString().length < 8) {
                context.getString(R.string.password_tidak_boleh_kurang_dari_8_karakter)
            } else {
                isErrorEnabled = false
                null
            }
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}
