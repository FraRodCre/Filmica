package com.example.fcojavierrodriguez.filmica.view.film

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.fcojavierrodriguez.filmica.R
import com.example.fcojavierrodriguez.filmica.data.Film
import com.example.fcojavierrodriguez.filmica.view.detail.DetailsActivity
import com.example.fcojavierrodriguez.filmica.view.detail.DetailsFragment
import com.example.fcojavierrodriguez.filmica.view.search.SearchFragment
import com.example.fcojavierrodriguez.filmica.view.trends.TrendsFragment
import com.example.fcojavierrodriguez.filmica.view.watchlist.WatchlistFragment
import kotlinx.android.synthetic.main.activity_films.*


// Constants to identify the different fragments
const val TAG_FILMS = "films"
const val TAG_WATCHLIST = "watchlist"
const val TAG_TRENDS = "trends"
const val TAG_SEARCH = "search"

class FilmsActivity : AppCompatActivity(), FilmsFragment.OnItemClickListener, WatchlistFragment.OnItemClickListener,
    TrendsFragment.OnItemClickListener, SearchFragment.OnItemClickListener {

    // Fragments to load in the activity
    private lateinit var filmsFragment: FilmsFragment
    private lateinit var watchlistFragment: WatchlistFragment
    private lateinit var trendsFragment: TrendsFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

        // Check if exist something fragment. If not exist it, create the fragment. Else, get and load the fragment.
        if (savedInstanceState == null) {
            setupFragments()
        } else {
            val activeTag = savedInstanceState.getString("active", TAG_FILMS)
            //restore the fragment selected in the activity
            restoreFragments(activeTag)
        }

        navigation?.setOnNavigationItemReselectedListener { item ->
            val id = item.itemId

            when (id) {
                R.id.action_discover -> showMainFragment(filmsFragment)
                R.id.action_watchlist -> showMainFragment(watchlistFragment)
                R.id.action_trends -> showMainFragment(trendsFragment)
                R.id.action_search -> showMainFragment(searchFragment)
            }

            true
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("active", activeFragment.tag)
    }

    private fun setupFragments() {
        filmsFragment = FilmsFragment()
        watchlistFragment = WatchlistFragment()
        trendsFragment = TrendsFragment()
        searchFragment = SearchFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.container_list, filmsFragment, TAG_FILMS)
            .add(R.id.container_list, watchlistFragment, TAG_WATCHLIST)
            .add(R.id.container_list, trendsFragment, TAG_TRENDS)
            .add(R.id.container_list, searchFragment, TAG_SEARCH)
            .hide(watchlistFragment)
            .hide(trendsFragment)
            .hide(searchFragment)
            .commit()

        activeFragment = filmsFragment
    }

    private fun restoreFragments(tag: String) {
        filmsFragment = supportFragmentManager.findFragmentByTag(TAG_FILMS) as FilmsFragment
        watchlistFragment = supportFragmentManager.findFragmentByTag(TAG_WATCHLIST) as WatchlistFragment
        trendsFragment = supportFragmentManager.findFragmentByTag(TAG_TRENDS) as TrendsFragment
        searchFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH) as SearchFragment

        if (tag == TAG_WATCHLIST) {
            activeFragment = watchlistFragment
        } else if (tag == TAG_FILMS) {
            activeFragment = filmsFragment
        } else if (tag == TAG_TRENDS) {
            activeFragment = trendsFragment
        } else {
            activeFragment = searchFragment
        }

    }


    override fun onItemClicked(film: Film) {
        showDetails(film.id)
    }

    private fun showMainFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()

        activeFragment = fragment

    }

    fun showDetails(id: String) {
        if (isTablet()) {
            showDetailsFragment(id)
        } else {
            launchDetailsActivity(id)
        }
    }

    private fun isTablet() = this.containerDetails != null


    private fun showDetailsFragment(id: String) {
        val detailsFragment = DetailsFragment.newInstance(id)
        img_placeholder_detail.visibility = View.INVISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerDetails, detailsFragment)
            .commit()

    }

    private fun launchDetailsActivity(id: String) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

}