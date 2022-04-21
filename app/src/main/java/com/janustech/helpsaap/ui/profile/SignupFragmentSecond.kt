package com.janustech.helpsaap.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentRegisterBinding
import com.janustech.helpsaap.databinding.FragmentRegisterSecondBinding
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
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
                    it.data?.data
                    showToast("Registration Success")
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