package com.ezaf.www.citisci.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel.INFO_ERR
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.io.ByteArrayOutputStream


class CameraActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPath: String = ""
    private var photoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        //TODO: change to view bind using a CameraViewModel
        takePictureBtn.setOnClickListener{
            dispatchTakePictureIntent()
        }
    }

    //TODO: move logic to CameraViewModel
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //TODO: move logic to CameraViewModel
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Logger.log(INFO_ERR,"error creating image file")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                            this,
                            getString(R.string.fileprovider_path),
                            it
                    )
                    Logger.log(INFO_ERR,"successfully got a file from FileProvider")
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    //TODO: move logic to CameraViewModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.log(INFO_ERR,"requestCode=$requestCode, resultCode=$resultCode, data=$data")

        Logger.log(INFO_ERR,"photoURI=$photoURI")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data?.getStringExtra("data") as Bitmap
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoURI)
            photoImgView.setImageBitmap(imageBitmap)

            toBase64Test(imageBitmap)
        }
    }

    private fun toBase64Test(imageBitmap: Bitmap?) {

        Observable.fromCallable {
            ByteArrayOutputStream()
        }.doOnNext{
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it) //bm is the bitmap object
            val b = it.toByteArray()
            val encodedImage = Base64.getEncoder().encodeToString(b)
            Logger.log(INFO_ERR,"base64pic [$encodedImage]")
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }
}
