package com.janustech.helpsaap.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentLayoutLoginBinding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding


class LoginFragment : BaseFragmentWithBinding<FragmentLayoutLoginBinding>(R.layout.fragment_layout_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSignupPrompt()
    }

    private fun setSignupPrompt(){
        val ss = SpannableString(getString(R.string.text_signup_prompt))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                showToast("Signup")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 24, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvPromptSignup.apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
            setTypeface(this.typeface, Typeface.BOLD)
            text = ss
        }
    }
}