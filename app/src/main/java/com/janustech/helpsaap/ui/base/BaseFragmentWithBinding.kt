package com.janustech.helpsaap.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


abstract class BaseFragmentWithBinding<VB : ViewDataBinding>(@LayoutRes private val layoutResID: Int) :
    BaseFragment() {

    private lateinit var _binding: VB
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}