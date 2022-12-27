package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppHomeBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.startup.AppIntroActivity
import com.janustech.helpsaap.utils.PaymentUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppHomeFragment : BaseFragmentWithBinding<FragmentAppHomeBinding>(R.layout.fragment_app_home), View.OnClickListener {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appHomeViewModel
        }
        setListeners()
        setObserver()
    }

    private fun setListeners(){
        binding.apply {
            ivProfileBg.setOnClickListener(this@AppHomeFragment)
            ivNeedsBg.setOnClickListener(this@AppHomeFragment)
            layDoD.setOnClickListener(this@AppHomeFragment)
            layAds.setOnClickListener(this@AppHomeFragment)
            btnPromptOffer.setOnClickListener(this@AppHomeFragment)
            layPostedAds.setOnClickListener(this@AppHomeFragment)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ivProfileBg -> {
                findNavController().navigate(AppHomeFragmentDirections.actionAppHomeFragmentToEditProfileFragment())
            }
            R.id.layDoD -> {
                findNavController().navigate(AppHomeFragmentDirections.actionAppHomeFragmentToDealOfDayFragment())
            }
            R.id.layAds -> {
                findNavController().navigate(AppHomeFragmentDirections.actionAppHomeFragmentToAdvertiseFragment())
            }
            R.id.btnPromptOffer -> {
                AddOfferBottomSheetDialogFragment(appHomeViewModel).show(
                    childFragmentManager,
                    "AddOfferBottomSheetDialogFragment"
                )
            }
            R.id.layPostedAds -> {
                findNavController().navigate(AppHomeFragmentDirections.actionAppHomeFragmentToPostedAdsFragment())
            }
            R.id.ivNeedsBg -> {
                activity?.apply {
                    launchActivity<AppIntroActivity>()
                    finish()
                }
            }
        }
    }

    private fun setObserver(){
        appHomeViewModel.offerSubmitStatusReceiver?.observe(viewLifecycleOwner){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            (activity as AppHomeActivity).hideProgress()
                            if (it.data?.isResponseSuccess() == true) {
                                (activity as AppHomeActivity).showAlertDialog("Offer added successfully!")
                            } else {
                                (activity as AppHomeActivity).showAlertDialog("Error occurred! Please try again")
                            }
                        }
                        Status.LOADING -> {
                            (activity as AppHomeActivity).showProgress()
                        }
                        else ->{
                            (activity as AppHomeActivity).hideProgress()
                            (activity as AppHomeActivity).showToast(it.message?:"Invalid Server Response")
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}