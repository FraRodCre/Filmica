package com.example.fcojavierrodriguez.filmica.data

import android.net.Uri
import com.example.fcojavierrodriguez.filmica.BuildConfig

object ApiRoutes {

    fun discoverUrl(
        language: String = "en-US",
        sort: String = "popularity.desc",
        page: Int = 1
    ): String {
        return getUriBuilder()
            .appendPath("discover")
            .appendPath("movie")
            .appendQueryParameter("language", language)
            .appendQueryParameter("sort_by", sort)
            .appendQueryParameter("page", page.toString())
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("include_video", "false")
            .build()
            .toString()
    }

    fun trendsUrl(mediaType: String = "movie", timeWindow: String = "day", page: Int = 1): String {
        return getUriBuilder()
            .appendPath("trending")
            .appendPath(mediaType)
            .appendPath(timeWindow)
            .build()
            .toString()
    }

    fun searchUrl(query: String, language: String = "en-US", page: Int = 1): String {
        return getUriBuilder()
            .appendPath("search")
            .appendPath("movie")
            .appendQueryParameter("query", query)
            .appendQueryParameter("language", language)
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("page", page.toString())
            .build()
            .toString()
    }

    private fun getUriBuilder() =
        Uri.Builder()
            .scheme("https")
            .authority("api.themoviedb.org")
            .appendPath("3")
            .appendQueryParameter("api_key", BuildConfig.MovieBDApiKey)
}