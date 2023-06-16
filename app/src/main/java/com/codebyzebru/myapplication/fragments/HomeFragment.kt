package com.codebyzebru.myapplication.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.TabLayoutAdapter
import com.codebyzebru.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
            when the fragment come in picture, respected navigation `menu item` must be highlighted
            and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_home).isChecked = true
        (activity as HomeActivity).title = "Home"

        //  Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBarLayout.setBackgroundColor(Color.TRANSPARENT)

        if (savedInstanceState == null) {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.home_viewPager, HistoryFragment()).commit()
        }

        val myAdapter = TabLayoutAdapter(requireActivity().supportFragmentManager)
        myAdapter.apply {
            addFragment(HistoryFragment(), "Sales")
            addFragment(HistoryFragment(), "History")
        }

        binding.homeViewPager.apply {
            adapter = myAdapter
            currentItem = 0
        }

        binding.homeTabLayoutCardView.cardElevation = 0F

        binding.homeTabLayout.apply {
            setupWithViewPager(binding.homeViewPager)
            setSelectedTabIndicator(null)
            tabRippleColor = null
        }

        /*
            TO ADD SPACE BETWEEN TABS OF TAB-LAYOUT
        */
        val tabs = binding.homeTabLayout.getChildAt(0) as ViewGroup

        for (i in 0 until tabs.childCount ) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginEnd = 15
            layoutParams.marginStart = 15
            layoutParams.width = 13
            tab.layoutParams = layoutParams
            binding.homeTabLayout.requestLayout()
        }
    }

}