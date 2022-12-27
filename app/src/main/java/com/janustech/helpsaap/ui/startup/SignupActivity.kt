package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivitySignupBinding
import com.janustech.helpsaap.ui.base.BaseActivity
import com.janustech.helpsaap.ui.profile.ProfileViewModel
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SignupActivity : BaseActivity<ActivitySignupBinding>(),
    PaymentResultListener {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayoutBinding(R.layout.activity_signup)
        setToolbarProperties(false, null)
        binding?.lifecycleOwner = this
    }

    override fun onCreateToolbar(): Toolbar? {
        return null
    }

    override fun onCreateLoader(): View? {
        return binding?.loadingView?.loaderView
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Log.e("Payyyy", "onPaymentSuccess: " + razorpayPaymentID)
        viewModel.updateRazorpayStatusRegisterPayment(true, razorpayPaymentID?:"0")
    }

    override fun onPaymentError(code: Int, response: String?) {
        try {
            Log.e("Payyyy", "onPaymentError: " + response)
            response?.let {
                val resObj = JSONObject(response)
                val error = resObj.getString("error")
                val errorObj = JSONObject(error)
                val errDesc = errorObj.getString("description")
                viewModel.updateRazorpayStatusRegisterPayment(false, errDesc?:"0")
            }?: run {
                viewModel.updateRazorpayStatusRegisterPayment(false, "Could not complete payment. Something went wrong.")
            }
        } catch (e: Exception) {
            viewModel.updateRazorpayStatusRegisterPayment(false, "Could not complete payment. Something went wrong.")
        }
    }
}