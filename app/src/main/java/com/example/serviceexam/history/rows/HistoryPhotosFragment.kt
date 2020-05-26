package com.example.serviceexam.history.rows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serviceexam.EXTRA_REPLY_NAME
import com.example.serviceexam.EXTRA_REPLY_URI_PHOT
import com.example.serviceexam.R
import com.example.serviceexam.history.db.History
import com.example.serviceexam.showDialog
import kotlinx.android.synthetic.main.fragment_history_photos.*


/**
 * A simple [Fragment] subclass.
 */
class HistoryPhotosFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryListAdapter
    private var nameUserPhotoToAdd: String = ""
    private var uriPhotoToAdd: String = ""
    private var idPhoto = 0
    private var uriPhotoToToDelete: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private fun obtainResultPhotoToAdd() {
        parentFragmentManager.setFragmentResultListener("ResultToAdd", this,
            FragmentResultListener { _, bundle ->
                val name = bundle.getString(EXTRA_REPLY_NAME).toString()
                val uri = bundle.getString(EXTRA_REPLY_URI_PHOT).toString()
                if (nameUserPhotoToAdd != name || nameUserPhotoToAdd.isEmpty()) {
                    nameUserPhotoToAdd = name
                }
                if (uriPhotoToAdd != uri || uriPhotoToAdd.isEmpty()) {
                    uriPhotoToAdd = uri
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_history_photos, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        registerObservers()
    }

    override fun onStart() {
        super.onStart()
        obtainResultPhotoToAdd()
        obtainPhotoToDelete()
    }

    private fun obtainPhotoToDelete() {
        parentFragmentManager.setFragmentResultListener("ResultToDelete", this,
            FragmentResultListener { _, bundle ->
                val uri = bundle.getString(EXTRA_REPLY_URI_PHOT).toString()
                if (uriPhotoToToDelete != uri || uriPhotoToToDelete.isEmpty()) {
                    uriPhotoToToDelete = uri
                }
            })
    }

    override fun onResume() {
        super.onResume()
        checkNewPhotos()
        checkPhotosToDelete()
    }

    private fun checkNewPhotos() {
        if (uriPhotoToAdd.isNotEmpty() && nameUserPhotoToAdd.isNotEmpty()) {
            val photo = History(idPhoto, uriPhotoToAdd, nameUserPhotoToAdd)
            historyViewModel.insert(photo)
        }
    }

    private fun checkPhotosToDelete() {
        if (uriPhotoToToDelete.isNotEmpty()) {
            historyViewModel.delete(uriPhotoToToDelete)
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryListAdapter(requireContext())
        recyclerViewHistory.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
    }

    private fun registerObservers() {
        historyViewModel.allItems.observe(viewLifecycleOwner, Observer { history ->
            history?.let {
                loadingHistory.visibility = View.INVISIBLE
                if (it.isNotEmpty()) {
                    adapter.setItem(it)
                } else {
                    showDialog(
                        requireContext(),
                        "Empty List",
                        "Do you want to navigate back?",
                        "OK",
                        { findNavController().navigateUp() },
                        "Cancel"
                    )
                }
            }
        })
    }
}
