package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentWebvPrivayPolicyBinding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding


class PrivacyPolicyFragment: BaseFragmentWithBinding<FragmentWebvPrivayPolicyBinding>(R.layout.fragment_webv_privay_policy) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppHomeActivity).showProgress()
        binding.webView.apply {
            loadUrl("http://helpsaap.com/privacy")
            settings.also {
                it.loadsImagesAutomatically = true
                it.javaScriptEnabled = true;
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    (activity as AppHomeActivity).hideProgress()
                }
            }
        }
    }
}