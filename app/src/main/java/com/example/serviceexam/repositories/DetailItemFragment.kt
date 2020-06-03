package com.example.serviceexam.repositories

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.serviceexam.R
import com.example.serviceexam.repositories.network.Properties
import kotlinx.android.synthetic.main.fragment_detail_item.*

/**
 * A simple [Fragment] subclass.
 */
class DetailItemFragment : Fragment() {

    private val args: DetailItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_item, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modifyScreen(args.repository)
    }

    private fun modifyScreen(data: Properties?) {
        Toast.makeText(requireContext(), data?.owner?.login, Toast.LENGTH_LONG ).show()
        nameOwner?.text = data?.owner?.login
        description?.text = data?.description
        /*Glide.with(requireActivity())
            .load(data?.owner?.imgSrcUrl)
            .into(imageAvatar)*/
    }
}
