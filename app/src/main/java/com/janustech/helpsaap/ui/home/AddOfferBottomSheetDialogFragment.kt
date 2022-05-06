package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAddOfferBottomSheetDialogBinding

class AddOfferBottomSheetDialogFragment(private val viewModel: AppHomeViewModel): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddOfferBottomSheetDialogBinding

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
            R.layout.fragment_add_offer_bottom_sheet_dialog,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnSubmit.setOnClickListener {
                if (etPercent.text.toString().isNotEmpty() && etPercent.text.toString().toInt() > 0){
                    viewModel.submitOffer(etPercent.text.toString())
                    dismiss()
                }else{
                    Toast.makeText(requireContext(), "Please provide a valid input", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}