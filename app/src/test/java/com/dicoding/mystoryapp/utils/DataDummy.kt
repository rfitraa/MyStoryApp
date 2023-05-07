package com.dicoding.mystoryapp.utils

import com.dicoding.mystoryapp.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "Dimas",
                "Lorem Ipsum",
                "story-FvU4u0Vp2S3PMsFg",
                -10.212,
                -16.002
            )
            items.add(story)
        }
        return items
    }
}