package com.janustech.helpsaap.utils

import android.app.Activity
import android.content.Context
import com.janustech.helpsaap.BuildConfig
import com.razorpay.Checkout
import org.json.JSONObject

class PaymentUtils(var activity: Activity) {

    fun startPayment() {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()
        co.setKeyID(BuildConfig.RZP_KID)

        try {
            val options = JSONObject()
            options.put("name", "Razorpay Corp")
            options.put("description", "Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
//            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount", "10")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email", "gaurav.kumar@example.com")
            prefill.put("contact", "9876543210")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}