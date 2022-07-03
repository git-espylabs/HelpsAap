package com.janustech.helpsaap.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.app.AppSettings
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt


object CommonUtils {
    private const val serverTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
    private const val serverTimeFormat2 = "yyyy-MM-dd"

    @SuppressLint("MissingPermission")
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

    private fun scaleDownImage(realImage: Bitmap): Bitmap {
        val maxImageSize = 300f
        val ratio = min(maxImageSize / realImage.width, maxImageSize / realImage.height)
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        return Bitmap.createScaledBitmap(realImage, width, height, true)
    }

    fun scaleDownCameraImage(captured: Bitmap, path: String, actualPath: String): Bitmap {
        val rotatedImg = getClearExifBitmap(path, actualPath) ?: captured
        return scaleDownImage(rotatedImg)
    }

    fun scaleDownGalleryImage(choosen: Bitmap, context: Context, uri: Uri): Bitmap {
        val rotatedImg = getClearExifBitmap(context, uri)?:choosen
        return scaleDownImage(rotatedImg)
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

    fun getConvertedDateWithMonthN(dateString: String): String =
        SimpleDateFormat(serverTimeFormat2, Locale.US).parse(dateString)?.let {
            SimpleDateFormat("dd MMM yyyy", Locale.US).format(it)
        } ?: ""


    fun isAppInstalled(ctx: Context, packageName: String): Boolean {
        val pm = ctx.packageManager
        val app_installed: Boolean = try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    fun contactWhatsap(ctx: Context, phone:String){
        val pm: PackageManager = ctx.packageManager
        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://api.whatsapp.com/send?phone=$phone:")
        ctx.startActivity(i)
    }

    fun openWhatsApp(ctx: Context, phone: String, packageWhatsap: String) {
        try {
            val validPhone = phone.substring(phone.length - 10);
            val packageManager: PackageManager = ctx.packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=+91$validPhone"
            i.setPackage(packageWhatsap)
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                startActivity(ctx, i, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun share(ctx: Context, shareBody: String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Share Content"
        )
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)

        ctx.startActivity(Intent.createChooser(intent, "Share using.."))
    }

    private fun getClearExifBitmap(currentPhotoPath: String, absolutePath: String): Bitmap?{
        val exif = ExifInterface(currentPhotoPath)
        var image = BitmapFactory.decodeFile(absolutePath)

        val matrix = Matrix()
        when(exif.getAttribute(ExifInterface.TAG_ORIENTATION)){
            ExifInterface.ORIENTATION_ROTATE_90.toString() -> {
                matrix.postRotate(90F)
            }
            ExifInterface.ORIENTATION_ROTATE_180.toString() -> {
                matrix.postRotate(180F)
            }
            ExifInterface.ORIENTATION_ROTATE_270.toString() -> {
                matrix.postRotate(270F)
            }
            else ->{
                matrix.postRotate(0F)
            }
        }
        image = Bitmap.createBitmap(image , 0, 0, image.width, image.height, matrix, true)

        return image
    }

    private fun getClearExifBitmap(context: Context, uri: Uri): Bitmap?{
        var outImage: Bitmap? = null
        var image = getBitmapFromUri(context, uri)
        image?.let { img->

            val parcelFileDescriptor: ParcelFileDescriptor? =
                context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            fileDescriptor?.let {
                val exif = ExifInterface(it)

                val matrix = Matrix()
                when(exif.getAttribute(ExifInterface.TAG_ORIENTATION)){
                    ExifInterface.ORIENTATION_ROTATE_90.toString() -> {
                        matrix.postRotate(90F)
                    }
                    ExifInterface.ORIENTATION_ROTATE_180.toString() -> {
                        matrix.postRotate(180F)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270.toString() -> {
                        matrix.postRotate(270F)
                    }
                    else ->{
                        matrix.postRotate(0F)
                    }
                }
                outImage = Bitmap.createBitmap(img , 0, 0, img.width, img.height, matrix, true)
            }
        }

        return outImage
    }


    fun writeLogFile(context: Context, logText: String) {

        if (BuildConfig.DEBUG.not()){
            return
        }

        try {
            val logTime =
                SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(Calendar.getInstance().time)
            val logDir = File(context.getExternalFilesDir(null), "Log")
            if (!logDir.exists()) {
                logDir.mkdir()
            }
            val logFile = File(logDir, "HAP_LOG.txt")
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append("$logTime : $logText\n\n")
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
}