package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentChangeLanguageBottomSheetBinding
import com.janustech.helpsaap.model.LanguageDataModel
import com.janustech.helpsaap.preference.AppPreferences

class ChangeLanguageBottomSheetFragment(private val viewModel: AppIntroViewModel, private val languageList: List<LanguageDataModel>) : BottomSheetDialogFragment()  {

    private lateinit var binding: FragmentChangeLanguageBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_change_language_bottom_sheet,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLanguageList()
    }

    private fun setLanguageList(){
        binding.myAdapter = LanguageChangeAdpater(requireContext(), languageList){ language ->
            language.let {
                val lanName = it.lang
                val lanId = it.id
                AppPreferences.apply {
                    userLanguage = lanName
                    userLanguageId = lanId
                }
                viewModel.updateLanguageSelection(it)
                dismiss()
            }
        }
    }
}