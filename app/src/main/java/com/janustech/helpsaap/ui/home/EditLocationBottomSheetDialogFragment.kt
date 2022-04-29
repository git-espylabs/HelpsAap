package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentEditLocationBottomSheetDialogBinding
import com.janustech.helpsaap.map.toLocationDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.startup.AppIntroActivity
import com.janustech.helpsaap.ui.startup.AppIntroViewModel
import com.janustech.helpsaap.utils.EditLocationListener

class EditLocationBottomSheetDialogFragment<out T>(private val viewModel: T, private val editLocationListener: EditLocationListener): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditLocationBottomSheetDialogBinding

    lateinit var locationsListAdapter: ArrayAdapter<Any>
    private var locationSuggestionList = listOf<LocationDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_location_bottom_sheet_dialog,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLocationDropdown()
        setObserver()
    }

    private fun setObserver(){
        if (viewModel is AppIntroViewModel) {
            (viewModel as AppIntroViewModel).locationListReceiver.observe(viewLifecycleOwner){
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
                    Status.LOADING ->{

                    }
                    else ->{
                        Toast.makeText(activity, "Invalid Server Response", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (viewModel is AppHomeViewModel) {
            (viewModel as AppHomeViewModel).locationListReceiver.observe(viewLifecycleOwner){
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
                    Status.LOADING ->{

                    }
                    else ->{
                        Toast.makeText(activity, "Invalid Server Response", Toast.LENGTH_SHORT).show()
                    }
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
                    if (s.toString().isNotEmpty()){
                        binding.ivClearSearch.visibility = View.VISIBLE
                    }else{
                        binding.ivClearSearch.visibility = View.GONE
                    }
                    autoCompleteTextHandler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                    autoCompleteTextHandler?.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                }
            })

            onItemClickListener =
                AdapterView.OnItemClickListener { _, _, pos, _ ->
                    val locationData = (locationsListAdapter.getItem(pos) as LocationDataModel)

                    locationData.let {
                        val locName = it.toString()
                        val locId = it.id
                        AppPreferences.apply {
                            userLocation = locName
                            userLocationId = locId
                        }
                        editLocationListener.onLocationSelected(it)
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (viewModel is AppIntroViewModel) {
                            (activity as AppIntroActivity).hideKeyboard()
                        } else if (viewModel is AppHomeViewModel){
                            (activity as AppHomeActivity).hideKeyboard()
                        }
                        dismiss()
                    }, 100)
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        if (viewModel is AppIntroViewModel) {
                            (viewModel as AppIntroViewModel).getLocationSuggestions(text.toString())
                        } else if (viewModel is AppHomeViewModel) {
                            (viewModel as AppHomeViewModel).getLocationSuggestions(text.toString())
                        }
                    }
                }
                false
            }
        }
    }
}