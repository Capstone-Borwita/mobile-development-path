package com.untillness.borwita.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.untillness.borwita.R
import com.untillness.borwita.helpers.AppHelpers

class AppEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs), View.OnTouchListener {

    private var prefixIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.outline_alternate_email_24) as Drawable

    init {
        startIconDrawable = prefixIcon

        setOnTouchListener(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.hint = context.getString(R.string.masukkan_email)
        this.editText?.maxLines = 1
        this.editText?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        this.editText?.doOnTextChanged { inputText, _, _, _ ->
            if (inputText.toString().isEmpty()) {
                error = context.getString(R.string.email_wajib_diisi)
                return@doOnTextChanged
            }

            if (AppHelpers.isValidEmail(inputText.toString())) {
                this@AppEmailEditText.error = null
                isErrorEnabled = false
            } else {
                this@AppEmailEditText.error =
                    context.getString(R.string.email_yang_kamu_masukkan_tidak_valid)
            }
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}
