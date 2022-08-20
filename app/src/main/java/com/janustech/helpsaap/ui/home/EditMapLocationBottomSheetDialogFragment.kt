package com.janustech.helpsaap.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.app.AppPermission
import com.janustech.helpsaap.databinding.FragmentEditMapLocationBottomSheetBinding
import com.janustech.helpsaap.extension.*
import com.janustech.helpsaap.location.GpsListener
import com.janustech.helpsaap.location.GpsManager
import com.janustech.helpsaap.ui.startup.SignupActivity
import kotlinx.coroutines.launch
import java.io.IOException


class EditMapLocationBottomSheetDialogFragment
    (private val viewModel: AppHomeViewModel): BottomSheetDialogFragment(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    GpsListener {
    private lateinit var binding: FragmentEditMapLocationBottomSheetBinding

    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: GoogleMap.OnCameraIdleListener? = null
    var lattitude = "0.0"
    var longitude = "0.0"
    var locationMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_map_location_bottom_sheet,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

            bottomSheet?.let { sheet ->
                BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(sheet).isDraggable = false
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@EditMapLocationBottomSheetDialogFragment)

        configureCameraIdle()

        binding.btnSubmit.setOnClickListener {
            viewModel.updateUserLocationFromMap()
            dismiss()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap?.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
            isMyLocationEnabled = true
            uiSettings.apply {
                isMyLocationButtonEnabled = true
                isZoomControlsEnabled = true
                setAllGesturesEnabled(true)
            }
            (activity as AppHomeActivity).showProgress()
            setOnMyLocationButtonClickListener(this@EditMapLocationBottomSheetDialogFragment)
            setOnMyLocationClickListener(this@EditMapLocationBottomSheetDialogFragment)
            GpsManager(this@EditMapLocationBottomSheetDialogFragment, requireActivity()).getLastLocation()

            setOnMapClickListener {
                locationMarker?.apply {  ->
                    remove()
                }
                locationMarker = addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("Your location"))
                this@EditMapLocationBottomSheetDialogFragment.lattitude = it.latitude.toString()
                this@EditMapLocationBottomSheetDialogFragment.longitude = it.longitude.toString()
                getPlaceData()

                viewModel.apply {
                    edtUserLat = it.latitude.toString()
                    edtUserLon = it.longitude.toString()
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
        (activity as AppHomeActivity).hideProgress()
        if (viewModel.edtUserLat == "0.0" || viewModel.edtUserLon ==" 0.0"){
            location?.let {
                this.lattitude = it.latitude.toString()
                this.longitude = it.longitude.toString()
            }
        }else{
            this.lattitude = viewModel.edtUserLat
            this.longitude = viewModel.edtUserLon
        }

        getPlaceData()

        mMap?.apply {
            val posLatLng = LatLng(this@EditMapLocationBottomSheetDialogFragment.lattitude.toDouble(), this@EditMapLocationBottomSheetDialogFragment.longitude.toDouble())
            moveCamera(CameraUpdateFactory.newLatLngZoom(posLatLng, 10F))
            locationMarker?.apply {  ->
                remove()
            }
            locationMarker = addMarker(MarkerOptions().position(posLatLng).title("Your location"))
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
                    /*profileViewModel.apply {
                        regLatitude = latLng.latitude.toString()
                        regLongitude = latLng.longitude.toString()
                    }*/
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getPlaceData(){
        lifecycleScope.launch {
            var data = "";
            val geocoder = Geocoder(requireActivity())
            val posLatLng = LatLng(this@EditMapLocationBottomSheetDialogFragment.lattitude.toDouble(), this@EditMapLocationBottomSheetDialogFragment.longitude.toDouble())
            val addressList = geocoder.getFromLocation(posLatLng.latitude, posLatLng.longitude, 1)
            if (addressList != null && addressList.size > 0) {

                if (addressList[0].getAddressLine(0).isNullOrEmpty().not() && addressList[0].getAddressLine(0) != "null"){
                    data += addressList[0].getAddressLine(0)
                }

                if (addressList[0].locality.isNullOrEmpty().not() && addressList[0].locality != "null"){
                    data += "\n" + addressList[0].locality
                }

                if (addressList[0].adminArea.isNullOrEmpty().not() && addressList[0].adminArea != "null"){
                    data += "\n" + addressList[0].adminArea
                }

                binding?.latlng.text = data
            }else{
                binding?.latlng.text = "Latitude, Longitude: \n" + this@EditMapLocationBottomSheetDialogFragment.lattitude + ", " + this@EditMapLocationBottomSheetDialogFragment.longitude
            }
        }
    }
}