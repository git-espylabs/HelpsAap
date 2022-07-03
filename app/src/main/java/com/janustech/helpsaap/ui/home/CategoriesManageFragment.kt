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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppHomeBinding
import com.janustech.helpsaap.databinding.FragmentManageCategoriesBinding
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding

class CategoriesManageFragment: BaseFragmentWithBinding<FragmentManageCategoriesBinding>(R.layout.fragment_manage_categories) {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null
    var categoryList = arrayListOf<CategoryDataModel>()
    var categoryServerList = arrayListOf<CategoryDataModel>()
    var categoryAddNewList = arrayListOf<CategoryDataModel>()

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    private var isDropDownItemSelected = false;
    private var itemRemove: CategoryDataModel? = null
    private var itemRemovePos = -1;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appHomeViewModel
            catListAdapter = CategoryManageListAdapter(requireContext(), categoryList){ obj, pos ->
                itemRemovePos = pos
                itemRemove = obj

                if (categoryServerList.any { it.id == itemRemove?.id }){
                    showDeleteWarning(obj.id)
                }else{
                    categoryList.removeAt(itemRemovePos)
                    binding.catListAdapter?.notifyItemRemoved(itemRemovePos)
                }
            }

            btnSubmit.setOnClickListener {
                if (categoryAddNewList.isNotEmpty()) {
                    appHomeViewModel.addCategories()
                } else {
                    Toast.makeText(requireContext(), "Select atleast one category and continue!", Toast.LENGTH_LONG).show()
                }
            }
        }

        setObserver()
        appHomeViewModel.getUserCategories()
        setCategoriesDropdown()


    }

    override fun onStop() {
        super.onStop()
        appHomeViewModel._addCatgoriesResponseStatus.value = null
        appHomeViewModel._categoriesReceiver.value = null
        appHomeViewModel._userCatsListReceiver.value = null
        appHomeViewModel._userCatRemoveReceiver.value = null
    }

    private fun setObserver() {

        appHomeViewModel.categoriesReceiver?.observe(viewLifecycleOwner){ res ->
            try {
                res?.let {
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
                                binding.actSearch.apply {
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
            } catch (e: Exception) {
            }
        }

        appHomeViewModel.addCatgoriesResponseStatus?.observe(viewLifecycleOwner){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            (activity as AppHomeActivity).hideProgress()
                            if (it.data?.isResponseSuccess() == true){
                                showToast("Categories Added Successfully")
                                appHomeViewModel.getUserCategories()
                            }else{
                                (activity as AppHomeActivity).showToast("Something went wrong try again later!")
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
            } catch (e: Exception) {
            }
        }

        appHomeViewModel.userCatsListReceiver?.observe(viewLifecycleOwner){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            (activity as AppHomeActivity).hideProgress()
                            val catList = it.data?.data?.map { obj -> obj.toCategoryDataModel() }?: listOf()
                            if (catList.isNotEmpty()) {
                                categoryServerList.clear()
                                categoryServerList.addAll(catList)
                                categoryList.clear()
                                categoryList.addAll(catList)
                                binding.catListAdapter?.notifyItemInserted(categoryList.size - 1)
                            }else{
                                binding.apply {
                                    title.visibility = View.GONE
                                    rvLocation.visibility = View.GONE
                                }
                            }
                        }
                        Status.LOADING -> {
                            (activity as AppHomeActivity).showProgress()
                        }
                        else ->{
                            (activity as AppHomeActivity).hideProgress()
                            binding.apply {
                                title.visibility = View.GONE
                                rvLocation.visibility = View.GONE
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }

        appHomeViewModel.userCatRemoveReceiver?.observe(viewLifecycleOwner){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            (activity as AppHomeActivity).hideProgress()
                            if (it.data?.isResponseSuccess() == true) {
                                categoryList.removeAt(itemRemovePos)
                                binding.catListAdapter?.notifyItemRemoved(itemRemovePos)
                            } else {
                                showAlertDialog("Operation failed! Cannot delete. Please contact admin")
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
            } catch (e: Exception) {
            }
        }
    }

    private fun setCategoriesDropdown(){
        binding.ivClearSearch.setOnClickListener {
            binding.actSearch.setText("")
        }

        binding.actSearch.apply {
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
                    (activity as AppHomeActivity).hideKeyboard()
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    if (categoryList.any { it.id == catData.id }) {
                        showAlertDialog("You have already added this category! Please select a different one")
                    } else {
                        catData.let {
                            binding.apply {
                                title.visibility = View.VISIBLE
                                rvLocation.visibility = View.VISIBLE
                            }
                            categoryList.add(it)
                            categoryAddNewList.add(it)
                            it.type = "1"
                            appHomeViewModel.addedCategories.add(it.id)
                            binding.catListAdapter?.notifyItemInserted(categoryList.size - 1)
                            binding.rvLocation.scrollToPosition(categoryList.size - 1)
                        }
                    }
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

    private fun showDeleteWarning(catid: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Item Remove!")
        builder.setMessage("Are you sure you want to remove this category?")
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            appHomeViewModel.removeUserCat(catid)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}