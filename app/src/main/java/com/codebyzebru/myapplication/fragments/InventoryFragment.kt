package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.ProductAdapter
import com.codebyzebru.myapplication.dataclasses.AddInventoryDataClass
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.android.synthetic.main.fragment_product_update.*
import kotlinx.android.synthetic.main.popup_layout_additem.view.*
import java.util.*

class InventoryFragment : Fragment() {

    private lateinit var popupView: View
    private var itemList = arrayListOf<ViewInventoryDataClass>()

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
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_inventory).isChecked = true
        (activity as HomeActivity).title = "Inventory"

        //  Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.inventory_recyclerView)

        val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").orderByChild("productName")

        //  applying `Layout` to Recyclerview
        recyclerView.layoutManager =LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Inventory Data
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewInventoryDataClass::class.java)
                        listedData!!.key = item.key.toString()
                        itemList.add(listedData)
                    }

                    recyclerView.adapter = ProductAdapter(requireContext(), itemList,
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

        view.findViewById<FloatingActionButton>(R.id.inventoryFrag_fab).setOnClickListener {
            //  subclass of `Dialog`
            val builder = AlertDialog.Builder(requireContext())
            //  Instantiates a layout XML file into its corresponding `View` objects
            val inflater = LayoutInflater.from(context)
            popupView = inflater.inflate(R.layout.popup_layout_additem, null)
            builder.setView(popupView)

            val alertDialog = builder.create()
            alertDialog.show()

            popupView.findViewById<Button>(R.id.btnSaveItem).setOnClickListener {
                itemList.clear()

                val addItem = AddInventoryDataClass(
                    productName = popupView.addItem_edtxt_productName.text.toString(),
                    purchasingPrice = popupView.addItem_edtxt_purchasePrise.text.toString().toInt(),
                    sellingPrice = popupView.addItem_edtxt_sellingPrice.text.toString().toInt(),
                    productQty = popupView.addItem_edtxt_productQty.text.toString().toInt()
                )

                val key = database.child("Inventory Data").push().key.toString()
                FirebaseDatabase.getInstance().getReference("Users/$userID").child("Inventory Data").child(key).setValue(addItem)

                alertDialog.dismiss()
            }

            popupView.findViewById<Button>(R.id.btn_additem_cancel).setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    private fun openUpdatePopup(listDataClass: ViewInventoryDataClass) {
        val updateDetailDialog = Dialog(requireContext())
        updateDetailDialog.setContentView(R.layout.fragment_product_update)
        updateDetailDialog.show()

        //  Setting data came from `PartiesFragment`
        key = listDataClass.key
        updateDetailDialog.update_productName.setText(listDataClass.productName)
        updateDetailDialog.update_purchasePrise.setText(listDataClass.purchasingPrice.toString())
        updateDetailDialog.update_sellingPrice.setText(listDataClass.sellingPrice.toString())
        updateDetailDialog.update_productQty.setText(listDataClass.productQty.toString())

        //  UPDATE BUTTON
        updateDetailDialog.btnUpdateItem.setOnClickListener {
            val addItem = AddInventoryDataClass(
                productName = updateDetailDialog.update_productName.text.toString(),
                purchasingPrice = updateDetailDialog.update_purchasePrise.text.toString().toInt(),
                sellingPrice = updateDetailDialog.update_sellingPrice.text.toString().toInt(),
                productQty = updateDetailDialog.update_productQty.text.toString().toInt()
            )

            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val thisKey = database.child("Inventory Data").push().key.toString()
            FirebaseDatabase.getInstance().getReference("Users/$userID").child("Inventory Data").child(thisKey).setValue(addItem)

            FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDetailDialog.dismiss()
                    Toast.makeText(context, "Item Updated!!", Toast.LENGTH_SHORT).show()
                }
        }

        //  DELETE BUTTON
        updateDetailDialog.btnDeleteItem.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDetailDialog.dismiss()
                    Toast.makeText(context, "Item Deleted!!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        itemList.clear()
    }

}