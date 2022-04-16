package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentSelectLocationAndLanguageBinding
import com.janustech.helpsaap.map.toLanguageDataModel
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.profile.LoginActivity

class FragmentSelectLocationAndLanguage: BaseFragmentWithBinding<FragmentSelectLocationAndLanguageBinding>(R.layout.fragment_select_location_and_language) {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    lateinit var locationsListAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
        }

        setLocationDropdown()
        setObserver()
    }

    private fun setObserver(){
        appIntroViewModel.languageListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as LoginActivity).hideProgress()
                    val languageList = it.data?.data
                    setLanguageList(languageList)
                }
                Status.LOADING -> {
                    (activity as LoginActivity).showProgress()
                }
                else ->{
                    (activity as LoginActivity).hideProgress()
                    (activity as LoginActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
    }

    private fun setLocationDropdown(){
        val testTist = arrayListOf<String>()
        testTist.add("Edappally, Ernakulam")
        testTist.add("Pathadipalam, Kochi")

        locationsListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            testTist
        )
        binding.tvDropdownLocation.setAdapter(locationsListAdapter)
    }

    private fun setLanguageList(languageList: List<LanguageListResponseData>?){
        if (languageList != null && languageList.isNotEmpty()){
            binding.myAdapter = LanguageListAdapter(requireContext(), languageList.map {
                it.toLanguageDataModel()
            })
        }
    }
}