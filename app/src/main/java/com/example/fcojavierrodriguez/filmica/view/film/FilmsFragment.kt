package com.example.fcojavierrodriguez.filmica.view.film

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fcojavierrodriguez.filmica.view.util.ItemOffsetDecoration
import com.example.fcojavierrodriguez.filmica.R
import com.example.fcojavierrodriguez.filmica.data.Film
import com.example.fcojavierrodriguez.filmica.data.FilmsRepo
import kotlinx.android.synthetic.main.fragment_films.*
import kotlinx.android.synthetic.main.layout_error.*

class FilmsFragment : Fragment() {

    private var page = 1

    lateinit var listener: OnItemClickListener

    val list: RecyclerView by lazy {
        val instance = view!!.findViewById<RecyclerView>(R.id.list_films)
        instance.addItemDecoration(ItemOffsetDecoration(R.dimen.offset_grid))
        instance.setHasFixedSize(true)
        instance
    }

    val adapter: FilmsAdapter by lazy {
        val instance = FilmsAdapter { film ->
            this.listener.onItemClicked(film)
        }

        instance
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnItemClickListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_films, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter

        setRecyclerViewScrollListener()

        btnRetry?.setOnClickListener { reload() }
    }

    override fun onResume() {
        super.onResume()
        this.reload()
    }

    fun reload() {
        FilmsRepo.discoverFilms(page, context!!,
            { films ->
                progress?.visibility = View.INVISIBLE
                layoutError?.visibility = View.INVISIBLE
                list.visibility = View.VISIBLE
                adapter.setFilms(films)
            },
            { error ->
                progress?.visibility = View.INVISIBLE
                list.visibility = View.INVISIBLE
                layoutError?.visibility = View.VISIBLE

                error.printStackTrace()
            })
    }

    private fun setRecyclerViewScrollListener() {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // Listen when the state on scroll is changed
            override fun onScrollStateChanged(list: RecyclerView, newState: Int) {
                super.onScrollStateChanged(list, newState)

                // Create a LinearLayout
                val linearLayout: LinearLayoutManager = list.layoutManager as LinearLayoutManager
                val lastItemPosition = linearLayout.findLastVisibleItemPosition()

                // Check if the count item in the list if the last visible position
                if(list.layoutManager!!.itemCount == lastItemPosition +1) {
                    // Next page
                    page++

                    FilmsRepo.discoverFilms(page, context!!,
                        { films ->
                            progress?.visibility = View.INVISIBLE
                            layoutError?.visibility = View.INVISIBLE
                            list.visibility = View.VISIBLE
                            adapter.setFilms(films)
                        },
                        { error ->
                            progress?.visibility = View.INVISIBLE
                            list.visibility = View.INVISIBLE
                            layoutError?.visibility = View.VISIBLE
                            error.printStackTrace()
                        })
                    adapter.notifyItemRangeInserted(lastItemPosition + 1  , adapter.itemCount)
                }
            }
        })
    }

    interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }

}