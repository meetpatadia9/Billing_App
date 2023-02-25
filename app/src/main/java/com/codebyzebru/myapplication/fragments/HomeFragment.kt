package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        /*
            when the fragment come in picture
            given item must be highlighted.
            and title of the activity must be sync with fragment
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_home).isChecked = true
        (activity as HomeActivity).setTitle("Home")

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}