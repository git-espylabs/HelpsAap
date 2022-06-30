package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAddCategoryBottomSheetBinding
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.network.Status

class AddCategoryBottomSheetDialogFragment(private val viewModel: AppHomeViewModel): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddCategoryBottomSheetBinding

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null
    var categoryList = arrayListOf<CategoryDataModel>()

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    private var isDropDownItemSelected = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_category_bottom_sheet,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCategoriesDropdown()
        setObserver()

        binding.apply {
            catListAdapter = CategoryListAdapter(categoryList)

            btnSubmit.setOnClickListener {
                if (categoryList.isNotEmpty()) {
                    viewModel.addCategories()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Select atleast one category and continue!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setObserver(){

        viewModel.categoriesReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    val dataList = it.data?.data
                    if (isDropDownItemSelected.not()) {
                        categoriesSuggestionList = dataList?.map { dat -> dat.toCategoryDataModel() } ?: listOf()
                        categoriesListAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            categoriesSuggestionList
                        )
                        binding.tvDropdownLocation.apply {
                            setAdapter(categoriesListAdapter)
                            showDropDown()
                        }
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

    private fun setCategoriesDropdown(){
        binding.ivClearSearch.setOnClickListener {
            binding.tvDropdownLocation.setText("")
        }

        binding.tvDropdownLocation.apply {
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
                    isDropDownItemSelected = false;
                    if (s.toString().isNotEmpty()){
                        binding.ivClearSearch.visibility = View.VISIBLE
                    }else{
                        binding.ivClearSearch.visibility = View.GONE
                    }
                    autoCompleteTextHandler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                    autoCompleteTextHandler?.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                }
            })

            onItemClickListener =
                AdapterView.OnItemClickListener { _, _, pos, _ ->
                    isDropDownItemSelected = true;
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    catData.let {
                        categoryList.add(it)
                        viewModel.addedCategories.add(it.id)
                        binding.catListAdapter?.notifyItemInserted(categoryList.size - 1)
                    }
                    (activity as AppHomeActivity).hideKeyboard()
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        viewModel.getCategories(text.toString())
                    }
                }
                false
            }
        }
    }
}