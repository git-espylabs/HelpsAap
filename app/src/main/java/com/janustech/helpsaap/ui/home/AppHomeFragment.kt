package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppHomeBinding
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
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
    }

    private fun setListeners(){
        binding.apply {
            ivProfileBg.setOnClickListener(this@AppHomeFragment)
            layDoD.setOnClickListener(this@AppHomeFragment)
            layAds.setOnClickListener(this@AppHomeFragment)
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
        }
    }
}