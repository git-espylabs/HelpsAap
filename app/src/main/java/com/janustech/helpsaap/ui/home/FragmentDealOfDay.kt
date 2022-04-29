package com.janustech.helpsaap.ui.home

import android.app.Activity
import android.app.DatePickerDialog
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
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentDealOfDayBinding
import com.janustech.helpsaap.databinding.FragmentDealOfDayBindingImpl
import com.janustech.helpsaap.map.toLocationDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.profile.PhotoOptionBottomSheetDialogFragment
import com.janustech.helpsaap.ui.startup.AppIntroActivity
import com.janustech.helpsaap.utils.CommonUtils
import com.janustech.helpsaap.utils.PhotoOptionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentDealOfDay: BaseFragmentWithBinding<FragmentDealOfDayBinding>(R.layout.fragment_deal_of_day),View.OnClickListener, PhotoOptionListener {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()
    private lateinit var currentPhotoPath: String
    private var photoFile: File? = null
    private var actualPath = ""

    lateinit var locationsListAdapter: ArrayAdapter<Any>
    private var locationSuggestionList = listOf<LocationDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    val DATE_FORMAT = "dd MMM yyyy"
    val DATE_FORMAT_REGULAR = "dd-MM-yyyy"
    val DATE_FORMAT_SERVER = "yyyy-MM-dd"
    var selectedDateFrom = "";
    var selectedDateTo = "";

    var locationsList = arrayListOf<LocationDataModel>()


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
            viewParent = this@FragmentDealOfDay
        }

        setObserver()
        setLocationDropdown()
        setSelectedLocationListView()
    }

    override fun onClick(v: View?) {
        when(v?.id){

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
                appHomeViewModel.postDeal(requireContext())
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
        appHomeViewModel.locationListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    val locationList = it.data?.data
                    locationSuggestionList = locationList?.map { locData -> locData.toLocationDataModel() } ?: listOf()
                    locationsListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        locationSuggestionList
                    )
                    binding.actLocation.setAdapter(locationsListAdapter)
                }
                Status.LOADING -> {
                }
                else ->{
                    (activity as AppHomeActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }

        appHomeViewModel.postDealResponseReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    showToast("Deal posted successfully")
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

    private fun setLocationDropdown(){
        locationsListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            locationSuggestionList
        )

        binding.ivClearSearch.setOnClickListener {
            binding.actLocation.setText("")
        }

        binding.actLocation.apply {
            threshold = 1

            setAdapter(locationsListAdapter)
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
                    val locationData = (locationsListAdapter.getItem(pos) as LocationDataModel)

                    locationData.let {
                        val locName = it.toString()
                        val locId = it.id
                        locationsList.add(it)
                        appHomeViewModel.selectedDealLocations.add(it.id)
                        binding.locListAdapter?.notifyItemInserted(locationsList.size - 1)
                    }
                    (activity as AppHomeActivity).hideKeyboard()

                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        appHomeViewModel.getLocationSuggestions(text.toString())
                    }
                }
                false
            }
        }
    }

    private fun setSelectedLocationListView(){
        binding.apply {
            locListAdapter = LocationListAdapter(locationsList)
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
                appHomeViewModel.selectedFromDealDate = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
            } else {
                binding.tvEndDate.text = SimpleDateFormat(DATE_FORMAT, Locale.US).format(c.time)
                selectedDateTo = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
                appHomeViewModel.selectedToDealDate = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.US).format(c.time)
            }
        }, year, month, day).show()
    }


    private fun showPhotoPickOption() {
        PhotoOptionBottomSheetDialogFragment(this@FragmentDealOfDay).show(
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
                appHomeViewModel.dealOfDayImage = actualPath
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
}