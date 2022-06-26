package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAboutUsBinding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding

class AboutUsFragment : BaseFragmentWithBinding<FragmentAboutUsBinding>(R.layout.fragment_about_us) {

    var aboutText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutText = arguments?.run { getString("aboutus") }?: kotlin.run { "" }
        binding.apply {
            tvBoutText.text = /*"\t\t" +*/ aboutText
        }
    }
}