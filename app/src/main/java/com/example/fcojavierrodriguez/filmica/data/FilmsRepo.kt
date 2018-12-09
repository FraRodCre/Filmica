package com.example.fcojavierrodriguez.filmica.data

import android.arch.persistence.room.Room
import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object FilmsRepo {
    private val films: MutableList<Film> = mutableListOf()


    @Volatile
    private var db: AppDatabase? = null

    // Create instance database
    private fun getDbInstance(context: Context): AppDatabase {
        if (db == null) {
            //Create database if not exist
            db = Room.databaseBuilder(context, AppDatabase::class.java, "filmica-db").build()
        }

        return db as AppDatabase
    }

    // Find film by id
    fun findFilmById(id: String): Film? {
        return films.find { film -> film.id == id }
    }

    // create a map with different films list
    private fun addNewFilms(newFilms: List<Film>) {
        newFilms.map { film ->
            if (!films.contains(film)) {
                films.add(film)
            }
        }
    }

    // Execute a request
    fun discoverFilms(
        page: Int = 1,
        context: Context,
        callbackSuccess: ((MutableList<Film>) -> Unit),
        callbackError: ((VolleyError) -> Unit)
    ) {

        if (films.isEmpty() || (page > 1)) {
            requestDiscoverFilms(page, callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(films)
        }
    }

    fun trendsFilms(
        page: Int = 1,
        context: Context,
        callbackSuccess: ((MutableList<Film>) -> Unit),
        callbackError: ((VolleyError) -> Unit)
    ) {

        if (films.isEmpty() || (page > 1)) {
            requestTrends(page, callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(films)
        }
    }

    // Save a film in watchlist
    fun saveFilm(context: Context, film: Film, callbackSuccess: (Film) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().insertFilm(film)
            }

            async.await()
            callbackSuccess.invoke(film)
        }
    }

    // Get lists of films to watch(watchlist)
    fun watchlist(context: Context, callbackSuccess: (List<Film>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().getFilms()
            }

            val films: List<Film> = async.await()
            addNewFilms(films)
            callbackSuccess.invoke(films)
        }

    }

    // Remove a film of my watchlist
    fun deleteFilm(context: Context, film: Film, callbackSuccess: (Film) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().deleteFilm(film)
            }

            async.await()
            callbackSuccess.invoke(film)
        }
    }

    // Get request for discover films
    private fun requestDiscoverFilms(
        page: Int,
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        // URL for discover films
        val url = ApiRoutes.discoverUrl(page = page)

        // Get request from the server in a JsonObjectRequest
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            // If request is Ok, return response
            { response ->
                val newFilms = Film.parseFilms(response)
                addNewFilms(newFilms)
                callbackSuccess.invoke(newFilms)
            },
            // else return error
            { error ->
                callbackError.invoke(error)
            })

        // Create a new request queque with all request. When request is finished, return the response or error contened in variable request.
        Volley.newRequestQueue(context)
            .add(request)
    }

    // Get request of trends
    private fun requestTrends(
        page: Int,
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        // URL for trend films
        val url = ApiRoutes.trendsUrl(page = page)

        // Get request from the server in a JsonObjectRequest
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            // If request is Ok, return response
            { response ->
                val newFilms = Film.parseFilms(response)
                addNewFilms(newFilms)
                callbackSuccess.invoke(newFilms)
            },
            // else return error
            { error ->
                callbackError.invoke(error)
            })

        // Create a new request queque with all request. When request is finished, return the response or error contened in variable request.
        Volley.newRequestQueue(context)
            .add(request)
    }

    // Get request of search films
    fun requestSearch(
        query: String,
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        // URL for search films
        val url = ApiRoutes.searchUrl(query)

        // Get request from the server in a JsonObjectRequest
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            // If request is Ok, return response
            { response ->
                var newFilms = Film.parseFilms(response)
                if (newFilms.size > 10) {
                    newFilms = newFilms.subList(0, 10)
                }
                addNewFilms(newFilms)
                callbackSuccess.invoke(newFilms)
            },
            // else return error
            { error ->
                callbackError.invoke(error)
            })

        // Create a new request queque with all request. When request is finished, return the response or error contened in variable request.
        Volley.newRequestQueue(context)
            .add(request)
    }
}