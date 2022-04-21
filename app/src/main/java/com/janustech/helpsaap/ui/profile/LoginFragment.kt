package com.janustech.helpsaap.ui.profile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentLayoutLoginBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.map.toUserData
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.startup.AppIntroActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragmentWithBinding<FragmentLayoutLoginBinding>(R.layout.fragment_layout_login) {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = profileViewModel
        }

        if (BuildConfig.DEBUG){
            binding.apply {
                etUserName.setText(profileViewModel.userName)
                etPassword.setText( profileViewModel.password)
            }
        }

        setSignupPrompt()
        setObserver()
    }

    private fun setObserver(){
        profileViewModel.loginResponseReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    (activity as LoginActivity).hideProgress()
                    handleLoginResponse(it.data?.data)
                }
                Status.LOADING -> {
                    (activity as LoginActivity).showProgress()
                }
                else ->{
                    (activity as LoginActivity).hideProgress()
                    (activity as LoginActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
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

    private fun handleLoginResponse(loginResponseData: LoginResponseData?){
        loginResponseData?.apply {
            AppPreferences.userId = id
            AppPreferences.userData = Gson().toJson(loginResponseData.toUserData())

            activity?.launchActivity<AppIntroActivity>()
            activity?.finish()
        }?: run {
            showAlertDialog("Invalid Server Response")
        }

    }
}