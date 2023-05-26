@file:Suppress("DEPRECATION")

package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.ProductAdapter
import com.codebyzebru.myapplication.databinding.FragmentInventoryBinding
import com.codebyzebru.myapplication.databinding.FragmentProductUpdateBinding
import com.codebyzebru.myapplication.databinding.PopupLayoutAdditemBinding
import com.codebyzebru.myapplication.dataclasses.AddInventoryDataClass
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class InventoryFragment : Fragment() {

    lateinit var binding: FragmentInventoryBinding
    private lateinit var addBinding: PopupLayoutAdditemBinding
    private lateinit var updateBinding: FragmentProductUpdateBinding

    private var itemList = arrayListOf<ViewInventoryDataClass>()

    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
            when the fragment come in picture, respected navigation `menu item` must be highlighted
            and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_inventory).isChecked = true
        (activity as HomeActivity).title = "Inventory"

        //  Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  applying `Layout` to Recyclerview
        binding.inventoryRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Inventory Data
        FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").orderByChild("productName")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            val listedData = item.getValue(ViewInventoryDataClass::class.java)
                            listedData!!.key = item.key.toString()
                            itemList.add(listedData)
                        }

                        binding.inventoryRecyclerView.adapter = ProductAdapter(requireContext(), itemList,
                            object : ProductAdapter.OnItemClick {
                                override fun onClick(listDataClass: ViewInventoryDataClass) {
                                    openUpdatePopup(listDataClass)
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error", error.toString())
                }
            })

        //  ADD NEW PRODUCT
        binding.inventoryFragFab.setOnClickListener {
            addBinding = PopupLayoutAdditemBinding.inflate(LayoutInflater.from(requireContext()))
            val addDialog = Dialog(requireContext())
            addDialog.setContentView(addBinding.root)
            addDialog.show()

            val pName = addBinding.addItemEdtxtProductName
            val pPrice = addBinding.addItemEdtxtPurchasePrise
            val sPrice = addBinding.addItemEdtxtSellingPrice
            val qty = addBinding.addItemEdtxtProductQty

            addBinding.btnSaveItem.setOnClickListener {
                if (pName.text.toString().trim() == "") {
                    addBinding.til1.helperText = "Required*"
                }
                else if (pPrice.text.toString().trim() == "") {
                    addBinding.til2.helperText = "Required*"
                }
                else if (sPrice.text.toString().trim() == "") {
                    addBinding.til3.helperText = "Required*"
                }
                else if (qty.text.toString().trim() == "") {
                    addBinding.til4.helperText = "Required*"
                }
                else {
                    itemList.clear()

                    val addItem = AddInventoryDataClass(
                        productName = pName.text.toString(),
                        purchasingPrice = pPrice.text.toString().toInt(),
                        sellingPrice = sPrice.text.toString().toInt(),
                        productQty = qty.text.toString().toInt()
                    )

                    val key = database.child("Inventory Data").push().key.toString()
                    FirebaseDatabase.getInstance().getReference("Users/$userID").child("Inventory Data").child(key).setValue(addItem)

                    greenToast("Item Added!!")
                    addDialog.dismiss()
                }
            }

            addBinding.btnAdditemCancel.setOnClickListener {
                addDialog.dismiss()
            }
        }
    }

    private fun openUpdatePopup(listDataClass: ViewInventoryDataClass) {
        updateBinding = FragmentProductUpdateBinding.inflate(LayoutInflater.from(requireContext()))

        val updateDialog = Dialog(requireContext())
        updateDialog.setContentView(updateBinding.root)
        updateDialog.show()

        val pName = updateBinding.updateProductName
        val pPrice = updateBinding.updatePurchasePrise
        val sPrice = updateBinding.updateSellingPrice
        val qty = updateBinding.updateProductQty

        //  Setting data came from `PartiesFragment`
        key = listDataClass.key
        pName.setText(listDataClass.productName)
        pPrice.setText(listDataClass.purchasingPrice.toString())
        sPrice.setText(listDataClass.sellingPrice.toString())
        qty.setText(listDataClass.productQty.toString())

        //  UPDATE BUTTON
        updateBinding.btnUpdateItem.setOnClickListener {
            if (pName.text.toString().trim() == "") {
                updateBinding.til1.helperText = "Required*"
            }
            else if (pPrice.text.toString().trim() == "") {
                updateBinding.til2.helperText = "Required*"
            }
            else if (sPrice.text.toString().trim() == "") {
                updateBinding.til3.helperText = "Required*"
            }
            else if (qty.text.toString().trim() == "") {
                updateBinding.til4.helperText = "Required*"
            }
            else {
                val addItem = AddInventoryDataClass(
                    productName = pName.text.toString(),
                    purchasingPrice = pPrice.text.toString().toInt(),
                    sellingPrice = sPrice.text.toString().toInt(),
                    productQty = qty.text.toString().toInt()
                )

                val thisKey = database.child("Inventory Data").push().key.toString()
                FirebaseDatabase.getInstance().getReference("Users/$userID").child("Inventory Data").child(thisKey).setValue(addItem)

                FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                    .addOnSuccessListener {
                        updateDialog.dismiss()
                        greenToast("Item Updated!!")
                    }
            }
        }

        //  DELETE BUTTON
        updateBinding.btnDeleteItem.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDialog.dismiss()
                    greenToast("Item Deleted!!")
                }
        }
    }

    private fun greenToast(message: String) {
        val toast: Toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        val view = toast.view

        //Gets the actual oval background of the Toast then sets the colour filter
        view!!.background.setColorFilter(resources.getColor(R.color.color5), PorterDuff.Mode.SRC_IN)

        //Gets the TextView from the Toast so it can be edited
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(resources.getColor(R.color.white))

        toast.show()
    }

    override fun onResume() {
        super.onResume()
        itemList.clear()
    }

}