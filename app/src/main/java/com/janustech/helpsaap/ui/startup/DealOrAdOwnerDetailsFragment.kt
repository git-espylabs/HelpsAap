package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentDealOrAdOwnerDetailsBinding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding

class DealOrAdOwnerDetailsFragment: BaseFragmentWithBinding<FragmentDealOrAdOwnerDetailsBinding>(R.layout.fragment_deal_or_ad_owner_details) {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()
    val args: DealOrAdOwnerDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = appIntroViewModel
        }

        setObserver()
    }

    private fun setObserver(){

    }
}