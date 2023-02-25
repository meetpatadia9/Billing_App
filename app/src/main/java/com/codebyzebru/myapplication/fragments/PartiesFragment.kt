package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.PartyAdapter
import com.codebyzebru.myapplication.dataclasses.PartyDataClass

class PartiesFragment : Fragment() {

    lateinit var popupView: View
    var partyList = arrayListOf<PartyDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TO ENABLE OPTION MENU IN FRAGMENT
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_parties).isChecked = true
        (activity as HomeActivity).setTitle("Parties")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.parties_recyclerView)

        /*
                1)  Create DataClass with required parameters
                2)  Create AdapterFile
        */
        //  STATIC DATA TO PLACE IN RECYCLE-VIEW
        partyList = ArrayList<PartyDataClass>()
        partyList.apply {
            add(PartyDataClass("Akhilesh Jani", "Jani Pvt. Ltd", 30024))
            add(PartyDataClass("Maharshi Pandya", "Well-care medical store", 6020))
            add(PartyDataClass("Keval Patel", "S. S. Ayurveda", 31053))
            add(PartyDataClass("Vishal Dodiya", "Vishal Clinic", 13300))
            add(PartyDataClass("Karan Sharma", "Ambika Ayurveda Store", 3000))

            add(PartyDataClass("Akhilesh Jani", "Jani Pvt. Ltd", 30024))
            add(PartyDataClass("Maharshi Pandya", "Well-care medical store", 6020))
            add(PartyDataClass("Akhilesh Jani", "Jani Pvt. Ltd", 30024))
            add(PartyDataClass("Maharshi Pandya", "Well-care medical store", 6020))
            add(PartyDataClass("Keval Patel", "S. S. Ayurveda", 31053))
            add(PartyDataClass("Vishal Dodiya", "Vishal Clinic", 13300))
            add(PartyDataClass("Karan Sharma", "Ambika Ayurveda Store", 3000))

            add(PartyDataClass("Akhilesh Jani", "Jani Pvt. Ltd", 30024))
            add(PartyDataClass("Maharshi Pandya", "Well-care medical store", 6020))
            add(PartyDataClass("Keval Patel", "S. S. Ayurveda", 31053))
            add(PartyDataClass("Vishal Dodiya", "Vishal Clinic", 13300))
            add(PartyDataClass("Karan Sharma", "Ambika Ayurveda Store", 3000))

            add(PartyDataClass("Akhilesh Jani", "Jani Pvt. Ltd", 30024))
            add(PartyDataClass("Maharshi Pandya", "Well-care medical store", 6020))
            add(PartyDataClass("Vishal Dodiya", "Vishal Clinic", 13300))
            add(PartyDataClass("Karan Sharma", "Ambika Ayurveda Store", 3000))
            add(PartyDataClass("Keval Patel", "S. S. Ayurveda", 31053))
            add(PartyDataClass("Vishal Dodiya", "Vishal Clinic", 13300))
            add(PartyDataClass("Karan Sharma", "Ambika Ayurveda Store", 3000))
        }

        //  applying `Layout` to Recyclerview
        recyclerView.layoutManager =LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //  applying `adapter` to Recyclerview
        recyclerView.adapter = PartyAdapter(requireContext(), partyList)
    }

    //TO INFLATE OPTION MENU
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.party_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.partiesFrag_menu_addParty -> {
                //  subclass of `Dialog`
                val builder = AlertDialog.Builder(requireContext())

                //  Instantiates a layout XML file into its corresponding `View` objects
                val inflater = LayoutInflater.from(context)
                popupView = inflater.inflate(R.layout.popup_layout_addparty, null)
                builder.setView(popupView)

                val alertDialog = builder.create()
                alertDialog.show()

                popupView.findViewById<Button>(R.id.btnSaveParty).setOnClickListener {
                    Toast.makeText(context, "Party Added", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }

                popupView.findViewById<Button>(R.id.btn_addparty_cancel).setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}