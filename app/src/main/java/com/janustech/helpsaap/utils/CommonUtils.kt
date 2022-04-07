package com.janustech.helpsaap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.janustech.helpsaap.app.AppSettings
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

object CommonUtils {
    private const val serverTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
    private const val serverTimeFormat2 = "yyyy-MM-dd"

    @Suppress("DEPRECATION")
    fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return cm.activeNetworkInfo?.isConnected ?: false
        }

        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

    fun getServerDateTimeFormat(date: Date): String =
        SimpleDateFormat(serverTimeFormat, Locale.US).format(date)

    fun getDrawable(context: Context, @DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(context, drawableRes)

    fun scaleDownImage(realImage: Bitmap): Bitmap {
        val maxImageSize = 300f
        val ratio = min(maxImageSize / realImage.width, maxImageSize / realImage.height)
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        return Bitmap.createScaledBitmap(realImage, width, height, true)
    }

    fun compressAndSaveImage(context: Context, realImage: Bitmap, fileName: String): File {
        val bytes = ByteArrayOutputStream()
        realImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val resizedFile: File = createImageFile(context, fileName)
        val fos = FileOutputStream(resizedFile)
        fos.write(bytes.toByteArray())
        fos.close()
        return resizedFile
    }

    fun createImageFile(context: Context, fileName: String): File {
        val storageDir: File? =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    fun getBitmapFromUri(context: Context, photoUri: Uri): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    photoUri
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, photoUri)
        }
    }

    fun getFileProviderName(context: Context) =
        "${context.packageName}.${AppSettings.CAP_FILE_PROVIDER}"

    fun getDateFromDateString(dateString: String): Date? =
        SimpleDateFormat(serverTimeFormat, Locale.US).parse(dateString)

    fun getYearMonthFromDate(dateString: String): String =
        SimpleDateFormat(serverTimeFormat, Locale.US).parse(dateString)?.let {
            SimpleDateFormat("MMMM yyyy", Locale.US).format(it)
        } ?: ""

    fun getYYYYmmDDFormat(dateString: String): String =
        SimpleDateFormat(serverTimeFormat, Locale.US).parse(dateString)?.let {
            SimpleDateFormat("yyyy-mm-dd", Locale.US).format(it)
        } ?: ""

    fun getConvertedDate(dateString: String): String =
        SimpleDateFormat(serverTimeFormat2, Locale.US).parse(dateString)?.let {
            SimpleDateFormat("dd-MM-yyyy", Locale.US).format(it)
        } ?: ""
}