package com.example.serviceexam.camera.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.serviceexam.*
import com.example.serviceexam.camera.gallery.GalleryFragmentArgs
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

val EXTENSION_WHITELIST = arrayOf("JPG")

/** Fragment used to present the user with a gallery of photos taken */
@Suppress("DEPRECATION")
class GalleryFragment internal constructor() : Fragment() {

    private val args: GalleryFragmentArgs by navArgs()

    private lateinit var mediaList: MutableList<File>

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment =
            PhotoFragment.create(
                mediaList[position]
            )
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true
        // Get root directory of media from navigation arguments
        val rootDirectory = File(args.rootDirectory)
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mediaList.isEmpty()) {
            view.delete_button.isEnabled = false
            view.share_button.isEnabled = false
        }
        val mediaViewPager = view.photo_view_pager.apply {
            //offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }

        view.back_button.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle share button press
        view.share_button.setOnClickListener {

            mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->
                val intent = Intent().apply {
                    // Infer media type from file extension
                    val mediaType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(mediaFile.extension)
                    val uri = FileProvider.getUriForFile(
                        requireContext(), BuildConfig.APPLICATION_ID + ".provider", mediaFile
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mediaType
                    action = Intent.ACTION_SEND
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
            }
        }

        // Handle delete button press
        view.delete_button.setOnClickListener {

            mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->
                showDialog(requireContext(), "Confirm", "Do you want to delete the current photo?",
                    "Ok",  {
                        mediaFile.delete()
                        mediaList.removeAt(mediaViewPager.currentItem)
                        mediaViewPager.adapter?.notifyDataSetChanged()
                        //Delete the photo from the DB
                        Bundle().apply {
                            putString(EXTRA_REPLY_URI_PHOT, mediaFile.absolutePath)
                        }.also {
                            setFragmentResult("ResultToDelete", it)
                        }
                        // If all photos have been deleted, return to camera
                        if (mediaList.isEmpty()) {
                            findNavController().navigateUp()
                        }
                    }, "Cancel")
            }
        }
    }
}
