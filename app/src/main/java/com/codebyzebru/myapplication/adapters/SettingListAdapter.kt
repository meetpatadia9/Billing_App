package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.dataclasses.SettingTitleDataClass
import kotlinx.android.synthetic.main.single_view_setting_title.view.*

class SettingListAdapter(val context: Context, private val settingTitles: List<SettingTitleDataClass>):
    RecyclerView.Adapter<SettingListAdapter.SettingListViewHolder>() {

    class SettingListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.setting_icon
        val title: TextView = itemView.setting_title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_view_setting_title, parent, false)
        return SettingListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return settingTitles.size
    }

    override fun onBindViewHolder(holder: SettingListViewHolder, position: Int) {
        holder.apply {
            Glide.with(context).load(settingTitles[position].icon).into(icon)
            title.text = settingTitles[position].settingTitle
        }
    }

}