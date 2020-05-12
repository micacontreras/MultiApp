package com.example.serviceexam.history.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serviceexam.EXTRA_REPLY_NAME
import com.example.serviceexam.EXTRA_REPLY_URI_PHOT
import com.example.serviceexam.R
import com.example.serviceexam.history.db.Photo
import kotlinx.android.synthetic.main.fragment_history_photos.*

/**
 * A simple [Fragment] subclass.
 */
class HistoryPhotosFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryListAdapter
    var nameUserPhoto: String = ""
    var uriPhoto: String = ""
    var idPhoto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private fun obtainResultPhoto() {
        parentFragmentManager.setFragmentResultListener("Result", this,
            FragmentResultListener { _, bundle ->
                val name = bundle.getString(EXTRA_REPLY_NAME).toString()
                val uri = bundle.getString(EXTRA_REPLY_URI_PHOT).toString()
                if (nameUserPhoto != name || nameUserPhoto.isEmpty()) {
                    nameUserPhoto = name
                }
                if (uriPhoto != uri || uriPhoto.isEmpty()) {
                    uriPhoto = uri
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_photos, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        registerObservers()
    }

    override fun onStart() {
        super.onStart()
        obtainResultPhoto()
    }

    override fun onResume() {
        super.onResume()
        checkNewPhotos()
    }

    private fun checkNewPhotos() {
        //showDialog(requireContext(), "Error", "Error addind photos", "Ok", {findNavController().navigateUp()})
        if(uriPhoto.isNotEmpty() && nameUserPhoto.isNotEmpty()){
            val photo = Photo(idPhoto, uriPhoto, nameUserPhoto)
            historyViewModel.insert(photo)
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryListAdapter(requireContext())
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewHistory)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = adapter
    }

    private fun registerObservers() {
        historyViewModel.allPhoto.observe(viewLifecycleOwner, Observer { photos ->
            photos?.let {
                loadingPhoto.visibility = View.INVISIBLE
                adapter.setPhoto(it)
            }
        })
    }
}
