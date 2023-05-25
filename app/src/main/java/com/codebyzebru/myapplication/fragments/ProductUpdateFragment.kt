package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.dataclasses.AddInventoryDataClass
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_product_update.*

class ProductUpdateFragment (private val listedData: ViewInventoryDataClass) : Fragment() {

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as HomeActivity).title = "Update Item"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Setting data came from `PartiesFragment`
        key = listedData.key
        update_productName.setText(listedData.productName)
        update_purchasePrise.setText(listedData.purchasingPrice.toString())
        update_sellingPrice.setText(listedData.sellingPrice.toString())
        update_productQty.setText(listedData.productQty.toString())

        //  UPDATE BUTTON
        btnUpdateItem.setOnClickListener {
            val addItem = AddInventoryDataClass(
                productName = update_productName.text.toString(),
                purchasingPrice = update_purchasePrise.text.toString().toInt(),
                sellingPrice = update_sellingPrice.text.toString().toInt(),
                productQty = update_productQty.text.toString().toInt()
            )

            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val thisKey = database.child("Inventory Data").push().key.toString()
            dbRef.getReference("Users/$userID").child("Inventory Data").child(thisKey).setValue(addItem)

            FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_home, InventoryFragment())
                        .addToBackStack(null)
                        .commit()
                    Toast.makeText(context, "Item Updated!!", Toast.LENGTH_SHORT).show()
                }
        }

        //  DELETE BUTTON
        btnDeleteItem.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_home, InventoryFragment())
                        .addToBackStack(null)
                        .commit()
                    Toast.makeText(context, "Item Deleted!!", Toast.LENGTH_SHORT).show()
                }
        }

        //  CANCEL BUTTON
        /*btn_update_cancel.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout_home, InventoryFragment())
                .addToBackStack(null)
                .commit()
        }*/
    }

}