package com.janustech.helpsaap.ui.startup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppIntroSearchListBinding
import com.janustech.helpsaap.extension.isValidPhoneNumber
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.map.toCompanyDataModel
import com.janustech.helpsaap.model.CompanyDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.profile.LoginActivity
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.CommonUtils.isAppInstalled


class AppIntroSearchListFragment: BaseFragmentWithBinding<FragmentAppIntroSearchListBinding>(R.layout.fragment_app_intro_search_list)  {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
            btnLogin.setOnClickListener { activity?.launchActivity<LoginActivity>()}
        }

        setObserver()
        appIntroViewModel.getCompanies()
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
                                intent.data = Uri.parse("tel:0123456789")
                                startActivity(intent)
                            }
                        }
                    }
                    "share" ->{

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
        }
    }
}