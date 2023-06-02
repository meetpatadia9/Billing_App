package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.adapters.HistoryAdapter
import com.codebyzebru.myapplication.databinding.FragmentHistoryBinding
import com.codebyzebru.myapplication.dataclasses.HistoryDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {

    lateinit var binding: FragmentHistoryBinding

    val billList = arrayListOf<HistoryDataClass>()

    private lateinit var database: DatabaseReference
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_history).isChecked = true
        (activity as HomeActivity).title = "History"
        */

        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  applying `Layout` to Recyclerview
        binding.historyFragRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Bill Data
        FirebaseDatabase.getInstance().getReference("Users/$userID/Bills")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.historyFragNoDataFrameLayout.visibility = View.GONE
                        for (item in snapshot.children) {
                            val listedData = item.getValue(HistoryDataClass::class.java)
                            listedData!!.key = item.key.toString()
                            billList.add(listedData)
                        }
                        binding.historyFragRecyclerView.adapter = HistoryAdapter(requireActivity(), billList)
                    }
                    else {
                        binding.historyFragNoDataFrameLayout.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error", error.toString())
                }
            })
    }

}