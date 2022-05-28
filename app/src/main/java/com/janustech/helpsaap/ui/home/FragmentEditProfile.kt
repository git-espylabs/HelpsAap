package com.janustech.helpsaap.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.R
import com.janustech.helpsaap.app.AppPermission
import com.janustech.helpsaap.databinding.FragmentEditProfileBinding
import com.janustech.helpsaap.extension.handlePermission
import com.janustech.helpsaap.extension.isNumeric
import com.janustech.helpsaap.extension.requestPermission
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.map.toLanguageDataModel
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.model.LanguageDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.profile.PhotoOptionBottomSheetDialogFragment
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.PhotoOptionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class FragmentEditProfile  : BaseFragmentWithBinding<FragmentEditProfileBinding>(R.layout.fragment_edit_profile),
    PhotoOptionListener {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()
    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    private lateinit var currentPhotoPath: String
    private lateinit var galleryImgUri: Uri
    private var photoFile: File? = null
    private var actualPath = ""

    var categoryList = arrayListOf<CategoryDataModel>()
    private var isCameraImage = true
    private lateinit var langListAdapter: ArrayAdapter<LanguageDataModel>


    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            setCameraPicToImageView()
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[AppPermission.PERMISSION_STORAGE[0]] == true) {
                dispatchPickPhotoIntent()
            }
        }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let {
                setGalleryPicToImageView(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appHomeViewModel
            tvPromptAddCategs.setOnClickListener {
                AddCategoryBottomSheetDialogFragment(appHomeViewModel).show(
                    childFragmentManager,
                    "AddCategoryFragment"
                )
            }
            btnContinue.setOnClickListener {
                appHomeViewModel.editProfile(requireContext())
            }
            promtOffer.setOnClickListener {
                AddOfferBottomSheetDialogFragment(appHomeViewModel).show(
                    childFragmentManager,
                    "AddOfferBottomSheetDialogFragment"
                )
            }
        }

        populatePercentage()
        setObserver()
        setSearchList()
        setSelectedCategoryListView()

        binding.ivLogo.apply {
            if (AppPreferences.userImageUrl.isNotEmpty()){
                Glide.with(this).load(BuildConfig.IMAGE_URL + AppPreferences.userImageUrl).into(this)
            }else if (AppPreferences.userImageDisk.isNotEmpty()){
                Glide.with(this).load(AppPreferences.userImageDisk).into(this)
            }
            setOnClickListener {
                showPhotoPickOption()
            }
        }

        appHomeViewModel.getLanguages()
    }

    override fun onStop() {
        super.onStop()
        appHomeViewModel._addCatgoriesResponseStatus.value = null
        appHomeViewModel._editSubmitStatusReceiver_.value = null
        appHomeViewModel._editSubmitStatusReceiver.value = null
        appHomeViewModel._offerSubmitStatusReceiver.value = null
    }

    override fun onTakePhotoSelected() {
        dispatchTakePictureIntent()
    }

    override fun onChoosePhotoSelected() {
        handlePermission(AppPermission.ACCESS_STORAGE,
            onGranted = {
                dispatchPickPhotoIntent()
            },
            onRationaleNeeded = {

            },
            onDenied = {
                requestPermission(requestPermissionLauncher, AppPermission.PERMISSION_STORAGE)
            }
        )
    }


    private fun setObserver(){
        appHomeViewModel.categoriesReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    val dataList = it.data?.data
                    categoriesSuggestionList = dataList?.map { dat -> dat.toCategoryDataModel() } ?: listOf()
                    categoriesListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        categoriesSuggestionList
                    )
                    binding.actCategory.apply {
                        setAdapter(categoriesListAdapter)
                        showDropDown()
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

        appHomeViewModel.editSubmitStatusReceiver?.observe(viewLifecycleOwner){ res ->
            try {
                res?.let {
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
            } catch (e: Exception) {
            }
        }

        appHomeViewModel.editSubmitStatusReceiver_?.observe(viewLifecycleOwner){ res ->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            (activity as AppHomeActivity).hideProgress()
                            if (it.data?.isResponseSuccess() == true) {
                                appHomeViewModel.updateUserDataWithEditSuccess()
                                AppPreferences.userLanguageId = appHomeViewModel.editLangId;
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
            } catch (e: Exception) {
            }
        }

        appHomeViewModel.languageListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    val languageList = it.data?.data
                    setLanguageList(languageList)
                }
                Status.LOADING -> {
                    (activity as AppHomeActivity).showProgress()
                }
                else ->{
                    (activity as AppHomeActivity).hideProgress()
                    (activity as AppHomeActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
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

        appHomeViewModel.offerSubmitStatusReceiver?.observe(viewLifecycleOwner){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            (activity as AppHomeActivity).hideProgress()
                            if (it.data?.isResponseSuccess() == true) {
                                appHomeViewModel.updateUserDataWithOfferPercentage()
                                populatePercentage()
                                (activity as AppHomeActivity).showAlertDialog("Offer added successfully!")
                            } else {
                                (activity as AppHomeActivity).showAlertDialog("Error occurred! Please try again")
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

    private fun setLanguageList(languageList: List<LanguageListResponseData>?){
        val list = languageList?.map {
            it.toLanguageDataModel()
        }?: listOf()


        langListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            list
        )

        binding.tvDropdownLang.apply {
            if (AppPreferences.userLanguageId.isNotEmpty()){
                setText(list.find { langData-> langData.id == AppPreferences.userLanguageId }?.lang?:"")
            }
            setOnItemClickListener { _, _, position, _ ->
                appHomeViewModel.editLangId = list[position].id
            }
            setAdapter(langListAdapter)
        }
    }

    private fun setSearchList(){

        binding.ivClearSearch.setOnClickListener {
            binding.actCategory.setText("")
        }

        binding.actCategory.apply {

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

    private fun showPhotoPickOption() {
        PhotoOptionBottomSheetDialogFragment(this@FragmentEditProfile).show(
            childFragmentManager,
            "ChoosePhotoFragment"
        )
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            try {
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        CommonUtils.getFileProviderName(requireContext()),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraLauncher.launch(takePictureIntent)
                }
            } catch (_: Exception) {
                showToast(R.string.no_apps_found)
            }
        }
    }

    private fun dispatchPickPhotoIntent() {
        try {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            galleryLauncher.launch(intent)
        } catch (_: Exception) {
            showToast(R.string.no_apps_found)
        }
    }

    private fun createImageFile(): File {
        return CommonUtils.createImageFile(requireContext(),
            "USER"
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun setCameraPicToImageView() {
        isCameraImage = true;
        scaleDownImage(BitmapFactory.decodeFile(currentPhotoPath))
        deleteOriginalCameraImage()
    }

    private fun setGalleryPicToImageView(uri: Uri) {
        isCameraImage = false;
        currentPhotoPath = uri.path?: ""
        galleryImgUri = uri
        photoFile = File(currentPhotoPath)
        CommonUtils.getBitmapFromUri(requireContext(), uri)
            ?.let { bitmap -> scaleDownImage(bitmap) }
    }

    private fun scaleDownImage(image: Bitmap) {
        with(CommonUtils.scaleDownImage(image)) {
            CommonUtils.compressAndSaveImage(requireContext(), this, "USER").also {
                actualPath = it.absolutePath
                appHomeViewModel.editProfImg = actualPath
                setImage(it.absolutePath)
            }
        }
    }

    private fun setImage(path: String){
        val image = if (isCameraImage) {
            CommonUtils.getClearExifBitmap(currentPhotoPath, path)
        } else {
            CommonUtils.getClearExifBitmap(requireContext(), galleryImgUri, path)
        }
        image?.let {
            binding.ivLogo.apply {
                setImageBitmap(image)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun deleteOriginalCameraImage() {
        try {
            photoFile?.delete()
        } catch (_: Exception) {
        }
        photoFile = null
    }

    private fun populatePercentage(){
        val offerPercent = appHomeViewModel.editOfferPercent
        if (offerPercent.isNotEmpty() && offerPercent.isNumeric() && offerPercent.toDouble() > 0){
            binding.promtOffer.apply {
                val str = "Offer: $offerPercent%"
                val content = SpannableString(str)
                content.setSpan(UnderlineSpan(), 0, str.length, 0)
                text = content
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_24, 0);
            }
        }else{
            binding.promtOffer.apply {
                val str = "Add Offer"
                val content = SpannableString(str)
                content.setSpan(UnderlineSpan(), 0, str.length, 0)
                text = content
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_24, 0, 0, 0);
            }
        }


    }
}