package com.ezaf.www.citisci.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.*
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.VerboseLevel.INFO_ERR
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.io.ByteArrayOutputStream


class CameraActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPath: String = ""
    private var photoURI: Uri? = null
    private lateinit var exp: Experiment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        exp = SharedDataHelper.focusedExp
        dispatchTakePictureIntent()

        acceptAndUploadBtn.setOnClickListener{
            finish()
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fn = Throwable().stackTrace[0].methodName
        Logger.log(INFO_ERR,"requestCode=$requestCode, resultCode=$resultCode, data=$data")

        Logger.log(INFO_ERR,"photoURI=$photoURI")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data?.getStringExtra("data") as Bitmap
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoURI)
//            val imageBitmap = rotateImageIfNeeded(MediaStore.Images.Media.getBitmap(this.contentResolver, photoURI))
            photoImgView.setImageBitmap(imageBitmap)
            toBase64(imageBitmap)

            RemoteDbHandler.sendMsg(RemoteDbHandler.MsgType.SEND_MAGNETIC_FIELD_SAMPLE, prepareImageSample(imageBitmap)).
                    doOnNext{
                        it.enqueue(object : Callback<ExpSampleList> {
                            override fun onResponse(call: Call<ExpSampleList>, response: Response<ExpSampleList>) {
                                Logger.log(VerboseLevel.INFO, "$fn: SEND_CAM_SAMPLE successfully sent.")
                                exp.actions[0].updateSamplesStatus()
                            }

                            override fun onFailure(call: Call<ExpSampleList>, t: Throwable) {
                                Logger.log(VerboseLevel.INFO, "$fn: failed to send SEND_CAM_SAMPLE.")
                            }
                        })
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }

    private fun prepareImageSample(imageBitmap: Bitmap): ExpSampleList {
        val sampleList = ExpSampleList()
        sampleList.addSample(ExpSample(exp._id, exp.actions[0]._id, "participant@gmail.com", toBase64(imageBitmap)))
        return sampleList
    }

    private fun toBase64(imageBitmap: Bitmap?) {

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

    private fun rotateImageIfNeeded(bitmap: Bitmap): Bitmap {
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val matrix = Matrix()
        when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90-> matrix.setRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180-> matrix.setRotate(180F)

        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
