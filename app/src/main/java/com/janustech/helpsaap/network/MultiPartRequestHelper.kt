package com.janustech.helpsaap.network

import android.content.Context
import com.google.android.gms.common.util.IOUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*


object MultiPartRequestHelper {

    const val PREFIX = "stream2file"
    const val SUFFIX = ".tmp"

    fun createFileRequestBody(imageFile: String?, fileName: String, context: Context): MultipartBody.Part {
        var file: File? = null
        var inputStream: InputStream? = null
        var placeHolderFile = "placeholder.png"
        var fileExtn = imageFile?.substring(imageFile.lastIndexOf("."))

        if ((imageFile != null && imageFile.isNotEmpty()) && fileExtn != SUFFIX) {
            inputStream  = FileInputStream(imageFile);
        } else {
            val am = context.assets
            try {
                if (fileExtn == SUFFIX){
                    placeHolderFile = "avatar.jpg"
                }
                inputStream = am.open("defaults/$placeHolderFile")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val bin = BufferedInputStream(inputStream)
        file = stream2file(bin)

        val requestFile = file!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())


        return createFormData(fileName, file.name, requestFile)
    }

    fun createFileRequestBody(imageFile: String, fileName: String): MultipartBody.Part{
        val file = File(imageFile)
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return createFormData(fileName, file.name, requestFile)
    }

    @Throws(IOException::class)
    fun stream2file(`in`: InputStream?): File? {
        val tempFile =
            File.createTempFile(PREFIX, SUFFIX)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile)
            .use { out -> `in`?.let { IOUtils.copyStream(it, out) } }
        return tempFile
    }

    fun createRequestBody(value: String) = value.toRequestBody("multipart/form-data".toMediaTypeOrNull())

    fun createRequestBody(key:String, value: String) = createFormData(key, value)

}