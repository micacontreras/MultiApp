package com.example.serviceexam.history.rows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.serviceexam.R
import com.example.serviceexam.history.db.History
import kotlinx.android.synthetic.main.photo_row_item.view.*


class HistoryListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<HistoryListAdapter.PhotoViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var list_photos = emptyList<History>()

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo = itemView.photo
        val photoName: TextView = itemView.userName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder(inflater.inflate(R.layout.photo_row_item, parent, false))

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val current = list_photos[position]
        Glide.with(holder.itemView)
            .load(current.photoUri)
            .into(holder.photo)
        holder.photoName.text = current.userName
    }

    internal fun setItem(photosList: List<History>) {
        this.list_photos = photosList
        notifyDataSetChanged()
    }

    override fun getItemCount() = list_photos.size
}