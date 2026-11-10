package com.smarthotel.app.ui.auth

import android.text.InputType
import android.widget.EditText

internal fun togglePasswordVisibility(field: EditText) {
    val visible = field.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    field.inputType = if (visible) {
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    } else {
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }
    field.setSelection(field.text.length)
}
