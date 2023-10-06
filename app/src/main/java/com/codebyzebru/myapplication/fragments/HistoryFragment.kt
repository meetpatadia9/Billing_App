package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.HistoryAdapter
import com.codebyzebru.myapplication.databinding.FragmentHistoryBinding
import com.codebyzebru.myapplication.dataclasses.HistoryDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    lateinit var binding: FragmentHistoryBinding

    lateinit var adapter: HistoryAdapter
    val billList = arrayListOf<HistoryDataClass>()
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
            when the fragment come in picture, respected navigation `menu item` must be highlighted
            and `title` of the activity must be sync with fragment.
        */

        /**
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_history).isChecked = true
        (activity as HomeActivity).title = "History"
        */

        //  Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).supportActionBar?.show()

        adapter = HistoryAdapter(requireActivity(), billList,
            object : HistoryAdapter.OnClick {
                override fun openInvoice(historyDataClass: HistoryDataClass) {
                    val bundle = Bundle()
                    bundle.putString("invoiceNo", historyDataClass.key)

                    val fragment = ViewInvoiceFragment()
                    fragment.arguments = bundle

                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_home, fragment, "VIEW-INVOICE")
                        .addToBackStack(null)
                        .commit()
                }
            })

        //  applying `Layout` to Recyclerview
        binding.historyFragRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Bill Data
        FirebaseDatabase.getInstance().getReference("Users/$userID/Bills")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.historyFragNoDataFrameLayout.visibility = View.GONE

                        for (item in snapshot.children) {
                            val listedData = item.getValue(HistoryDataClass::class.java)
                            listedData!!.key = item.key.toString()
                            billList.add(listedData)
                        }

                        binding.historyFragRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                    else {
                        binding.historyFragNoDataFrameLayout.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
    }

}