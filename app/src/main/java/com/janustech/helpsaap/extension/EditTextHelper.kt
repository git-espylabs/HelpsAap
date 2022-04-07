package com.janustech.helpsaap.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

fun EditText.setActionListener(onSearch: () -> Unit) {
    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(textView: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearch()
                return true
            }
            return false
        }
    })
}

fun EditText.setTextChangedListener(afterTextChanged: (query: String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable.toString().trim())
        }
    })
}