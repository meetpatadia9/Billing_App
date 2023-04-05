package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.PartyAdapter
import com.codebyzebru.myapplication.dataclasses.AddPartyDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.popup_layout_addparty.*
import kotlinx.android.synthetic.main.popup_layout_addparty.view.*

class PartiesFragment : Fragment() {

    private lateinit var popupView: View
    private var partyList = arrayListOf<ViewPartyDataClass>()

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  TO ENABLE OPTION MENU IN FRAGMENT
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_parties).isChecked = true
        (activity as HomeActivity).title = "Parties"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.parties_recyclerView)

        database = Firebase.database.reference

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").orderByChild("partyName")


        //  applying `Layout` to Recyclerview
        recyclerView.layoutManager =LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewPartyDataClass::class.java)
                        listedData!!.key = item.key.toString()
                        partyList.add(listedData)
                    }
                    recyclerView.adapter = PartyAdapter(requireContext(), partyList,
                        object : PartyAdapter.OnItemClick {
                            override fun onClick(listDataClass: ViewPartyDataClass) {
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.layout_home, PartyUpdateFragment(listDataClass))
                                    .addToBackStack(null)
                                    .commit()
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })
    }

    //  TO INFLATE OPTION MENU
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.party_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //  menu click listener
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

                val radioGroup: RadioGroup = popupView.findViewById(R.id.addParty_rg_type)
                var radioButton: RadioButton?

                popupView.findViewById<Button>(R.id.btnSaveParty).setOnClickListener {
                    partyList.clear()

                    val selectedID = radioGroup.checkedRadioButtonId
                    radioButton = popupView.findViewById(selectedID)

                    val addParty = AddPartyDataClass(
                        partyName = popupView.addParty_edtxt_partyName.text.toString(),
                        companyName = popupView.addParty_edtxt_company_name.text.toString(),
                        address = popupView.addParty_edtxt_address.text.toString(),
                        email = popupView.addParty_edtxt_email.text.toString(),
                        contact = popupView.addParty_edtxt_contact.text.toString(),
                        type = radioButton?.text.toString()
                    )

                    val userID = FirebaseAuth.getInstance().currentUser!!.uid
                    val key = database.child("Party Data").push().key.toString()
                    dbRef.getReference("Users/$userID").child("Party Data").child(key).setValue(addParty)

                    alertDialog.dismiss()
                }

                popupView.findViewById<Button>(R.id.btn_addparty_cancel).setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        partyList.clear()
    }

}