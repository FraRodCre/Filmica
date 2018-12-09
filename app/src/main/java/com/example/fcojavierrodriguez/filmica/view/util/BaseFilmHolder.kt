package com.example.fcojavierrodriguez.filmica.view.util

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.fcojavierrodriguez.filmica.data.Film

open class BaseFilmHolder(
    itemView: View,
    clickListener: ((Film) -> Unit)? = null
) : RecyclerView.ViewHolder(itemView) {
    lateinit var film: Film

    init{
        // Notify when do click in a film
        itemView.setOnClickListener{
            clickListener?.invoke(film)
        }
    }

    // Oper permit rewrite method with inheritance
    open fun bindFilm(film: Film) {
        this.film = film
    }
}