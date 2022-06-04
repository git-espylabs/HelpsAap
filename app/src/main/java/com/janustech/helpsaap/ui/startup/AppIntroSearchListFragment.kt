package com.janustech.helpsaap.ui.startup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
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


class AppIntroSearchListFragment: BaseFragmentWithBinding<FragmentAppIntroSearchListBinding>(R.layout.fragment_app_intro_search_list),
    EditLocationListener {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
            btnLogin.setOnClickListener { activity?.launchActivity<LoginActivity>()}
            tvLocation.setOnClickListener {
                EditLocationBottomSheetDialogFragment(appIntroViewModel, this@AppIntroSearchListFragment).show(
                    childFragmentManager,
                    "EditLocationFragment"
                )
            }

            btnProfileIco.setOnClickListener {
                showPopup(it)
            }

            if (AppPreferences.userId.isNotEmpty()){
                btnLogin.visibility = View.INVISIBLE
                btnLogin.isEnabled = false

                btnProfileIco.visibility = View.VISIBLE
                btnProfileIco.isEnabled = true
            }else{
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

    private fun setObserver(){
        appIntroViewModel.companyListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    setCompanyList(dataList?.map { obj-> obj.toCompanyDataModel() })
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

    private fun setCompanyList(companyList: List<CompanyDataModel>?){
        if (companyList != null && companyList.isNotEmpty()){

            binding.cmpnyAdapter = CompanyListAdapter(companyList){ model, action ->
                when(action){
                    "call" ->{
                        model.phone_number.let {
                            if (it.isValidPhoneNumber()){
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:" + model.phone_number)
                                startActivity(intent)
                            }
                        }
                    }
                    "share" ->{
                        CommonUtils.share(requireContext())
                    }
                    "whatsap" ->{
                        when {
                            isAppInstalled(requireContext(), "com.whatsapp.w4b") -> {
                                CommonUtils.openWhatsApp(requireContext(), model.phone_number, "com.whatsapp.w4b")
                            }
                            isAppInstalled(requireContext(), "com.whatsapp") -> {
                                CommonUtils.openWhatsApp(requireContext(), model.phone_number, "com.whatsapp")
                            }
                            else -> {
                                showAlertDialog("whatsApp is not installed")
                            }
                        }
                    }
                }
            }
        }else{
            showAlertDialog("No companies found!")
        }
    }

    private fun showPopup(v : View){
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_sub_home, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.actionLogout-> {
                    AppPreferences.clearAll()
                    findNavController().navigate(AppIntroSearchListFragmentDirections.actionAppIntroSearchListToSelectLocationFragment())
                }
                R.id.actionProfile ->{
                    activity?.launchActivity<AppHomeActivity>()
                    activity?.finish()
                }
            }
            true
        }
        popup.show()
    }

}