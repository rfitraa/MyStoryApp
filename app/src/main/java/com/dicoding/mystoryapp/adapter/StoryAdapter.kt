package com.dicoding.mystoryapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.databinding.ItemStoryBinding
import com.dicoding.mystoryapp.response.ListStoryItem

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK){

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
        val listStory = getItem(position)
        holder.binding.apply {
            if (listStory != null) {
                holder.binding(listStory)
                holder.itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(
                        listStory
                    )
                }
            }
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return  oldItem.id == newItem.id
            }
        }
    }
}