package com.example.fcojavierrodriguez.filmica.view.film

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.View
import com.example.fcojavierrodriguez.filmica.R
import com.example.fcojavierrodriguez.filmica.data.Film
import com.example.fcojavierrodriguez.filmica.view.util.BaseFilmAdapter
import com.example.fcojavierrodriguez.filmica.view.util.BaseFilmHolder
import com.example.fcojavierrodriguez.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_film.view.*

class FilmsAdapter(itemClickListener: ((Film) -> Unit)? = null) :
    BaseFilmAdapter<FilmsAdapter.FilmViewHolder>(
        layoutItem = R.layout.item_film,
        holderCreator = { view -> FilmViewHolder(view, itemClickListener) }
    ) {

    class FilmViewHolder(
        view: View,
        listener: ((Film) -> Unit)? = null
    ) : BaseFilmHolder(view, listener) {
        override fun bindFilm(film: Film) {
            super.bindFilm(film)

            with(itemView)
            {
                // Modify the item(Fielm) with the content of the film
                labelTitle.text = film.title
                titleGenre.text = film.genre
                labelVotes.text = film.voteRating.toString()
                loadImage()
            }
        }

        private fun loadImage() {

            val target = SimpleTarget(
                successCallback = { bitmap, from ->

                    itemView.imgPoster.setImageBitmap(bitmap)
                    setColorFrom(bitmap)
                })

            itemView.imgPoster.tag = target

            Picasso.get()
                .load(film.getPosterUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(target)
        }

        private fun setColorFrom(bitmap: Bitmap) {

            // Generate method, create an AsyncTask
            Palette.from(bitmap).generate { palette ->
                // Color by default
                val defaultColor = ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                // Get vibrant color from bitmap
                val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch
                // Final color
                val color = swatch?.rgb ?: defaultColor

                itemView.container.setBackgroundColor(color)
                itemView.containerData.setBackgroundColor(color)
            }
        }
    }
}