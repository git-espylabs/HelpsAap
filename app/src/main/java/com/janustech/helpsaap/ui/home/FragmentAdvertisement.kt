package com.janustech.helpsaap.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAdvertisementBinding
import com.janustech.helpsaap.databinding.FragmentAdvertisementBindingImpl
import com.janustech.helpsaap.model.AdsPackageModel
import com.janustech.helpsaap.model.PublishTypeModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.profile.PhotoOptionBottomSheetDialogFragment
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.PhotoOptionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentAdvertisement: BaseFragmentWithBinding<FragmentAdvertisementBinding>(R.layout.fragment_advertisement),
    View.OnClickListener, PhotoOptionListener {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()

    private lateinit var currentPhotoPath: String
    private lateinit var galleryImgUri: Uri
    private var photoFile: File? = null
    private var actualPath = ""
    private var isCameraImage = true

    val DATE_FORMAT = "dd MMM yyyy"
    val DATE_FORMAT_REGULAR = "dd-MM-yyyy"
    val DATE_FORMAT_SERVER = "yyyy-MM-dd"
    var selectedDateFrom = "";
    var selectedDateTo = "";
    var selectedPackageDuration = 0
    private lateinit var publishListAdapter: ArrayAdapter<PublishTypeModel>


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
            viewModel = appHomeViewModel
            viewParent = this@FragmentAdvertisement

        }

        setPublishLocations()
        setObserver()

    }

    override fun onStop() {
        super.onStop()
        appHomeViewModel._publishAdsResponseReceiver.value = null
    }

    override fun onClick(p0: View?) {
        when(p0?.id){

            R.id.tvStartDate -> {
                showDatePickerDialog(1)
            }
            R.id.tvEndDate -> {
                showDatePickerDialog(2)
            }
            R.id.promptFileSelect -> {
                showPhotoPickOption()
            }
            R.id.btnPost -> {

                if (appHomeViewModel.adsImage.isEmpty()){
                    showAlertDialog("Please select an Ad Image")
                }else if(selectedPackageDuration <= 0 ||
                    appHomeViewModel.selectedAMount.isEmpty() ||
                    appHomeViewModel.selectedPublicLocationId.isEmpty() ||
                    appHomeViewModel.selectedPublicLocationType.isEmpty()){
                    showAlertDialog("Please select a valid publishing location & package")
                }else{
                    val curDateString = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(Calendar.getInstance().time)
                    val currentDate = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).parse(curDateString)

                    val expDateCal = Calendar.getInstance().apply {
                        time = currentDate as Date
                        add(Calendar.MONTH,selectedPackageDuration)
                    }
                    val expDateString = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(expDateCal.time)

                    appHomeViewModel.selectedFromAdsDate = curDateString
                    appHomeViewModel.selectedToAdsDate = expDateString

                    appHomeViewModel.postAds(requireContext())
                }
            }

        }
    }

    override fun onTakePhotoSelected() {
        dispatchTakePictureIntent()
    }

    override fun onChoosePhotoSelected() {
        dispatchPickPhotoIntent()
    }

    private fun setObserver(){
        appHomeViewModel.publishAdsResponseReceiver?.observe(viewLifecycleOwner){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            resetViews()
                            (activity as AppHomeActivity).hideProgress()
                            showToast("Ad posted successfully")
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
            } catch (e: Exception) {
            }
        }
    }


    private fun setPublishLocations(){
        val publishList = arrayListOf<PublishTypeModel>()
        val statePacks = listOf(
            AdsPackageModel("1","₹ 99/Month", "99", 1 ),
            AdsPackageModel("2","₹ 149/Month", "149", 2 ),
            AdsPackageModel("3","₹ 199/Month", "199", 3 ),
        )
        val districtPacks = listOf(
            AdsPackageModel("1","₹ 89/Month", "89", 1 ),
            AdsPackageModel("2","₹ 139/Month", "139", 2 ),
            AdsPackageModel("3","₹ 189/Month", "189", 3 ),
        )
        val corporationPacks = listOf(
            AdsPackageModel("1","₹ 79/Month", "79", 1 ),
            AdsPackageModel("2","₹ 129/Month", "129", 2 ),
            AdsPackageModel("3","₹ 179/Month", "179", 3 ),
        )
        val municipalityPacks = listOf(
            AdsPackageModel("1","₹ 89/Month", "89", 1 ),
            AdsPackageModel("2","₹ 139/Month", "139", 2 ),
            AdsPackageModel("3","₹ 189/Month", "189", 3 ),
        )
        val panchayathPacks = listOf(
            AdsPackageModel("1","₹ 49/Month", "49", 1 ),
            AdsPackageModel("2","₹ 99/Month", "99", 2 ),
            AdsPackageModel("3","₹ 149/Month", "149", 3 ),
        )
        publishList.add(PublishTypeModel("4", "State", statePacks))
        publishList.add(PublishTypeModel("5", "District", districtPacks))
        publishList.add(PublishTypeModel("3", "Corporation", corporationPacks))
        publishList.add(PublishTypeModel("2", "Municipality", municipalityPacks))
        publishList.add(PublishTypeModel("1", "Grama Panchayath", panchayathPacks))

        binding.apply {
            publishTypesAdapter = PublishTypesAdapter(requireContext(), publishList){ packageTypeId, packageTypeName, _, packagePrice, packageDuration ->
                appHomeViewModel.selectedPublicLocationId = packageTypeId
                appHomeViewModel.selectedPublicLocationType = packageTypeName
                appHomeViewModel.selectedAMount = packagePrice
                selectedPackageDuration = packageDuration
            }
        }
    }

    private fun showDatePickerDialog(dateType: Int){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        DatePickerDialog(requireActivity(), { _, year, monthOfYear, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            if (dateType == 1) {
                selectedDateFrom = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
                appHomeViewModel.selectedFromAdsDate = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
            } else {
                selectedDateTo = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
                appHomeViewModel.selectedToAdsDate = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
            }
        }, year, month, day).show()
    }

    private fun showPhotoPickOption() {
        PhotoOptionBottomSheetDialogFragment(this@FragmentAdvertisement).show(
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
                appHomeViewModel.adsImage = actualPath
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

    private fun resetViews(){
        appHomeViewModel.selectedPublicLocationId = ""
        appHomeViewModel.selectedPublicLocationType = ""
        appHomeViewModel.selectedAMount = ""
        appHomeViewModel.selectedFromAdsDate = ""
        appHomeViewModel.selectedToAdsDate = ""
        appHomeViewModel.adsImage = ""
        selectedPackageDuration = 0
        setPublishLocations()
        binding.apply {
            ivUpload.setImageResource(R.drawable.ic_upload)
        }
    }
}