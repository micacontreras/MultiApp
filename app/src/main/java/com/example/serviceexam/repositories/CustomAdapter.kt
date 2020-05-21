package com.example.serviceexam.repositories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.serviceexam.R
import com.example.serviceexam.repositories.network.Properties
import kotlinx.android.synthetic.main.text_row_item.view.*


class CustomAdapter(var context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    lateinit var onClick: (Properties) -> Unit
    private var dataSet = emptyList<Properties>()


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.text_row_item, viewGroup, false)
        return this.ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.bindResponse(item, onClick)
    }

    override fun getItemCount() =  dataSet.size

    fun addItems(items: List<Properties>) {
        this.dataSet = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v

        fun bindResponse(properties: Properties, onClick: (Properties) -> Unit) = with(itemView){
            view.idRepository.text= properties.id.toString()
            view.nameRepository.text = properties.full_name
            Glide.with(context)
                .load(properties.owner.imgSrcUrl)
                .into(view.imageView)
            setOnClickListener{ onClick(properties) }
        }
    }
}
