package com.janustech.helpsaap.utils

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.R
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.preference.AppPreferences
import com.razorpay.Checkout
import org.json.JSONObject

class PaymentUtils(var activity: Activity) {

    fun startPayment(
        amount: String = "100",
        desc: String = "Helps Aap Payment",
        userEmail: String = "",
        userPhone: String = ""
    ) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()
        co.setKeyID(BuildConfig.RZP_KID)

        try {
            val options = JSONObject()
            options.put("name", activity.getString(R.string.app_name))
            options.put("description", desc)
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount)

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            getUserObjectFromPreference()?.let { user->
                val prefill = JSONObject()
                prefill.put("email", user.email)
                prefill.put("contact", user.phoneNumber)

                options.put("prefill", prefill)
            }?: run {
                val prefill = JSONObject()
                if (userEmail.isNotEmpty()){
                    prefill.put("email", userEmail)
                }
                if (userPhone.isNotEmpty()){
                    prefill.put("contact", userPhone)
                }

                options.put("prefill", prefill)
            }
            co.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUserObjectFromPreference(): UserData? {
        val json = AppPreferences.userData
        return try {
            Gson().fromJson(json, UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}