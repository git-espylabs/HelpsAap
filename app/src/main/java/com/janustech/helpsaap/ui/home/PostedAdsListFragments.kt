package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAdvertisementBinding
import com.janustech.helpsaap.databinding.FragmentPostedAdsListBinding
import com.janustech.helpsaap.map.toPostedAdDataModel
import com.janustech.helpsaap.model.PostedAdDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostedAdsListFragments: BaseFragmentWithBinding<FragmentPostedAdsListBinding>(R.layout.fragment_posted_ads_list) {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()

    var postedList: List<PostedAdDataModel> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = appHomeViewModel
            viewParent = this@PostedAdsListFragments

        }
        setObserver()
        appHomeViewModel.getPostedAds()
    }

    private fun setObserver(){
        appHomeViewModel.postedAdsListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    if (it.data?.isResponseSuccess() == true && it.data.data != null){
                        postedList = it.data.data.map { dat -> dat.toPostedAdDataModel() }
                        setList()
                    }
                }
                Status.LOADING -> {
                    (activity as AppHomeActivity).showProgress()
                }
                else ->{
                    (activity as AppHomeActivity).hideProgress()
                    (activity as AppHomeActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
    }

    private fun setList(){
        binding.apply {
            postedAdsAdapter = PostedAdsAdapter(requireContext(), postedList)
        }
    }
}