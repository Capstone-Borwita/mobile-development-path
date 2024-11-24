package com.untillness.borwita.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.isEmpty(): Boolean {
    return this.editText?.text?.length == 0
}