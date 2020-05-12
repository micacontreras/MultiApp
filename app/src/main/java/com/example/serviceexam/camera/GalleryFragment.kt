package com.example.serviceexam.camera

import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.serviceexam.*
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
        override fun getItem(position: Int): Fragment = PhotoFragment.create(mediaList[position])
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
            view.findViewById<ImageButton>(R.id.delete_button).isEnabled = false
            view.findViewById<ImageButton>(R.id.share_button).isEnabled = false
        }
        val mediaViewPager = view.findViewById<ViewPager>(R.id.photo_view_pager).apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }

        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle share button press
        view.findViewById<ImageButton>(R.id.share_button).setOnClickListener {

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
        view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {

            mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->
                showDialog(requireContext(), "Confirm", "Do you want to delete the current photo?",
                    "Ok",  {
                        mediaFile.delete()
                        mediaList.removeAt(mediaViewPager.currentItem)
                        mediaViewPager.adapter?.notifyDataSetChanged()

                        // If all photos have been deleted, return to camera
                        if (mediaList.isEmpty()) {
                            findNavController().navigateUp()
                        }
                    }, "Cancel")
            }
        }
    }
}
