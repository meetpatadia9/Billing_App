package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
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

        btnUpdateParty.setOnClickListener {

        }

        btnDeleteParty.setOnClickListener {

        }

        btn_update_party_cancel.setOnClickListener {

        }
    }

}