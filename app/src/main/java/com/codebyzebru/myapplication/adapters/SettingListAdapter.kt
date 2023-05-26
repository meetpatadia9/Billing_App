package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebyzebru.myapplication.activities.SignInSignUpActivity
import com.codebyzebru.myapplication.databinding.SingleViewSettingTitleBinding
import com.codebyzebru.myapplication.dataclasses.SettingTitleDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SettingListAdapter(val context: Context, private val settingTitles: List<SettingTitleDataClass>, private val listener: SettingInterface):
    RecyclerView.Adapter<SettingListAdapter.SettingListViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    class SettingListViewHolder(val itemBinding: SingleViewSettingTitleBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingListViewHolder {
        val itemBinding = SingleViewSettingTitleBinding.inflate(LayoutInflater.from(context), parent, false)
        return SettingListViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return settingTitles.size
    }

    override fun onBindViewHolder(holder: SettingListViewHolder, position: Int) {
        holder.apply {
            with(settingTitles[position]) {
                Glide.with(context).load(this.icon).into(itemBinding.settingIcon)
                itemBinding.settingTitle.text = this.settingTitle
            }

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