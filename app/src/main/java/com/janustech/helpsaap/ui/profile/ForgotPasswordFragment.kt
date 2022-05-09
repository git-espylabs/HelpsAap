package com.janustech.helpsaap.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentForgetPasswordBinding
import com.janustech.helpsaap.databinding.FragmentLayoutLoginBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.home.AppHomeActivity
import com.janustech.helpsaap.ui.startup.SignupActivity

class ForgotPasswordFragment : BaseFragmentWithBinding<FragmentForgetPasswordBinding>(R.layout.fragment_forget_password), View.OnClickListener {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = profileViewModel
            viewParent = this@ForgotPasswordFragment
            groupEmail.visibility = View.VISIBLE
            groupOtp.visibility = View.GONE
            groupResetPass.visibility = View.GONE
            btnContinueLogin.visibility = View.GONE
        }

        setObserver()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSubmitOtp ->{
                val optTxt = binding.otpIew.text.toString()
                if (optTxt.isNotEmpty()){
                    profileViewModel.processVerifyOtp(optTxt)
                }else{
                    showToast("Please enter the valid OTP received in your email")
                }
            }
            R.id.btnResetPass ->{
                binding.apply {
                    val newPassTxt = etNewPassword.text.toString()
                    val confirmPassTxt = etConfmNewPassword.text.toString()

                    if (newPassTxt.isNotEmpty() && confirmPassTxt.isNotEmpty() && newPassTxt == confirmPassTxt){
                        profileViewModel.processResetPassword(newPassTxt)
                    }else if (newPassTxt != confirmPassTxt){
                        showToast("Password does not match!")
                    }else{
                        showToast("Please enter valid inputs!")
                    }
                }
            }
            R.id.btnContinueLogin ->{
                (activity as LoginActivity).onBackPressed()
            }
        }
    }

    private fun setObserver(){
        profileViewModel.otpSendStatusReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    (activity as LoginActivity).hideProgress()
                    profileViewModel.fgtPasCustomerId = it.data?.data?:""
                    binding.apply {
                        btnSendOtp.text = "Resend OTP"
                        tvPromptOtpEnter.text = "Enter the OTP sent to\n" + profileViewModel.fgtPasEmail
                        groupOtp.visibility = View.VISIBLE
                    }
                }
                Status.LOADING -> {
                    (activity as LoginActivity).hideKeyboard()
                    (activity as LoginActivity).showProgress()
                }
                else ->{
                    (activity as LoginActivity).hideProgress()
                    (activity as LoginActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }

        }

        profileViewModel.verifyOtpStatusReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    if (it.data?.data?.id?:"" == profileViewModel.fgtPasCustomerId){
                        (activity as LoginActivity).hideProgress()
                        showAlertDialog("OTP verified successfully, Please reset your password")
                        binding.apply {
                            groupEmail.visibility = View.GONE
                            groupOtp.visibility = View.GONE
                            groupResetPass.visibility = View.VISIBLE
                        }
                    }else{
                        (activity as LoginActivity).showAlertDialog("Failed to validate OTP! Try again.")
                    }
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

        profileViewModel.resetPassStatusReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    (activity as LoginActivity).hideProgress()
                    showAlertDialog("Your HelpsAap Password changed successfully.")
                    binding.apply {
                        groupEmail.visibility = View.GONE
                        groupOtp.visibility = View.GONE
                        groupResetPass.visibility = View.GONE
                        btnContinueLogin.visibility = View.VISIBLE
                    }
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

}