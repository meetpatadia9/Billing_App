package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.activities.LoginActivity
import com.codebyzebru.myapplication.dataclasses.ProfileDataClass
import com.codebyzebru.myapplication.dataclasses.SettingTitleDataClass
import com.codebyzebru.myapplication.fragments.ProfileFragment
import com.codebyzebru.myapplication.fragments.SettingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.single_view_setting_title.view.*

class SettingListAdapter(val context: Context, private val settingTitles: List<SettingTitleDataClass>):
    RecyclerView.Adapter<SettingListAdapter.SettingListViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").orderByChild("partyName")

        holder.apply {
            Glide.with(context).load(settingTitles[position].icon).into(icon)
            title.text = settingTitles[position].settingTitle

            auth = FirebaseAuth.getInstance()
            database = Firebase.database.reference

            itemView.setOnClickListener {
                if (settingTitles[position].settingTitle == "User Profile") {
                    dbRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (item in snapshot.children) {
                                    val user = item.getValue(ProfileDataClass::class.java)
                                    user!!.key = item.key.toString()
                                    Toast.makeText(context, item.key.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("Error ~ Profile", error.toString())
                        }
                    })
                }
                else if (settingTitles[position].settingTitle == "Logout") {
                    Firebase.auth.signOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

}