package com.example.fcojavierrodriguez.filmica.view.watchlist

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.View
import com.example.fcojavierrodriguez.filmica.R
import com.example.fcojavierrodriguez.filmica.data.Film
import com.example.fcojavierrodriguez.filmica.view.util.BaseFilmAdapter
import com.example.fcojavierrodriguez.filmica.view.util.BaseFilmHolder
import com.example.fcojavierrodriguez.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_watchlist.view.*

class WatchlistAdapter(itemClickListener: ((Film) -> Unit)? = null) :
    BaseFilmAdapter<WatchlistAdapter.WatchlistHolder>(
        layoutItem = R.layout.item_watchlist,
        holderCreator = { view -> WatchlistHolder(view, itemClickListener) }) {

    class WatchlistHolder(itemView: View, listener: ((Film) -> Unit)? = null) : BaseFilmHolder(itemView, listener) {

        override fun bindFilm(film: Film) {
            super.bindFilm(film)

            with(itemView) {
                labelTitle.text = film.title
                labelOverview.text = film.overview
                labelVotes.text = film.voteRating.toString()
                laodImage()

            }
        }

        private fun laodImage() {
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
                val overlayColor = Color.argb(
                    (Color.alpha(color) * 0.5).toInt(),
                    Color.red(color), Color.green(color),
                    Color.blue(color)
                )

                itemView.imgOverlay.setBackgroundColor(overlayColor)

            }
        }
    }
}