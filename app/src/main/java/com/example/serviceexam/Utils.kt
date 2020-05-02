package com.example.serviceexam

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener

fun showDialog(context: Context, title: String, message: String, positiveButton: String, positiveAction: (() -> Unit)? = null ){
    AlertDialog.Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButton) { _, _ -> positiveAction?.invoke() }
        create()
        show()
    }
}

fun Fragment.setResultListener(requestKey: String, action: (key: String, bundle: Bundle) -> Unit): Bundle {

    return Bundle().also {
        parentFragmentManager.setFragmentResultListener(requestKey, this,
            FragmentResultListener { key, bundle ->
                it
            })
    }
}
