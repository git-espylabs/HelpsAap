package com.janustech.helpsaap.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.janustech.helpsaap.R
import com.janustech.helpsaap.app.AppPermission
import com.janustech.helpsaap.databinding.FragmentRegisterBinding
import com.janustech.helpsaap.databinding.FragmentRegisterSecondBinding
import com.janustech.helpsaap.extension.handlePermission
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.extension.requestPermission
import com.janustech.helpsaap.location.GpsManager
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.map.toUserData
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.SignupResponse
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.home.AppHomeActivity
import com.janustech.helpsaap.ui.startup.AppIntroActivity
import com.janustech.helpsaap.ui.startup.SignupActivity
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.PhotoOptionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class SignupFragmentSecond : BaseFragmentWithBinding<FragmentRegisterSecondBinding>(R.layout.fragment_register_second),
    PhotoOptionListener {

    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var currentPhotoPath: String
    private lateinit var galleryImgUri: Uri
    private var photoFile: File? = null
    private var actualPath = ""

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    private var selectedCategory = ""
    private var isCameraImage = true

    private var isDropDownItemSelected = false;


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
            if (permissions[AppPermission.PERMISSION_STORAGE[1]] == true) {
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
            viewModel = profileViewModel

            btnFinish.isEnabled = false
            btnFinish.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.disabled_app_accent_color)
            btnFinish.setOnClickListener {
                activity?.let {
                    profileViewModel.registerApp(it)
                }
            }

            promptFileSelect.setOnClickListener {
                showPhotoPickOption()
            }

            cbTnc.setOnCheckedChangeListener { _, isChecked ->
                btnFinish.isEnabled = isChecked
                if (isChecked) {
                    btnFinish.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.app_accent_color)
                }else{
                    btnFinish.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.disabled_app_accent_color)
                }
            }
        }

        setTncPromptText()

        setObserver()
        setSearchList()
    }

    private fun setTncPromptText(){
        val ss = SpannableString(getString(R.string.agree_tnc2))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                findNavController().navigate(SignupFragmentSecondDirections.actionSignupFragmentSecondToTncFragment(""))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 9, getString(R.string.agree_tnc2).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvPromptTnc.apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
            setTypeface(this.typeface, Typeface.BOLD)
            text = ss
        }
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
        profileViewModel.registerResponseReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    (activity as SignupActivity).hideProgress()
                    handleSignupResponse(it.data?.data)
                }
                Status.LOADING -> {
                    (activity as SignupActivity).showProgress()
                }
                else ->{
                    (activity as SignupActivity).hideProgress()
                    (activity as SignupActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }

        profileViewModel.categoriesReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as SignupActivity).hideProgress()
                    val dataList = it.data?.data
                    if (isDropDownItemSelected.not()) {
                        categoriesSuggestionList = dataList?.map { dat -> dat.toCategoryDataModel() } ?: listOf()
                        categoriesListAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            categoriesSuggestionList
                        )
                        binding.categorySpinner.apply {
                            setAdapter(categoriesListAdapter)
                            showDropDown()
                        }
                    }
                }
                Status.LOADING -> {
                    (activity as SignupActivity).showProgress()
                }
                else ->{
                    (activity as SignupActivity).hideProgress()
                    (activity as SignupActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
    }



    private fun handleSignupResponse(signupResponse: SignupResponse?){
        signupResponse?.apply {
            val userData = signupResponse.toUserData()
            AppPreferences.userId = userData.userId
            AppPreferences.userData = Gson().toJson(userData)
            if (userData.photo != null) {
                AppPreferences.userImageUrl = userData.photo
            }
            activity?.launchActivity<AppHomeActivity>{
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity?.finish()
        }?: run {
            showAlertDialog("Invalid Server Response")
        }

    }

    private fun showPhotoPickOption() {
        PhotoOptionBottomSheetDialogFragment(this@SignupFragmentSecond).show(
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
        val scaledImage = if (isCameraImage){
            CommonUtils.scaleDownCameraImage(image, currentPhotoPath, photoFile?.absolutePath?:"")
        }else{
            CommonUtils.scaleDownGalleryImage(image, requireContext(), galleryImgUri)
        }
        with(scaledImage) {
            CommonUtils.compressAndSaveImage(requireContext(), this, "USER").also {
                actualPath = it.absolutePath
                profileViewModel.regImage = actualPath
                setImage(it.absolutePath)
            }
        }
    }

    private fun setImage(path: String){
        val image = BitmapFactory.decodeFile(path)
        image?.let {
            binding.ivUpload.apply {
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

    private fun setSearchList(){

        binding.ivClearSearch.setOnClickListener {
            binding.categorySpinner.setText("")
        }

        binding.categorySpinner.apply {

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
                    isDropDownItemSelected = true;
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    catData.let {
                        selectedCategory = it.id
                        profileViewModel.regCategoryId = it.id
                    }
                    (activity as SignupActivity).hideKeyboard()
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        profileViewModel.getCategories(text.toString())
                    }
                }
                false
            }
        }
    }
}