package com.janustech.helpsaap.ui.startup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentHelplineContactBottomSheetBinding
import com.janustech.helpsaap.extension.isValidPhoneNumber
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.PhotoOptionListener

class HelplineContactBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentHelplineContactBottomSheetBinding
    val helplineNum = "9207777133"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_helpline_contact_bottom_sheet,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.tvPhone.setOnClickListener {

            helplineNum.let {
                if (it.isValidPhoneNumber()){
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + helplineNum)
                    startActivity(intent)
                }
            }
            dismiss()
        }
        binding.tvWhats.setOnClickListener {
            if (helplineNum.isValidPhoneNumber()){
                when {
                    CommonUtils.isAppInstalled(requireContext(), "com.whatsapp.w4b") -> {
                        CommonUtils.openWhatsApp(requireContext(), helplineNum, "com.whatsapp.w4b")
                    }
                    CommonUtils.isAppInstalled(requireContext(), "com.whatsapp") -> {
                        CommonUtils.openWhatsApp(requireContext(), helplineNum, "com.whatsapp")
                    }
                    else -> {
                        Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Not a valid number", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }
}