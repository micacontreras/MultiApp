package com.example.serviceexam.repositories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.bumptech.glide.Glide
import com.example.serviceexam.R
import com.example.serviceexam.repositories.network.Properties
import com.example.serviceexam.setResultListener
import kotlinx.android.synthetic.main.fragment_detail_item.*

/**
 * A simple [Fragment] subclass.
 */
class DetailItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_item, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        obteinUser()
    }

    private fun obteinUser() {
       parentFragmentManager.setFragmentResultListener("Data", this,
            FragmentResultListener { _, bundle ->
                bundle.getParcelable<Properties>("DataUser").also {
                    modifyScreen(it)
                }
            })
        //Terminar de probar!!

        /*setResultListener("Data"){ _, bundle ->
            modifyScreen(bundle.getBundle("DataSend")?.getParcelable("DataUser"))
        }*/
    }

    private fun modifyScreen(data: Properties?) {

        nameOwner.text = data?.owner?.login
        description.text = data?.description
        Glide.with(requireActivity())
            .load(data?.owner?.imgSrcUrl)
            .into(imageAvatar)
    }
}
