package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.PartyAdapter
import com.codebyzebru.myapplication.databinding.FragmentPartiesBinding
import com.codebyzebru.myapplication.databinding.FragmentPartyUpdateBinding
import com.codebyzebru.myapplication.databinding.PopupLayoutAddpartyBinding
import com.codebyzebru.myapplication.databinding.ToastErrorBinding
import com.codebyzebru.myapplication.databinding.ToastSuccessBinding
import com.codebyzebru.myapplication.dataclasses.AddPartyDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PartiesFragment : Fragment() {

    lateinit var binding: FragmentPartiesBinding
    private lateinit var addBinding: PopupLayoutAddpartyBinding
    private lateinit var updateBinding: FragmentPartyUpdateBinding

    private var partyList = arrayListOf<ViewPartyDataClass>()
    lateinit var partyAdapter: PartyAdapter

    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
            when the fragment come in picture, respected navigation `menu item` must be highlighted
            and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_parties).isChecked = true
        (activity as HomeActivity).title = "Parties"

        //  Inflate the layout for this fragment
        binding = FragmentPartiesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  applying `Layout` to Recyclerview
        binding.partiesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        partyAdapter = PartyAdapter(requireContext(), partyList,
            object : PartyAdapter.OnItemClick {
                override fun onClick(listDataClass: ViewPartyDataClass) {
                    openUpdatePopup(listDataClass)
                }
            })

        //  Fetching Party Data
        database.orderByChild("partyName")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    partyList.clear()
                    if (snapshot.exists()) {
                        binding.partyFragNoDataFrameLayout.visibility = View.GONE
                        for (item in snapshot.children) {
                            val listedData = item.getValue(ViewPartyDataClass::class.java)
                            listedData!!.key = item.key.toString()
                            partyList.add(listedData)
                        }
                        binding.partiesRecyclerView.adapter = partyAdapter
                    }
                    else {
                        partyAdapter.notifyDataSetChanged()
                        binding.partyFragNoDataFrameLayout.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })


        //  ADD NEW PARTY
        binding.partyFragFab.setOnClickListener {
            addBinding = PopupLayoutAddpartyBinding.inflate(LayoutInflater.from(requireContext()))
            val addDialog = Dialog(requireContext())
            addDialog.setContentView(addBinding.root)
            addDialog.show()

            val name = addBinding.addPartyEdtxtPartyName
            val org = addBinding.addPartyEdtxtCompanyName
            val address = addBinding.addPartyEdtxtAddress
            val email = addBinding.addPartyEdtxtEmail
            val contact = addBinding.addPartyEdtxtContact
            var radioButton: RadioButton?

            //  `TextWatcher` on editTexts
            name.addTextChangedListener(addTextWatcher)
            org.addTextChangedListener(addTextWatcher)
            address.addTextChangedListener(addTextWatcher)
            email.addTextChangedListener(addTextWatcher)
            contact.addTextChangedListener(addTextWatcher)

            //  ADD BUTTON
            addBinding.btnSaveParty.setOnClickListener {
                addDialog.dismiss()

                if (name.text.toString().trim() == "") {
                    addBinding.til1.helperText = "Name is require"
                } else if (contact.text.toString().trim() == "") {
                    addBinding.til5.helperText = "Phone number is require"
                } else {      //  accepting condition
                    partyList.clear()

                    val selectedID = addBinding.addPartyRgType.checkedRadioButtonId
                    radioButton = addDialog.findViewById(selectedID)

                    val addParty = AddPartyDataClass(
                        partyName = name.text.toString(),
                        companyName = org.text?.toString(),
                        address = address.text?.toString(),
                        email = email.text?.toString(),
                        contact = contact.text.toString(),
                        type = radioButton?.text.toString()
                    )

                    val key = database.child("Party Data").push().key.toString()
                    FirebaseDatabase.getInstance().getReference("Users/$userID").child("Party Data")
                        .child(key).setValue(addParty)
                        .addOnSuccessListener {
                            greenToast("Party added to list.")
                        }
                        .addOnFailureListener {
                            redToast(it.message.toString())
                        }
                }
            }

            //  CANCEL BUTTON
            addBinding.btnAddpartyCancel.setOnClickListener {
                addDialog.dismiss()
            }
        }
    }

    private fun openUpdatePopup(listDataClass: ViewPartyDataClass) {
        updateBinding = FragmentPartyUpdateBinding.inflate(LayoutInflater.from(requireContext()))
        val updateDialog = Dialog(requireContext())
        updateDialog.setContentView(updateBinding.root)
        updateDialog.show()

        val name = updateBinding.updatePartyName
        val org = updateBinding.updateCompanyName
        val address = updateBinding.updateAddress
        val email = updateBinding.updateEmail
        val contact = updateBinding.updateContact
        var radioButton: RadioButton?

        //  Setting data came from `PartiesFragment`
        key = listDataClass.key
        name.setText(listDataClass.partyName)
        org.setText(listDataClass.companyName)
        address.setText(listDataClass.address)
        email.setText(listDataClass.email)
        contact.setText(listDataClass.contact)

        if (listDataClass.type == "B2B") {
            updateBinding.updateRBtnB2B.isChecked = true
        } else if (listDataClass.type == "B2C") {
            updateBinding.updateRBtnB2C.isChecked = true
        }

        //  `TextWatcher` on editTexts
        name.addTextChangedListener(updateTextWatcher)
        org.addTextChangedListener(updateTextWatcher)
        address.addTextChangedListener(updateTextWatcher)
        email.addTextChangedListener(updateTextWatcher)
        contact.addTextChangedListener(updateTextWatcher)

        //  UPDATE BUTTON
        updateBinding.btnUpdateParty.setOnClickListener {
            if (name.text.toString().trim() == "") {
                updateBinding.til1.helperText = "Name is require"
            }
            else if (contact.text.toString().trim() == "") {
                updateBinding.til5.helperText = "Phone number is require"
            }
            else {
                updateDialog.dismiss()

                val selectedID = updateBinding.updateRgType.checkedRadioButtonId
                radioButton = updateDialog.findViewById(selectedID)
                //  NAME
                database.child("$key/partyName").setValue(updateBinding.updatePartyName.text.toString())
                //  COMPANY NAME
                if (org.text?.toString()?.trim()?.isNotEmpty() == true) {
                    database.child("$key/companyName").setValue(updateBinding.updateCompanyName.text.toString())
                } else {
                    database.child("$key/companyName").setValue("")
                }
                //  ADDRESS
                if (address.text?.toString()?.trim()?.isNotEmpty() == true) {
                    database.child("$key/address").setValue(updateBinding.updateAddress.text.toString())
                } else {
                    database.child("$key/address").setValue("")
                }
                //  EMAIL
                database.child("$key/email").setValue(updateBinding.updateEmail.text.toString().trim())
                //  EMAIL
                database.child("$key/contact").setValue(updateBinding.updateContact.text.toString().trim())
                //  GENDER
                if (radioButton?.text.toString().isNotEmpty()) {
                    database.child("$key/type").setValue(radioButton)
                } else {
                    database.child("$key/type").setValue("")
                }
                greenToast("Party data updated.")
                /*val selectedID = updateBinding.updateRgType.checkedRadioButtonId
                radioButton = updateDialog.findViewById(selectedID)

                val addParty = AddPartyDataClass(
                    partyName = name.text.toString(),
                    companyName = org.text?.toString(),
                    address = address.text?.toString(),
                    email = email.text?.toString(),
                    contact = contact.text.toString(),
                    type = radioButton?.text.toString()
                )

                val thisKey = database.child("Party Data").push().key.toString()
                FirebaseDatabase.getInstance().getReference("Users/$userID").child("Party Data")
                    .child(thisKey).setValue(addParty)

                FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").child(key)
                    .removeValue()
                    .addOnSuccessListener {
                        updateDialog.dismiss()
                        greenToast("Party data updated.")
                    }
                    .addOnFailureListener {
                        redToast(it.message.toString())
                    }*/
            }
        }

        //  DELETE BUTTON
        updateBinding.btnDeleteParty.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDialog.dismiss()
                    greenToast("Party removed from list.")
                }
                .addOnFailureListener {
                    redToast(it.message.toString())
                }
        }
    }

    //  `TextWatcher` for Add-Item-Dialog fields
    private val addTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            addBinding.til1.helperText = ""
            addBinding.til5.helperText = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s.hashCode()) {
                addBinding.addPartyEdtxtPartyName.text.hashCode() -> {
                    if (addBinding.addPartyEdtxtPartyName.text.toString().trim() == "") {
                        addBinding.til1.helperText = "Name is require"
                    }
                }
                addBinding.addPartyEdtxtContact.text.hashCode() -> {
                    if (addBinding.addPartyEdtxtContact.text.toString().trim() == "") {
                        addBinding.til5.helperText = "Phone number is require"
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            when (s.hashCode()) {
                addBinding.addPartyEdtxtPartyName.text.hashCode() -> {
                    if (addBinding.addPartyEdtxtPartyName.text.toString().trim() == "") {
                        addBinding.til1.helperText = "Name is require"
                    }
                }
                addBinding.addPartyEdtxtContact.text.hashCode() -> {
                    if (addBinding.addPartyEdtxtContact.text.toString().trim() == "") {
                        addBinding.til5.helperText = "Phone number is require"
                    }
                }
            }
        }
    }

    //  `TextWatcher` for Update-Item-Dialog fields
    private val updateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            updateBinding.til1.helperText = ""
            updateBinding.til5.helperText = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s.hashCode()) {
                updateBinding.updatePartyName.text.hashCode() -> {
                    if (updateBinding.updatePartyName.text.toString().trim() == "") {
                        updateBinding.til1.helperText = "Name is require"
                    }
                }
                updateBinding.updateContact.text.hashCode() -> {
                    if (updateBinding.updateContact.text.toString().trim() == "") {
                        updateBinding.til5.helperText = "Phone number is require"
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            when (s.hashCode()) {
                updateBinding.updatePartyName.text.hashCode() -> {
                    if (updateBinding.updatePartyName.text.toString().trim() == "") {
                        updateBinding.til1.helperText = "Name is require"
                    }
                }
                updateBinding.updateContact.text.hashCode() -> {
                    if (updateBinding.updateContact.text.toString().trim() == "") {
                        updateBinding.til5.helperText = "Phone number is require"
                    }
                }
            }
        }
    }

    private fun redToast(message: String) {
        val toastBinding = ToastErrorBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = message
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun greenToast(message: String) {
        val toastBinding = ToastSuccessBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = message
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    override fun onResume() {
        super.onResume()
        partyList.clear()
    }

}