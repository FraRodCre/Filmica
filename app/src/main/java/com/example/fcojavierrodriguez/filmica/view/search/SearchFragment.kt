package com.example.fcojavierrodriguez.filmica.view.search


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.fcojavierrodriguez.filmica.R
import com.example.fcojavierrodriguez.filmica.data.Film
import com.example.fcojavierrodriguez.filmica.data.FilmsRepo
import com.example.fcojavierrodriguez.filmica.view.watchlist.WatchlistAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_not_found.*

class SearchFragment : Fragment() {

    private var isWritting: Boolean = false

    lateinit var listener: SearchFragment.OnItemClickListener

    val adapter: WatchlistAdapter by lazy {
        val instance = WatchlistAdapter { film ->
            this.listener.onItemClicked(film)
        }
        instance
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SearchFragment.OnItemClickListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_search.adapter = adapter

        // Notify when content in the editext have been modified
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                text.let {
                    val textQuery = text.toString()

                    if (textQuery.length > 3) {
                        isWritting = false
                        progress?.visibility = View.VISIBLE
                        configureScreenQuerySearch(textQuery)
                    }else{
                        isWritting = true
                        progress?.visibility = View.GONE
                    }

                    layoutError?.visibility = View.GONE
                    layoutNotFound?.visibility = View.GONE
                    list_search.visibility = View.GONE

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        btnRetry?.setOnClickListener { configureScreenQuerySearch("") }

    }

    // Show the layout error, not found or films
    private fun configureScreenQuerySearch(query: String ) {

        FilmsRepo.requestSearch(
            query, { films ->
                // Check user is written and exist the film
                if(!isWritting && films.count() > 0) {
                    progress?.visibility = View.GONE
                    layoutError?.visibility = View.GONE
                    layoutNotFound?.visibility = View.GONE
                    list_search.visibility = View.VISIBLE
                    adapter.setFilms(films)

                 // Not exits the film
                }else if( films.size == 0){
                    progress?.visibility = View.GONE
                    layoutError?.visibility = View.GONE
                    layoutNotFound?.visibility = View.VISIBLE
                    list_search.visibility = View.GONE
                }

            },
            { error ->
                progress?.visibility = View.INVISIBLE
                layoutError?.visibility = View.VISIBLE
                layoutNotFound?.visibility = View.INVISIBLE
                list_search.visibility = View.INVISIBLE

                error.printStackTrace()
            },
            context!!
        )
    }

    interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }
}
