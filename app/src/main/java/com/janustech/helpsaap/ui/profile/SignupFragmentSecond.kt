package com.janustech.helpsaap.ui.profile

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
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentRegisterBinding
import com.janustech.helpsaap.databinding.FragmentRegisterSecondBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.map.toUserData
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.Status
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
    private var photoFile: File? = null
    private var actualPath = ""

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    private var selectedCategory = ""


    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            setCameraPicToImageView()
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

            btnFinish.setOnClickListener {
                activity?.let {
                    profileViewModel.registerApp(it)
                }
            }

            promptFileSelect.setOnClickListener {
                showPhotoPickOption()
            }
        }

        setObserver()
        setSearchList()
    }

    override fun onTakePhotoSelected() {
        dispatchTakePictureIntent()
    }

    override fun onChoosePhotoSelected() {
        dispatchPickPhotoIntent()
    }

    private fun setObserver(){
        profileViewModel.registerResponseReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    (activity as SignupActivity).hideProgress()

                    AppPreferences.userId = it.data?.data?:""
                    val userData = profileViewModel.run {
                        UserData(it.data?.data?:"", regName, regMob, regWhatsapNo, regEmail, regWeb, regPin, "", "")
                    }
                    AppPreferences.userData = Gson().toJson(userData)


                    showToast("Registration Success")
                    activity?.launchActivity<AppHomeActivity>{
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    activity?.finish()
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
        scaleDownImage(BitmapFactory.decodeFile(currentPhotoPath))
        deleteOriginalCameraImage()
    }

    private fun setGalleryPicToImageView(uri: Uri) {
        currentPhotoPath = uri.path?: ""
        photoFile = File(currentPhotoPath)
        CommonUtils.getBitmapFromUri(requireContext(), uri)
            ?.let { bitmap -> scaleDownImage(bitmap) }
    }

    private fun scaleDownImage(image: Bitmap) {
        with(CommonUtils.scaleDownImage(image)) {
            CommonUtils.compressAndSaveImage(requireContext(), this, "USER").also {
                actualPath = it.absolutePath
                profileViewModel.regImage = actualPath
                setImage(it.absolutePath)
            }
        }
    }

    private fun setImage(path: String){
        val image = CommonUtils.getClearExifBitmap(currentPhotoPath, path)
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
                        selectedCategory = it.id
                        profileViewModel.regCategoryId = it.id
                        (activity as SignupActivity).hideKeyboard()
                    }
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