package com.janustech.helpsaap.ui.startup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentDealOrAdOwnerDetailsBinding
import com.janustech.helpsaap.extension.isNumeric
import com.janustech.helpsaap.extension.isValidPhoneNumber
import com.janustech.helpsaap.map.toProfileViewDataModel
import com.janustech.helpsaap.model.ProfileViewDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import java.util.*

class DealOrAdOwnerDetailsFragment: BaseFragmentWithBinding<FragmentDealOrAdOwnerDetailsBinding>(R.layout.fragment_deal_or_ad_owner_details) {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()
    val args: DealOrAdOwnerDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = appIntroViewModel
        }

        setObserver()
        if (args.id.isNotEmpty() && args.id != "0") {
            appIntroViewModel.getProfileData(args.id)
        } else {
            (activity as AppIntroActivity).showAlertDialog("User not found!")
        }
    }

    private fun setObserver(){
        appIntroViewModel.profileDataReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    if (dataList != null && dataList.isNotEmpty()){
                        showProfileData(dataList[0].toProfileViewDataModel())
                    }else{
                        (activity as AppIntroActivity).showAlertDialog(it.message?:"No data found")
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

    private fun showProfileData(obj: ProfileViewDataModel){
        binding.apply {
            ivLogo.also {
                Glide.with(it).load(BuildConfig.IMAGE_URL + obj.photo).into(it)
            }
            tvName.text = obj.cus_name
            tvBName.text = obj.businessname
            tvAreaBName.text = obj.areaname
            if (obj.phone_number.isNotEmpty()) {
                tvPhone.also {
                    it.text = getUnderlinedString(obj.phone_number)
                    it.setOnClickListener {
                        if (obj.phone_number.isValidPhoneNumber()){
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:" + obj.phone_number)
                            startActivity(intent)
                        }
                    }
                }
            }else{
                tvLocation.text = "Phone Number Not Available"
            }

            if (obj.email.isNotEmpty()) {
                tvEmail.text = obj.email
            }else{
                tvLocation.text = "Email Not Available"
            }

            if (obj.website.isNotEmpty()) {
                tvWeb.text = obj.website
            }else{
                tvLocation.text = "Website Not Available"
            }

            if (obj.whatsapp.isNotEmpty()) {
                tvWhatsap.text = getUnderlinedString(obj.whatsapp)
            }else{
                tvLocation.text = "Whatsap Available"
            }
            if (obj.offerpercentage.isNotEmpty() && obj.offerpercentage.isNumeric() && obj.offerpercentage.toDouble() > 0) {
                tvOffer.text = obj.offerpercentage +"%"
            } else {
                tvOffer.text = "0%"
            }

            if (obj.lat.isNotEmpty() && obj.long.isNotEmpty()){
                tvLocation.also {
                    it.text = getUnderlinedString(obj.lat+","+obj.long)
                    it.setOnClickListener {
                        openMap(obj.lat.toDouble(), obj.long.toDouble())
                    }
                }
            }else{
                tvLocation.text = "Not Available"
            }
        }
    }

    private fun getUnderlinedString(str: String): SpannableString{
        val content = SpannableString(str)
        content.setSpan(UnderlineSpan(), 0, str.length, 0)
        return content
    }

    private fun openMap(latitude: Double, longitude: Double){
        val uri: String = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}