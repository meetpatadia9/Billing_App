package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.SignInSignUpActivity
import com.codebyzebru.myapplication.dataclasses.ProfileDataClass
import com.codebyzebru.myapplication.dataclasses.SettingTitleDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.single_view_setting_title.view.*

class SettingListAdapter(val context: Context, private val settingTitles: List<SettingTitleDataClass>, val listener: SettingInterface):
    RecyclerView.Adapter<SettingListAdapter.SettingListViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val userData = arrayListOf<ProfileDataClass>()

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

            auth = FirebaseAuth.getInstance()
            database = Firebase.database.reference

            itemView.setOnClickListener {
                if (settingTitles[position].settingTitle == "User Profile") {
                    listener.onCLick(FirebaseAuth.getInstance().currentUser!!.uid)
                }
                else if (settingTitles[position].settingTitle == "Logout") {
                    Firebase.auth.signOut()
                    context.startActivity(
                        Intent(context, SignInSignUpActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }
            }
        }
    }

    interface SettingInterface {
        fun onCLick(userID: String)
    }

}