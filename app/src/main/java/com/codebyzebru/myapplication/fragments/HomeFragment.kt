package com.codebyzebru.myapplication.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.TabLayoutAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_home).isChecked = true
        (activity as HomeActivity).title = "Home"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.home_tabLayout)
        val viewPager = view.findViewById<ViewPager>(R.id.home_viewPager)
        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)

        appBarLayout.setBackgroundColor(Color.TRANSPARENT)

        if (savedInstanceState == null) {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.home_viewPager, HistoryFragment()).commit()
        }

        val myAdapter = TabLayoutAdapter(requireActivity().supportFragmentManager)
        myAdapter.apply {
            addFragment(HistoryFragment(), "Sales")
            addFragment(HistoryFragment(), "History")
        }

        viewPager.apply {
            adapter = myAdapter
            currentItem = 0
        }

        val cardView = view.findViewById<CardView>(R.id.home_tabLayout_cardView)
        cardView.cardElevation = 0F

        tabLayout.apply {
            setupWithViewPager(viewPager)
            setSelectedTabIndicator(null)
            tabRippleColor = null
        }
    }

}