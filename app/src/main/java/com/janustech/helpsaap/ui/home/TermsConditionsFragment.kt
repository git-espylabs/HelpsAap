package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentTermsConditions1Binding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding

class TermsConditionsFragment : BaseFragmentWithBinding<FragmentTermsConditions1Binding>(R.layout.fragment_terms_conditions1) {

    var tc_url = "https://helpsaap.com/terms"
    //val args: TermsConditionsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //tcText = args.tnc
      /*  binding.apply {
            tvDescText.text = *//*"\t\t" +*//* tcText
        }*/



        (activity as AppHomeActivity).showProgress()
        binding.webView.apply {
            loadUrl(tc_url)
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