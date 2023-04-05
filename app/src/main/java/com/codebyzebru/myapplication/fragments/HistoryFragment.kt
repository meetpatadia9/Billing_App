package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.HistoryAdapter
import com.codebyzebru.myapplication.dataclasses.HistoryDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {

    val billList = arrayListOf<HistoryDataClass>()

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_history).isChecked = true
        (activity as HomeActivity).title = "History"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.historyFrag_recyclerView)

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Bills")

        //  applying `Layout` to Recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Bill Data
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(HistoryDataClass::class.java)
                        listedData!!.key = item.key.toString()
                        billList.add(listedData)
                    }
                    recyclerView.adapter = HistoryAdapter(requireContext(), billList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        billList.clear()
    }

}