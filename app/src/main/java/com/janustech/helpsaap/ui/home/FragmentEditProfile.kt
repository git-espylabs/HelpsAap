package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentEditProfileBinding
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentEditProfile  : BaseFragmentWithBinding<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()
    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    var categoryList = arrayListOf<CategoryDataModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appHomeViewModel
        }

        setObserver()
        setSearchList()
        setSelectedCategoryListView()
    }


    private fun setObserver(){
        appHomeViewModel.categoriesReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    val dataList = it.data?.data
                    categoriesSuggestionList = dataList?.map { dat -> dat.toCategoryDataModel() } ?: listOf()
                    categoriesListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        categoriesSuggestionList
                    )
                    binding.actCategory.setAdapter(categoriesListAdapter)
                }
                Status.LOADING -> {
                }
                else ->{
                    (activity as AppHomeActivity).showToast(it.message?:"Invalid Server Response")
                }
            }
        }

        appHomeViewModel.editSubmitStatusReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    if (it.data?.isResponseSuccess() == true) {
                        (activity as AppHomeActivity).showAlertDialog("Profile edited successfully!")
                    } else {
                        (activity as AppHomeActivity).showAlertDialog("Edit profile failed! Please try again")
                    }
                }
                Status.LOADING -> {
                    (activity as AppHomeActivity).showProgress()
                }
                else ->{
                    (activity as AppHomeActivity).hideProgress()
                    (activity as AppHomeActivity).showToast(it.message?:"Invalid Server Response")
                }
            }
        }
    }

    private fun setSearchList(){

        categoriesListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoriesSuggestionList
        )

        binding.ivClearSearch.setOnClickListener {
            binding.actCategory.setText("")
        }

        binding.actCategory.apply {
            threshold = 1

            setAdapter(categoriesListAdapter)

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    binding.apply {
                        if (s.toString().isNotEmpty()){
                            binding.ivClearSearch.visibility = View.VISIBLE
                        }else{
                            binding.ivClearSearch.visibility = View.GONE
                        }
                    }
                    autoCompleteTextHandler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                    autoCompleteTextHandler?.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                }
            })

            onItemClickListener =
                AdapterView.OnItemClickListener { _, _, pos, _ ->
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    catData.let {
                        categoryList.add(it)
                        appHomeViewModel.addedCategories.add(it.id)
                        binding.catListAdapter?.notifyItemInserted(categoryList.size - 1)
                    }
                    (activity as AppHomeActivity).hideKeyboard()
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        appHomeViewModel.getCategories(text.toString())
                    }
                }
                false
            }
        }
    }



    private fun setSelectedCategoryListView(){
        binding.apply {
            catListAdapter = CategoryListAdapter(categoryList)
        }
    }
}