package com.example.serviceexam.history.photos

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.serviceexam.R
import com.example.serviceexam.history.db.Photo
import com.example.serviceexam.repositories.network.Properties
import kotlinx.android.synthetic.main.photo_row_item.view.*
import kotlinx.android.synthetic.main.text_row_item.view.*

class HistoryListAdapter internal constructor(context: Context) : RecyclerView.Adapter<HistoryListAdapter.PhotoViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var photos = emptyList<Photo>() // Cached copy of words

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.photo)
        val photoName: TextView = itemView.findViewById(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = inflater.inflate(R.layout.photo_row_item, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val current = photos[position]
        Glide.with(holder.itemView)
            .load(current.photo)
            .into(holder.photo)
        holder.photoName.text = current.userName
    }

    internal fun setPhoto(photos: List<Photo>) {
        this.photos = photos
        notifyDataSetChanged()
    }

    override fun getItemCount() = photos.size
}