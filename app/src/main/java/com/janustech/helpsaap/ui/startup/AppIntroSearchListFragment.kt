package com.janustech.helpsaap.ui.startup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppIntroSearchListBinding
import com.janustech.helpsaap.extension.isValidPhoneNumber
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.map.toCompanyDataModel
import com.janustech.helpsaap.model.CompanyDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.home.AppHomeActivity
import com.janustech.helpsaap.ui.home.EditLocationBottomSheetDialogFragment
import com.janustech.helpsaap.ui.profile.LoginActivity
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.CommonUtils.isAppInstalled
import com.janustech.helpsaap.utils.EditLocationListener
import java.util.*


class AppIntroSearchListFragment :
    BaseFragmentWithBinding<FragmentAppIntroSearchListBinding>(R.layout.fragment_app_intro_search_list),
    EditLocationListener {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
            btnLogin.setOnClickListener { activity?.launchActivity<LoginActivity>() }
            tvLocation.setOnClickListener {
                EditLocationBottomSheetDialogFragment(
                    appIntroViewModel,
                    this@AppIntroSearchListFragment
                ).show(
                    childFragmentManager,
                    "EditLocationFragment"
                )
            }

            btnProfileIco.setOnClickListener {
                showPopup(it)
            }

            if (AppPreferences.userId.isNotEmpty()) {
                btnLogin.visibility = View.INVISIBLE
                btnLogin.isEnabled = false

                btnProfileIco.visibility = View.VISIBLE
                btnProfileIco.isEnabled = true
            } else {
                btnLogin.visibility = View.VISIBLE
                btnLogin.isEnabled = true

                btnProfileIco.visibility = View.INVISIBLE
                btnProfileIco.isEnabled = false
            }
        }

        setObserver()
        appIntroViewModel.getCompanies()
    }

    override fun onLocationSelected(location: LocationDataModel) {
        location.let {
            appIntroViewModel.userLocationName = it.toString()
            appIntroViewModel.userLocationId = it.id
            binding.tvLocation.text = it.toString()
        }
    }

    private fun setObserver() {
        appIntroViewModel.companyListReceiver.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    setCompanyList(dataList?.map { obj -> obj.toCompanyDataModel() })
                }
                Status.LOADING -> {
                    (activity as AppIntroActivity).showProgress()
                }
                else -> {
                    (activity as AppIntroActivity).hideProgress()
                    (activity as AppIntroActivity).showAlertDialog(
                        it.message ?: "Invalid Server Response"
                    )
                }
            }
        }
    }

    private fun setCompanyList(companyList: List<CompanyDataModel>?) {
        if (companyList != null && companyList.isNotEmpty()) {

            binding.cmpnyAdapter = CompanyListAdapter(companyList) { model, action ->
                when (action) {
                    "call" -> {
                        model.phone_number.let {
                            if (it.isValidPhoneNumber()) {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:" + model.phone_number)
                                startActivity(intent)
                            }
                        }
                    }
                    "share" -> {
                        CommonUtils.share(requireContext(), createShareContent(model))
                    }
                    "whatsap" -> {
                        if (model.phone_number.isValidPhoneNumber()) {
                            when {
                                isAppInstalled(requireContext(), "com.whatsapp.w4b") -> {
                                    CommonUtils.openWhatsApp(
                                        requireContext(),
                                        model.phone_number,
                                        "com.whatsapp.w4b"
                                    )
                                }
                                isAppInstalled(requireContext(), "com.whatsapp") -> {
                                    CommonUtils.openWhatsApp(
                                        requireContext(),
                                        model.phone_number,
                                        "com.whatsapp"
                                    )
                                }
                                else -> {
                                    showAlertDialog("whatsApp is not installed")
                                }
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Not a valid number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    "loc" -> {
                        if (model.lat.isNotEmpty() && model.longi.isNotEmpty()) {
                            var markerName = model.businessname.ifEmpty { model.cus_name }
                            markerName = markerName + ", " + model.areaname
                            gotToLocation(markerName, model.lat.toFloat(), model.longi.toFloat())
                        }
                    }
                    "web" -> {
                        openWeb(model.website)
                    }
                }
            }
        } else {
            showAlertDialog("No companies found!")
        }
    }

    private fun createShareContent(obj: CompanyDataModel): String {
        var name = obj.businessname.ifEmpty { obj.cus_name }
        var area = obj.areaname

        var data = name + " - " + appIntroViewModel.userSelectedCategoryName + "\n"
        if (area.isNotEmpty()) {
            data = data + area + "\n"
        }
        if (obj.address.isNotEmpty()) {
            data = data + obj.address + "\n"
        }
        if (obj.phone_number.isNotEmpty()) {
            data = data + "Phone: " + obj.phone_number + "\n"
        }
        if (obj.website.isNotEmpty()) {
            data = data + "Website: " + obj.website + "\n"
        }
        if (obj.lat.isNotEmpty() && obj.longi.isNotEmpty()) {
            data =
                data + "Location : " + "https://www.google.com/maps/search/?api=1&query=" + obj.lat + "," + obj.longi + "\n"
        }
        data =
            "$data Playstore Link: https://play.google.com/store/apps/details?id=com.janustech.helpsaap \n"


        return data
    }

    private fun openMap(latitude: Double, longitude: Double) {
        try {
            val uri: String =
                java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            requireContext().startActivity(intent)
        } catch (e: Exception) {
        }
    }

    private fun gotToLocation(markerName: String?, latitude: Float?, longitude: Float?) {
        val uri = java.lang.String.format(
            Locale.ENGLISH,
            "geo:0,0?q=%f,%f(%s)",
            latitude,
            longitude,
            markerName
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    private fun openWeb(url: String) {
        try {
            if (url.isNotEmpty()) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("http://$url")
                startActivity(i)
            } else {
                showToast("No website found!")
            }
        } catch (e: Exception) {
            showToast("No website found!")
        }
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_sub_home, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionLogout -> {
                    AppPreferences.clearAll()
                    findNavController().navigate(AppIntroSearchListFragmentDirections.actionAppIntroSearchListToSelectLocationFragment())
                }
                R.id.actionProfile -> {
                    activity?.launchActivity<AppHomeActivity>()
                    activity?.finish()
                }
            }
            true
        }
        popup.show()
    }

}