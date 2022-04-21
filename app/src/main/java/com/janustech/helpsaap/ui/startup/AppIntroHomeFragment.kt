package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppIntroHomeBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.map.toAdsDataModel
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.map.toDealsOfDayDataModel
import com.janustech.helpsaap.model.AdsDataModel
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.model.DealOfDayDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.profile.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppIntroHomeFragment: BaseFragmentWithBinding<FragmentAppIntroHomeBinding>(R.layout.fragment_app_intro_home) {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
            btnLogin.setOnClickListener { activity?.launchActivity<LoginActivity>()}
            btnRegister.setOnClickListener {
                (activity as AppIntroActivity).showProgress()
                activity?.launchActivity<SignupActivity>()
            }
        }

        setObserver()
        setSearchList()
        appIntroViewModel.getCategories()
        appIntroViewModel.getDealsOfTheDay()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppIntroActivity).hideProgress()
    }

    private fun setObserver(){
        appIntroViewModel.dealsOfDay.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    setDealsOfDay(dataList?.map { dOd -> dOd.toDealsOfDayDataModel() } ?: listOf())
                }
                Status.LOADING -> {
                    (activity as AppIntroActivity).showProgress()
                }
                else ->{
                    (activity as AppIntroActivity).hideProgress()
                    (activity as AppIntroActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }

            appIntroViewModel.getAdsList()
        }

        appIntroViewModel.adsListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    setAdsList(dataList?.map { adls -> adls.toAdsDataModel() } ?: listOf())
                }
                Status.LOADING -> {
                    (activity as AppIntroActivity).showProgress()
                }
                else ->{
                    (activity as AppIntroActivity).hideProgress()
                    (activity as AppIntroActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }

        appIntroViewModel.categoriesReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    categoriesSuggestionList = dataList?.map { dat -> dat.toCategoryDataModel() } ?: listOf()
                    categoriesListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        categoriesSuggestionList
                    )
                    binding.actSearch.setAdapter(categoriesListAdapter)
                }
                Status.LOADING -> {
                    (activity as AppIntroActivity).showProgress()
                }
                else ->{
                    (activity as AppIntroActivity).hideProgress()
                    (activity as AppIntroActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }

    }

    private fun setSearchList(){

        categoriesListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoriesSuggestionList
        )
        binding.actSearch.apply {
            threshold = 1

            setAdapter(categoriesListAdapter)

            onItemClickListener =
                OnItemClickListener { _, _, pos, _ ->
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    catData.let {
                        val locName = it.category
                        val locId = it.id
                    }
                }
        }
    }

    private fun setDealsOfDay(dOdList: List<DealOfDayDataModel>?){
        if (dOdList != null && dOdList.isNotEmpty()){
            binding.tvPromptDeals.visibility = View.VISIBLE
            binding.dOdAdapter = DealOfDayAdapter(requireContext(), dOdList)
        }else{
            binding.tvPromptDeals.visibility = View.GONE
        }
    }

    private fun setAdsList(adsList: List<AdsDataModel>?){
        if (adsList != null && adsList.isNotEmpty()){
            binding.tvPromptAds.visibility = View.VISIBLE
            binding.adsListAdapter = AdsListAdapter(requireContext(), adsList)
        }else{
            binding.tvPromptAds.visibility = View.GONE
        }
    }


}