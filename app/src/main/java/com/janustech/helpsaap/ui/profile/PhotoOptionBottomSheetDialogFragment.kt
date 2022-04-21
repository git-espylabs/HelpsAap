package com.janustech.helpsaap.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentPhotoOptionBottomSheetDialogBinding
import com.janustech.helpsaap.utils.PhotoOptionListener

class PhotoOptionBottomSheetDialogFragment(private val listener: PhotoOptionListener) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPhotoOptionBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_photo_option_bottom_sheet_dialog,
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
        binding.tvChoosePhoto.setOnClickListener {
            listener.onChoosePhotoSelected()
            dismiss()
        }
        binding.tvTakePhoto.setOnClickListener {
            listener.onTakePhotoSelected()
            dismiss()
        }
    }
}