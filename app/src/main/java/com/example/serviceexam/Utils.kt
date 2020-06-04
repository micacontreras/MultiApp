package com.example.serviceexam

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun showDialog(context: Context, title: String, message: String, positiveButton: String,  positiveAction: (() -> Unit)? = null, negativeButton:String?= null ){
    AlertDialog.Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButton) { _, _ -> positiveAction?.invoke() }
        setNegativeButton(negativeButton){_, _ -> }
        create()
        show()
    }
}

const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

const val EXTRA_REPLY_NAME = "com.example.serviceexam.camera.REPLY.name"
const val EXTRA_REPLY_URI_PHOT = "com.example.serviceexam.camera.REPLY.uri"

/*fun GoogleMap.loadWithDefaultUiConfigurations(activity: Activity) {
    uiSettings.isZoomGesturesEnabled = true
    try {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                activity, R.raw.style_google_map
            )
        )
    } catch (exception: Exception) {
        Log.e("GoogleMap", "loadWithDefaultUIConfigurations", exception)
    }
}*/
