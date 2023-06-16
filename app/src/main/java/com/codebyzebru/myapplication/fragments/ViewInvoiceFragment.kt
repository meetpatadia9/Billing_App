package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.databinding.FragmentViewInvoiceBinding
import com.codebyzebru.myapplication.dataclasses.BillDataClass
import com.codebyzebru.myapplication.dataclasses.ProfileDataClass
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewInvoiceFragment : Fragment() {

    lateinit var binding: FragmentViewInvoiceBinding

    private lateinit var invoiceKey: String
    private lateinit var userID: String
    lateinit var buyerUID: String
    val invoicedItems = arrayListOf<PurchasedItemDataClass>()

    private lateinit var userDatabase: DatabaseReference
    private lateinit var billDatabase: DatabaseReference
    private lateinit var partyDatabase: DatabaseReference
    private lateinit var purchasedItem: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        userDatabase = Firebase.database.reference.child("Users/$userID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentViewInvoiceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).supportActionBar?.hide()

        invoiceKey = this.arguments?.getString("invoiceNo").toString()
        billDatabase = Firebase.database.reference.child("Users/$userID/Bills/$invoiceKey")

        binding.btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        //  FETCHING LOGGED-IN USER'S DETAILS
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("snapshot", snapshot.toString())
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(ProfileDataClass::class.java)
                    userData!!.key = snapshot.key.toString()

                    if (userData.companyName == "") {
                        binding.txtSellerOrg.text = getString(R.string.default_org)
                    }
                    else {
                        binding.txtSellerOrg.text = userData.companyName
                    }
                    binding.txtSellerContact.text = userData.contact
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Failed to fetch user's detail", error.message)
            }
        })

        //  FETCHING INVOICE'S DETAILS
        billDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("billDatabase.snapshot", snapshot.toString())
                    val invoiceData = snapshot.getValue(BillDataClass::class.java)
                    invoiceData!!.key = snapshot.key.toString()

                    Log.d("invoiceData.purchasedItems", invoiceData.purchasedItems.toString())

                    val total = invoiceData.subTotal + invoiceData.tax /*+ invoiceData.billTotal*/

                    binding.txtInvoiceNo.text = invoiceData.billNo
                    binding.txtInvoiceDate.text = invoiceData.date
                    buyerUID = invoiceData.buyerUID
                    binding.txtPartyName.text = invoiceData.buyer
                    /*if (invoiceData.organization!!.isEmpty() && invoiceData.address!!.isNotEmpty()) {
                        binding.txtPartyOrg.text = invoiceData.address
                    } else if (invoiceData.organization.isNotEmpty() && invoiceData.address!!.isEmpty()) {
                        binding.txtPartyOrg.text = invoiceData.organization
                    } else {
                        binding.txtPartyOrg.visibility = View.GONE
                    }*/
                    binding.txtAmount.text = invoiceData.subTotal.toString()
                    binding.txtTotalAmount.text = total.toString()

                    //  FETCHING PURCHASED ITEMS DETAILS
                    purchasedItem = Firebase.database.reference.child("Users/$userID/Bills/${invoiceData.key}/purchasedItems/")
                    purchasedItem.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Log.d("purchasedItem.snapshot", snapshot.toString())
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("no purchasedItem", error.message)
                        }
                    })

                    //  FETCHING BUYER'S DETAILS
                    partyDatabase = Firebase.database.reference.child("Users/$userID/Party Data/$buyerUID")
                    partyDatabase.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d("partyDatabase.snapshot", snapshot.toString())
                            val buyerData = snapshot.getValue(ViewPartyDataClass::class.java)
                            binding.txtPartyOrg.text = buyerData?.companyName
                            if (buyerData!!.companyName == "") {
                                binding.txtPartyOrg.visibility = View.GONE
                                if (buyerData.address == "") {
                                    binding.txtPartyOrg.visibility = View.GONE
                                } else {
                                    binding.txtPartyOrg.visibility = View.VISIBLE
                                    binding.txtPartyOrg.text = buyerData.address
                                }
                            } else {
                                binding.txtPartyOrg.visibility = View.VISIBLE
                                binding.txtPartyOrg.text = buyerData.companyName
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Failed to fetch buyer's detail", error.message)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Failed to fetch invoice detail", error.message)
            }
        })
    }

}