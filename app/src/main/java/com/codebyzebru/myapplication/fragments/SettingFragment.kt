package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity


class SettingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_setting).isChecked = true
        (activity as HomeActivity).setTitle("Setting")
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }




}