package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.dataclasses.AddPartyDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_party_update.*

class PartyUpdateFragment(private val listDataClass: ViewPartyDataClass) : Fragment() {

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as HomeActivity).title = "Update Party"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_party_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        key = listDataClass.key
        update_partyName.setText(listDataClass.partyName)
        update_company_name.setText(listDataClass.companyName)
        update_address.setText(listDataClass.address)
        update_email.setText(listDataClass.email)
        update_contact.setText(listDataClass.contact)

        if (listDataClass.type == "B2B") {
            update_rBtn_B2B.isChecked = true
        } else if (listDataClass.type == "B2C") {
            update_rBtn_B2C.isChecked = true
        }

        val radioGroup: RadioGroup = view.findViewById(R.id.update_rg_type)
        var radioButton: RadioButton?

        btnUpdateParty.setOnClickListener {
            val selectedID = radioGroup.checkedRadioButtonId
            radioButton = view.findViewById(selectedID)

            val addParty = AddPartyDataClass(
                partyName = update_partyName.text.toString(),
                companyName = update_company_name.text.toString(),
                address = update_address.text.toString(),
                email = update_email.text.toString(),
                contact = update_contact.text.toString(),
                type = radioButton!!.text.toString()
            )

            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val thisKey = database.child("Party Data").push().key.toString()
            dbRef.getReference("Users/$userID").child("Party Data").child(thisKey).setValue(addParty)

            FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").child(key).removeValue()
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.layout_home, PartiesFragment()).addToBackStack(null).commit()
                    Toast.makeText(context, "Party Updated!!", Toast.LENGTH_SHORT).show()
                }
        }

        btnDeleteParty.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").child(key).removeValue()
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.layout_home, PartiesFragment()).addToBackStack(null).commit()
                    Toast.makeText(context, "Party Deleted!!", Toast.LENGTH_SHORT).show()
                }
        }

        btn_update_party_cancel.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout_home, PartiesFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}