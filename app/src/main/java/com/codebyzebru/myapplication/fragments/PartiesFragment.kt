package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_parties.*
import kotlinx.android.synthetic.main.fragment_party_update.*
import kotlinx.android.synthetic.main.popup_layout_addparty.*
import kotlinx.android.synthetic.main.popup_layout_addparty.view.*

class PartiesFragment : Fragment() {

    private lateinit var popupView: View
    private var partyList = arrayListOf<ViewPartyDataClass>()

    private lateinit var database: DatabaseReference
    lateinit var userID: String
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_parties).isChecked = true
        (activity as HomeActivity).title = "Parties"

        //  Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.parties_recyclerView)

        val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").orderByChild("partyName")

        //  applying `Layout` to Recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Party Data
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                partyList.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewPartyDataClass::class.java)
                        listedData!!.key = item.key.toString()
                        partyList.add(listedData)
                    }

                    recyclerView.adapter = PartyAdapter(requireContext(), partyList,
                        object : PartyAdapter.OnItemClick {
                            override fun onClick(listDataClass: ViewPartyDataClass) {
                                openUpdatePopup(listDataClass)
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })

        view.findViewById<FloatingActionButton>(R.id.partyFrag_fab).setOnClickListener {
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
                FirebaseDatabase.getInstance().getReference("Users/$userID").child("Party Data").child(key).setValue(addParty)

                alertDialog.dismiss()
            }

            popupView.findViewById<Button>(R.id.btn_addparty_cancel).setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    private fun openUpdatePopup(listDataClass: ViewPartyDataClass) {
        val updateDetailDialog = Dialog(requireContext())
        updateDetailDialog.setContentView(R.layout.fragment_party_update)
        updateDetailDialog.show()

        //  Setting data came from `PartiesFragment`
        key = listDataClass.key
        updateDetailDialog.update_partyName.setText(listDataClass.partyName)
        updateDetailDialog.update_company_name.setText(listDataClass.companyName)
        updateDetailDialog.update_address.setText(listDataClass.address)
        updateDetailDialog.update_email.setText(listDataClass.email)
        updateDetailDialog.update_contact.setText(listDataClass.contact)

        if (listDataClass.type == "B2B") {
            updateDetailDialog.update_rBtn_B2B.isChecked = true
        } else if (listDataClass.type == "B2C") {
            updateDetailDialog.update_rBtn_B2C.isChecked = true
        }

        val radioGroup: RadioGroup = updateDetailDialog.findViewById(R.id.update_rg_type)
        var radioButton: RadioButton?

        //  UPDATE BUTTON
        updateDetailDialog.btnUpdateParty.setOnClickListener {
            val selectedID = radioGroup.checkedRadioButtonId
            radioButton = updateDetailDialog.findViewById(selectedID)

            val addParty = AddPartyDataClass(
                partyName = updateDetailDialog.update_partyName.text.toString(),
                companyName = updateDetailDialog.update_company_name.text.toString(),
                address = updateDetailDialog.update_address.text.toString(),
                email = updateDetailDialog.update_email.text.toString(),
                contact = updateDetailDialog.update_contact.text.toString(),
                type = radioButton?.text.toString()
            )

            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val thisKey = database.child("Party Data").push().key.toString()
            FirebaseDatabase.getInstance().getReference("Users/$userID").child("Party Data").child(thisKey).setValue(addParty)

            FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDetailDialog.dismiss()
                    Toast.makeText(context, "Party Updated!!", Toast.LENGTH_SHORT).show()
                }
        }

        //  DELETE BUTTON
        updateDetailDialog.btnDeleteParty.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDetailDialog.dismiss()
                    Toast.makeText(context, "Party Deleted!!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        partyList.clear()
    }

}