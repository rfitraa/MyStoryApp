package com.dicoding.mystoryapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.mystoryapp.api.ApiService
import com.dicoding.mystoryapp.response.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val preference: Preference): PagingSource<Int, ListStoryItem>() {

    companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = preference.getData().token

            if (token!!.isNotEmpty()){
                val responseData = apiService.getAllStories(token = "Bearer $token", position, params.loadSize)
                if (responseData.isSuccessful){
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position -1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                }else{
                    LoadResult.Error(
                        Exception("Story Failed")
                    )
                }
            }else{
                LoadResult.Error(
                    Exception("Story Failed")
                )
            }
        }catch (e: Exception){
            return LoadResult.Error(e)
        }
    }
}