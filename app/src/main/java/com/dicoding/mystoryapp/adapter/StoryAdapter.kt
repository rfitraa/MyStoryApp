package com.dicoding.mystoryapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.databinding.ItemStoryBinding
import com.dicoding.mystoryapp.response.ListStoryItem

class StoryAdapter(private val listStory: List<ListStoryItem>): RecyclerView.Adapter<StoryAdapter.ViewHolder>(){

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun binding(story: ListStoryItem){
            binding.tvUsername.text = story.name
            binding.tvDesc.text = story.description
            Glide.with(itemView)
                .load(story.photoUrl)
                .into(binding.ivProfile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {
        holder.binding(listStory[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(
                listStory[holder.adapterPosition]
            )
        }
    }

    override fun getItemCount(): Int {
        return listStory.size
    }
}