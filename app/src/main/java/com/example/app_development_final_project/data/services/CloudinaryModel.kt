package com.example.app_development_final_project.data.services

import android.graphics.Bitmap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.app_development_final_project.base.OptionalCallback
import com.example.app_development_final_project.base.WatchItApplication
import com.example.app_development_final_project.extensions.toFile
import java.io.File

class CloudinaryModel {
    private fun uploadImageToCloudinary(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val context = WatchItApplication.getAppContext()
        val file: File = bitmap.toFile(context, name)

        MediaManager.get().upload(file.path)
            .option("folder", "images")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    fun uploadImage(image: Bitmap, name: String, callback: OptionalCallback<String>) {
        uploadImageToCloudinary(
            bitmap = image,
            name = name,
            onSuccess = callback,
            onError = { callback(null) }
        )
    }
}