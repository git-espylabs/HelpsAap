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
import com.janustech.helpsaap.ui.home.EditLocationBottomSheetDialogFragment
import com.janustech.helpsaap.ui.profile.LoginActivity
import com.janustech.helpsaap.utils.EditLocationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppIntroHomeFragment: BaseFragmentWithBinding<FragmentAppIntroHomeBinding>(R.layout.fragment_app_intro_home), EditLocationListener {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
            btnLogin.setOnClickListener { activity?.launchActivity<LoginActivity>()}
            btnRegister.setOnClickListener {
                (activity as AppIntroActivity).showProgress()
                activity?.launchActivity<SignupActivity>()
            }
            tvLocation.setOnClickListener {
                EditLocationBottomSheetDialogFragment(appIntroViewModel, this@AppIntroHomeFragment).show(
                    childFragmentManager,
                    "EditLocationFragment"
                )
            }
        }

        setObserver()
        setSearchList()
    }

    override fun onResume() {
        super.onResume()
        if (appIntroViewModel.userSelectedCategory.isNotEmpty()){
            appIntroViewModel.getDealsOfTheDay(appIntroViewModel.userSelectedCategory)
        }
    }

    override fun onStop() {
        super.onStop()
        appIntroViewModel._categoriesReceiver.value = null
        (activity as AppIntroActivity).hideProgress()
    }

    override fun onLocationSelected(location: LocationDataModel) {
        location.let {
            appIntroViewModel.userLocationName = it.toString()
            appIntroViewModel.userLocationId = it.id
            binding.tvLocation.text = it.toString()
        }
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
                    setDealsOfDay(listOf())
                }
            }

            appIntroViewModel.getAdsList(appIntroViewModel.userSelectedCategory)
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
                    setAdsList(listOf())
                }
            }
        }

        appIntroViewModel.categoriesReceiver?.observe(viewLifecycleOwner){ result ->
            result?.let {
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
                        binding.actSearch.apply {
                            setAdapter(categoriesListAdapter)
                            showDropDown()
                        }
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

    }

    private fun setSearchList(){
        binding.ivClearSearch.setOnClickListener {
            binding.actSearch.setText("")
        }

        binding.actSearch.apply {

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    binding.apply {
                        if (s.toString().isNotEmpty()){
                            binding.ivClearSearch.visibility = View.VISIBLE
                        }else{
                            binding.ivClearSearch.visibility = View.GONE
                        }
                    }
                    autoCompleteTextHandler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                    autoCompleteTextHandler?.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                }
            })

            onItemClickListener =
                OnItemClickListener { _, _, pos, _ ->
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    catData.let {
                        appIntroViewModel.userSelectedCategory = it.id
                        appIntroViewModel.userSelectedCategoryName = it.category
                        (activity as AppIntroActivity).hideKeyboard()
                        setText("")
                        findNavController().navigate(AppIntroHomeFragmentDirections.actionAppIntroHomeToAppIntroSearchList())
                    }
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        appIntroViewModel.getCategories(text.toString())
                    }
                }
                false
            }
        }
    }

    private fun setDealsOfDay(dOdList: List<DealOfDayDataModel>?){
        if (dOdList != null && dOdList.isNotEmpty()){
            binding.apply {
                tvPromptDeals.visibility = View.VISIBLE
                rvDealOfDay.visibility = View.VISIBLE
                dOdAdapter = DealOfDayAdapter(requireContext(), dOdList)
            }
        }else{
            binding.apply {
                tvPromptDeals.visibility = View.GONE
                rvDealOfDay.visibility = View.GONE
            }
        }
    }

    private fun setAdsList(adsList: List<AdsDataModel>?){
        if (adsList != null && adsList.isNotEmpty()){
            binding.apply {
                tvPromptAds.visibility = View.VISIBLE
                rvAds.visibility = View.VISIBLE
                adsListAdapter = AdsListAdapter(requireContext(), adsList)
            }
        }else{
            binding.apply {
                tvPromptAds.visibility = View.GONE
                rvAds.visibility = View.GONE
            }
        }
    }


}