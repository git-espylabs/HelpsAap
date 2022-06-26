package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentTermsConditionsBinding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding

class TermsConditionsFragment : BaseFragmentWithBinding<FragmentTermsConditionsBinding>(R.layout.fragment_terms_conditions) {

    var tcText = ""
    val args: TermsConditionsFragmentArgs by navArgs()

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tcText = args.tnc
        binding.apply {
            tvDescText.text = /*"\t\t" +*/ tcText
        }
    }
}