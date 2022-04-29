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
import com.janustech.helpsaap.databinding.FragmentSelectLocationAndLanguageBinding
import com.janustech.helpsaap.extension.setCompoundDrawableStartIcon
import com.janustech.helpsaap.map.toLanguageDataModel
import com.janustech.helpsaap.map.toLocationDataModel
import com.janustech.helpsaap.model.LanguageDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentSelectLocationAndLanguage: BaseFragmentWithBinding<FragmentSelectLocationAndLanguageBinding>(R.layout.fragment_select_location_and_language) {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    lateinit var locationsListAdapter: ArrayAdapter<Any>
    private var locationSuggestionList = listOf<LocationDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
        }

        setObserver()
        appIntroViewModel.getLanguages()
        setLocationDropdown()
    }

    private fun setObserver(){
        appIntroViewModel.languageListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val languageList = it.data?.data
                    setLanguageList(languageList)
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
        appIntroViewModel.locationListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    val locationList = it.data?.data
                    locationSuggestionList = locationList?.map { locData -> locData.toLocationDataModel() } ?: listOf()
                    locationsListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        locationSuggestionList
                    )
                    binding.tvDropdownLocation.setAdapter(locationsListAdapter)
                }
                Status.LOADING -> {
                }
                else ->{
                    (activity as AppIntroActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
    }

    private fun setLocationDropdown(){
        locationsListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            locationSuggestionList
        )

        binding.ivClearSearch.setOnClickListener {
            binding.tvDropdownLocation.setText("")
        }

        binding.tvDropdownLocation.apply {
            threshold = 1

            setAdapter(locationsListAdapter)
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
                    val locationData = (locationsListAdapter.getItem(pos) as LocationDataModel)

                    locationData.let {
                        val locName = it.toString()
                        val locId = it.id
                        appIntroViewModel.apply{
                            userLocationName = locName
                            userLocationId = locId
                        }
                        AppPreferences.apply {
                            userLocation = locName
                            userLocationId = locId
                        }
                    }
                    (activity as AppIntroActivity).hideKeyboard()

                    if (appIntroViewModel.userLanguageId.isNotEmpty()) {
                        findNavController().navigate(FragmentSelectLocationAndLanguageDirections.actionSelectLocationFragmentToAppIntroHome())
                    }
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        appIntroViewModel.getLocationSuggestions(text.toString())
                    }
                }
                false
            }
        }
    }

    private fun setLanguageList(languageList: List<LanguageListResponseData>?){
        if (languageList != null && languageList.isNotEmpty()){
            binding.myAdapter = LanguageListAdapter(languageList.map {
                it.toLanguageDataModel()
            }){ model ->

                model.let {
                    val lanName = it.lang
                    val lanId = it.id
                    appIntroViewModel.apply {
                        userLanguage = lanName
                        userLanguageId = lanId
                    }
                    AppPreferences.apply {
                        userLanguage = lanName
                        userLanguageId = lanId
                    }

                }

                if (appIntroViewModel.userLocationId.isNotEmpty()){
                    findNavController().navigate(FragmentSelectLocationAndLanguageDirections.actionSelectLocationFragmentToAppIntroHome())
                }
            }
        }
    }
}