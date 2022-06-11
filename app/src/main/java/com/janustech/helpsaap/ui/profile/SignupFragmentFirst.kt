package com.janustech.helpsaap.ui.profile

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.janustech.helpsaap.R
import com.janustech.helpsaap.app.AppPermission
import com.janustech.helpsaap.databinding.FragmentRegisterBinding
import com.janustech.helpsaap.extension.*
import com.janustech.helpsaap.location.GpsListener
import com.janustech.helpsaap.location.GpsManager
import com.janustech.helpsaap.map.toLocationDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.startup.SignupActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class SignupFragmentFirst : BaseFragmentWithBinding<FragmentRegisterBinding>(R.layout.fragment_register),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    GpsListener {

    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: GoogleMap.OnCameraIdleListener? = null
    var lattitude = "0.0"
    var longitude = "0.0"
    var locationMarker: Marker? = null

    lateinit var locationsListAdapter: ArrayAdapter<Any>
    private var locationSuggestionList = listOf<LocationDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[AppPermission.PERMISSION_LOCATION[0]] == true && permissions[AppPermission.PERMISSION_LOCATION[1]] == true) {
                mMap?.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                    setOnMyLocationButtonClickListener(this@SignupFragmentFirst)
                    setOnMyLocationClickListener(this@SignupFragmentFirst)

                    (activity as SignupActivity).showProgress()
                    GpsManager(this@SignupFragmentFirst, requireActivity()).getLastLocation()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = profileViewModel

            btnContinue.setOnClickListener {
                findNavController().navigate(SignupFragmentFirstDirections.actionSignupFragmentFirstToSignupFragmentSecond())
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        configureCameraIdle()
        setObserver()
        setLocationDropdown()


        binding.mapDummy.apply {
            setOnTouchListener { _, event ->
                val action = event.action
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Disallow ScrollView to intercept touch events.
                        binding.svw.requestDisallowInterceptTouchEvent(true)
                        // Disable touch on transparent view
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        // Allow ScrollView to intercept touch events.
                        binding.svw.requestDisallowInterceptTouchEvent(false)
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        binding.svw.requestDisallowInterceptTouchEvent(true)
                        false
                    }
                    else -> true
                }
            }
        }
    }



    @SuppressLint("MissingPermission")
    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap
        mMap?.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.apply {
                isZoomControlsEnabled = true
                setAllGesturesEnabled(true)
            }

            handlePermission(AppPermission.ACCESS_FINE_LOCATION,
                onGranted = {
                    (activity as SignupActivity).showProgress()
                    GpsManager(this@SignupFragmentFirst, requireActivity()).getLastLocation()
                },
                onRationaleNeeded = {
                    showPermissionRationaleDialog()
                },
                onDenied = {
                    requestPermission(requestPermissionLauncher, AppPermission.PERMISSION_LOCATION)
                }
            )

            setOnMapClickListener {
                locationMarker?.apply {  ->
                    remove()
                }
                locationMarker = addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("Your location"))

                profileViewModel.apply {
                    regLatitude = it.latitude.toString()
                    regLongitude = it.longitude.toString()
                }
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {

    }

    override fun onLocationUpdate(location: Location?) {
        (activity as SignupActivity).hideProgress()
        location?.let {
            this.lattitude = it.latitude.toString()
            this.longitude = it.longitude.toString()

            profileViewModel.apply {
                regLatitude = it.latitude.toString()
                regLongitude = it.longitude.toString()
            }

            mMap?.apply {
                moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 10F))
                locationMarker?.apply {  ->
                    remove()
                }
                locationMarker = addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("Your location"))
//                setOnCameraIdleListener(onCameraIdleListener)
            }
        }
    }

    override fun onLocationDisabled() {

    }

    private fun configureCameraIdle(){
        onCameraIdleListener = GoogleMap.OnCameraIdleListener {
            val latLng = mMap!!.cameraPosition.target
            val geocoder = Geocoder(activity)
            val point = CameraUpdateFactory.newLatLngZoom(
                LatLng(latLng.latitude, latLng.longitude),
                10f
            )
            mMap!!.moveCamera(point)
            mMap!!.animateCamera(point)

            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val locality = addressList[0].getAddressLine(0)
                    val country = addressList[0].countryName
                    lattitude = "" + latLng.latitude
                    longitude = "" + latLng.longitude
                    profileViewModel.apply {
                        regLatitude = latLng.latitude.toString()
                        regLongitude = latLng.longitude.toString()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun showPermissionRationaleDialog(){
        requireContext().showAppDialog(cancelable = false, cancelableTouchOutside = false){
            setMessage("Permission required. Request Again")
            positiveButton {
                requestPermission(requestPermissionLauncher, AppPermission.PERMISSION_LOCATION)
            }
            negativeButton {

            }
        }
    }

    private fun setObserver(){
        profileViewModel.locationListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as SignupActivity).hideProgress()
                    val locationList = it.data?.data
                    locationSuggestionList = locationList?.map { locData -> locData.toLocationDataModel() } ?: listOf()
                    locationsListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        locationSuggestionList
                    )
                    binding.actLocation.apply {
                        setAdapter(locationsListAdapter)
                        showDropDown()
                    }
                }
                Status.LOADING -> {
                    (activity as SignupActivity).showProgress()
                }
                else ->{
                    (activity as SignupActivity).hideProgress()
                    (activity as SignupActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
    }

    private fun setLocationDropdown(){

        binding.ivClearSearch.setOnClickListener {
            binding.actLocation.setText("")
        }

        binding.actLocation.apply {
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
                AdapterView.OnItemClickListener { _, _, pos, _ ->
                    val locationData = (locationsListAdapter.getItem(pos) as LocationDataModel)

                    locationData.let {
                        val locName = it.toString()
                        val locId = it.id
                        profileViewModel.apply {
                            regPin = locId
                        }
                        AppPreferences.apply {
                            userLocation = locName
                            userLocationId = locId
                        }
                    }
                    (activity as SignupActivity).hideKeyboard()
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        profileViewModel.getLocationSuggestions(text.toString())
                    }
                }
                false
            }
        }
    }
}