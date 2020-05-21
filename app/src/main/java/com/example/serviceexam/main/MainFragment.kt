package com.example.serviceexam.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.serviceexam.R
import com.example.serviceexam.showDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserSaved()
    }

    private fun checkUserSaved() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val userSaved = sharedPref.getString(getString(R.string.name), null)
        if (!userSaved.isNullOrEmpty()) {
            showDialog(
                requireContext(),
                "Bienvenido",
                "Ha ingresado con el usuario $userSaved",
                "Ok"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnList.setOnClickListener{findNavController().navigate(MainFragmentDirections.navigateToListRepositories())}
        btnTakePhoto.setOnClickListener { findNavController().navigate(MainFragmentDirections.navigateToCamera()) }
        btnHistory.setOnClickListener{findNavController().navigate(MainFragmentDirections.navigateToHistory())}
        signOffButton.setOnClickListener { Bundle().apply {
            putBoolean("SignOff", true)
        }.also {
            setFragmentResult("IsSignOffSelect", it)
            findNavController().navigateUp() } }
    }
}
