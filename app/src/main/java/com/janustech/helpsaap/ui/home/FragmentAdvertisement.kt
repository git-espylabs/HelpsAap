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
    private var photoFile: File? = null
    private var actualPath = ""
    val DATE_FORMAT = "dd MMM yyyy"
    val DATE_FORMAT_REGULAR = "dd-MM-yyyy"
    val DATE_FORMAT_SERVER = "yyyy-MM-dd"
    var selectedDateFrom = "";
    var selectedDateTo = "";
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
                appHomeViewModel.postAds(requireContext())
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
        appHomeViewModel.publishAdsResponseReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
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
    }


    private fun setPublishLocations(){

        binding.tvDropdownClub.setText("State")
        val publishList = arrayListOf<PublishTypeModel>()
        publishList.add(PublishTypeModel("4", "State"))
        publishList.add(PublishTypeModel("5", "District"))
        publishList.add(PublishTypeModel("1", "Panchayth"))
        publishList.add(PublishTypeModel("2", "Municipality"))
        publishList.add(PublishTypeModel("3", "Corporation"))

        publishListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            publishList
        )
        binding.tvDropdownClub.setAdapter(publishListAdapter)

        setPriceLayout(publishList[0])

        binding.tvDropdownClub.setOnItemClickListener { _, _, position, _ ->
            appHomeViewModel.selectedPublicLocationId = publishList[position].publishTypeId
            appHomeViewModel.selectedPublicLocationType = publishList[position].publishTypeId
            setPriceLayout(publishList[position])
        }
    }

    private fun setPriceLayout(publishTypeModel: PublishTypeModel){

        binding.apply {
            lPubLoc.run{
                tvLocTitle.text = publishTypeModel.publishTypeIdName
                btnPrice1.text = "₹ 79/\nMonth"
                btnPrice2.text = "₹ 139/\n2 Month"
                btnPrice3.text = "₹ 189/\n3 Month"
                btnPrice1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                btnPrice1.setBackgroundResource(R.drawable.rounded_rect_green_filled)
                appHomeViewModel.selectedAMount = "79"

                btnPrice1.setOnClickListener {
                    appHomeViewModel.selectedAMount = "79"

                    btnPrice1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    btnPrice1.setBackgroundResource(R.drawable.rounded_rect_green_filled)

                    btnPrice2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btnPrice2.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice3.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btnPrice3.setBackgroundResource(R.drawable.rounded_rect_grey_filled)
                }

                btnPrice2.setOnClickListener {
                    appHomeViewModel.selectedAMount = "139"

                    btnPrice1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btnPrice1.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    btnPrice2.setBackgroundResource(R.drawable.rounded_rect_green_filled)

                    btnPrice3.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btnPrice3.setBackgroundResource(R.drawable.rounded_rect_grey_filled)
                }

                btnPrice3.setOnClickListener {
                    appHomeViewModel.selectedAMount = "189"

                    btnPrice1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btnPrice1.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btnPrice2.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice3.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    btnPrice3.setBackgroundResource(R.drawable.rounded_rect_green_filled)
                }
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
                binding.tvStartDate.text = SimpleDateFormat(DATE_FORMAT, Locale.US).format(c.time)
                selectedDateFrom = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
                appHomeViewModel.selectedFromAdsDate = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
            } else {
                binding.tvEndDate.text = SimpleDateFormat(DATE_FORMAT, Locale.US).format(c.time)
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
                appHomeViewModel.adsImage = actualPath
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
}